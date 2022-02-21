package com.orbitalsonic.androidinapppurchasingv4kotlin

import android.app.Activity
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.android.billingclient.api.*

class BillingUtilsIAP  constructor(mContext: Context) {

    var isPremium: MutableLiveData<Boolean> = MutableLiveData(false)

    init {
        if (billingClient == null) {
            purchaseUpdateListener =
                PurchasesUpdatedListener { billingResult: BillingResult, purchases: List<Purchase>? ->
                    Log.i("BillingTag", "getOldPurchases: in Listener")
                    if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
                        for (purchase in purchases) {
                            handlePurchase(mContext,purchase)
                        }
                    } else if (billingResult.responseCode == BillingClient.BillingResponseCode.USER_CANCELED) {
                        Log.i("BillingTag", "getOldPurchases: User Cancelled")
                    } else {
                        Log.i("BillingTag", "getOldPurchases: Other Error")
                    }
                }
            billingClient = BillingClient.newBuilder(mContext)
                .setListener(purchaseUpdateListener!!)
                .enablePendingPurchases()
                .build()
            setupConnection(mContext)
        }
    }

    private fun setupConnection(context: Context) {
        billingClient?.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    isBillingReady = true
                    Log.i("BillingTag", "onBillingServiceDisconnected: Setup Connection")
                    getOldPurchases(context)
                }
            }

            override fun onBillingServiceDisconnected() {
                Log.i("BillingTag", "onBillingServiceDisconnected: Setup Connection Failed")
                isBillingReady = false
            }
        })
    }

    fun purchase(activity: Activity, product: String) {
        if (isBillingReady) {
            val skuList: MutableList<String> = ArrayList()
            skuList.add(product)
            val params = SkuDetailsParams.newBuilder()
            params.setSkusList(skuList).setType(BillingClient.SkuType.INAPP)
            billingClient!!.querySkuDetailsAsync(
                params.build()
            ) { _, skuDetailsList: List<SkuDetails?>? ->
                // Process the result.
                if (skuDetailsList != null) {
                    if (skuDetailsList.isNotEmpty()) {
                        val billingFlowParams = BillingFlowParams.newBuilder()
                            .setSkuDetails(skuDetailsList[0]!!)
                            .build()
                        val responseCode = billingClient!!.launchBillingFlow(
                            activity,
                            billingFlowParams
                        ).responseCode
                        if (responseCode != BillingClient.BillingResponseCode.OK) {
                            Log.i("BillingTag", "getOldPurchases: Please try Again Later1")
                        }
                    }
                }
            }
        } else {
            Log.i("BillingTag", "getOldPurchases: Please try Again Later2")
            setupConnection(activity)
        }
    }

    // here product ID is package name
    private fun handlePurchase(context: Context, purchase: Purchase) {
        val acknowledgePurchaseResponseListener = AcknowledgePurchaseResponseListener {
            Log.i(
                "BillingTag",
                "getOldPurchases: Acknowledge"
            )
        }
        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
            purchase.skus.forEach { sku ->
                val lifetime = if (BuildConfig.DEBUG)
                    "android.test.purchased" else
                    context.packageName
                if (sku == lifetime) {
                    isPremium.postValue(true)
                    Log.d("Billing_status", isPremium.value.toString())
                    isPurchased = true

                    SharedPreferencesUtils.setBillingPurchasing(context,true)
                    Log.i("BillingTag", "handlePurchase: premium")
                    Toast.makeText(context, "Purchased successfully", Toast.LENGTH_SHORT).show()
                }
                if (!purchase.isAcknowledged) {
                    val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                        .setPurchaseToken(purchase.purchaseToken)
                        .build()
                    billingClient!!.acknowledgePurchase(
                        acknowledgePurchaseParams,
                        acknowledgePurchaseResponseListener
                    )
                }
            }
        } else {
            Toast.makeText(context, "Purchased dismissed", Toast.LENGTH_SHORT).show()
        }
    }

    // here product ID is package name
    fun getOldPurchases(context: Context) {
        val result = billingClient?.queryPurchasesAsync(
            BillingClient.SkuType.INAPP,
            object : PurchasesResponseListener {
                override fun onQueryPurchasesResponse(
                    p0: BillingResult,
                    purchases: MutableList<Purchase>,
                ) {
                    for (purchase in purchases) {
                        val lifetime = if (BuildConfig.DEBUG) "android.test.purchased"
                        else context.packageName

                        if (purchase.skus.contains(lifetime)) {
                            Log.d("Billing_status_old", true.toString())
                            isPremium.postValue(true)
                            isPurchased = true

                            SharedPreferencesUtils.setBillingPurchasing(context,true)

                        }
                    }
                }

            })

    }

    companion object {
        var billingClient: BillingClient? = null
        var purchaseUpdateListener: PurchasesUpdatedListener? = null
        var isBillingReady = false
        var isPurchased = false
//        val LIFETIME = if (BuildConfig.DEBUG) "android.test.purchased" else "ads_free"
    }


}
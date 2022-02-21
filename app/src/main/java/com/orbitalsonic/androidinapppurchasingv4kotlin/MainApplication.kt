package com.orbitalsonic.androidinapppurchasingv4kotlin

import android.app.Application

class MainApplication:Application() {

    private lateinit var billingUtilsIAP: BillingUtilsIAP
    override fun onCreate() {
        super.onCreate()
        billingUtilsIAP= BillingUtilsIAP(this)
        billingUtilsIAP.getOldPurchases(this)

    }


}
package com.orbitalsonic.androidinapppurchasingv4kotlin

import android.content.Context
import android.content.SharedPreferences

object SharedPreferencesUtils {

    fun getBillingPurchasing(mContext: Context):Boolean{
        val sharedPreferences: SharedPreferences=mContext.getSharedPreferences("BillingPurchasingPrefs", Context.MODE_PRIVATE)
        return sharedPreferences.getBoolean("BillingPurchasingValue", true)
    }

    fun setBillingPurchasing(mContext: Context, isActive: Boolean){
        val sharedPreferences: SharedPreferences=mContext.getSharedPreferences("BillingPurchasingPrefs", Context.MODE_PRIVATE)
        val sharedPreferencesEditor: SharedPreferences.Editor  = sharedPreferences.edit()
        sharedPreferencesEditor.putBoolean("BillingPurchasingValue", isActive)
        sharedPreferencesEditor.apply()
    }

}
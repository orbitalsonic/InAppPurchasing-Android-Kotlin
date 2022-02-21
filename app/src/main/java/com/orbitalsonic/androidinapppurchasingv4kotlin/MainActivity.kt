package com.orbitalsonic.androidinapppurchasingv4kotlin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class MainActivity : AppCompatActivity() {

    private lateinit var billingUtilsIAP: BillingUtilsIAP

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        billingUtilsIAP = BillingUtilsIAP(this)

        findViewById<Button>(R.id.btn_purchase).setOnClickListener {
            // here product id is packageName
            val lifetime = if (BuildConfig.DEBUG)
                "android.test.purchased" else
                packageName
            billingUtilsIAP.purchase(this, lifetime)
        }
    }
}
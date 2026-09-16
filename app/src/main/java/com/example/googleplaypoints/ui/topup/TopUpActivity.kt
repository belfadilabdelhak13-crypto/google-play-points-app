package com.example.googleplaypoints.ui.topup

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.googleplaypoints.databinding.ActivityTopupBinding
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.PurchasesUpdatedListener
import kotlinx.coroutines.launch

class TopUpActivity : AppCompatActivity(), PurchasesUpdatedListener {
    private lateinit var binding: ActivityTopupBinding
    private lateinit var billingClient: BillingClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTopupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupBillingClient()
        setupUI()
    }

    private fun setupBillingClient() {
        billingClient = BillingClient.newBuilder(this)
            .setListener(this)
            .enablePendingPurchases()
            .build()

        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingServiceDisconnected() {}
            override fun onBillingSetupFinished(billingResult: com.android.billingclient.api.BillingResult) {}
        })
    }

    private fun setupUI() {
        binding.btnTopUp.setOnClickListener {
            initiateTopUp()
        }
    }

    private fun initiateTopUp() {
        val topUpAmount = binding.etTopUpAmount.text.toString().toDoubleOrNull() ?: return
        lifecycleScope.launch {
            // Initiate top-up process
        }
    }

    override fun onPurchasesUpdated(
        billingResult: com.android.billingclient.api.BillingResult,
        purchases: MutableList<com.android.billingclient.api.Purchase>?
    ) {
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
            for (purchase in purchases) {
                handlePurchase(purchase)
            }
        }
    }

    private fun handlePurchase(purchase: com.android.billingclient.api.Purchase) {
        // Handle purchase verification
    }
}
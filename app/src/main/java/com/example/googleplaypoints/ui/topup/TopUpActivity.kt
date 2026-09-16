package com.example.googleplaypoints.ui.topup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.googleplaypoints.databinding.ActivityTopupBinding
import com.example.googleplaypoints.databinding.ItemTopupBinding
import com.example.googleplaypoints.data.model.TopUp
import com.example.googleplaypoints.util.ToastHelper
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class TopUpActivity : AppCompatActivity(), PurchasesUpdatedListener {
    private lateinit var binding: ActivityTopupBinding
    private lateinit var billingClient: BillingClient
    private lateinit var viewModel: TopUpViewModel
    private lateinit var topUpAdapter: TopUpAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTopupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(TopUpViewModel::class.java)
        setupBillingClient()
        setupRecyclerView()
        observeViewModel()
        setupUI()
        viewModel.loadTopUpHistory()
    }

    private fun setupBillingClient() {
        billingClient = BillingClient.newBuilder(this)
            .setListener(this)
            .enablePendingPurchases()
            .build()

        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingServiceDisconnected() {
                ToastHelper.showShort(this@TopUpActivity, "Billing service disconnected")
            }

            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    ToastHelper.showShort(this@TopUpActivity, "Billing ready")
                }
            }
        })
    }

    private fun setupRecyclerView() {
        topUpAdapter = TopUpAdapter()
        binding.rvTopupHistory.apply {
            layoutManager = LinearLayoutManager(this@TopUpActivity)
            adapter = topUpAdapter
        }
    }

    private fun setupUI() {
        binding.btnTopUp.setOnClickListener {
            initiateTopUp()
        }
    }

    private fun initiateTopUp() {
        val amount = binding.etTopUpAmount.text.toString().toDoubleOrNull()
        val packageName = binding.spinnerApps.selectedItem?.toString() ?: "com.example.app"
        val appName = "Test App"

        if (amount == null || amount <= 0) {
            ToastHelper.showShort(this, "Please enter a valid amount")
            return
        }

        viewModel.initiateTopUp(packageName, appName, amount)
        binding.etTopUpAmount.text.clear()
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.topUpHistory.collect { topUps ->
                topUpAdapter.submitList(topUps)
            }
        }

        lifecycleScope.launch {
            viewModel.errorMessage.collect { error ->
                if (error != null) {
                    ToastHelper.showLong(this@TopUpActivity, "Error: $error")
                    viewModel.clearMessages()
                }
            }
        }

        lifecycleScope.launch {
            viewModel.successMessage.collect { success ->
                if (success != null) {
                    ToastHelper.showShort(this@TopUpActivity, success)
                    viewModel.clearMessages()
                }
            }
        }

        lifecycleScope.launch {
            viewModel.isLoading.collect { isLoading ->
                binding.progressBar.visibility = if (isLoading) android.view.View.VISIBLE else android.view.View.GONE
            }
        }
    }

    override fun onPurchasesUpdated(billingResult: BillingResult, purchases: MutableList<Purchase>?) {
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
            for (purchase in purchases) {
                handlePurchase(purchase)
            }
        } else {
            ToastHelper.showShort(this, "Purchase failed: ${billingResult.debugMessage}")
        }
    }

    private fun handlePurchase(purchase: Purchase) {
        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
            ToastHelper.showShort(this, "Purchase successful!")
            viewModel.clearTopUpInProgress()
            viewModel.loadTopUpHistory()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (billingClient.isReady) {
            billingClient.endConnection()
        }
    }
}

class TopUpAdapter : RecyclerView.Adapter<TopUpAdapter.TopUpViewHolder>() {
    private var topUps: List<TopUp> = emptyList()

    fun submitList(newTopUps: List<TopUp>) {
        topUps = newTopUps
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TopUpViewHolder {
        val binding = ItemTopupBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TopUpViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TopUpViewHolder, position: Int) {
        holder.bind(topUps[position])
    }

    override fun getItemCount() = topUps.size

    class TopUpViewHolder(private val binding: ItemTopupBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(topUp: TopUp) {
            binding.apply {
                tvAppName.text = topUp.appName
                tvTopUpAmount.text = "\$${String.format("%.2f", topUp.topUpAmount)}"
                tvTopUpStatus.text = topUp.status.toString()
                tvTransactionId.text = "ID: ${topUp.transactionId.take(8)}..."
                tvTopUpDate.text = java.text.SimpleDateFormat("MMM dd, yyyy", java.util.Locale.getDefault()).format(topUp.timestamp)

                tvTopUpStatus.setTextColor(
                    when (topUp.status.toString()) {
                        "COMPLETED" -> android.graphics.Color.GREEN
                        "FAILED" -> android.graphics.Color.RED
                        "PENDING" -> android.graphics.Color.YELLOW
                        else -> android.graphics.Color.GRAY
                    }
                )
            }
        }
    }
}
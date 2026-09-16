package com.example.googleplaypoints.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.googleplaypoints.databinding.ActivityMainBinding
import com.example.googleplaypoints.ui.auth.LoginActivity
import com.example.googleplaypoints.ui.points.PointsActivity
import com.example.googleplaypoints.ui.topup.TopUpActivity
import com.example.googleplaypoints.util.ToastHelper
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
    }

    private fun setupUI() {
        binding.apply {
            btnViewPoints.setOnClickListener {
                startActivity(Intent(this@MainActivity, PointsActivity::class.java))
            }

            btnTopUp.setOnClickListener {
                startActivity(Intent(this@MainActivity, TopUpActivity::class.java))
            }

            btnLogout.setOnClickListener {
                logout()
            }

            tvWelcome.text = "Welcome to Google Play Points Manager"
        }
    }

    private fun logout() {
        lifecycleScope.launch {
            ToastHelper.showShort(this@MainActivity, "Logging out...")
            startActivity(Intent(this@MainActivity, LoginActivity::class.java))
            finish()
        }
    }
}
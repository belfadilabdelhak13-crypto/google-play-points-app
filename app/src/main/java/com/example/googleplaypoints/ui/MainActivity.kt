package com.example.googleplaypoints.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.googleplaypoints.databinding.ActivityMainBinding
import com.example.googleplaypoints.ui.auth.LoginActivity
import com.example.googleplaypoints.ui.points.PointsActivity
import com.example.googleplaypoints.ui.topup.TopUpActivity
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
    }

    private fun setupUI() {
        binding.btnViewPoints.setOnClickListener {
            startActivity(Intent(this, PointsActivity::class.java))
        }

        binding.btnTopUp.setOnClickListener {
            startActivity(Intent(this, TopUpActivity::class.java))
        }

        binding.btnLogout.setOnClickListener {
            logout()
        }
    }

    private fun logout() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}
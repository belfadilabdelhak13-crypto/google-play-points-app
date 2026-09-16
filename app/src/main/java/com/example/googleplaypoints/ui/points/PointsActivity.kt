package com.example.googleplaypoints.ui.points

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.googleplaypoints.databinding.ActivityPointsBinding
import com.example.googleplaypoints.databinding.ItemPointBinding
import com.example.googleplaypoints.data.model.Points
import com.example.googleplaypoints.util.ToastHelper
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PointsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPointsBinding
    private lateinit var pointsAdapter: PointsAdapter
    private lateinit var viewModel: PointsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPointsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(PointsViewModel::class.java)
        setupRecyclerView()
        observeViewModel()
        loadPoints()
    }

    private fun setupRecyclerView() {
        pointsAdapter = PointsAdapter()
        binding.rvPoints.apply {
            layoutManager = LinearLayoutManager(this@PointsActivity)
            adapter = pointsAdapter
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.totalPoints.collect { total ->
                binding.tvTotalPoints.text = total.toString()
            }
        }

        lifecycleScope.launch {
            viewModel.recentPoints.collect { points ->
                pointsAdapter.submitList(points)
            }
        }

        lifecycleScope.launch {
            viewModel.errorMessage.collect { error ->
                if (error != null) {
                    ToastHelper.showLong(this@PointsActivity, error)
                    viewModel.clearError()
                }
            }
        }

        lifecycleScope.launch {
            viewModel.isLoading.collect { isLoading ->
                binding.progressBar.visibility = if (isLoading) android.view.View.VISIBLE else android.view.View.GONE
            }
        }
    }

    private fun loadPoints() {
        viewModel.loadPoints()
    }
}

class PointsAdapter : RecyclerView.Adapter<PointsAdapter.PointsViewHolder>() {
    private var points: List<Points> = emptyList()

    fun submitList(newPoints: List<Points>) {
        points = newPoints
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PointsViewHolder {
        val binding = ItemPointBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PointsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PointsViewHolder, position: Int) {
        holder.bind(points[position])
    }

    override fun getItemCount() = points.size

    class PointsViewHolder(private val binding: ItemPointBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(point: Points) {
            binding.apply {
                tvPointsAmount.text = "+${point.pointsAmount} pts"
                tvPointsType.text = point.pointsType.toString()
                tvPointsDescription.text = point.description.ifEmpty { "Points" }
                tvPointsDate.text = java.text.SimpleDateFormat("MMM dd, yyyy", java.util.Locale.getDefault()).format(point.timestamp)
            }
        }
    }
}
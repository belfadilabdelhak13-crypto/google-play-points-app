package com.example.googleplaypoints.ui.points

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.googleplaypoints.databinding.ActivityPointsBinding
import com.example.googleplaypoints.data.model.Points
import kotlinx.coroutines.launch

class PointsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPointsBinding
    private lateinit var pointsAdapter: PointsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPointsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        loadPoints()
    }

    private fun setupRecyclerView() {
        pointsAdapter = PointsAdapter()
        binding.rvPoints.apply {
            layoutManager = LinearLayoutManager(this@PointsActivity)
            adapter = pointsAdapter
        }
    }

    private fun loadPoints() {
        lifecycleScope.launch {
            // Load points from repository
            // pointsAdapter.submitList(points)
        }
    }
}

class PointsAdapter : androidx.recyclerview.widget.RecyclerView.Adapter<PointsAdapter.PointsViewHolder>() {
    private var points: List<Points> = emptyList()

    fun submitList(newPoints: List<Points>) {
        points = newPoints
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int): PointsViewHolder {
        val view = android.widget.TextView(parent.context)
        return PointsViewHolder(view as android.view.View)
    }

    override fun onBindViewHolder(holder: PointsViewHolder, position: Int) {
        // Bind points data
    }

    override fun getItemCount() = points.size

    class PointsViewHolder(itemView: android.view.View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView)
}
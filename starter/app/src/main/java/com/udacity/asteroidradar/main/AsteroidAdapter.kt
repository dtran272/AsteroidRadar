package com.udacity.asteroidradar.main

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.R
import com.udacity.asteroidradar.databinding.AsteroidViewItemBinding

class AsteroidAdapter(private val onClickListener: OnClickListener) :
    ListAdapter<Asteroid, AsteroidAdapter.AsteroidViewHolder>(AsteroidDiffCallback()) {

    override fun onBindViewHolder(holder: AsteroidViewHolder, position: Int) {
        val asteroid = getItem(position)

        // Set onClickListener
        holder.itemView.setOnClickListener {
            onClickListener.onClick(asteroid)
        }

        holder.bind(asteroid)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AsteroidViewHolder {
        return AsteroidViewHolder.from(parent)
    }

    class AsteroidViewHolder private constructor(private val binding: AsteroidViewItemBinding, private val context: Context) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(asteroid: Asteroid) {
            binding.viewModel = asteroid
            binding.isHazardousIcon.contentDescription = getHazardousContentDescription(asteroid.isPotentiallyHazardous)

            // This is important, because it forces the data binding to execute immediately,
            // which allows the RecyclerView to make the correct view size measurements
            binding.executePendingBindings()
        }

        private fun getHazardousContentDescription(isPotentiallyHazardous: Boolean): String {
            val hazardousContentDescription = if (isPotentiallyHazardous)
                context.getString(R.string.hazardous_asteroid)
            else
                context.getString(R.string.not_hazardous_asteroid)

            return context.getString(R.string.hazardous_icon_content_description) + " $hazardousContentDescription"
        }

        companion object {
            fun from(parent: ViewGroup): AsteroidViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = AsteroidViewItemBinding.inflate(layoutInflater, parent, false)

                return AsteroidViewHolder(binding, parent.context)
            }
        }
    }

    class AsteroidDiffCallback : DiffUtil.ItemCallback<Asteroid>() {
        override fun areItemsTheSame(oldItem: Asteroid, newItem: Asteroid): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Asteroid, newItem: Asteroid): Boolean {
            return oldItem.id == newItem.id
        }

    }

    class OnClickListener(val clickListener: (asteroid: Asteroid) -> Unit) {
        fun onClick(asteroid: Asteroid) = clickListener(asteroid)
    }
}
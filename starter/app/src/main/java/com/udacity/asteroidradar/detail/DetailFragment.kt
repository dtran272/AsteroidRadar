package com.udacity.asteroidradar.detail


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.R
import com.udacity.asteroidradar.databinding.FragmentDetailBinding

class DetailFragment : Fragment() {
    private lateinit var binding: FragmentDetailBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDetailBinding.inflate(inflater)
        binding.lifecycleOwner = this

        val asteroid = DetailFragmentArgs.fromBundle(requireArguments()).selectedAsteroid

        binding.asteroid = asteroid

        binding.helpButton.setOnClickListener {
            displayAstronomicalUnitExplanationDialog()
        }

        setupContentDescription(asteroid)

        return binding.root
    }

    private fun displayAstronomicalUnitExplanationDialog() {
        val builder = AlertDialog.Builder(requireActivity())
            .setMessage(getString(R.string.astronomical_unit_explanation))
            .setPositiveButton(android.R.string.ok, null)
        builder.create().show()
    }

    private fun setupContentDescription(asteroid: Asteroid) {
        // Setup details image
        val imageContentDescription = if (asteroid.isPotentiallyHazardous)
            getString(R.string.potentially_hazardous_asteroid_image)
        else
            getString(R.string.not_hazardous_asteroid_image)

        binding.activityMainImageOfTheDay.contentDescription = imageContentDescription
    }
}

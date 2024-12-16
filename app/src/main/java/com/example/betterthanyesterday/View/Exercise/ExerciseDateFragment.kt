package com.example.betterthanyesterday.View.Exercise

import android.Manifest
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.clearFragmentResultListener
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import com.example.betterthanyesterday.ExerciseViewModel
import com.example.betterthanyesterday.R
import com.example.betterthanyesterday.View.Adapter.WeatherDataAdapter
import com.example.betterthanyesterday.data.CurrentLocation
import com.example.betterthanyesterday.databinding.FragmentExerciseDateBinding
import com.example.betterthanyesterday.storage.SharedPreferencesManager
import com.google.android.gms.location.LocationServices
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel


class ExerciseDateFragment : Fragment() {

    companion object {
        const val REQUEST_KEY_MANUAL_LOCATION_SEARCH = "REQUEST_KEY_MANUAL_LOCATION_SEARCH"
        const val KEY_LOCATION_TEXT = "location_text"
        const val KEY_LOCATION_LATITUDE = "latitude"
        const val KEY_LOCATION_LONGITUDE = "longitude"
    }

    private val exerciseViewModel: ExerciseViewModel by viewModel()
    private val fusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(requireContext())
    }

    private val geocoder by lazy { Geocoder(requireContext()) }
    private var binding: FragmentExerciseDateBinding? = null

    private var selectedDate: String? = null
    private val weatherDataAdapter = WeatherDataAdapter(
        onLocationClicked = { showLocationOptions() }
    )

    private var isInitialLocationSet: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentExerciseDateBinding.inflate(inflater)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setWeatherDataAdapter()
        setCurrentLocation(currentLocation = sharedPreferencesManager.getCurrentLocation())
        setObservers()
        if (!isInitialLocationSet) {
            setCurrentLocation(currentLocation = sharedPreferencesManager.getCurrentLocation())
            isInitialLocationSet = true
        }

        binding?.calendarView?.setOnDateChangeListener { _, year, month, dayOfMonth ->
            selectedDate = "$year-${(month + 1).toString().padStart(2, '0')}-${
                dayOfMonth.toString().padStart(2, '0')
            }"
            binding?.selectedDateTxt?.text = "선택된 날짜: $selectedDate"
            binding?.goToDetailsBtn?.isEnabled = true
        }

        binding?.goToDetailsBtn?.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("selectedDate", selectedDate)
            findNavController().navigate(
                R.id.action_exerciseDateFragment_to_exerciseDetailsFragment,
                bundle
            )
        }
    }

    private fun setObservers() {
        with(exerciseViewModel) {
            currentLocation.observe(viewLifecycleOwner) {
                val currentLocationDataState = it.getContentIfNotHandled() ?: return@observe
                if (currentLocationDataState.isLoading) {
                    showLoading()
                }
                currentLocationDataState.currentLocation?.let { currentLocation ->
                    hideLoading()
                    sharedPreferencesManager.saveCurrentLocation(currentLocation)
                    setCurrentLocation(currentLocation)
                }
                currentLocationDataState.error?.let { error ->
                    hideLoading()
                    Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
                }
            }

            weatherData.observe(viewLifecycleOwner) {
                val weatherDataState = it.getContentIfNotHandled() ?: return@observe
                binding?.swipeRefreshLayout?.isRefreshing = weatherDataState.isLoading
                weatherDataState.currentWeather?.let{ currentWeather ->
                    weatherDataAdapter.setCurrentWeather(currentWeather)
                }
                weatherDataState.error?.let { error ->
                    Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    private fun setWeatherDataAdapter() {
        binding?.weatherDataRecyclerView?.adapter = weatherDataAdapter
    }

    private fun setCurrentLocation(currentLocation: CurrentLocation? = null) {
        weatherDataAdapter.setCurrentLocation(currentLocation ?: CurrentLocation())
        currentLocation?.let { getWeatherData(currentLocation= it) }
    }

    private fun updateWeatherData(newLocation: String) {
        val updatedData = CurrentLocation(location = newLocation)
        weatherDataAdapter.setCurrentLocation(updatedData)
    }

    private fun getCurrentLocation() {
        exerciseViewModel.getCurrentLocation(fusedLocationProviderClient, geocoder)
    }

    private val locationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            getCurrentLocation()
        } else {
            Toast.makeText(requireContext(), "Location permission denied", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun isLocationPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(), Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestLocationPermission() {
        locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    private fun proceedWithCurrentLocation() {
        if (isLocationPermissionGranted()) {
            getCurrentLocation()
        } else {
            requestLocationPermission()
        }
    }

    private fun showLocationOptions() {
        val options = arrayOf("Current Location", "Search Location")
        AlertDialog.Builder(requireContext()).apply {
            setTitle("Select Location")
            setItems(options) { _, which ->
                when (which) {
                    0 -> proceedWithCurrentLocation()
                    1 -> startManualLocationSearch()
                }
            }
            show()
        }
    }

    private fun showLoading() {
        binding?.let {
            it.weatherDataRecyclerView.visibility = View.GONE
            it.swipeRefreshLayout?.isRefreshing = true
        }
    }

    private fun hideLoading() {
        binding?.let {
            it.weatherDataRecyclerView.visibility = View.VISIBLE
            it.swipeRefreshLayout?.isRefreshing = false
        }
    }

    private val sharedPreferencesManager: SharedPreferencesManager by inject()

    private fun startManualLocationSearch() {
        startListeningManualLocationSelection()
        findNavController().navigate(R.id.action_exerciseDateFragment_to_locationFragment)
    }

    private fun startListeningManualLocationSelection() {
        setFragmentResultListener(REQUEST_KEY_MANUAL_LOCATION_SEARCH) { _, bundle ->
            stopListeningManualLocationSelection()
            val currentLocation = CurrentLocation(
                location = bundle.getString(KEY_LOCATION_TEXT) ?: "N/A",
                latitude = bundle.getDouble(KEY_LOCATION_LATITUDE),
                longitude = bundle.getDouble(KEY_LOCATION_LONGITUDE)
            )
            sharedPreferencesManager.saveCurrentLocation(currentLocation)
            setCurrentLocation(currentLocation)
        }
    }

    private fun stopListeningManualLocationSelection() {
        clearFragmentResultListener(REQUEST_KEY_MANUAL_LOCATION_SEARCH)
    }

    private fun getWeatherData(currentLocation: CurrentLocation) {
        if (currentLocation.latitude != null && currentLocation.longitude != null) {
            exerciseViewModel.getWeatherData(
                latitude = currentLocation.latitude,
                longitude = currentLocation.longitude
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}

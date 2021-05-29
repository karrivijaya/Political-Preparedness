package com.example.android.politicalpreparedness.representative

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.databinding.FragmentRepresentativeBinding
import com.example.android.politicalpreparedness.network.models.Address
import com.example.android.politicalpreparedness.representative.adapter.RepresentativeListAdapter
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.util.Locale

class DetailFragment : Fragment() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    companion object {
        //TODO: Add Constant for Location request
        private const val LOCATION_REQUEST_CODE = 20

        private const val TAG = "Representative Fragment"
    }

    // Declare ViewModel
    val viewModel: RepresentativeViewModel by lazy {
        val activity = requireNotNull(this.activity){
            "You can only access the viewModel after onViewCreated()"
        }
        ViewModelProvider(this, RepresentativeViewModelFactory(activity.application)).get(RepresentativeViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View{

        //Establish bindings
        val binding = FragmentRepresentativeBinding.inflate(inflater)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        //Define and assign Representative adapter
        val representativeAdapter = RepresentativeListAdapter()
        //Populate Representative adapter
        binding.representativeList.adapter = representativeAdapter

        // setting spinner adapter
        ArrayAdapter.createFromResource(requireActivity(), R.array.states, android.R.layout.simple_spinner_item)
                .also{
                    adapter ->
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    binding.state.adapter = adapter
                }

        //TODO: Establish button listeners for field and location search
        binding.buttonSearch.setOnClickListener {
            hideKeyboard()
            viewModel.getRepresentativesByAddress()
        }

        binding.buttonLocation.setOnClickListener {
            checkLocationPermissions()
        }

        viewModel.representatives.observe(viewLifecycleOwner, Observer { representatives ->
            representatives?.let{
                representativeAdapter.submitList(representatives)
            }
        })

        binding.state.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{

            override fun onItemSelected(adapterView: AdapterView<*>?, view: View?, position: Int, Id: Long) {

                viewModel.state.value = adapterView?.getItemAtPosition(position).toString()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        }

        //using Fused location services to get the last known location which in many cases is the current location
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        return binding.root

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        //TODO: Handle location permission result to get location on permission granted

        Log.d(TAG, "onRequestPermissionResult")

        if (requestCode == LOCATION_REQUEST_CODE) {
            Log.d(TAG, "inside onRequestPermissionResult")
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "inside grantResults")
                Toast.makeText(requireContext(), "Permission granted", Toast.LENGTH_SHORT).show()
                getLocation()
            } else {
                Toast.makeText(requireContext(), "Permission not granted", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun checkLocationPermissions() {
        if (isPermissionGranted()) {
            getLocation()
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(),
                            Manifest.permission.ACCESS_FINE_LOCATION)) {
                AlertDialog.Builder(requireActivity())
                        .setTitle(getString(R.string.permission_needed))
                        .setMessage(getString(R.string.location_permission_message))
                        .setPositiveButton(getString(R.string.permission_OK), DialogInterface.OnClickListener { dialogInterface, i ->
                            this.requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_REQUEST_CODE)
                        })
                        .setNegativeButton(getString(R.string.permission_cancel), DialogInterface.OnClickListener { dialogInterface, i ->
                            dialogInterface.dismiss()
                        })
                        .create().show()
            } else {
                this.requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_REQUEST_CODE)
            }
        }
    }

    private fun isPermissionGranted() : Boolean {
        //TODO: Check if permission is already granted and return (true = granted, false = denied/other)
        //TODO: temporarily returning true...need to change
        return PackageManager.PERMISSION_GRANTED ==
                ActivityCompat.checkSelfPermission(requireContext(),
                        Manifest.permission.ACCESS_FINE_LOCATION)
    }

    @SuppressLint("MissingPermission")
    private fun getLocation() {
        Log.d(TAG, "inside getLocation()")

        //TODO: Get location from LocationServices
        //TODO: The geoCodeLocation method is a helper function to change the lat/long location to a human readable street address
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->

            if (location != null) {
                viewModel.showErrorRetrievingAddress("")
                val address = geoCodeLocation(location)
                if(address != null){
                    viewModel.getAddressFromGeoLocation(address)
                    viewModel.getRepresentativesByAddress()
                }
            }
            else{
                viewModel.showErrorRetrievingAddress(getString(R.string.location_null))
            }
        }

        fusedLocationClient.lastLocation.addOnFailureListener{ exception ->

            exception.message?.let { viewModel.showErrorRetrievingAddress(it) }
        }
    }

     private fun geoCodeLocation(location: Location): Address? {
        val geocoder = Geocoder(context, Locale.getDefault())
        try {
            return geocoder.getFromLocation(location.latitude, location.longitude, 1)
                    .map { address ->
                        Log.d(TAG, address.toString())
                        Address(address.thoroughfare, address.subThoroughfare, address.locality, address.adminArea, address.postalCode)
                    }
                    .first()
        }
        catch (e: Exception){
            e.message?.let{
                Log.d(TAG, it)
                viewModel.showErrorRetrievingAddress(getString(R.string.error_geocoder_address))
            }
            return null
        }
    }

    private fun hideKeyboard() {
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view!!.windowToken, 0)
    }

}
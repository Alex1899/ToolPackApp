package com.example.toolpackapp.activities.driver.bottomNav.maps

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import com.example.toolpackapp.R
import com.example.toolpackapp.databinding.FragmentMapsBinding
import com.example.toolpackapp.utils.drawableToBitmap
import com.example.toolpackapp.utils.getAddress
import com.example.toolpackapp.utils.getLocationFromPostcode
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.gms.common.GooglePlayServicesRepairableException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.places.ui.PlacePicker
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.io.IOException
import java.util.*


class MapsFragment : Fragment(), GoogleMap.OnMarkerClickListener {
    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 23
        private const val PLACE_PICKER_REQUEST = 24

    }

    private var binding: FragmentMapsBinding? = null
    private var mGoogleMap: GoogleMap? = null
    private var start: LatLng? = null
    private var end: LatLng? = null
    private lateinit var myLocation: Location
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    //to get location permissions.
    private var locationPermission = false

    //polyline object
    private var polylines: List<Polyline>? = null

    private var pickupAddress: String? = null
    private var deliveryAddress: String? = null



    private val callback = OnMapReadyCallback { googleMap ->
        mGoogleMap = googleMap
        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */


        googleMap.uiSettings.isZoomControlsEnabled = true
        googleMap.setOnMarkerClickListener(this)
        setUpMap()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val fragmentBinding = FragmentMapsBinding.inflate(inflater, container, false)
        binding = fragmentBinding

        if(arguments !== null){
            pickupAddress = requireArguments().getString("pickupAddress")
            deliveryAddress = requireArguments().getString("deliveryAddress")
            Log.d("Maps", "pickup -> $pickupAddress delivery -> $deliveryAddress")
        }

        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment =
            childFragmentManager.findFragmentById(com.example.toolpackapp.R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
    }

    override fun onMarkerClick(p0: Marker?) = false



    private fun setUpMap() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
            return
        }
        mGoogleMap?.isMyLocationEnabled = true
        fusedLocationClient.lastLocation.addOnSuccessListener(requireActivity()) { location ->
            // Got last known location. In some rare situations this can be null.
            if (location != null) {
                myLocation = location
                val currentLatLng = LatLng(location.latitude, location.longitude)
                placeMarkerOnMap(currentLatLng)
                mGoogleMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 12f))
            }
        }

        if(pickupAddress !== null && deliveryAddress !== null){
            val pickupLocation = getLocationFromPostcode(pickupAddress!!, requireActivity())!!
            val addressText = getAddress(pickupLocation, requireContext())
            Log.d("Maps", "pickupLocation geocord -> $pickupLocation")
            val deliveryLocation = getLocationFromPostcode(deliveryAddress!!, requireActivity())

            val options = MarkerOptions().position(pickupLocation!!).title(addressText)
            val drawable = ResourcesCompat.getDrawable(resources, R.drawable.ic_box_map, null)!!
            options.icon(BitmapDescriptorFactory.fromBitmap(drawableToBitmap(drawable)!!))

            mGoogleMap?.addMarker(options)
            mGoogleMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(pickupLocation!!, 12f))
        }
    }


    private fun placeMarkerOnMap(location: LatLng) {
        val markerOptions = MarkerOptions().position(location)
        val title = getAddress(location, requireContext())
        markerOptions.title(title)
//        val bitmap = BitmapFactory.decodeResource(context?.resources, R.drawable.ic_baseline_person_24)
        val drawable = ResourcesCompat.getDrawable(resources, R.drawable.ic_baseline_person_24, null)!!
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(drawableToBitmap(drawable)!!))
        mGoogleMap?.addMarker(markerOptions)
    }



}

package com.example.toolpackapp.activities.driver.bottomNav.maps

import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import com.directions.route.*
import com.example.toolpackapp.R
import com.example.toolpackapp.databinding.FragmentMapsBinding
import com.example.toolpackapp.utils.drawableToBitmap
import com.example.toolpackapp.utils.getAddress
import com.example.toolpackapp.utils.getLocationFromPostcode
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.snackbar.Snackbar
import java.util.*


class MapsFragment : Fragment(), RoutingListener, GoogleMap.OnMarkerClickListener {
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
            val pickupAddressText = getAddress(pickupLocation, requireContext())

            Log.d("Maps", "pickupLocation geocord -> $pickupLocation")
            val deliveryLocation = getLocationFromPostcode(deliveryAddress!!, requireActivity())!!
            val deliveryAddressText = getAddress(deliveryLocation, requireContext())

            val pickupOptions = MarkerOptions().position(pickupLocation).title(pickupAddressText)
            val pickupDrawable = ResourcesCompat.getDrawable(resources, R.drawable.ic_box_map, null)!!
            pickupOptions.icon(BitmapDescriptorFactory.fromBitmap(drawableToBitmap(pickupDrawable)!!))

            val deliveryOptions = MarkerOptions().position(deliveryLocation).title(deliveryAddressText)
            val deliveryDrawable = ResourcesCompat.getDrawable(resources, R.drawable.ic_maps_building_site, null)!!

            pickupOptions.icon(BitmapDescriptorFactory.fromBitmap(drawableToBitmap(pickupDrawable)!!))
            mGoogleMap?.addMarker(pickupOptions)
            deliveryOptions.icon(BitmapDescriptorFactory.fromBitmap(drawableToBitmap(deliveryDrawable)!!))

            mGoogleMap?.addMarker(deliveryOptions)
            mGoogleMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(pickupLocation, 12f))
        }
    }


    private fun placeMarkerOnMap(location: LatLng) {
        val markerOptions = MarkerOptions().position(location)
        val title = getAddress(location, requireContext())
        markerOptions.title(title)
//      val bitmap = BitmapFactory.decodeResource(context?.resources, R.drawable.ic_baseline_person_24)
        val drawable = ResourcesCompat.getDrawable(
            resources,
            R.drawable.ic_baseline_person_24,
            null
        )!!
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(drawableToBitmap(drawable)!!))
        mGoogleMap?.addMarker(markerOptions)
    }

    //Routing call back functions.
    override fun onRoutingFailure(e: RouteException) {
        val parentLayout: View = view?.findViewById(android.R.id.content)!!
        val snackbar = Snackbar.make(parentLayout, e.toString(), Snackbar.LENGTH_LONG)
        snackbar.show()
//    Findroutes(start,end);
    }

    override fun onRoutingStart() {
        Toast.makeText(requireContext(), "Finding Route...", Toast.LENGTH_LONG).show()
    }

    override fun onRoutingSuccess(p0: ArrayList<Route>?, p1: Int) {
        TODO("Not yet implemented")
    }

    override fun onRoutingCancelled() {
        TODO("Not yet implemented")
    }


}

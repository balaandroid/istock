package com.fertail.istock.googleMap

import android.Manifest
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import com.fertail.istock.R
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.OnMapsSdkInitializedCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import io.reactivex.annotations.Nullable
import org.w3c.dom.Document
import java.io.IOException

class GoogleMapActivity : AppCompatActivity() , OnMapsSdkInitializedCallback {
    private lateinit var imageView: ImageView
    private var currentLocation: Location? = null
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private var latLngList = mutableListOf<LatLng>()
    private lateinit var searchView: SearchView
    private lateinit var supportMapFragment: SupportMapFragment
    private var addressList: List<Address>? = null
    private var previousMarker: Marker? = null
    private var mGoogleMap: GoogleMap? = null
    private var circleRadius: Double = 10000.0 // 50 km

    private var googleDirection: GoogleDirectionNew? = null
    var mDoc: Document? = null
    private var polyline: Polyline? = null
    private var deslat:Double = 0.0
    private var deslong:Double = 0.0
    private lateinit var  latLng:LatLng

    private var currentLatitude: Double = 0.0
    private var currentLongitude: Double = 0.0

    companion object {
        const val REQUEST_CODE = 101
        private const val REQUEST_CODE_1 = 100
        private const val LOCATION_UPDATE_FASTEST_INTERVAL = 0L
        private const val LOCATION_UPDATE_INTERVAL = 0L
    }

    override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_google_map)
        val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        imageView = findViewById(R.id.imag_map)
        searchView = findViewById(R.id.idSearchView)
        latLngList = mutableListOf()

         deslat = intent.getDoubleExtra("desLat", 0.0)
        deslong= intent.getDoubleExtra("desLong", 0.0)

        getLocation()
        supportMapFragment =
            supportFragmentManager.findFragmentById(R.id.fragment_map) as SupportMapFragment
        MapsInitializer.initialize(applicationContext, MapsInitializer.Renderer.LEGACY, this)

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finish()
            }
        })

        imageView.setOnClickListener {
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                Toast.makeText(
                    applicationContext,
                    "GPS is Enabled in your device",
                    Toast.LENGTH_SHORT
                ).show()
                getLocation()
            } else {
                Toast.makeText(
                    applicationContext,
                    "GPS is Disabled in your device",
                    Toast.LENGTH_LONG
                ).show()
                showGPSDisabledAlertToUser()
            }
        }
    }

    private fun searchLocation(googleMap: GoogleMap) {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                val location = searchView.query.toString()
                previousMarker?.remove()
                val geocoder = Geocoder(this@GoogleMapActivity)
                try {
                    addressList = geocoder.getFromLocationName(location, 1)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
                if (!addressList.isNullOrEmpty()) {
                    val address = addressList!![0]
                    val latLng = LatLng(address.latitude, address.longitude)
                    previousMarker = googleMap.addMarker(
                        MarkerOptions().position(latLng)
                            .title(location)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                    )
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10f))
                    addressList = listOf()
                } else {
                    Toast.makeText(
                        this@GoogleMapActivity,
                        "Please Enter Correct Location",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText.isNullOrEmpty() && previousMarker != null) {
                    previousMarker?.remove()
                }
                return false
            }
        })
    }

    private fun getLocation() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this, arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ), REQUEST_CODE
            )
        } else {
            val task: Task<Location> = fusedLocationProviderClient.lastLocation
            task.addOnSuccessListener(OnSuccessListener { location ->
                Log.d("SAMPLECLASS", "BEFORE: $location")
                if (location != null) {
                    Log.d("SAMPLECLASS", "onSuccess: $location")
                    currentLocation = location
                    currentLatitude=currentLocation!!.latitude
                    currentLongitude=currentLocation!!.longitude
                    requestDirection(mGoogleMap!!)
                    latLng = LatLng(currentLocation!!.latitude, currentLocation!!.longitude)
                    mGoogleMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
                    mGoogleMap?.addMarker(MarkerOptions().position(latLng))
                    Toast.makeText(applicationContext, "${currentLocation!!.latitude} ${currentLocation!!.longitude}", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                getLocation()
            } else {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun showGPSDisabledAlertToUser() {
        val locationRequest = LocationRequest.create()
            .setInterval(LOCATION_UPDATE_INTERVAL)
            .setFastestInterval(LOCATION_UPDATE_FASTEST_INTERVAL)
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)

        val builder = LocationSettingsRequest.Builder()
            .addAllLocationRequests(listOf(locationRequest))

        LocationServices.getSettingsClient(this)
            .checkLocationSettings(builder.build())
            .addOnSuccessListener {
                getLocation()
            }
            .addOnFailureListener { ex ->
                if (ex is ResolvableApiException) {
                    try {
                        ex.startResolutionForResult(this@GoogleMapActivity, REQUEST_CODE_1)
                    } catch (sendEx: IntentSender.SendIntentException) {
                        sendEx.printStackTrace()
                    }
                }
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, @Nullable data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (REQUEST_CODE_1 == requestCode) {
            if (resultCode == RESULT_OK) {
                try {
                    getLocation()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }


    override fun onMapsSdkInitialized(renderer: MapsInitializer.Renderer) {
        when (renderer) {
            MapsInitializer.Renderer.LATEST -> supportMapFragment.getMapAsync { googleMap ->
                mGoogleMap = googleMap
                searchLocation(mGoogleMap!!)
            }

            MapsInitializer.Renderer.LEGACY -> supportMapFragment.getMapAsync { googleMap ->
                requestDirection(googleMap)
                searchLocation(googleMap)
            }
        }
    }

    private fun requestDirection(googleMap: GoogleMap) {
        latLngList.add(LatLng(currentLatitude, currentLongitude))

//        latLngList.add(LatLng(deslat, deslong))

        latLngList.add(LatLng(13.0337902, 80.2771553))
//        latLngList.add(LatLng(12.9078987, 80.0728225))
        googleDirection = GoogleDirectionNew(this)
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLngList[0], 15f))
        googleDirection!!.setOnDirectionResponseListener { status, doc, gd ->
            mDoc = doc
            addMultipleMarkers(googleMap, latLngList)
            if (status.equals("Ok", ignoreCase = true)) {
                val polylineOptions: PolylineOptions = gd.getPolyline(doc, 5, Color.BLUE)
                if (polylineOptions != null) {
                    polyline = googleMap.addPolyline(polylineOptions)
                    googleMap.isTrafficEnabled = true
                }
            } else {
                println("Request Denied: $status")
            }
        }
        googleDirection!!.setLogging(true)
        googleDirection!!.request(latLngList, GoogleDirectionNew.MODE_DRIVING)

    }

    private fun addMultipleMarkers(googleMap: GoogleMap, coordinates: List<LatLng>) {
        for (coordinate in coordinates) {
            val markerOptions = MarkerOptions()
                .position(coordinate)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
            googleMap.addMarker(markerOptions)
        }
    }
}
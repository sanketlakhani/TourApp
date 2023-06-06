package com.example.masterprojectapp


import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.icu.text.RelativeDateTimeFormatter
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.akexorcist.googledirection.DirectionCallback
import com.akexorcist.googledirection.GoogleDirection
import com.akexorcist.googledirection.constant.AvoidType
import com.akexorcist.googledirection.constant.TransportMode
import com.akexorcist.googledirection.model.Direction
import com.akexorcist.googledirection.model.Route
import com.akexorcist.googledirection.util.DirectionConverter
import com.example.masterprojectapp.databinding.ActivityMapLoadingBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class ActivityMapLoading : AppCompatActivity(), OnMapReadyCallback {
    lateinit var mMap: GoogleMap
    var curr_latLng: LatLng? = null
    lateinit var referenceDatabase: DatabaseReference
    var polyline: Polyline? = null
    private val LOCATION_MIN_UPDATE_TIME = 10
    private val LOCATION_MIN_UPDATE_DISTANCE = 1000
    var locationManager: LocationManager? = null
    var location: Location? = null
    lateinit var binding: ActivityMapLoadingBinding

    val goa = LatLng(15.399381099434574, 73.81129097709409)
    val kashmir = LatLng(34.18142844309194, 74.72710191894423)
    val kedarnath = LatLng(30.736525859794252, 79.06672975930265)
    val mumbai = LatLng(18.922298666734623, 72.83463281935062)
    val masoori = LatLng(30.440811276895445, 78.06940182950639)
    val ladakh = LatLng(34.14283692902197, 77.55571561284819)
    val kerala = LatLng(9.599315586528762, 76.52819763304025)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        referenceDatabase = FirebaseDatabase.getInstance().reference

        binding = ActivityMapLoadingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        initView()

    }

    private fun initView() {
//        mapBinding.imgBackMap.setOnClickListener {
//            onBackPressed()
    }


    private fun drawMarker(latLng: LatLng, flag: Boolean) {

        if (mMap != null) {
            val markerOptions = MarkerOptions()
//            mMap!!.clear()
            markerOptions.position(latLng)
//                        markerOptions.title(title);
            if (flag) {
//                markerOptions.icon(BitmapFromVector(this, R.drawable.ic_map_event));
                markerOptions.icon(BitmapFromVector(this, 1))
            } else {
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
            }
            mMap.addMarker(markerOptions)
            //            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
//            mMap.animateCamera(CameraUpdateFactory.zoomTo(12));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12f))
        }
    }

    private fun BitmapFromVector(context: Context, vectorResId: Int): BitmapDescriptor {
        val vectorDrawable = ContextCompat.getDrawable(context, vectorResId)
        vectorDrawable!!.setBounds(
            0,
            0,
            vectorDrawable.intrinsicWidth,
            vectorDrawable.intrinsicHeight
        )
        val bitmap = Bitmap.createBitmap(
            vectorDrawable.intrinsicWidth,
            vectorDrawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        vectorDrawable.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

    private val locationListener: LocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
//            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
//            drawMarker(latLng);
//            locationManager.removeUpdates(locationListener);
        }

        override fun onStatusChanged(s: String, i: Int, bundle: Bundle) {}
        override fun onProviderEnabled(s: String) {}
        override fun onProviderDisabled(s: String) {}
    }

    fun getCurrentLocation() {
        val mapLocation = intent.getStringExtra("DESTINATION")
        try {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {

                val isGPSEnabled =
                    locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)
                val isNetworkEnabled =
                    locationManager!!.isProviderEnabled(LocationManager.NETWORK_PROVIDER)


                if (!isGPSEnabled && !isNetworkEnabled) {
                    //                    showToast(getString(R.string.please_on_your_GPS_location));
                    Toast.makeText(this, "please_on_your_GPS_location", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    location = null
                    if (isGPSEnabled) {
                        locationManager!!.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER,
                            LOCATION_MIN_UPDATE_TIME.toLong(),
                            LOCATION_MIN_UPDATE_DISTANCE.toFloat(),
                            locationListener
                        )
                        location =
                            locationManager!!.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                    }
                    if (isNetworkEnabled) {
                        locationManager!!.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            LOCATION_MIN_UPDATE_TIME.toLong(),
                            LOCATION_MIN_UPDATE_DISTANCE.toFloat(),
                            locationListener
                        )
                        location =
                            locationManager!!.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                    }
                    if (location != null) {

                        curr_latLng = LatLng(location!!.latitude, location!!.longitude)
                        drawMarker(curr_latLng!!, false)

                        if (mapLocation.equals("Goa")) {

                            drawline(goa.latitude, goa.longitude, TransportMode.WALKING)
                        } else if (mapLocation.equals("Kedarnath ")) {
                            drawline(kedarnath.latitude, kedarnath.longitude, TransportMode.WALKING)
                        } else if (mapLocation.equals("Ladakh ")) {
                            drawline(ladakh.latitude, ladakh.longitude, TransportMode.WALKING)
                        } else if (mapLocation.equals("Mumbai ")) {
                            drawline(mumbai.latitude, mumbai.longitude, TransportMode.WALKING)
                        } else if (mapLocation.equals("Masoori")) {
                            drawline(masoori.latitude, masoori.longitude, TransportMode.WALKING)
                        } else if (mapLocation.equals("Kashmir ")) {
                            drawline(kashmir.latitude, kashmir.longitude, TransportMode.WALKING)
                        } else if (mapLocation.equals("Kerala")) {
                            drawline(kerala.latitude, kerala.longitude, TransportMode.WALKING)
                        } else {
                            Toast.makeText(this, "Can't Locate", Toast.LENGTH_SHORT).show()
                        }
                        return
                    } else {
                        getCurrentLocation()
                    }
                }
            } else {
                if (ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                        12
                    )
                }
                if (ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
                        13
                    )
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, " " + e.message, Toast.LENGTH_SHORT).show()
//            onBackPressed()
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == 12) {
            getCurrentLocation()
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    companion object {
        private const val LOCATION_MIN_UPDATE_TIME = 10
        private const val LOCATION_MIN_UPDATE_DISTANCE = 1000
        var isDisplayDirection = false
    }

    private fun drawline(lat: Double, longt: Double, mode: String) {
        try {
            drawMarker(LatLng(lat, longt), false)
            GoogleDirection.withServerKey("AIzaSyBv6cUUv3hbIEDcG69F297b37KqrTjepSg")
                .from(LatLng(curr_latLng!!.latitude, curr_latLng!!.longitude))
                .to(LatLng(lat, longt))
                .avoid(AvoidType.FERRIES)
                .avoid(AvoidType.HIGHWAYS)
                .avoid(AvoidType.TOLLS)
                .transportMode(mode)
                .execute(object : DirectionCallback {


                    override fun onDirectionSuccess(direction: Direction?) {
                        directionsuccess(direction)

                    }

                    override fun onDirectionFailure(t: Throwable) {
                        Toast.makeText(
                            this@ActivityMapLoading,
                            "Direction Failed",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }
                })
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("TAG", "drawline:exce " + e.message)
        }
    }

    private fun directionsuccess(direction: Direction?) {
        try {
            if (direction!!.isOK) {
                val route = direction.routeList[0]
                if (route != null && !route.legList.isEmpty()) {
                    val distance = route.legList[0].distance
                    val duration = route.legList[0].duration
                    val directionPositionList = route.legList[0].directionPoint
                    if (!directionPositionList.isEmpty()) {
                        if (polyline != null) {
                            polyline!!.remove()
                        }
                        polyline = mMap.addPolyline(
                            DirectionConverter.createPolyline(
                                this,
                                directionPositionList,
                                5,
                                ContextCompat.getColor(this, R.color.purple_700)
                            )
                        )
                        setCameraWithCoordinationBounds(route)
                    } else {
                        Toast.makeText(this, "noroute_available", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "noroute_available", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "noroute_available", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setCameraWithCoordinationBounds(route: Route) {
        val southwest = route.bound.southwestCoordination.coordination
        val northeast = route.bound.northeastCoordination.coordination
        val bounds = LatLngBounds(southwest, northeast)
        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100))
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        getCurrentLocation()
        val lMap = intent.getStringExtra("DESTINATION")
    }


}
package com.example.mukgoapplication.map

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.mukgoapplication.R
import com.google.android.gms.location.*
import com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap

    /**현재 정보 업데이트 변수*/
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    /**권한요청 변수*/
    private val permissionRequest = 99
    private var permissions = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        if (!isPermitted()) {
            ActivityCompat.requestPermissions(this, permissions,permissionRequest)
        }


        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    /**권한요청*/
    private fun isPermitted(): Boolean {
        for (permission in permissions) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    permission
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return false
            }
        }
        return true
    }


    /**map 세팅*/
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.setMinZoomPreference(3.0F) // 최소 크기
        mMap.setMaxZoomPreference(20.0F) // 최대 크기

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this) // 현재위치
        setUpdateLocationListener() // 현재위치 업데이트

//        val sydney = LatLng(-34.0, 151.0)
//        val smhrd = LatLng(35.14982776049108, 126.91994477076531)
//        mMap.addMarker(
//            MarkerOptions()
//                .position(smhrd)
////            .title("Marker in Sydney"))
//                .title("Marker in 스마트인재개발원")
//        )
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(smhrd))
//        mMap.moveCamera(CameraUpdateFactory.zoomTo(15F));
//        mMap.isTrafficEnabled = true
    }

    @SuppressLint("MissingPermission")
    fun setUpdateLocationListener() {
        val locationRequest =
            LocationRequest.Builder(
                PRIORITY_HIGH_ACCURACY, // 높은 정확도
                1000) // 1초에 한 번
                .build()
        //  location 요청 함수,  (locationRequest, locationCallback)
        var locationCallback = object : LocationCallback(){
            override fun onLocationResult(locationResult: LocationResult) {
                    locationResult.locations.withIndex().forEach{
                        setLastLocation(it.value)
                    }
            }
        }

        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.myLooper()
        )
    }

    /**마지막 위치 정보*/
    fun setLastLocation(location: Location){
        val myLocation = LatLng(location.latitude, location.longitude)

        val markerOptions =
            MarkerOptions()
                .position(myLocation)
                .title("현재위치")
        mMap.addMarker(markerOptions)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation,15F))
    }


}
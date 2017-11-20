//package com.acv.airmad
//
//import android.os.Bundle
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import com.google.android.gms.maps.CameraUpdateFactory
//import com.google.android.gms.maps.GoogleMap
//import com.google.android.gms.maps.OnMapReadyCallback
//import com.google.android.gms.maps.SupportMapFragment
//import com.google.android.gms.maps.model.LatLng
//import com.google.android.gms.maps.model.Marker
//import com.google.android.gms.maps.model.MarkerOptions
//import kotlinx.coroutines.experimental.android.UI
//import kotlinx.coroutines.experimental.launch
//
//
//class MapFragment : SupportMapFragment(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener{
//    override fun onMarkerClick(marker: Marker?): Boolean {
//
//    }
//
//    private lateinit var map: GoogleMap
//    private val madrid by lazy { LatLng(40.4165000, -3.7025600) }
//
//
//    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
//        val rootView = super.onCreateView(inflater, container, savedInstanceState)
//
//        getMapAsync(this)
//
//        return rootView
//    }
//
//    override fun onMapReady(googleMap: GoogleMap) = with(googleMap) {
//        addMarker(MarkerOptions().position(madrid).title("Marker in Sydney"))
//        moveCamera(CameraUpdateFactory.newLatLng(madrid))
//        animateCamera(CameraUpdateFactory.zoomTo(15.0f))
//
////        IO.asyncContext()
////                .runAsync {
////                    retrofit(client()).allStations().execute().body()!!.forEach { addMarker(MarkerOptions().position(LatLng(it.latitude.toDouble(), it.longitude.toDouble()))) }
////                }.ev().attempt().unsafeRunSync()
//        launch(UI) {
//            // launch coroutine in UI context
//            val size = retrofit(client()).allStations().execute().body()!!.forEach { addMarker(MarkerOptions().position(LatLng(it.latitude.toDouble(), it.longitude.toDouble()))) }
////            delay(2000)
////            Toast.makeText(applicationContext, "safsdf", Toast.LENGTH_LONG).show()
//        }
//        map = this
//    }
//
//}
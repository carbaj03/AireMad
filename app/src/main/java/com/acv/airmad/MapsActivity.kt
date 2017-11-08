package com.acv.airmad

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.WindowManager
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kategory.Either
import kategory.effects.IO
import kategory.effects.asyncContext
import kategory.effects.ev
import kategory.right
import kotlinx.android.synthetic.main.activity_maps.*
import kotlinx.android.synthetic.main.view_map.*
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.run

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var map: GoogleMap
    private val madrid by lazy { LatLng(40.4165000, -3.7025600) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        setSupportActionBar(toolbar)
        supportActionBar!!.apply {
            setDisplayShowTitleEnabled(true)
            setDisplayHomeAsUpEnabled(true)
            title = ""
        }

        (fragmentMap as SupportMapFragment).getMapAsync(this)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
        search.clearFocus()


    }

    suspend fun sus() = run(CommonPool) { retrofit(client()).allStations().execute() }

    override fun onMapReady(googleMap: GoogleMap) = with(googleMap) {
        addMarker(MarkerOptions().position(madrid).title("Marker in Sydney"))
        moveCamera(CameraUpdateFactory.newLatLng(madrid))
        animateCamera(CameraUpdateFactory.zoomTo(15.0f))

        IO.asyncContext()
                .runAsync {
                    retrofit(client()).allStations().execute().body()!!.forEach { addMarker(MarkerOptions().position(LatLng(it.latitude.toDouble(), it.longitude.toDouble()))) }
                }.ev().attempt().unsafeRunSync()
//        launch(UI) {
//            // launch coroutine in UI context
//            val size = retrofit(client()).allStations().execute().body()!!.forEach { addMarker(MarkerOptions().position(LatLng(it.latitude.toDouble(), it.longitude.toDouble()))) }
////            delay(2000)
////            Toast.makeText(applicationContext, "safsdf", Toast.LENGTH_LONG).show()
//        }
        map = this
    }
}

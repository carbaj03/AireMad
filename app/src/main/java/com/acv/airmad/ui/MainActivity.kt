package com.acv.airmad.ui

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.MenuItem
import android.view.WindowManager
import com.acv.airmad.*
import com.acv.airmad.ui.detail.DetailFragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.appbar.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch


class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var map: GoogleMap
    private val madrid
            by lazy { LatLng(40.4165000, -3.7025600) }
    private val model
            by lazy { viewModelProviders<StationViewModel>() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        strictMode()

        setSupportActionBar(toolbarMap)
        actionbar()

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        launch(UI){}

    }

    override fun onBackPressed() =
            if (supportFragmentManager.backStackEntryCount > 0) {
                Log.e("hide", "fsafd")
                supportFragmentManager.popBackStack()
                window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
                searchMap.clearFocus()
            } else {
                super.onBackPressed()
            }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        Log.e("hide", "fsafd")
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
        currentFocus?.apply { inputMethodManager().hideSoftInputFromWindow(windowToken, 0) }
        searchMap.clearFocus()
        return super.onOptionsItemSelected(item)
    }

    override fun onMapReady(googleMap: GoogleMap) = with(googleMap) {
        addMarker(MarkerOptions().position(madrid).title("Marker in Sydney")).tag = 1
        moveCamera(CameraUpdateFactory.newLatLng(madrid))
        animateCamera(CameraUpdateFactory.zoomTo(15.0f))

        launch(UI) {
            observe { model.getStations() } `do` {
                it.map {
                    addMarker(MarkerOptions()
                            .position(LatLng(it.latitude.toDouble(), it.longitude.toDouble()))
                            .title("Melbourne")
                            .snippet("Population: 4,137,400")
                            .icon(bitmapDescriptorFromVector(R.drawable.ic_restaurant_menu_black_24dp))
                    ).tag = it.id
                }
            }
        }

        setOnMarkerClickListener({
            load<DetailFragment>()
            Log.e("sf", "${it.id}, ${it.tag}, ${it.title}")
            false
        })
        map = this
    }

    private fun Context.bitmapDescriptorFromVector(vectorResId: Int): BitmapDescriptor {
        val vectorDrawable = ContextCompat.getDrawable(this, vectorResId)
        vectorDrawable!!.setBounds(0, 0, vectorDrawable.intrinsicWidth, vectorDrawable.intrinsicHeight)
        val bitmap = Bitmap.createBitmap(vectorDrawable.intrinsicWidth, vectorDrawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        vectorDrawable.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

}

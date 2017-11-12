package com.acv.airmad

import android.content.res.Resources
import android.os.Bundle
import android.support.design.widget.BottomSheetBehavior
import android.support.design.widget.BottomSheetBehavior.*
import android.support.design.widget.CoordinatorLayout
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.util.TypedValue
import android.view.MenuItem
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.WindowManager
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.appbar.*
import kotlinx.android.synthetic.main.bottom_sheet.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var map: GoogleMap
    private val madrid by lazy { LatLng(40.4165000, -3.7025600) }
    private val from by lazy { BottomSheetBehavior.from(container) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        strictMode()

        setSupportActionBar(toolbarMap)
        actionbar()

        with(appbarMap.layoutParams as CoordinatorLayout.LayoutParams) {
            this.setMargins(getMargin(), getMargin() + getStatusBarHeight(resources), getMargin(), getMargin())
            appbarMap.layoutParams = this
        }

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)


//        load<MapFragment>(R.id.mapContainer)

        searchMap focusChange { _, _ ->
            load<ListFragment>()
            from.setState(BottomSheetBehavior.STATE_EXPANDED)
        }

        from.setBottomSheetCallback(object : BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    STATE_COLLAPSED -> {
                        load<ListFragment>()
                        appbarMap.visibility = VISIBLE
                    }
                    STATE_EXPANDED -> { appbarMap.visibility = GONE }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                // React to dragging events
            }
        })
        container click { from.setState(BottomSheetBehavior.STATE_EXPANDED) }
    }

    private fun getStatusBarHeight(resources: Resources): Int {
        val identifier = resources.getIdentifier("status_bar_height", "dimen", "android")
        return when {
            identifier > 0 -> resources.getDimensionPixelSize(identifier)
            else -> 0
        }
    }

    private fun getMargin() =
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8f, resources.displayMetrics).toInt()

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
        from.state = BottomSheetBehavior.STATE_COLLAPSED
        return super.onOptionsItemSelected(item)
    }

    override fun onMapReady(googleMap: GoogleMap) = with(googleMap) {
        addMarker(MarkerOptions().position(madrid).title("Marker in Sydney")).tag = 1
        moveCamera(CameraUpdateFactory.newLatLng(madrid))
        animateCamera(CameraUpdateFactory.zoomTo(15.0f))

//        IO.asyncContext()
//                .runAsync {
//                    retrofit(client()).allStations().execute().body()!!.forEach { addMarker(MarkerOptions().position(LatLng(it.latitude.toDouble(), it.longitude.toDouble()))) }
//                }.ev().attempt().unsafeRunSync()
        launch(UI) {
            retrofit(client()).allStations().execute().body()!!.forEach {
                addMarker(MarkerOptions().position(LatLng(it.latitude.toDouble(), it.longitude.toDouble()))).tag = it.id
            }
        }
        setOnMarkerClickListener({
            load<DetailFragment>()
            Log.e("sf", "${it.id}, ${it.tag}, ${it.title}")
            true
        })
        map = this
    }

}

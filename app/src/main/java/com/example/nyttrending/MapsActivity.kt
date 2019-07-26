package com.example.nyttrending

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.Toolbar
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import com.example.nyttrending.Model.CoordinateInput
import com.example.nyttrending.Model.MarkerLocation

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.ui.IconGenerator
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.activity_maps.*
import kotlinx.android.synthetic.main.activity_maps.view.*
import okhttp3.OkHttpClient
import java.util.*

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private val TAG = "TAG"
    private lateinit var appPrefs : SharedPreferences
    private lateinit var instructionsPopup : Dialog
    private lateinit var mTopToolbar : androidx.appcompat.widget.Toolbar

    private val BASE_URL = "http://10.51.235.194:3000/graphql"
    private var okHttpClient: OkHttpClient? = null
    private var apolloClient: ApolloClient? = null

    var lastMarkerUpdateTime = System.currentTimeMillis()

    var iconGenerator: IconGenerator? = null

    //Callback variables handling the data received.
    //mapQueryCallback returns articles for a specific location.
    val mapQueryCallback = object : ApolloCall.Callback<MapQuery.Data>() {
        override fun onFailure(e: ApolloException) {
            Log.e("TAG", e.toString())
        }

        override fun onResponse(response: Response<MapQuery.Data>) {
            Log.e("TAG", response.data().toString())
            response.data()?.articles
        }
    }
    //locationQueryCallback returns the locations to show on the screen with markers.
    val locationQueryCallback = object : ApolloCall.Callback<LocationQuery.Data>() {
        override fun onFailure(e: ApolloException) {
            Log.e("TAG", e.toString())
        }
        override fun onResponse(response: Response<LocationQuery.Data>) {
            //Iterate thru response data & create a list.
            val markerLocations = response.data()?.locations?.map { MarkerLocation(it.name(), it.location().latitude(), it.location().longitude()) }
            runOnUiThread {
                placeMarkers(markerLocations!!)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        okHttpClient = OkHttpClient.Builder().build()
        apolloClient = ApolloClient.builder()
            .serverUrl(BASE_URL)
            .okHttpClient(okHttpClient!!)
            .build()

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        Log.e(TAG, "onCreate()")
        mapFragment.getMapAsync(this)

        //Setup instructions popup.
        instructionsPopup = Dialog(this)
        instructionsPopup.findViewById<View>(R.layout.popup_instructions)

        //Setup toolbar.
        setupToolbar()
        showInstructionsPopup()

        iconGenerator = IconGenerator(this)

    }

    private fun placeMarkers(markerLocations: List<MarkerLocation>) {
        for(i in 0..markerLocations.size-1) {
            val markerLocation = markerLocations.get(i)
            Log.e(TAG, "Placing marker " + markerLocation.name)
            val location = LatLng(markerLocation.latitude, markerLocation.longitude)
            mMap.addMarker(MarkerOptions().position(location).title(markerLocation.name)
                .icon(BitmapDescriptorFactory.fromBitmap(iconGenerator!!.makeIcon(markerLocation.name))))
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.mapType = GoogleMap.MAP_TYPE_NORMAL

        Log.e(TAG, "onMapReady()")

        // Add a marker in Sydney and move the camera
        val sydney = LatLng(-34.0, 151.0)
        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))

        mMap.setOnCameraMoveListener {
            val currentUpdateTime = System.currentTimeMillis()
            if(currentUpdateTime - lastMarkerUpdateTime >= 500) {
                lastMarkerUpdateTime = currentUpdateTime
                mMap.clear()                //When moving, remove all markers from the app before putting new ones.
                Log.e(TAG, "Camera's moving + API call.")
                makeMarkerLocationAPICall()
            }
        }
    }

    private fun makeMarkerLocationAPICall() {
        var  curScreen = mMap.getProjection().getVisibleRegion().latLngBounds
        val northEast = curScreen.northeast
        val southWest = curScreen.southwest
        val sw = type.CoordinateInput.builder().latitude(southWest.latitude).longitude(southWest.longitude).build()
        val ne = type.CoordinateInput.builder().latitude(northEast.latitude).longitude(northEast.longitude).build()
        val zoom = mMap.cameraPosition.zoom.toInt()
        Log.e(TAG, "Current Zoom lvl. is $zoom")
        var locationQ = LocationQuery.builder().neCoord(ne).swCoord(sw).zoomLevel(zoom).build()

        apolloClient!!.query(locationQ).enqueue(locationQueryCallback)
    }

    private fun setupToolbar() {
        mTopToolbar = findViewById(R.id.toolbar)
        val font : Typeface = Typeface.createFromAsset(assets, "fonts/NYTCheltenhamExtraBold.otf")
        mTopToolbar.searchBar.setTypeface(font)
        setSupportActionBar(mTopToolbar)

        searchBar.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
            if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                (keyCode == KeyEvent.KEYCODE_ENTER)) {
                goToTrendingPage()
            }
            true
        })
        searchLogo.setOnClickListener {
            goToTrendingPage()
        }
    }

    private fun goToTrendingPage() {
        var intent = Intent(applicationContext, TrendingActivity::class.java)
        startActivity(intent)
    }

    private fun showInstructionsPopup() {
        //Check if first open.
        appPrefs = getSharedPreferences("FirstTimeOpen", Context.MODE_PRIVATE)
        var appIsOpenedForFirstTime : Boolean = appPrefs.getBoolean("appIsOpenedForFirstTime", true)

        if(appIsOpenedForFirstTime) {
            var editor : SharedPreferences.Editor = appPrefs.edit()
            editor.putBoolean("appIsOpenedForFirstTime", false)
            editor.commit()

            //Show popup
            instructionsPopup.setContentView(R.layout.popup_instructions)
            val transparentColor = ColorDrawable(Color.TRANSPARENT)
            instructionsPopup.window.setBackgroundDrawable(transparentColor)
            instructionsPopup.show()

            toolbar.nytLogo.alpha = .3f
            toolbar.searchLogo.alpha = .3f
            toolbar.searchBar.alpha = .3f

            var gotItBtn : Button = instructionsPopup.findViewById(R.id.gotItButton)
            gotItBtn.setOnClickListener {
                instructionsPopup.dismiss()
                toolbar.nytLogo.alpha = 1f
                toolbar.searchLogo.alpha = 1f
                toolbar.searchBar.alpha = 1f
            }
        }

    }

}

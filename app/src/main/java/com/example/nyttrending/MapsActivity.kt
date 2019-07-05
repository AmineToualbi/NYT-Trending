package com.example.nyttrending

import android.app.Dialog
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.Toolbar

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.activity_maps.*
import kotlinx.android.synthetic.main.activity_maps.view.*

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private val TAG = "TAG"
    private lateinit var appPrefs : SharedPreferences
    private lateinit var instructionsPopup : Dialog
    private lateinit var mTopToolbar : androidx.appcompat.widget.Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        //Setup instructions popup.
        instructionsPopup = Dialog(this)
        instructionsPopup.findViewById<View>(R.layout.popup_instructions)

        //Setup toolbar.
        mTopToolbar = findViewById(R.id.toolbar)
        val font : Typeface = Typeface.createFromAsset(assets, "fonts/NYTCheltenhamExtraBold.otf")
        mTopToolbar.searchBar.setTypeface(font)
        setSupportActionBar(mTopToolbar)

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

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        Log.e(TAG, "onCreate()")
        mapFragment.getMapAsync(this)
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.mapType = GoogleMap.MAP_TYPE_NORMAL

        Log.e(TAG, "onMapReady()")

        // Add a marker in Sydney and move the camera
        val sydney = LatLng(-34.0, 151.0)
        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))

        mMap.setOnMapClickListener {
            Log.e(TAG, "CLICK")
        }
    }


//    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
//        menuInflater.inflate(R.menu.menu_main, menu)
//        return true
//    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        var id : Int = item!!.itemId

        if(id == R.id.nytLogo) {
            Log.e(TAG, "CLICKED ON LOGO")
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}

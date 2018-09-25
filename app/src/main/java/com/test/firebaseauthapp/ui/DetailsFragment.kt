package com.test.firebaseauthapp.ui

import android.app.Activity
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.view.PagerAdapter
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import com.beust.klaxon.JsonArray
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.test.firebaseauthapp.R
import com.test.firebaseauthapp.R.id.images
import com.test.firebaseauthapp.helper.SliderAdapter
import io.ticofab.androidgpxparser.parser.GPXParser
import org.jetbrains.anko.async
import org.jetbrains.anko.uiThread
import org.xmlpull.v1.XmlPullParserException
import io.ticofab.androidgpxparser.parser.domain.Gpx
import kotlinx.android.synthetic.main.fragment_details.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException


class DetailsFragment : Fragment(), OnMapReadyCallback,
        GoogleMap.OnMarkerClickListener {

    override fun onMarkerClick(p0: Marker?) = false

    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var lastLocation: Location
    private lateinit var locationCallback: LocationCallback
    private lateinit var locationRequest: LocationRequest
    private var locationUpdateState = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_details, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val fileTitle = arguments!!.getString("fileTitle")
        val fileOverview = arguments!!.getString("fileOverview")
        val fileDiffLevel = arguments!!.getString("fileDiffLevel")
        val fileCity = arguments!!.getString("fileCity")
        val fileImage = arguments!!.getString("fileImage")
        val fileImagesArray = arguments!!.getString("fileImagesArray")
//        val strippedString = fileImagesArray.slice(IntRange(1, fileImagesArray.length-2)).split(,)
        val imagesArray: IntArray = intArrayOf()

        val resultJson = JSONObject(fileImagesArray)
        val resultArray = resultJson.getJSONArray("results")

//        for (i in 0 until strippedString.length - 1){
//            resources.getIdentifier(strippedString[i], "drawable", context!!.packageName)
//        }
        Log.e("HERE", resultJson.toString())
//        val mappedArray = strippedString.map { it.toInt() }.toTypedArray()

        detailTitle.text = fileTitle
        subtitle.text = fileCity
        description.text = fileOverview

        val imageName = resources.getIdentifier(fileImage, "drawable", context!!.packageName)
        trailHero.setBackgroundResource(imageName)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = childFragmentManager.findFragmentById(R.id.fMap) as SupportMapFragment
        mapFragment.getMapAsync(this)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity!!)

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult) {
                super.onLocationResult(p0)

                lastLocation = p0.lastLocation
//                placeMarkerOnMap(LatLng(lastLocation.latitude, lastLocation.longitude), "You are here")
            }
        }
        createLocationRequest()
        val images: Array<Int> = arrayOf(R.drawable.c2c1, R.drawable.c2c2)
        val adapter: PagerAdapter = SliderAdapter(this.context!!, images)
        viewpager.adapter = adapter

        left_nav.setOnClickListener {
            var tab = viewpager.currentItem
            if (tab >= 1) {
                tab--
                viewpager.currentItem = tab
            } else if (tab < 1) {
                viewpager.currentItem = images.size -1
            }
        }
        right_nav.setOnClickListener {
            var tab = viewpager.currentItem
            if (tab < images.size - 1) {
                tab++
                viewpager.currentItem = tab
            } else {
                viewpager.currentItem = 0
            }
        }
    }
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isScrollGesturesEnabled = true
        mMap.setOnMarkerClickListener(this)

        val latLongB = LatLngBounds.Builder()

        val fileName = arguments!!.getString("fileName")

        val mParser = GPXParser()
        var parsedGpx: Gpx? = null
        try {
            val `in` = context!!.assets.open(fileName!!.plus(".gpx"))
            parsedGpx = mParser.parse(`in`)
        } catch (e: IOException) {
            // do something with this exception
            e.printStackTrace()
        } catch (e: XmlPullParserException) {
            e.printStackTrace()
        }

        if (parsedGpx == null) {
            // error parsing track
        } else {
            // do something with the parsed track
            async {
                uiThread { _ ->
                    val tracks = parsedGpx.tracks
                    // traversing through tracks
                    for (track in tracks) {
                        val segments = track.trackSegments
                        // traversing through segments
                        for(segment in segments) {
                            val trackPoint = segment.trackPoints
                            val l = trackPoint.size
                            // traversing through track points
                            for (i in trackPoint.indices) {
                                val lat = trackPoint[i].latitude
                                val lng = trackPoint[i].longitude
                                // First point position
                                val position = LatLng(lat, lng)
                                val options = PolylineOptions()
                                // As long as the track point has another track point after it
                                if(i < l -1) {
                                    val nextLat = trackPoint[i+1].latitude
                                    val nextLon = trackPoint[i+1].longitude
                                    // Next point position
                                    val nextPosition = LatLng(nextLat, nextLon)
                                    // Add all points to the bounds of the map
                                    latLongB.include(position)
                                    // Create the 2 point path
                                    options.add(position, nextPosition)
                                    options.color(Color.BLUE)
                                    options.width(10f)
                                    // Add the 2 point path to the map
                                    mMap.addPolyline(options)
                                } else {
                                    // Add the last point to the map bounds and map path
                                    latLongB.include(position)
                                    options.add(position)
                                }
                            }
                        }
                    }
                    // Build the map bounds
                    val bounds = latLongB.build()
                    // Position the map within map bounds
                    mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100))
                }
            }
        }
        setUpMap()
    }
    private fun setUpMap() {
        if (ActivityCompat.checkSelfPermission(context!!,
                        android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity!!,
                    arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
            return
        }

        mMap.isMyLocationEnabled = true

        fusedLocationClient.lastLocation.addOnSuccessListener(activity!!) { location ->
            // Got last known location. In some rare situations this can be null.
            if (location != null) {
                lastLocation = location
            }
        }
    }

    private fun startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(context!!,
                        android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity!!,
                    arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                    LOCATION_PERMISSION_REQUEST_CODE)
            return
        }
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null /* Looper */)
    }

    private fun createLocationRequest() {
        locationRequest = LocationRequest()
        locationRequest.interval = 10000
        locationRequest.fastestInterval = 5000
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

        val builder = LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest)

        val client = LocationServices.getSettingsClient(activity!!)
        val task = client.checkLocationSettings(builder.build())

        task.addOnSuccessListener {
            locationUpdateState = true
            startLocationUpdates()
        }
        task.addOnFailureListener { e ->
            if (e is ResolvableApiException) {
                // Location settings are not satisfied, but this can be fixed
                // by showing the user a dialog.
                try {
                    // Show the dialog by calling startResolutionForResult(),
                    // and check the result in onActivityResult().
                    e.startResolutionForResult(activity,
                            REQUEST_CHECK_SETTINGS)
                } catch (sendEx: IntentSender.SendIntentException) {
                    // Ignore the error.
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CHECK_SETTINGS) {
            if (resultCode == Activity.RESULT_OK) {
                locationUpdateState = true
                startLocationUpdates()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    override fun onResume() {
        super.onResume()
        if (!locationUpdateState) {
            startLocationUpdates()
        }
    }

    companion object {
        fun newInstance(fileName: String,
                        fileTitle: String,
                        fileOverview: String,
                        fileDiffLevel: String,
                        fileCity: String,
                        fileImage: String,
                        fileImagesArray: JSONArray
        ): DetailsFragment {
            val args = Bundle()



            args.putString("fileName", fileName)
            args.putString("fileTitle", fileTitle)
            args.putString("fileOverview", fileOverview)
            args.putString("fileDiffLevel", fileDiffLevel)
            args.putString("fileCity", fileCity)
            args.putString("fileImage", fileImage)

            args.putParcelableArray("fileImagesArray", arrayOf(fileImagesArray))

            val fragment = DetailsFragment()
            fragment.arguments = args
            return fragment
        }
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
        private const val REQUEST_CHECK_SETTINGS = 2
    }
}

package com.test.firebaseauthapp.ui

import android.app.Activity
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.view.PagerAdapter
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.test.firebaseauthapp.R
import com.test.firebaseauthapp.helper.SliderAdapter
import io.ticofab.androidgpxparser.parser.GPXParser
import org.jetbrains.anko.async
import org.jetbrains.anko.uiThread
import org.xmlpull.v1.XmlPullParserException
import io.ticofab.androidgpxparser.parser.domain.Gpx
import kotlinx.android.synthetic.main.fragment_details.*
import java.io.IOException
import java.lang.Exception
import kotlin.collections.ArrayList

class DetailsFragment : Fragment(), OnMapReadyCallback,
        GoogleMap.OnMarkerClickListener {

//    Firebase references
    private var mDatabaseReference: DatabaseReference? = null
    private var mDatabase: FirebaseDatabase? = null
    private var mAuth: FirebaseAuth? = null

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

        mDatabase = FirebaseDatabase.getInstance()
        mDatabaseReference = mDatabase!!.reference.child("Users")
        mAuth = FirebaseAuth.getInstance()

        val fileId = arguments!!.getString("fileId")
        val fileTitle = arguments!!.getString("fileTitle")
        val fileOverview = arguments!!.getString("fileOverview")
        val fileDiffLevel = arguments!!.getString("fileDiffLevel")
        val fileLength = arguments!!.getString("fileLength")
        val fileCity = arguments!!.getString("fileCity")
        val fileImage = arguments!!.getString("fileImage")
        val startingLat = arguments!!.getDouble("startingLat")
        val startingLon = arguments!!.getDouble("startingLon")
        val fileImagesArray = arguments!!.getIntegerArrayList("fileImagesArray")!!
        val mUser = mAuth!!.currentUser
        val mUserUid = mUser!!.uid
        val mRefFavorited = mDatabase!!.reference.child("Trails").child(fileId!!).child("favoritedBy")

        // Allow user to scroll around map without scrolling the whole page
        transparentView.setOnTouchListener { _, event->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    scroller.requestDisallowInterceptTouchEvent(true)
                    return@setOnTouchListener  false
                }
                MotionEvent.ACTION_UP -> {
                    scroller.requestDisallowInterceptTouchEvent(false)
                    return@setOnTouchListener  true
                }
                else -> {
                    scroller.requestDisallowInterceptTouchEvent(false)
                    return@setOnTouchListener  true
                }
            }
        }

        detailTitle.text = fileTitle
        subtitle.text = fileCity
        description.text = fileOverview
        difficulty.text = fileDiffLevel
        trail_length.text = fileLength
        // Get the Int resource value for the image file
        val imageName = resources.getIdentifier(fileImage, "drawable", context!!.packageName)
        // Set the image in the hero banner for the Trail detail fragment
        trailHero.setBackgroundResource(imageName)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = childFragmentManager.findFragmentById(R.id.fMap) as SupportMapFragment
        val allMapFragment = childFragmentManager.findFragmentById(R.id.allMap) as SupportMapFragment
        mapFragment.getMapAsync(this)


        fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity!!)
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult) {
                super.onLocationResult(p0)

                lastLocation = p0.lastLocation
                placeMarkerOnMap(LatLng(lastLocation.latitude, lastLocation.longitude), "You are here")
            }
        }
        createLocationRequest()

        // Back button function
        back.setOnClickListener {
            openFragment()
        }
        // Link the slider adapter to the Page Viewer
        val adapter: PagerAdapter = SliderAdapter(this.context!!, fileImagesArray)
        viewpager.adapter = adapter
        // Controls for image slider
        left_nav.setOnClickListener {
            var tab = viewpager.currentItem
            if (tab >= 1) {
                tab--
                viewpager.currentItem = tab
            } else if (tab < 1) {
                viewpager.currentItem = fileImagesArray.size -1
            }
        }
        right_nav.setOnClickListener {
            var tab = viewpager.currentItem
            if (tab < fileImagesArray.size - 1) {
                tab++
                viewpager.currentItem = tab
            } else {
                viewpager.currentItem = 0
            }
        }

        // When Map It! is clicked, open maps app with specified location
        mapit!!.setOnClickListener {
            try {
                val uri = Uri.parse("geo:$startingLat,$startingLon?q=$startingLat,$startingLon($fileTitle)")
                val intent = Intent(android.content.Intent.ACTION_VIEW, uri)
                startActivity(intent)
            } catch (e:Exception) {
                Log.e("HERE", e.toString())
            }

        }
        fullMap.setOnClickListener {
            allMapFragment.getMapAsync(this)
            hiddenMap!!.animate().translationY(0.toFloat())
        }
        closebtn!!.setOnClickListener {
            hiddenMap!!.animate().translationY(hiddenMap!!.height.toFloat())
        }

        // Listen for favorited data changes in firebase
        mRefFavorited.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val userfavorited = snapshot.child(mUserUid).value
                // When Favorite button is clicked
                if (userfavorited != null) {
                    // If already favorited, remove favorite
                    favButton?.setBackgroundResource(resources.getIdentifier("heart_icon", "drawable", context!!.packageName))
                } else {
                     // Else, add to favorites
                    favButton?.setBackgroundResource(resources.getIdentifier("fav_sel", "drawable", context!!.packageName))
                }
                favButton?.setOnClickListener {
                    if(userfavorited != null) {
                        mRefFavorited.child(mUserUid).removeValue()
                    } else {
                        mRefFavorited.child(mUserUid).setValue(ServerValue.TIMESTAMP)
                    }
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {}
        })
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

    private fun placeMarkerOnMap(location: LatLng, title: String) {
        val markerOptions = MarkerOptions().position(location)

        markerOptions.title(title)

        mMap.addMarker(markerOptions)
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

    override fun onDestroy() {
        super.onDestroy()
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    override fun onLowMemory() {
        super.onLowMemory()
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    override fun onResume() {
        super.onResume()
        if (!locationUpdateState) {
            startLocationUpdates()
        }
    }

    // Opens selected fragment
    private fun openFragment() {
        fragmentManager!!.popBackStackImmediate()
    }

    companion object {
        fun newInstance(fileId: String,
                        fileName: String,
                        fileTitle: String,
                        fileOverview: String,
                        fileDiffLevel: String,
                        fileLength: String,
                        fileCity: String,
                        fileImage: String,
                        startingLat: Double,
                        startingLon: Double,
                        favorites: ArrayList<String>,
                        fileImagesArray: ArrayList<Int>
        ): DetailsFragment {
            val args = Bundle()
            args.putString("fileId", fileId)
            args.putString("fileName", fileName)
            args.putString("fileTitle", fileTitle)
            args.putString("fileOverview", fileOverview)
            args.putString("fileDiffLevel", fileDiffLevel)
            args.putString("fileLength", fileLength)
            args.putString("fileCity", fileCity)
            args.putString("fileImage", fileImage)
            args.putDouble("startingLat", startingLat)
            args.putDouble("startingLon", startingLon)
            args.putStringArrayList("favoritedBy", favorites)
            args.putIntegerArrayList("fileImagesArray", fileImagesArray)

            val fragment = DetailsFragment()
            fragment.arguments = args
            return fragment
        }
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
        private const val REQUEST_CHECK_SETTINGS = 2
    }
}

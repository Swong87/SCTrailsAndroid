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
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.test.firebaseauthapp.R
import io.ticofab.androidgpxparser.parser.GPXParser
import org.jetbrains.anko.async
import org.jetbrains.anko.uiThread
import org.xmlpull.v1.XmlPullParserException
import io.ticofab.androidgpxparser.parser.domain.Gpx
import kotlinx.android.synthetic.main.fragment_details.*
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
    }
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.setOnMarkerClickListener(this)

        val latLongB = LatLngBounds.Builder()

        val fileName = arguments!!.getString("fileName")

        val mParser = GPXParser()
        var parsedGpx: Gpx? = null
        try {
            val `in` = context!!.assets.open(fileName.plus(".gpx"))
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
                uiThread { it ->
//                    val points = ArrayList<LatLng>()
                    val tracks = parsedGpx.tracks

                    // traversing through routes
                    for (track in tracks) {
                        val segments = track.trackSegments
                        for(segment in segments) {
                            val trkpt = segment.trackPoints
                            val l = trkpt.size
                            // Add map point for each track point in map
                            for (i in trkpt.indices) {
                                val lat = trkpt[i].latitude
                                val lng = trkpt[i].longitude
                                val position = LatLng(lat, lng)
                                val options = PolylineOptions()
                                if(i < l -1) {
                                    val nlat = trkpt[i+1].latitude
                                    val nlng = trkpt[i+1].longitude
                                    val nextposition = LatLng(nlat, nlng)
                                    // Add all points to the bounds of the map
                                    latLongB.include(position)
                                    options.add(position, nextposition)
                                    options.color(Color.BLUE)
                                    options.width(10f)
                                    mMap.addPolyline(options)
                                } else {
                                    latLongB.include(position)
                                    options.add(position)
                                }
                            }
                        }
                    }
                    // Build the map bounds
                    val bounds = latLongB.build()
                    // Add line connecting all points
                    // Position the map where all points are included
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
                        fileImage: String
        ): DetailsFragment {
            val args = Bundle()
            args.putString("fileName", fileName)
            args.putString("fileTitle", fileTitle)
            args.putString("fileOverview", fileOverview)
            args.putString("fileDiffLevel", fileDiffLevel)
            args.putString("fileCity", fileCity)
            args.putString("fileImage", fileImage)
            val fragment = DetailsFragment()
            fragment.arguments = args
            return fragment
        }
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
        private const val REQUEST_CHECK_SETTINGS = 2
    }
}

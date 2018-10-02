package com.test.firebaseauthapp.ui

import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.test.firebaseauthapp.R
import com.test.firebaseauthapp.helper.TrailHelper
import kotlinx.android.synthetic.main.fragment_list_view.*

import android.support.v7.widget.LinearLayoutManager
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.test.firebaseauthapp.helper.Trail

class ListViewFragment : Fragment() {
    // Get the recycler view
    private lateinit var recyclerView: RecyclerView
    private lateinit var mContent: Fragment
    private lateinit var fusedLocationClient: FusedLocationProviderClient


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_list_view, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        if (ContextCompat.checkSelfPermission(context!!,
                        android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity!!,
                    arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), ListViewFragment.LOCATION_PERMISSION_REQUEST_CODE)
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context!!)

        var userLocation: LatLng

        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            // After getting permission for device location, set it here
            userLocation = LatLng(location!!.latitude, location.longitude)

            // Set up recyclerView with data from json file
            val llm = LinearLayoutManager(activity)
            val trails = TrailHelper.getTrailsFromJson("trails.json", context!!)
            llm.orientation = LinearLayoutManager.VERTICAL
            rv_trails_list.layoutManager = llm
            // send data to the trail adapter and set to recycler view
            rv_trails_list.adapter = TrailAdapter(trails, context!!, userLocation) { partItem : Trail -> partItemClicked(partItem) }


            // When Map View is clicked, change bottom nav item id to map view
            tv_map_view.setOnClickListener {
                val view = activity!!.findViewById(R.id.navigationView) as BottomNavigationView
                view.selectedItemId = R.id.navigation_mapView
            }

            if (savedInstanceState != null) {
                //Restore the fragment's state here
                mContent = fragmentManager!!.getFragment(savedInstanceState, "ListViewFragment")!!
            }
        }
        recyclerView = rv_trails_list
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        fragmentManager!!.putFragment(outState, "ListViewFragment", mContent)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (ActivityCompat.checkSelfPermission(context!!,
                        android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity!!,
                    arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), 1)
            return
        }
    }
    // When trail is clicked open trail detail fragment
    private fun partItemClicked(partItem : Trail) {
//        Log.d(partItem.filePath, "CLICK!")
        // Get JSONArray from json file
        val images = partItem.images
        // Prepare empty array list for conversion
        val list = ArrayList<Int>()
        // Change all image files names from json into Int image resources and add them to empty array list
        for (i in 0 until images.length()) {
            val imageInt = resources.getIdentifier(images[i].toString(), "drawable", context!!.packageName)
            list.add(imageInt)
        }
//        Log.e("HERE", list.toString())
        // Pass data of selected Trail into the Details fragment
        openFragment(DetailsFragment.newInstance(
                partItem.filePath,
                partItem.title,
                partItem.overview,
                partItem.diffLevel,
                partItem.length.toString(),
                partItem.city,
                partItem.trailImage,
                partItem.startingPointLat,
                partItem.startingPointLong,
                partItem.favorite,
                list
        ))
    }

    // Opens selected fragment
    private fun openFragment(fragment: Fragment) {
        val transaction = fragmentManager!!.beginTransaction()
        transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.exit_to_right, R.anim.enter_from_left)
        transaction.replace(R.id.container, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    companion object {
        fun newInstance(): ListViewFragment = ListViewFragment()

        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }
}

package com.test.firebaseauthapp.ui

import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.test.firebaseauthapp.R
import com.test.firebaseauthapp.helper.FavHelper
import com.test.firebaseauthapp.helper.Trail
import kotlinx.android.synthetic.main.fragment_favorites.*

class FavoritesFragment : Fragment() {
    // Get the recycler view
    private lateinit var recyclerView: RecyclerView
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_favorites, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (ContextCompat.checkSelfPermission(context!!,
                        android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity!!,
                    arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), FavoritesFragment.LOCATION_PERMISSION_REQUEST_CODE)
        }
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context!!)

        var userLocation: LatLng? = null

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                // After getting permission for device location, set it here
                userLocation = LatLng(location!!.latitude, location.longitude)

                // Set up recyclerView with data from json file
                val llm = LinearLayoutManager(this.activity)
                FavHelper.getFavsFromDB{ trailsArray ->
                    val sortedByName = trailsArray.sortedWith(compareBy{ it.title })
                    val newTrailsArray = ArrayList<Trail>()
                    for (obj in sortedByName) {
                        newTrailsArray.add(obj)
                    }
                    // send data to the trail adapter and set to recycler view
                    rv_trails_list.adapter = FavAdapter(newTrailsArray, context!!, userLocation) { partItem : Trail -> partItemClicked(partItem) }
                    recyclerView = rv_trails_list
                }
                llm.orientation = LinearLayoutManager.VERTICAL
                rv_trails_list.layoutManager = llm

                // When Map View is clicked, change bottom nav item id to map view

                // When sort button is clicked, toggle "by name" and "by recent"
                var sortedByRecent = false
                sortby.setOnClickListener {
                    if (!sortedByRecent) {
                        FavHelper.getFavsFromDB { trailsArray ->
                            val sortedByRec = trailsArray.sortedWith(compareByDescending { it.favoritedBy[0] })
                            val newTrailsArray = ArrayList<Trail>()
                            for (obj in sortedByRec) {
                                newTrailsArray.add(obj)
                            }
                            // send data to the trail adapter and set to recycler view
                            rv_trails_list.adapter = FavAdapter(newTrailsArray, context!!, userLocation) { partItem: Trail -> partItemClicked(partItem) }
                            recyclerView = rv_trails_list
                        }
                        llm.orientation = LinearLayoutManager.VERTICAL
                        rv_trails_list.layoutManager = llm
                        sortedByRecent = true
                        sortby.text = "SORT BY NAME"
                    } else {
                        FavHelper.getFavsFromDB { trailsArray ->
                            val sortedByName = trailsArray.sortedWith(compareBy{ it.title })
                            val newTrailsArray = ArrayList<Trail>()
                            for (obj in sortedByName) {
                                newTrailsArray.add(obj)
                            }
                            // send data to the trail adapter and set to recycler view
                            rv_trails_list.adapter = TrailAdapter(newTrailsArray, context!!, userLocation) { partItem: Trail -> partItemClicked(partItem) }
                            recyclerView = rv_trails_list
                        }
                        llm.orientation = LinearLayoutManager.VERTICAL
                        rv_trails_list.layoutManager = llm
                        sortedByRecent = false
                        sortby.text = "SORT BY RECENT"
                    }
                }

            }
        fusedLocationClient.lastLocation
                .addOnFailureListener {

                    // Set up recyclerView with data from json file
                    val llm = LinearLayoutManager(this.activity)
                    FavHelper.getFavsFromDB{ trailsArray ->
                        val sortedByName = trailsArray.sortedWith(compareBy{ it.title })
                        val newTrailsArray = ArrayList<Trail>()
                        for (obj in sortedByName) {
                            newTrailsArray.add(obj)
                        }
                        // send data to the trail adapter and set to recycler view
                        rv_trails_list.adapter = FavAdapter(newTrailsArray, context!!, userLocation) { partItem : Trail -> partItemClicked(partItem) }
                        recyclerView = rv_trails_list
                    }
                    llm.orientation = LinearLayoutManager.VERTICAL
                    rv_trails_list.layoutManager = llm

                    // When Map View is clicked, change bottom nav item id to map view

                    // When sort button is clicked, toggle "by name" and "by recent"
                    var sortedByRecent = false
                    sortby.setOnClickListener {
                        if (!sortedByRecent) {
                            FavHelper.getFavsFromDB { trailsArray ->
                                val sortedByRec = trailsArray.sortedWith(compareByDescending { it.favoritedBy[0] })
                                val newTrailsArray = ArrayList<Trail>()
                                for (obj in sortedByRec) {
                                    newTrailsArray.add(obj)
                                }
                                // send data to the trail adapter and set to recycler view
                                rv_trails_list.adapter = FavAdapter(newTrailsArray, context!!, userLocation) { partItem: Trail -> partItemClicked(partItem) }
                                recyclerView = rv_trails_list
                            }
                            llm.orientation = LinearLayoutManager.VERTICAL
                            rv_trails_list.layoutManager = llm
                            sortedByRecent = true
                            sortby.text = "SORT BY NAME"
                        } else {
                            FavHelper.getFavsFromDB { trailsArray ->
                                val sortedByName = trailsArray.sortedWith(compareBy{ it.title })
                                val newTrailsArray = ArrayList<Trail>()
                                for (obj in sortedByName) {
                                    newTrailsArray.add(obj)
                                }
                                // send data to the trail adapter and set to recycler view
                                rv_trails_list.adapter = TrailAdapter(newTrailsArray, context!!, userLocation) { partItem: Trail -> partItemClicked(partItem) }
                                recyclerView = rv_trails_list
                            }
                            llm.orientation = LinearLayoutManager.VERTICAL
                            rv_trails_list.layoutManager = llm
                            sortedByRecent = false
                            sortby.text = "SORT BY RECENT"
                        }
                    }

                }



    }

//    override fun onSaveInstanceState(outState: Bundle) {
//        super.onSaveInstanceState(outState)
//        fragmentManager!!.putFragment(outState, "ListViewFragment", mContent)
//    }
    // When trail is clicked open trail detail fragment
    private fun partItemClicked(partItem : Trail) {
        // Get JSONArray from json file
        val images = partItem.images
        // Prepare empty array list for conversion
        val list = ArrayList<Int>()
        // Change all image files names from json into Int image resources and add them to empty array list
        images.forEach{
            val imageInt = resources.getIdentifier(it.toString(), "drawable", context!!.packageName)
            list.add(imageInt)
        }
//        for (i in 0 until images.length()) {
//            val imageInt = resources.getIdentifier(images[i].toString(), "drawable", context!!.packageName)
//            list.add(imageInt)
//        }
        // Pass data of selected Trail into the Details fragment
        openFragment(DetailsFragment.newInstance(
                partItem.id,
                partItem.filePath,
                partItem.title,
                partItem.overview,
                partItem.diffLevel,
                partItem.length.toString(),
                partItem.city,
                partItem.trailImage,
                partItem.startingPointLat,
                partItem.startingPointLong,
                partItem.favoritedBy,
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
        fun newInstance(): FavoritesFragment = FavoritesFragment()

        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }
}

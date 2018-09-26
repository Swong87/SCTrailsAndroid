package com.test.firebaseauthapp.ui

import android.annotation.SuppressLint
import android.location.Location
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.test.firebaseauthapp.R
import com.test.firebaseauthapp.helper.Trail
import com.test.firebaseauthapp.helper.TrailHelper
import kotlinx.android.synthetic.main.fragment_list_view.*

class FavoritesFragment : Fragment() {
    // Get the recycler view
    private lateinit var recyclerView: RecyclerView
    private lateinit var mContent: Fragment
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_favorites, container, false)
    }

    @SuppressLint("MissingPermission")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context!!)


        var userLocation: LatLng
        fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    // After getting permission for device location, set it here
                    userLocation = LatLng(location!!.latitude, location.longitude)

                    // Set up recyclerView with data from json file
                    val llm = LinearLayoutManager(activity)
                    val trails = TrailHelper.getTrailsFromJson("trails.json", context!!)
                    llm.orientation = LinearLayoutManager.VERTICAL
                    rv_trails_list.layoutManager = llm
                    // send data to the trail adapter and set to recycler view
                    rv_trails_list.adapter = TrailAdapter(trails, context!!, userLocation) { partItem : Trail -> partItemClicked(partItem) }
                    recyclerView = rv_trails_list

                }

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        fragmentManager!!.putFragment(outState, "ListViewFragment", mContent)
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
    }
}

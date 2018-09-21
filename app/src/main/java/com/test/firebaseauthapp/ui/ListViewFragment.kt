package com.test.firebaseauthapp.ui

import android.content.Intent
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
import android.util.Log
import com.test.firebaseauthapp.helper.Trail


class ListViewFragment : Fragment() {
    // Get the recycler view
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_list_view, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        // Set up recyclerView with data from json file
        val llm = LinearLayoutManager(activity)
        val trails = TrailHelper.getTrailsFromJson("trails.json", context!!)
        llm.orientation = LinearLayoutManager.VERTICAL
        rv_trails_list.layoutManager = llm
        // set on click on each trail item
        rv_trails_list.adapter = TrailAdapter(trails, context!!, { partItem : Trail -> partItemClicked(partItem) })
        recyclerView = rv_trails_list

        // When Map View is clicked, change bottom nav item id to map view
        tv_map_view.setOnClickListener {
            val view = activity!!.findViewById(R.id.navigationView) as BottomNavigationView
            view.selectedItemId = R.id.navigation_mapView
        }


    }
    // When trail is clicked open trail detail fragment
    private fun partItemClicked(partItem : Trail) {
        Log.d(partItem.filePath, "CLICK!")
        openFragment(DetailsFragment.newInstance(partItem.filePath, partItem.title, partItem.overview, partItem.diffLevel, partItem.city, partItem.trailImage))
    }

    // Opens selected fragment
    private fun openFragment(fragment: Fragment) {
        val transaction = fragmentManager!!.beginTransaction()
        transaction.replace(R.id.container, fragment)
        transaction.commit()
    }

    companion object {
        fun newInstance(): ListViewFragment = ListViewFragment()
    }
}

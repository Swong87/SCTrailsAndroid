package com.test.firebaseauthapp.ui

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
import com.test.firebaseauthapp.helper.Trail


class ListViewFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private var TRAILSList = ArrayList<Trail>()
    private var listener: ItemFragment.OnListFragmentInteractionListener? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_list_view, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val llm = LinearLayoutManager(activity)
        val trails = TrailHelper.getTrailsFromJson("trails.json", context!!)
        llm.orientation = LinearLayoutManager.VERTICAL
        rv_trails_list.layoutManager = llm
        rv_trails_list.adapter = TrailAdapter(trails, context!!, listener)

        recyclerView = rv_trails_list

        tv_map_view.setOnClickListener {
            val view = activity!!.findViewById(R.id.navigationView) as BottomNavigationView
            view.selectedItemId = R.id.navigation_mapView
            val mapViewFragment = MapViewFragment.newInstance()
            openFragment(mapViewFragment)
        }


    }

    fun itemClicked(item: Trail) {

    }

    private fun openFragment(fragment: Fragment) {
        val transaction = fragmentManager!!.beginTransaction()
        transaction.replace(R.id.container, fragment)
        transaction.commit()
    }

    companion object {
        fun newInstance(): ListViewFragment = ListViewFragment()
    }
}

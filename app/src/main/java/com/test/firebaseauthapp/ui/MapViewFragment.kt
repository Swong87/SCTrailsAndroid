package com.test.firebaseauthapp.ui

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.test.firebaseauthapp.R

class MapViewFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_map_view, container, false)
    }

    companion object {
        fun newInstance(): MapViewFragment = MapViewFragment()
    }
}

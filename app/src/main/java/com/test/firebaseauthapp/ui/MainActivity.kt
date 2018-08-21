package com.test.firebaseauthapp.ui

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v7.app.ActionBar
import com.test.firebaseauthapp.R
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var toolbar: ActionBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        toolbar = supportActionBar!!
        // Makes the initial tab the list view when directed to the mainactivity
        navigationView!!.selectedItemId = R.id.navigation_listView
        navigationView!!.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        openFragment(ListViewFragment.newInstance())
    }
    // Opens selected fragment
    private fun openFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_profile -> {
                toolbar.title = "Profile"
                val profileFragment = ProfileFragment.newInstance()
                openFragment(profileFragment)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_favorites -> {
                toolbar.title = "Favorites"
                val favoritesFragment = FavoritesFragment.newInstance()
                openFragment(favoritesFragment)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_listView -> {
                toolbar.title = "List View"
                val listViewFragment = ListViewFragment.newInstance()
                openFragment(listViewFragment)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_mapView -> {
                toolbar.title = "Map View"
                val mapViewFragment = MapViewFragment.newInstance()
                openFragment(mapViewFragment)
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }



}

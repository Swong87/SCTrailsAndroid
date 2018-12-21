package com.test.firebaseauthapp.ui

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.util.Log
import com.test.firebaseauthapp.R
import com.test.firebaseauthapp.helper.TrailHelper
import kotlinx.android.synthetic.main.activity_main.*
import io.ticofab.androidgpxparser.parser.GPXParser
import org.xmlpull.v1.XmlPullParserException
import io.ticofab.androidgpxparser.parser.domain.Gpx
import java.io.IOException


class MainActivity : AppCompatActivity() {

    private lateinit var mContent: Fragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState != null) {
            //Restore the fragment's state here
            mContent = supportFragmentManager.getFragment(savedInstanceState, "TabFragment")!!
        } else {
            // Makes the initial tab the list view when directed to the mainactivity
            navigationView!!.selectedItemId = R.id.navigation_listView
            navigationView!!.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
            openFragment(ListViewFragment.newInstance())

            val mParser = GPXParser()

            var parsedGpx: Gpx? = null
            try {
                val `in` = assets.open("Ais_Trail.gpx")
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
                Log.d("Debug", "This is my log message at the debug level here")
                // do something with the parsed track
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        supportFragmentManager.putFragment(outState, "TabFragment", mContent)
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
//                toolbar.title = "Profile"
                val profileFragment = ProfileFragment.newInstance()
                openFragment(profileFragment)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_favorites -> {
//                toolbar.title = "Favorites"
                val favoritesFragment = FavoritesFragment.newInstance()
                openFragment(favoritesFragment)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_listView -> {
//                toolbar.title = "List View"
                val listViewFragment = ListViewFragment.newInstance()
                openFragment(listViewFragment)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_mapView -> {
//                toolbar.title = "Map View"
                val mapViewFragment = MapViewFragment.newInstance()
                openFragment(mapViewFragment)
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }




}

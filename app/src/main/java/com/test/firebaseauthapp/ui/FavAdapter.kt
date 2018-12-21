package com.test.firebaseauthapp.ui

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.google.android.gms.maps.model.LatLng
import com.test.firebaseauthapp.R
import com.test.firebaseauthapp.helper.Trail
import kotlinx.android.synthetic.main.fragment_item.view.*

class FavAdapter constructor(
        private var items: ArrayList<Trail>,
        private val context: Context,
        private val userLocation: LatLng?,
        private val clickListener: (Trail) -> Unit
)
    : RecyclerView.Adapter<FavAdapter.ViewHolder>() {
    override fun getItemCount(): Int = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(context)
                .inflate(R.layout.fragment_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.ivFavIcon.setBackgroundResource(context.resources.getIdentifier("heart_icon", "drawable", context.packageName))

        // Get item from database
        val item = items[position]

        // Get identifier for the item's image file
        val imageName = context.resources.getIdentifier(item.trailImage, "drawable", context.packageName)
        // Give the view holder the title and image info
        holder.tvTrailName.text = item.title
        holder.ivTrailImage.setBackgroundResource(imageName)

        val trailStartPoint = LatLng(item.startingPointLat, item.startingPointLong)
        val set = "%.2f"
        // Give the view holder the distance of trail from device location
        if(userLocation != null) {
            holder.tvTrailDistance.text = set.format(getDistance(userLocation, trailStartPoint))
        } else {
            holder.tvTrailDistance.text = ""
        }
        // Give the view holder an onClick listener attached to the specific trail
        (holder).bind(items[position], clickListener)

        // Get JSONArray from json file
        val images = item.images
        // Prepare empty array list for conversion of images array
        val list = ArrayList<Int>()
        // Change all image files names from json into Int image resources and add them to empty array list
        images.forEach{
            val imageInt = context.resources.getIdentifier(it.toString(), "drawable", context.packageName)
            list.add(imageInt)
        }
    }

    private fun getDistance(loc: LatLng, dest: LatLng): Double {
        val theta = loc.longitude - dest.longitude
        var dist = Math.sin(deg2rad(loc.latitude)) * Math.sin(deg2rad(dest.latitude)) + Math.cos(deg2rad(loc.latitude)) * Math.cos(deg2rad(dest.latitude)) * Math.cos(deg2rad(theta))
        dist = Math.acos(dist)
        dist = rad2deg(dist)
        dist *= 60.0 * 1.1515

        return dist
    }

    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    /*::    This function converts decimal degrees to radians                        :*/
    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    private fun deg2rad(deg: Double): Double {
        return deg * Math.PI / 180.0
    }

    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    /*::    This function converts radians to decimal degrees                        :*/
    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    private fun rad2deg(rad: Double): Double {
        return rad * 180 / Math.PI
    }

    class ViewHolder(mView: View) : RecyclerView.ViewHolder(mView) {
        // Get the view holder's title, image, distance
        val tvTrailName: TextView = mView.trail_name
        val ivTrailImage: ImageView = mView.trail_image
        val tvTrailDistance: TextView = mView.trail_distance
        val ivFavIcon: ImageView = mView.fav_icon

        fun bind(part: Trail, clickListener: (Trail) -> Unit) {
            itemView.setOnClickListener { clickListener(part)}
        }
    }
}

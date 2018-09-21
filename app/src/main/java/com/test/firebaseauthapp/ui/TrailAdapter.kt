package com.test.firebaseauthapp.ui

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.test.firebaseauthapp.R
import com.test.firebaseauthapp.helper.Trail


import kotlinx.android.synthetic.main.fragment_item.view.*
//Puts trail data into the fragment item

class TrailAdapter constructor(
        private val items: ArrayList<Trail>,
        private val context: Context,
        private val clickListener: (Trail) -> Unit
)
    : RecyclerView.Adapter<TrailAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context)
                .inflate(R.layout.fragment_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // Get item from json file
        val item = items[position]
        // Get identifier for the item's image file
        val imageName = context.resources.getIdentifier(item.trailImage, "drawable", context.packageName)
        // Give the view holder the title and image info
        holder.tvTrailName.text = item.title
        holder.ivTrailImage.setBackgroundResource(imageName)
        // Give the view holder an onClick listener
        (holder).bind(items[position], clickListener)
    }

    override fun getItemCount(): Int = items.size

    class ViewHolder(mView: View) : RecyclerView.ViewHolder(mView) {
        // Get the view holder's title and image
        val tvTrailName: TextView = mView.trail_name
        val ivTrailImage: ImageView = mView.trail_image

        fun bind(part: Trail, clickListener: (Trail) -> Unit) {
            itemView.setOnClickListener { clickListener(part)}
        }
    }
}

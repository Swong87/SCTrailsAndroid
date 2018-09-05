package com.test.firebaseauthapp.ui

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.test.firebaseauthapp.R
import com.test.firebaseauthapp.helper.Trail


import com.test.firebaseauthapp.ui.ItemFragment.OnListFragmentInteractionListener

import kotlinx.android.synthetic.main.fragment_item.view.*
//Puts trail data into the fragment item
class TrailAdapter constructor(
        private val items: ArrayList<Trail>,
        private val context: Context,
        private val mListener: OnListFragmentInteractionListener?
)
    : RecyclerView.Adapter<TrailAdapter.ViewHolder>() {

    private val mOnClickListener: View.OnClickListener

    init {
        mOnClickListener = View.OnClickListener { v ->
            val item = v.tag as Trail
            mListener?.onListFragmentInteraction(item)
            Log.d(item.filePath, "CLICK!")

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context)
                .inflate(R.layout.fragment_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        val imageName = context.resources.getIdentifier(item.trailImage, "drawable", context.packageName)
        holder.tvTrailName.text = item.title
        holder.ivTrailImage.setBackgroundResource(imageName)

        with(holder.mView) {
            tag = item
            setOnClickListener(mOnClickListener)
        }
    }

    override fun getItemCount(): Int = items.size

    class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        val tvTrailName: TextView = mView.trail_name
        val ivTrailImage: ImageView = mView.trail_image

        override fun toString(): String {
            return super.toString() + " '" + ivTrailImage.background + "'"
        }

        fun bind(items: ArrayList<Trail>, listener: ContentListener) {

            // this is the click listener. It calls the onItemClicked interface method implemented in the Activity
            itemView.setOnClickListener {
                listener.onItemClicked(items[adapterPosition])
            }
        }

    }

    interface ContentListener {
        fun onItemClicked(item: Trail)
    }

}

package com.test.firebaseauthapp.helper

import android.content.Context
import android.support.v4.view.PagerAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import com.test.firebaseauthapp.R

class SliderAdapter(var context: Context, var images: ArrayList<Int>) : PagerAdapter() {
    private lateinit var inflater:LayoutInflater

    override fun isViewFromObject(view: View, any: Any): Boolean = view == any as LinearLayout

    override fun getCount(): Int = images.size

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val image:ImageView
        inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view:View = inflater.inflate(R.layout.slider_image_item, container, false)
        image = view.findViewById(R.id.slider_image)
        image.setBackgroundResource(images[position])
        container.addView(view)
        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, any: Any) {
        container.removeView(any as LinearLayout)
    }
}
package com.test.firebaseauthapp.helper

import android.content.Context
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

object FavoritesHelper {

    private const val KEY_TITLE = "title"
    private const val KEY_TRAIL_IMAGE = "trailImage"
    private const val KEY_OVERVIEW = "overview"
    private const val KEY_CAT = "cat"
    private const val KEY_FILE_PATH = "filePath"
    private const val KEY_STARTING_POINT_LAT = "startingPointLat"
    private const val KEY_STARTING_POINT_LONG = "startingPointLong"
    private const val KEY_DIFF_LEVEL = "diffLevel"
    private const val KEY_LENGTH = "length"
    private const val KEY_TYPE = "type"
    private const val KEY_CITY = "city"
    private const val KEY_IMAGES = "images"
    private const val KEY_FAVORITE = "favorite"

    fun getTrailsFromJson(fileName: String, context: Context): ArrayList<Trail> {

        val trails = ArrayList<Trail>()

        try {
            // Load the JSONArray from the file
            val jsonString = loadJsonFromFile(fileName, context)
            val json = JSONObject(jsonString)
            val jsonTrails = json.getJSONArray("trails")

            // Create the list of Trails
            for (index in 0 until jsonTrails.length()) {
                val trailTitle = jsonTrails.getJSONObject(index).getString(KEY_TITLE)
                val trailImage = jsonTrails.getJSONObject(index).getString(KEY_TRAIL_IMAGE)
                val trailOverview = jsonTrails.getJSONObject(index).getString(KEY_OVERVIEW)
                val trailCat = jsonTrails.getJSONObject(index).getString(KEY_CAT)
                val trailFilePath = jsonTrails.getJSONObject(index).getString(KEY_FILE_PATH)
                val trailStartingPointLat = jsonTrails.getJSONObject(index).getDouble(KEY_STARTING_POINT_LAT)
                val trailStartingPointLong = jsonTrails.getJSONObject(index).getDouble(KEY_STARTING_POINT_LONG)
                val trailDiffLevel = jsonTrails.getJSONObject(index).getString(KEY_DIFF_LEVEL)
                val trailLength = jsonTrails.getJSONObject(index).getDouble(KEY_LENGTH)
                val trailType = jsonTrails.getJSONObject(index).getString(KEY_TYPE)
                val trailCity = jsonTrails.getJSONObject(index).getString(KEY_CITY)
                val trailImages = jsonTrails.getJSONObject(index).getJSONArray(KEY_IMAGES)
                val trailFavorite = jsonTrails.getJSONObject(index).getBoolean(KEY_FAVORITE)
                trails.add(Trail(trailTitle, trailImage, trailOverview, trailCat, trailFilePath, trailStartingPointLat, trailStartingPointLong, trailDiffLevel, trailLength, trailType, trailCity, trailImages, trailFavorite))
            }
        } catch (e: JSONException) {
            return trails
        }

        return trails
    }

    private fun loadJsonFromFile(filename: String, context: Context): String {
        var json = ""

        try {
            val input = context.assets.open(filename)
            val size = input.available()
            val buffer = ByteArray(size)
            input.read(buffer)
            input.close()
            json = buffer.toString(Charsets.UTF_8)
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return json
    }
}
package com.test.firebaseauthapp.helper

data class Trail(val id: String,
                val title: String,
                val trailImage: String,
                val overview: String,
                val cat: String,
                val filePath: String,
                val startingPointLat: Double,
                val startingPointLong: Double,
                val diffLevel: String,
                val length: Double,
                val type: String,
                val city: String,
                val images: MutableList<Any>,
                var favoritedBy: ArrayList<String>)

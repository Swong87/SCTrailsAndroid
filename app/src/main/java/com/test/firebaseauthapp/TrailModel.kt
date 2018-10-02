package com.test.firebaseauthapp

data class TrailModel(
        val title: String = "",
        val trailImage: String = "",
        val overview: String = "",
        val cat: String = "",
        val filePath: String = "",
        val startingLat: Double,
        val startingLon: Double,
        val diffLevel: String = "",
        val length: Double,
        val type: String = "",
        val city: String = "",
        val images: ArrayList<Int>,
        val favorite: Boolean,
        var uuid: String = ""

)
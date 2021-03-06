package com.test.firebaseauthapp.helper

import com.google.firebase.database.*

object TrailHelper {

    private var mDatabase: FirebaseDatabase? = null
    private var mDatabaseTrails: DatabaseReference? = null

    fun getTrailsFromDB(callback: (ArrayList<Trail>) -> Unit) {
        val trailsArray = ArrayList<Trail>()
        mDatabase = FirebaseDatabase.getInstance()
        mDatabaseTrails = mDatabase!!.reference.child("Trails")
        mDatabaseTrails!!.addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onCancelled(snapshot: DatabaseError) {
                println(snapshot.message)
            }

            override fun onDataChange (snapshot: DataSnapshot) {
                val children = snapshot.children
                children.forEach {
                    val trailImages : MutableList<Any> = mutableListOf()
                    val trailFavorites : ArrayList<String> = arrayListOf()
                    it.child("images").children.forEach {
                        trailImages.add(it.value!!)
                    }
                    it.child("favoritedBy").children.forEach {
                        trailFavorites.add(it.value!!.toString())
                    }
                    val trailId = it.child("id").value.toString()
                    val trailTitle = it.child("title").value.toString()
                    val trailImage = it.child("trailImage").value.toString()
                    val trailOverview = it.child("overview").value.toString()
                    val trailCat = it.child("cat").value.toString()
                    val trailFilePath = it.child("filePath").value.toString()
                    val trailStartingPointLat = it.child("startingPointLat").value.toString().toDouble()
                    val trailStartingPointLong = it.child("startingPointLong").value.toString().toDouble()
                    val trailDiffLevel = it.child("diffLevel").value.toString()
                    val trailLength = it.child("length").value.toString().toDouble()
                    val trailType = it.child("type").value.toString()
                    val trailCity = it.child("city").value.toString()
                    trailsArray.add(Trail(trailId, trailTitle, trailImage, trailOverview, trailCat, trailFilePath, trailStartingPointLat, trailStartingPointLong, trailDiffLevel, trailLength, trailType, trailCity, trailImages, trailFavorites))
                }
                callback(trailsArray)
            }
        })
    }
}
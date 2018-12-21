package com.test.firebaseauthapp.helper

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

object FavHelper {

    //    Firebase references
    private var mAuth: FirebaseAuth? = null
    private var mDatabase: FirebaseDatabase? = null
    private var mDatabaseTrails: DatabaseReference? = null


    fun getFavsFromDB(callback: (ArrayList<Trail>) -> Unit) {
        mAuth = FirebaseAuth.getInstance()
        mDatabase = FirebaseDatabase.getInstance()
        mDatabaseTrails = mDatabase!!.reference.child("Trails")
        mDatabaseTrails!!.addListenerForSingleValueEvent(object : ValueEventListener {
            val trailsArray = ArrayList<Trail>()
            val mUser = mAuth!!.currentUser
            val mUserUid = mUser!!.uid

            override fun onCancelled(snapshot: DatabaseError) {
                println(snapshot.message)
            }

            override fun onDataChange (snapshot: DataSnapshot) {
                val children = snapshot.children
                // Loop through each Trail in database
                children.forEach {
                    // Trail images
                    val trailImages : MutableList<Any> = mutableListOf()
                    it.child("images").children.forEach {
                        trailImages.add(it.value!!)
                    }
                    // Trail favoritedBy array with users: added by timestamp
                    val trailFavorites : ArrayList<String> = arrayListOf()
                    it.child("favoritedBy").children.forEach {
                        trailFavorites.add(it.value!!.toString())
                    }
                    //Trail ID
                    val trailId = it.child("id").value.toString()
                    // Timestamp value of when trail was added to favorites(value can be NULL)
                    val isfavorited = snapshot.child(trailId).child("favoritedBy").child(mUserUid).value

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
                    // Check if trail is favorited by current user
                    if (isfavorited != null) {
                        // If trail is favorited by current user, add trail to array
                        trailsArray.add(Trail(trailId, trailTitle, trailImage, trailOverview, trailCat, trailFilePath, trailStartingPointLat, trailStartingPointLong, trailDiffLevel, trailLength, trailType, trailCity, trailImages, trailFavorites))
                    } else {
                        // Else do something
                    }
                }
                // return array list of trails
                callback(trailsArray)
            }
        })
    }
}
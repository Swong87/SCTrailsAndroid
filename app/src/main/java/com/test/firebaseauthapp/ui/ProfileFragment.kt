package com.test.firebaseauthapp.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.facebook.login.LoginManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import com.test.firebaseauthapp.R
import com.test.firebaseauthapp.helper.CircleTransform
import kotlinx.android.synthetic.main.fragment_profile.view.*
import java.io.IOException


class ProfileFragment : Fragment() {

    //Firebase references
    private var mDatabaseReference: DatabaseReference? = null
    private var mDatabase: FirebaseDatabase? = null
    private var mAuth: FirebaseAuth? = null
    private var mStorage: FirebaseStorage? = null

    //track Choosing Image Intent
    private val CHOOSING_IMAGE_REQUEST = 1234

    private var fileUri: Uri? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        view.logoutBtn.setOnClickListener { logoutUser() }
        mDatabase = FirebaseDatabase.getInstance()
        mDatabaseReference = mDatabase!!.reference.child("Users")
        mAuth = FirebaseAuth.getInstance()
        val mUser = mAuth!!.currentUser
        mStorage = FirebaseStorage.getInstance()
        // Create a Storage Ref w/ username
        val storageRef = mStorage!!.reference
        // Current User ID
        val mUserReference = mDatabaseReference!!.child(mUser!!.uid)
        // Download stored user image to the profile image slot if there is one
        storageRef.child(mUserReference.toString()).downloadUrl
            .addOnSuccessListener {
                Picasso.with(activity)
                        .load(it)
                        .transform(CircleTransform())
                        .into(view.ib_profile_pic)
            }
        // Find User name and display it on profile page
        mUserReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                view.tv_name.text = snapshot.child("name").value as String

            }
            override fun onCancelled(databaseError: DatabaseError) {}
        })
        // Add on click event to upload a new photo from local
        view.upload_pic.setOnClickListener { showChoosingFile() }

        // Inflate the layout for this fragment
        return view
    }



    private fun logoutUser() {
        // Firebase
        mAuth!!.signOut()
        // Faceboook
        LoginManager.getInstance().logOut()
        val intent = Intent(activity, LandingActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
    }
    // Opens local images to choose new picture
    private fun showChoosingFile() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Image"), CHOOSING_IMAGE_REQUEST)
    }
    // After user chooses picture, store file in firebase and display it on profile page
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        mAuth = FirebaseAuth.getInstance()
        val mUser = mAuth!!.currentUser
        val mUserReference = mDatabaseReference!!.child(mUser!!.uid)
        mStorage = FirebaseStorage.getInstance()
        // Create a Storage Ref w/ username
        val storageRef = mStorage!!.reference
        val imageReference = storageRef.child(mUserReference.toString())

        if (requestCode == CHOOSING_IMAGE_REQUEST && data != null && data.data != null) {
            fileUri = data.data
            try {
                var value: Double
                imageReference.putFile(fileUri!!)
                    .addOnProgressListener { taskSnapshot ->
                        value = (100.0 * taskSnapshot.bytesTransferred) / taskSnapshot.totalByteCount
                        Log.v("value", "value==$value")
                    }
                    .addOnSuccessListener {
                        Toast.makeText(this.context, "Image uploaded successfully!", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener{
                        exception -> exception.printStackTrace()
                    }
                Picasso.with(activity)
                        .load(fileUri)
                        .transform(CircleTransform())
                        .into(view!!.ib_profile_pic)

            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    companion object {
        fun newInstance(): ProfileFragment = ProfileFragment()
    }
}

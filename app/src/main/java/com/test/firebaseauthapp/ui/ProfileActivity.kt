package com.test.firebaseauthapp.ui

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.facebook.login.LoginManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.test.firebaseauthapp.R
import kotlinx.android.synthetic.main.activity_profile.*

class ProfileActivity : AppCompatActivity() {

    //Firebase references
    private var mDatabaseReference: DatabaseReference? = null
    private var mDatabase: FirebaseDatabase? = null
    private var mAuth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        initialise()
    }

    private fun initialise() {

        mDatabase = FirebaseDatabase.getInstance()
        mDatabaseReference = mDatabase!!.reference.child("Users")
        mAuth = FirebaseAuth.getInstance()
        logoutBtn!!.setOnClickListener { logoutUser() }
        nav_button!!.setOnClickListener { startActivity(Intent(this@ProfileActivity,
                ListViewFragment::class.java)) }
    }

    private fun logoutUser() {
        mAuth!!.signOut()
        LoginManager.getInstance().logOut()
        val intent = Intent(this@ProfileActivity, LandingActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
    }

    override fun onStart() {
        super.onStart()
        val mUser = mAuth!!.currentUser
        val mUserReference = mDatabaseReference!!.child(mUser!!.uid)
        tv_email!!.text = mUser.email
        mUserReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                tv_name!!.text = snapshot.child("name").value as String
            }
            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

}

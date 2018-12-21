package com.test.firebaseauthapp.ui

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.facebook.login.LoginManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import com.test.firebaseauthapp.R
import com.test.firebaseauthapp.helper.CircleTransform
import kotlinx.android.synthetic.main.fragment_profile.view.*


class ProfileFragment : Fragment() {

    //Firebase references
    private var mDatabaseReference: DatabaseReference? = null
    private var mDatabase: FirebaseDatabase? = null
    private var mAuth: FirebaseAuth? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        view.logoutBtn.setOnClickListener { logoutUser() }
        mDatabase = FirebaseDatabase.getInstance()
        mDatabaseReference = mDatabase!!.reference.child("Users")
        mAuth = FirebaseAuth.getInstance()
        val mUser = mAuth!!.currentUser
        val mUserReference = mDatabaseReference!!.child(mUser!!.uid)

        mUserReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                view.tv_name.text = snapshot.child("name").value as String
            }
            override fun onCancelled(databaseError: DatabaseError) {}
        })

        Picasso.with(activity)
                .load(mUser.photoUrl)
                .transform(CircleTransform())
                .into(view.ib_profile_pic)
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

    companion object {
        fun newInstance(): ProfileFragment = ProfileFragment()
    }
}

package com.test.firebaseauthapp.ui

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.test.firebaseauthapp.R
import kotlinx.android.synthetic.main.activity_landing.*


class LandingActivity : AppCompatActivity() {

    //UI elements
    private val TAG = "LandingActivity"

    // Firebase elements
    private var mAuth: FirebaseAuth? = null
    private var mDatabaseReference: DatabaseReference? = null
    private var mDatabase: FirebaseDatabase? = null

    //Facebook Callback manager
    private var callbackManager: CallbackManager? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_landing)

        initialise()
    }

    private fun initialise() {
        // Firebase elements
        mAuth = FirebaseAuth.getInstance()
        mDatabase = FirebaseDatabase.getInstance()
        mDatabaseReference = mDatabase!!.reference.child("Users")
        // When the sign in button is pressed, direct to the login activity
        btnSignIn!!
                .setOnClickListener { startActivity(Intent(this@LandingActivity,
                        LoginActivity::class.java)) }
        // When Facebook button is pressed
        fbLogin!!.setOnClickListener{
            login_button!!.performClick()
        }
        // create callback manager
        callbackManager = CallbackManager.Factory.create()
        // When FB button is pressed set email permissions
        login_button!!.setReadPermissions("email")
        // then begin registration with FB
        login_button!!.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {
                // App code
                handleFacebookAccessToken(loginResult.accessToken)
            }
            override fun onCancel() {
                // App code
                mAuth!!.signOut()
                LoginManager.getInstance().logOut()
            }
            override fun onError(exception: FacebookException) {
                // App code
            }
        })
        // When "Don't have an account yet?" is clicked, direct to create account activity
        needAccount!!.setOnClickListener {
            val intent = Intent(this@LandingActivity, CreateAccountActivity::class.java)
            startActivity(intent)
        }
        // When Landing page is hit sign out of firebase and facebook
        mAuth!!.signOut()
        LoginManager.getInstance().logOut()
    }
    // Handle Facebook auth codes
    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager!!.onActivityResult(requestCode, resultCode, data)
    }
    // Take facebook credentials and direct to main activity
    private fun handleFacebookAccessToken(token: AccessToken) {
        Log.d(TAG, "handleFacebookAccessToken:$token")
        val credential = FacebookAuthProvider.getCredential(token.token)
        indeterminateBar!!.visibility = View.VISIBLE
        mAuth!!
            .signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                indeterminateBar!!.visibility = View.GONE
                if (task.isSuccessful) {

                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "createUserWithEmail:success")
                    val userId = mAuth!!.currentUser!!.uid
                    val userName = mAuth!!.currentUser!!.displayName
                    //update user profile information
                    val currentUserDb = mDatabaseReference!!.child(userId)
                    currentUserDb.child("name").setValue(userName)

                    val intent = Intent(this@LandingActivity, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    startActivity(intent)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    Toast.makeText(this@LandingActivity, "Authentication failed.",
                            Toast.LENGTH_SHORT).show()
                }
            }
    }

}

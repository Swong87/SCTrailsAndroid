

package com.test.firebaseauthapp.ui

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.*
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
import kotlinx.android.synthetic.main.activity_create_account.*


class CreateAccountActivity : AppCompatActivity() {
    private val TAG = "CreateAccountActivity"

    //Firebase references
    private var mDatabaseReference: DatabaseReference? = null
    private var mDatabase: FirebaseDatabase? = null
    private var mAuth: FirebaseAuth? = null

    //global variables
    private var fullName: String? = null
    private var email: String? = null
    private var password: String? = null
    private var confirmPassword: String? = null

    //Facebook Callback manager
    var callbackManager: CallbackManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_account)

        initialise()
    }

    private fun initialise() {
        // Firebase elements
        mDatabase = FirebaseDatabase.getInstance()
        mDatabaseReference = mDatabase!!.reference.child("Users")
        mAuth = FirebaseAuth.getInstance()
        // When SIGN UP button is pressed
        btn_register!!.setOnClickListener { createNewAccount() }

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

        alreadyHaveAccount!!
                .setOnClickListener { startActivity(Intent(this@CreateAccountActivity,
                        LoginActivity::class.java)) }
    }
    // Inputs registered data into firebase and then runs the updateUserInfoAndUI function
    private fun createNewAccount() {
        fullName = et_name?.text.toString()
        email = et_email?.text.toString()
        password = et_password?.text.toString()
        confirmPassword = et_confirm_password?.text.toString()
        if (!TextUtils.isEmpty(fullName) && !TextUtils.isEmpty(email) && !TextUtils.isEmpty(password) && !TextUtils.isEmpty(confirmPassword)) {
            // If the password matches the confirm password
            if (password == confirmPassword) {
                // Show progress bar
                indeterminateBar!!.visibility = View.VISIBLE

                mAuth!!.createUserWithEmailAndPassword(email!!, password!!).addOnCompleteListener(this) {
                    task ->
//                    mProgressBar!!.visibility = View.GONE
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "createUserWithEmail:success")
                        val userId = mAuth!!.currentUser!!.uid
                        //Verify Email
                        verifyEmail()
                        //update user profile information in firebase
                        val currentUserDb = mDatabaseReference!!.child(userId)
                        currentUserDb.child("name").setValue(fullName)
                        // After successful login go to listview main activity
                        updateUserInfoAndUI()

                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "createUserWithEmail:failure", task.exception)
                        Toast.makeText(this@CreateAccountActivity, "Authentication failed.",
                                Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, "Passwords don't match", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Enter all details", Toast.LENGTH_SHORT).show()
        }
    }
    // Direct to Main Activity
    private fun updateUserInfoAndUI() {
        val intent = Intent(this@CreateAccountActivity, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
    }
    // Sends email verification
    private fun verifyEmail() {
        val mUser = mAuth!!.currentUser
        mUser!!.sendEmailVerification()
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this@CreateAccountActivity,
                            "Verification email sent to " + mUser.getEmail(),
                            Toast.LENGTH_SHORT).show()
                } else {
                    Log.e(TAG, "sendEmailVerification", task.exception)
                    Toast.makeText(this@CreateAccountActivity,
                            "Failed to send verification email.",
                            Toast.LENGTH_SHORT).show()
                }
            }
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

                    val intent = Intent(this@CreateAccountActivity, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    startActivity(intent)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    Toast.makeText(this@CreateAccountActivity, "Authentication failed.",
                            Toast.LENGTH_SHORT).show()
                }
            }
    }
}

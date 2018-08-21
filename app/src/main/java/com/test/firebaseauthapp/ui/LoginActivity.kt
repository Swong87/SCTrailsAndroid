

package com.test.firebaseauthapp.ui

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.test.firebaseauthapp.R
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    private val TAG = "LoginActivity"
    //global variables
    private var email: String? = null
    private var password: String? = null
    //Firebase references
    private var mAuth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        initialise()
    }

    private fun initialise() {
        mAuth = FirebaseAuth.getInstance()
        tv_forgot_password!!
                .setOnClickListener { startActivity(Intent(this@LoginActivity,
                        ForgotPasswordActivity::class.java)) }
        tv_register_account!!
                .setOnClickListener { startActivity(Intent(this@LoginActivity,
                        CreateAccountActivity::class.java)) }
        btn_login!!.setOnClickListener { loginUser() }
    }

    private fun loginUser() {
        email = et_email?.text.toString()
        password = et_password?.text.toString()
        if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {
            indeterminateBar!!.visibility = View.VISIBLE
            Log.d(TAG, "Logging in user.")
            mAuth!!.signInWithEmailAndPassword(email!!, password!!)
                .addOnCompleteListener(this) { task ->
                    indeterminateBar!!.visibility = View.GONE
                    if (task.isSuccessful) {
                        // Sign in success, update UI with signed-in user's information
                        Log.d(TAG, "signInWithEmail:success")
                        updateUI()
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.e(TAG, "signInWithEmail:failure", task.exception)
                        Toast.makeText(this@LoginActivity, "Incorrect Username/Password", Toast.LENGTH_SHORT).show()
                    }
                }
        } else {
            Toast.makeText(this, "Enter all details", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateUI() {
        val intent = Intent(this@LoginActivity, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
//        finish()
    }
}

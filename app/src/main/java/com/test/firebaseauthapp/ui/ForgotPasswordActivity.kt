package com.test.firebaseauthapp.ui

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.test.firebaseauthapp.R
import kotlinx.android.synthetic.main.activity_forgot_password.*

class ForgotPasswordActivity : AppCompatActivity() {

    private val TAG = "ForgotPasswordActivity"
    //Firebase references
    private var mAuth: FirebaseAuth? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        initialise()
    }
    private fun initialise() {
        mAuth = FirebaseAuth.getInstance()
        btn_submit!!.setOnClickListener { sendPasswordResetEmail() }
    }

    private fun sendPasswordResetEmail() {
        val email = et_email?.text.toString()
        if (!TextUtils.isEmpty(email)) {
            mAuth!!
                    .sendPasswordResetEmail(email)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val message = "Email sent."
                            Log.d(TAG, message)
                            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                            updateUI()
                        } else {
                            Log.w(TAG, task.exception!!.message)
                            Toast.makeText(this, "No user found with this email.", Toast.LENGTH_SHORT).show()
                        }
                    }
        } else {
            Toast.makeText(this, "Enter Email", Toast.LENGTH_SHORT).show()
        }
    }
    private fun updateUI() {
        val intent = Intent(this@ForgotPasswordActivity, LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
    }

}

package com.example.semakerandroid.UI

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.arellomobile.mvp.MvpAppCompatActivity
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.arellomobile.mvp.presenter.ProvidePresenterTag
import com.example.semakerandroid.Presenters.SignupPresenter
import com.example.semakerandroid.R
import com.example.semakerandroid.SignupView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_signup.*


class SignupActivity : MvpAppCompatActivity(), SignupView, View.OnClickListener {

    @InjectPresenter
    lateinit var signupPresenter: SignupPresenter

    @ProvidePresenterTag(presenterClass = SignupPresenter::class)
    fun provideSignupPresenterTag(): String = "Hello"

    @ProvidePresenter
    fun provideSignupPresenter() = SignupPresenter()

    private lateinit var auth: FirebaseAuth
    lateinit var dbreference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)
        btn_sbm.setOnClickListener(this)
        auth = FirebaseAuth.getInstance()
    }

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        updateUI(currentUser)
    }

    private fun signUp() {
        if (!TextUtils.isEmpty(et_username.text.toString()) && !TextUtils.isEmpty(et_email.text.toString()) && !TextUtils.isEmpty(
                et_pw.text.toString()
            ) && !TextUtils.isEmpty(
                et_rpw.text.toString()
            ) && et_pw.text.toString() == et_rpw.text.toString()
        ) {
            val query: Query =
                FirebaseDatabase.getInstance().reference.child("Users").orderByChild("username")
                    .equalTo(et_username.text.toString())
            query.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(@NonNull dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.childrenCount > 0) {
                        Toast.makeText(
                            baseContext, "Имя пользователя уже существует.",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        auth.createUserWithEmailAndPassword(
                            et_email.text.toString(),
                            et_pw.text.toString()
                        )
                            .addOnCompleteListener(this@SignupActivity) { task ->
                                if (task.isSuccessful) {
                                    register(et_username.text.toString(), et_email.text.toString())
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Toast.makeText(
                                        baseContext, "Authentication failed.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    updateUI(null)
                                }
                            }
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Toast.makeText(
                        baseContext, "Reading data failed",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
        } else
            Toast.makeText(
                baseContext, "Authentication failed.",
                Toast.LENGTH_SHORT
            ).show()
    }

    private fun updateUI(user: FirebaseUser?) {
        if (user != null) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            show()
        }
    }

    private fun register(username: String, email: String) {
        hide()
        dbreference =
            FirebaseDatabase.getInstance().getReference("Users").child(auth.currentUser!!.uid)
        val hashMap: HashMap<String, String> = HashMap()
        hashMap["id"] = auth.currentUser!!.uid
        hashMap["username"] = username
        hashMap["name"] = ""
        hashMap["birthdate"] = ""
        hashMap["sex"] = ""
        hashMap["city"] = ""
        hashMap["about"] = ""
        hashMap["email"] = email
        hashMap["phone"] = ""
        hashMap["imageURL"] = "default"

        dbreference.setValue(hashMap).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                updateUI(auth.currentUser)
            }
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btn_sbm -> signUp()
        }
    }

    override fun hide() {
        linearLayout4.visibility = CoordinatorLayout.INVISIBLE
        progressBar.visibility = CoordinatorLayout.VISIBLE
    }

    override fun show() {
        linearLayout4.visibility = CoordinatorLayout.VISIBLE
        progressBar.visibility = CoordinatorLayout.INVISIBLE
    }
}

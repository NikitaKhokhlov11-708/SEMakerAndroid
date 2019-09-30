package com.example.semakerandroid.UI

import android.app.DatePickerDialog
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.Fragment
import com.arellomobile.mvp.MvpAppCompatFragment
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.arellomobile.mvp.presenter.ProvidePresenterTag
import com.example.semakerandroid.EditProfileView
import com.example.semakerandroid.Models.User
import com.example.semakerandroid.Presenters.EditProfilePresenter
import com.example.semakerandroid.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_editprofile.view.*
import java.util.*
import kotlin.collections.HashMap


class EditprofileFragment : MvpAppCompatFragment(), EditProfileView {

    @InjectPresenter
    lateinit var editProfilePresenter: EditProfilePresenter

    @ProvidePresenterTag(presenterClass = EditProfilePresenter::class)
    fun provideEditProfilePresenterTag(): String = "Hello"

    @ProvidePresenter
    fun provideEditProfilePresenter() = EditProfilePresenter()

    val fuser: FirebaseUser? = FirebaseAuth.getInstance().currentUser
    val dbreference: DatabaseReference =
        FirebaseDatabase.getInstance().getReference("Users").child(fuser!!.uid)
    lateinit var user: User
    private var root: View? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.fragment_editprofile, null)

        hide()
        dbreference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(@NonNull dataSnapshot: DataSnapshot) {
                user = dataSnapshot.getValue<User>(User::class.java)!!
                root!!.et_username.setText(user.username)
                root!!.et_name.setText(user.name)
                root!!.et_birthdate.text = user.birthdate

                if (user.sex == "Ж")
                    root!!.sw_sex.isChecked = true

                root!!.et_city.setText(user.city)
                root!!.et_about.setText(user.about)
                root!!.et_phone.setText(user.phone)
                show()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                context?.let {
                    Toast.makeText(
                        it, "Failed to read value.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        })

        val datePickerDialog = DatePickerDialog(
            context,
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                root!!.et_birthdate.text =
                    dayOfMonth.toString() + "/" + (monthOfYear + 1) + "/" + year
            },
            Calendar.getInstance().get(Calendar.YEAR) - 18,
            Calendar.getInstance().get(Calendar.MONTH),
            Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        )

        root!!.et_birthdate.setOnClickListener {
            datePickerDialog.datePicker.maxDate = Calendar.getInstance().timeInMillis
            datePickerDialog.show()
        }


        root!!.confirm.setOnClickListener {
            if (!TextUtils.isEmpty(root!!.et_username.text.toString())
                && !TextUtils.isEmpty(root!!.et_name.text.toString())
                && !TextUtils.isEmpty(root!!.et_birthdate.text.toString())
                && !TextUtils.isEmpty(root!!.et_city.text.toString())
                && !TextUtils.isEmpty(root!!.et_about.text.toString())
                && !TextUtils.isEmpty(root!!.et_phone.text.toString())
            ) {
                val query: Query =
                    FirebaseDatabase.getInstance().reference.child("Users").orderByChild("username")
                        .equalTo(root!!.et_username.text.toString())
                query.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(@NonNull dataSnapshot: DataSnapshot) {
                        if (dataSnapshot.childrenCount > 0 && !dataSnapshot.hasChild(fuser!!.uid)) {
                            context?.let {
                                Toast.makeText(
                                    it, "Имя пользователя занято.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } else {
                            register()
                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        context?.let {
                            Toast.makeText(
                                it, "Reading data failed",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                })
            }
        }
        return root
    }

    private fun register() {
        hide()
        val hashMap: HashMap<String, String> = HashMap()
        hashMap["id"] = user.id!!
        hashMap["username"] = root!!.et_username.text.toString()
        hashMap["name"] = root!!.et_name.text.toString()
        hashMap["birthdate"] = root!!.et_birthdate.text.toString()
        if (root!!.sw_sex.isChecked)
            hashMap["sex"] = "Ж"
        else
            hashMap["sex"] = "М"
        hashMap["city"] = root!!.et_city.text.toString()
        hashMap["about"] = root!!.et_about.text.toString()
        hashMap["email"] = user.email!!
        hashMap["phone"] = root!!.et_phone.text.toString()
        hashMap["imageURL"] = user.imageURL!!


        dbreference.setValue(hashMap).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                changeFragment(ProfileFragment())
                show()
            }
        }
    }

    override fun changeFragment(fragment: Fragment) {
        val fragmentTransaction = activity!!.supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.container, fragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }

    override fun hide() {
        root!!.linearLayout.visibility = CoordinatorLayout.INVISIBLE
        root!!.progressBar.visibility = CoordinatorLayout.VISIBLE
    }

    override fun show() {
        root!!.linearLayout.visibility = CoordinatorLayout.VISIBLE
        root!!.progressBar.visibility = CoordinatorLayout.INVISIBLE
    }
}
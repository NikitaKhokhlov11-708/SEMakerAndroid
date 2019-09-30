package com.example.semakerandroid.Presenters

import android.os.Bundle
import androidx.annotation.NonNull
import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.example.semakerandroid.DetailsView
import com.example.semakerandroid.Models.Event
import com.example.semakerandroid.Models.User
import com.example.semakerandroid.UI.EditeventFragment
import com.example.semakerandroid.UI.GetApplicationsFragment
import com.example.semakerandroid.UI.OrganizationFragment
import com.example.semakerandroid.UI.TakepartFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import java.util.*

@InjectViewState
class DetailsPresenter : MvpPresenter<DetailsView>() {

    val fuser: FirebaseUser? = FirebaseAuth.getInstance().currentUser
    val dbreference: DatabaseReference = FirebaseDatabase.getInstance().getReference("Events")

    fun cancelApplication(event: Event) {
        if (fuser != null) {
            FirebaseDatabase.getInstance().getReference("Applications").child(fuser.uid)
                .child(event.id!!)
                .setValue(null).addOnCompleteListener {
                }

            FirebaseDatabase.getInstance().getReference("Events").child(event.id!!)
                .child("Applications").child(fuser.uid)
                .setValue(null).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        viewState.addApps()

                        viewState.openFragment(TakepartFragment())
                    }
                }
        }
    }

    fun apply(event: Event) {
        var user: User
        val hashMap: HashMap<String, String> = HashMap()
        if (fuser != null) {
            val userreference =
                FirebaseDatabase.getInstance().getReference("Users").child(fuser.uid)
            userreference.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(@NonNull dataSnapshot: DataSnapshot) {
                    hashMap["id"] = event.id!!
                    hashMap["aid"] = event.aid!!
                    hashMap["name"] = event.name!!
                    hashMap["description"] = event.description!!
                    hashMap["city"] = event.city!!
                    hashMap["sid"] = event.sid!!
                    hashMap["time"] = event.time!!
                    hashMap["places"] = event.places!!
                    hashMap["imageURL"] = event.imageURL!!
                    FirebaseDatabase.getInstance().getReference("Applications").child(fuser.uid)
                        .child(
                            event.id!!
                        )
                        .setValue(hashMap).addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                viewState.substractApps()

                                viewState.openFragment(TakepartFragment())
                            }
                        }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                }
            })

            userreference.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(@NonNull dataSnapshot: DataSnapshot) {
                    user = dataSnapshot.getValue(User::class.java)!!

                    val hashMap: HashMap<String, String> = HashMap()
                    hashMap["id"] = user.id!!
                    hashMap["username"] = user.username!!
                    hashMap["name"] = user.name!!
                    hashMap["birthdate"] = user.birthdate!!
                    hashMap["sex"] = user.sex!!
                    hashMap["city"] = user.city!!
                    hashMap["about"] = user.about!!
                    hashMap["email"] = user.email!!
                    hashMap["phone"] = user.phone!!
                    hashMap["imageURL"] = user.imageURL!!


                    FirebaseDatabase.getInstance().getReference("Events").child(event.id!!)
                        .child("Applications").child(fuser.uid)
                        .setValue(hashMap).addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                viewState.openFragment(TakepartFragment())
                            }
                        }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                }
            })
        }
    }

    fun edit(event: Event) {
        val editeventFragment = EditeventFragment()
        val arguments = Bundle()
        arguments.putSerializable("event", event)
        editeventFragment.arguments = arguments
        viewState.openFragment(editeventFragment)
    }

    fun delete(event: Event) {
        dbreference.child(event.id!!).setValue(null)
        viewState.openFragment(OrganizationFragment())
    }

    fun getApplications(event: Event) {
        val getApplicationsFragment = GetApplicationsFragment()
        val arguments = Bundle()
        arguments.putSerializable("event", event)
        getApplicationsFragment.arguments = arguments
        viewState.openFragment(getApplicationsFragment)
    }
}
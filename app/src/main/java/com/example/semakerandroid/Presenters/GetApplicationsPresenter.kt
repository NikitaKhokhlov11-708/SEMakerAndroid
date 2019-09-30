package com.example.semakerandroid.Presenters

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.example.semakerandroid.GetApplicationsView
import com.example.semakerandroid.Models.Event
import com.example.semakerandroid.Models.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

@InjectViewState
class GetApplicationsPresenter : MvpPresenter<GetApplicationsView>() {

    fun getAllUsers(event: Event) {
        viewState.hide()
        val rootRef = FirebaseDatabase.getInstance().reference
        rootRef.child("Events").child(event.id!!).child("Applications")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val listUser = ArrayList<User>()

                    dataSnapshot.children.forEach {
                        val userModel = it.getValue(User::class.java)
                        listUser.add(userModel!!)
                    }

                    viewState.initializeRecycler(listUser)
                    viewState.show()
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    throw databaseError.toException()
                }
            })
    }
}
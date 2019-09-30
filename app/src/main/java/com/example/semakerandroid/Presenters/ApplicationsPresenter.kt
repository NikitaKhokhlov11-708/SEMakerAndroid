package com.example.semakerandroid.Presenters

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.example.semakerandroid.ApplicationsView
import com.example.semakerandroid.Models.Event
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

@InjectViewState
class ApplicationsPresenter : MvpPresenter<ApplicationsView>() {

    val fuser: FirebaseUser? = FirebaseAuth.getInstance().currentUser

    fun getAllEvents() {
        viewState.hide()
        val rootRef = FirebaseDatabase.getInstance().reference
        rootRef.child("Applications").child(fuser!!.uid)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val listEvent = ArrayList<Event>()

                    for (valueRes in dataSnapshot.children) {
                        val eventModel = valueRes.getValue(Event::class.java)
                        listEvent.add(eventModel!!)
                    }
                    viewState.initializeRecycler(listEvent)
                    viewState.show()
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    throw databaseError.toException()
                }
            })
    }
}
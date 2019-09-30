package com.example.semakerandroid.Presenters

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.example.semakerandroid.FeventsView
import com.example.semakerandroid.Models.Event
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

@InjectViewState
class FeventsPresenter : MvpPresenter<FeventsView>() {

    var listEvents = ArrayList<Event>()

    fun getAllEvents() {
        viewState.hide()
        val valueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (valueRes in dataSnapshot.children) {
                    val eventModel = valueRes.getValue(Event::class.java)
                    listEvents.add(eventModel!!)
                }

                viewState.initRecycler()
                viewState.show()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                throw databaseError.toException()
            }
        }

        val rootRef = FirebaseDatabase.getInstance().reference
        rootRef.child("Events").addListenerForSingleValueEvent(valueEventListener)
    }
}
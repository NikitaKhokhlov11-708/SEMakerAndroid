package com.example.semakerandroid.Presenters

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.example.semakerandroid.EditEventView
import com.example.semakerandroid.Models.Sport
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.*

@InjectViewState
class EditEventPresenter : MvpPresenter<EditEventView>() {

    val list = ArrayList<String>()

    fun getEventData() {
        val valueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (ds in dataSnapshot.children) {
                    val sportModel = ds.getValue(Sport::class.java)
                    list.add(sportModel!!.name!!)
                }

                viewState.initSpinner()
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        }
        FirebaseDatabase.getInstance().getReference("Sports")
            .addListenerForSingleValueEvent(valueEventListener)
    }
}
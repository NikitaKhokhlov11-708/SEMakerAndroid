package com.example.semakerandroid.Presenters

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.example.semakerandroid.AddEventView
import com.example.semakerandroid.Models.Sport
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

@InjectViewState
class AddEventPresenter : MvpPresenter<AddEventView>() {
    val list = ArrayList<String>()

    fun getSports() {
        val valueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (ds in dataSnapshot.children) {
                    val sportModel = ds.getValue(Sport::class.java)
                    list.add(sportModel!!.name!!)
                }

                viewState.initializeSpinner()
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        }

        FirebaseDatabase.getInstance().getReference("Sports")
            .addListenerForSingleValueEvent(valueEventListener)
    }
}
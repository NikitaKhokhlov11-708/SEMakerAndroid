package com.example.semakerandroid.Models

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.semakerandroid.R
import com.example.semakerandroid.UI.DetailsFragment
import com.example.semakerandroid.inflate
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.recyclerview_item_row.view.*


class RecyclerAdapter : RecyclerView.Adapter<RecyclerAdapter.EventHolder>() {
    var events = ArrayList<Event>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventHolder {
        val inflatedView = parent.inflate(R.layout.recyclerview_item_row, false)
        return EventHolder(inflatedView)
    }

    override fun getItemCount() = events.size

    override fun onBindViewHolder(holder: EventHolder, position: Int) {
        val itemEvent = events[position]
        holder.bindEvent(itemEvent)
    }

    class EventHolder(v: View) : RecyclerView.ViewHolder(v), View.OnClickListener {
        private var view: View = v
        private var event: Event? = null
        val fuser: FirebaseUser? = FirebaseAuth.getInstance().currentUser

        init {
            v.setOnClickListener(this)
        }

        override fun onClick(v: View) {
            val detailsFragment = DetailsFragment()
            val arguments = Bundle()
            arguments.putSerializable("event", event)

            val rootRef = FirebaseDatabase.getInstance().reference
            rootRef.child("Users").child(fuser!!.uid).child("Applications")
                .addListenerForSingleValueEvent(object :
                    ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val user = dataSnapshot.getValue(User::class.java)
                        arguments.putSerializable("user", user)
                        detailsFragment.arguments = arguments
                        val activity = view.context as AppCompatActivity
                        val fragmentTransaction = activity.supportFragmentManager.beginTransaction()
                        fragmentTransaction.replace(R.id.container, detailsFragment)
                        fragmentTransaction.addToBackStack(null)
                        fragmentTransaction.commit()
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        throw databaseError.toException()
                    }
                })
        }

        companion object {
            private val EVENT_KEY = "EVENT"
        }

        fun bindEvent(event: Event) {
            this.event = event
            Glide.with(view.context).load(event.imageURL).into(view.event_image)
            view.name.text = event.name
            view.city.text = "г." + event.city
            view.date.text = "Дата проведения: " + event.time
        }
    }
}

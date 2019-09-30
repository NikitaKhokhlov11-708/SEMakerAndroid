package com.example.semakerandroid.Models

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.semakerandroid.R
import com.example.semakerandroid.UI.UserDetailsFragment
import com.example.semakerandroid.inflate
import kotlinx.android.synthetic.main.recyclerview_item_row.view.*


class UsersRecyclerAdapter : RecyclerView.Adapter<UsersRecyclerAdapter.UsersHolder>() {
    var users = ArrayList<User>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsersHolder {
        val inflatedView = parent.inflate(R.layout.recyclerview_item_row, false)
        return UsersHolder(inflatedView)
    }

    override fun getItemCount() = users.size

    override fun onBindViewHolder(holder: UsersHolder, position: Int) {
        val itemUser = users[position]
        holder.bindUser(itemUser)
    }

    class UsersHolder(v: View) : RecyclerView.ViewHolder(v), View.OnClickListener {
        private var view: View = v
        private var user: User? = null

        init {
            v.setOnClickListener(this)
        }

        override fun onClick(v: View) {
            val userDetailsFragment = UserDetailsFragment()
            val arguments = Bundle()
            arguments.putSerializable("user", user)
            userDetailsFragment.arguments = arguments
            val activity = view.context as AppCompatActivity
            val fragmentTransaction = activity.supportFragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.container, userDetailsFragment)
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        }

        companion object {
            private val USER_KEY = "USER"
        }

        fun bindUser(user: User) {
            this.user = user
            Glide.with(view.context).load(user.imageURL).into(view.event_image)
            view.name.text = user.name
            view.city.text = "Пол: " + user.sex
            view.date.text = "Дата рождения: " + user.birthdate
        }
    }
}

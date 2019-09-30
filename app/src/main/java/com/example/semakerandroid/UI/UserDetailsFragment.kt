package com.example.semakerandroid.UI

import android.os.Bundle
import android.text.TextUtils.isEmpty
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.arellomobile.mvp.MvpAppCompatFragment
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.arellomobile.mvp.presenter.ProvidePresenterTag
import com.bumptech.glide.Glide
import com.example.semakerandroid.Models.User
import com.example.semakerandroid.Presenters.UserDetailsPresenter
import com.example.semakerandroid.R
import com.example.semakerandroid.UserDetailsView
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_profile.view.*

class UserDetailsFragment : MvpAppCompatFragment(), UserDetailsView {

    @InjectPresenter
    lateinit var userDetailsPresenterPresenter: UserDetailsPresenter

    @ProvidePresenterTag(presenterClass = UserDetailsPresenter::class)
    fun provideUserDetailsPresenterTag(): String = "Hello"

    @ProvidePresenter
    fun provideUserDetailsPresenter() = UserDetailsPresenter()

    private var root: View? = null
    val dbreference: DatabaseReference = FirebaseDatabase.getInstance().getReference("Users")
    lateinit var user: User

    companion object {
        fun newInstance(): ProfileFragment =
            ProfileFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.fragment_userdetails, null)

        hide()

        val bundle = arguments

        user = bundle!!.getSerializable("user") as User

        dbreference.child(user.id!!).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(@NonNull dataSnapshot: DataSnapshot) {
                val user = dataSnapshot.getValue(User::class.java)
                root!!.tv_username.text = user?.username
                if (!isEmpty(user?.name))
                    root!!.userfio.text = user?.name
                if (!isEmpty(user?.birthdate))
                    root!!.userbirthdate.text = user?.birthdate
                if (!isEmpty(user?.sex))
                    root!!.usersex.text = user?.sex
                if (!isEmpty(user?.city))
                    root!!.usercity.text = user?.city
                if (!isEmpty(user?.about))
                    root!!.userabout.text = user?.about
                root!!.useremail.text = user?.email
                if (!isEmpty(user?.phone))
                    root!!.userphone.text = user?.phone
                if (user!!.imageURL == "default") {
                    root!!.profile_image.setImageResource(R.drawable.ic_launcher_background)
                } else {
                    context?.let {
                        Glide.with(it).load(user.imageURL).into(root!!.profile_image)
                    }
                }
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

        return root
    }

    override fun hide() {
        root!!.linearLayout2.visibility = CoordinatorLayout.GONE
        root!!.progressBar.visibility = CoordinatorLayout.VISIBLE
    }

    override fun show() {
        root!!.linearLayout2.visibility = CoordinatorLayout.VISIBLE
        root!!.progressBar.visibility = CoordinatorLayout.INVISIBLE
    }
}
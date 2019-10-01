package com.example.semakerandroid.UI

import android.os.Bundle
import android.text.TextUtils.isEmpty
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.Fragment
import com.arellomobile.mvp.MvpAppCompatFragment
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.arellomobile.mvp.presenter.ProvidePresenterTag
import com.bumptech.glide.Glide
import com.example.semakerandroid.DetailsView
import com.example.semakerandroid.Models.Event
import com.example.semakerandroid.Models.Sport
import com.example.semakerandroid.Models.User
import com.example.semakerandroid.Presenters.DetailsPresenter
import com.example.semakerandroid.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_details.view.*
import java.util.*
import kotlin.collections.ArrayList


class DetailsFragment : MvpAppCompatFragment(), DetailsView {

    @InjectPresenter
    lateinit var detailsPresenter: DetailsPresenter

    @ProvidePresenterTag(presenterClass = DetailsPresenter::class)
    fun provideDetailsPresenterTag(): String = "Hello"

    @ProvidePresenter
    fun provideDetailsPresenter() = DetailsPresenter()

    val fuser: FirebaseUser? = FirebaseAuth.getInstance().currentUser

    private var root: View? = null
    val list = ArrayList<String>()
    var sportName: String = "0"
    lateinit var event: Event
    lateinit var user: User


    companion object {
        fun newInstance(): DetailsFragment =
            DetailsFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.fragment_details, null)

        hide()

        val bundle = arguments

        event = bundle!!.getSerializable("event") as Event
        user = bundle!!.getSerializable("user") as User

        context?.let {
            Glide.with(it).load(event.imageURL).into(root!!.event_image)
        }

        val valueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (ds in dataSnapshot.children) {
                    val sportModel = ds.getValue(Sport::class.java)
                    list.add(sportModel!!.name!!)
                }

                sportName = list[event.sid!!.toInt()]
                root!!.evntsport.text = sportName
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        }
        FirebaseDatabase.getInstance().getReference("Sports")
            .addListenerForSingleValueEvent(valueEventListener)

        root!!.tv_name.text = event.name
        root!!.evntdescr.text = event.description
        root!!.evntcity.text = event.city
        root!!.evnttime.text = event.time
        val rootRef = FirebaseDatabase.getInstance().reference
        rootRef.child("Events").child(event.id!!)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    root!!.evntplaces.text = dataSnapshot.getValue(Event::class.java)!!.places
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    throw databaseError.toException()
                }
            })

        root!!.delete.setOnClickListener {
            detailsPresenter.delete(event)
        }

        root!!.edit.setOnClickListener {
            detailsPresenter.edit(event)
        }

        root!!.apply.setOnClickListener {
            if (!isEmpty(user.name) && !isEmpty(user.birthdate) && !isEmpty(user.sex)
                && !isEmpty(user.city) && !isEmpty(user.about) && !isEmpty(user.phone) && !isEmpty(
                    user.email
                )
            )
                detailsPresenter.apply(event)
            else context?.let {
                Toast.makeText(
                    it, "Необходимо полностью заполнить профиль",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        root!!.cancel.setOnClickListener {
            detailsPresenter.cancelApplication(event)
        }

        root!!.getApplications.setOnClickListener {
            detailsPresenter.getApplications(event)
        }

        show()

        return root
    }

    override fun openFragment(fragment: Fragment) {
        val fragmentTransaction = activity!!.supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.container, fragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }

    override fun hide() {
        root!!.linearLayout3.visibility = CoordinatorLayout.INVISIBLE
        root!!.progressBar.visibility = CoordinatorLayout.VISIBLE
    }

    override fun show() {
        root!!.linearLayout3.visibility = CoordinatorLayout.VISIBLE
        root!!.progressBar.visibility = CoordinatorLayout.INVISIBLE
        if (event.aid == fuser!!.uid) {
            root!!.getApplications.visibility = View.VISIBLE
            root!!.edit.visibility = View.VISIBLE
            root!!.delete.visibility = View.VISIBLE
        } else {
            val rootRef = FirebaseDatabase.getInstance().getReference("Events").child(event.id!!)
                .child("Applications")
            rootRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (!snapshot.hasChild(fuser.uid)) {
                        if (event.places!!.toInt() > 0)
                            root!!.apply.visibility = View.VISIBLE
                    } else root!!.cancel.visibility = View.VISIBLE
                }

                override fun onCancelled(p0: DatabaseError) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }
            })
        }
    }

    override fun substractApps() {
        val map = HashMap<String, Any>()
        map["places"] = (root!!.evntplaces!!.text.toString().toInt() - 1).toString()
        FirebaseDatabase.getInstance().getReference("Events").child(event.id!!).updateChildren(map)
    }

    override fun addApps() {
        val map = HashMap<String, Any>()
        map["places"] = (root!!.evntplaces!!.text.toString().toInt() + 1).toString()
        FirebaseDatabase.getInstance().getReference("Events").child(event.id!!).updateChildren(map)
    }
}
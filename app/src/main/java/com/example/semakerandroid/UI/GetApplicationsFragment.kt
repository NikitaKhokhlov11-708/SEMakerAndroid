package com.example.semakerandroid.UI

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.arellomobile.mvp.MvpAppCompatFragment
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.arellomobile.mvp.presenter.ProvidePresenterTag
import com.example.semakerandroid.GetApplicationsView
import com.example.semakerandroid.Models.Event
import com.example.semakerandroid.Models.User
import com.example.semakerandroid.Models.UsersRecyclerAdapter
import com.example.semakerandroid.Presenters.GetApplicationsPresenter
import com.example.semakerandroid.R
import kotlinx.android.synthetic.main.fragment_active.view.*

class GetApplicationsFragment : MvpAppCompatFragment(), GetApplicationsView {

    @InjectPresenter
    lateinit var getApplicationsPresenter: GetApplicationsPresenter

    @ProvidePresenterTag(presenterClass = GetApplicationsPresenter::class)
    fun provideGetApplicationsPresenterTag(): String = "Hello"

    @ProvidePresenter
    fun provideGetApplicationsPresenter() = GetApplicationsPresenter()

    private var root: View? = null
    private lateinit var adapter: UsersRecyclerAdapter
    lateinit var event: Event


    companion object {
        fun newInstance(): ActiveFragment =
            ActiveFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.fragment_active, null)
        val bundle = arguments
        event = bundle!!.getSerializable("event") as Event

        getApplicationsPresenter.getAllUsers(event)

        return root
    }

    override fun initializeRecycler(listEvents: ArrayList<User>) {
        adapter = UsersRecyclerAdapter()
        adapter.users = listEvents
        root!!.recyclerView.layoutManager = LinearLayoutManager(context)
        root!!.recyclerView.adapter = adapter
        val dividerItemDecoration = DividerItemDecoration(
            context,
            LinearLayoutManager(context).orientation
        )
        root!!.recyclerView.addItemDecoration(dividerItemDecoration)
    }

    override fun hide() {
        root!!.recyclerView.visibility = CoordinatorLayout.INVISIBLE
        root!!.progressBar.visibility = CoordinatorLayout.VISIBLE
    }

    override fun show() {
        root!!.recyclerView.visibility = CoordinatorLayout.VISIBLE
        root!!.progressBar.visibility = CoordinatorLayout.INVISIBLE
    }
}
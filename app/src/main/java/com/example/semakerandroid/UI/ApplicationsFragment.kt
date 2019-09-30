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
import com.example.semakerandroid.ApplicationsView
import com.example.semakerandroid.Models.Event
import com.example.semakerandroid.Models.RecyclerAdapter
import com.example.semakerandroid.Presenters.ApplicationsPresenter
import com.example.semakerandroid.R
import kotlinx.android.synthetic.main.fragment_applications.view.*


class ApplicationsFragment : MvpAppCompatFragment(),
    ApplicationsView {

    @InjectPresenter
    lateinit var applicationsPresenter: ApplicationsPresenter

    @ProvidePresenterTag(presenterClass = ApplicationsPresenter::class)
    fun provideApplicationsPresenterTag(): String = "Hello"

    @ProvidePresenter
    fun provideApplicationsPresenter() = ApplicationsPresenter()

    private var root: View? = null
    private var adapter: RecyclerAdapter =
        RecyclerAdapter()


    companion object {
        fun newInstance(): ApplicationsFragment =
            ApplicationsFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.fragment_applications, null)
        applicationsPresenter.getAllEvents()

        return root
    }

    override fun initializeRecycler(listEvent: ArrayList<Event>) {
        adapter.events = listEvent
        root!!.recyclerView.layoutManager = LinearLayoutManager(context)
        root!!.recyclerView.adapter = adapter
        val dividerItemDecoration = DividerItemDecoration(
            context,
            LinearLayoutManager(activity).orientation
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
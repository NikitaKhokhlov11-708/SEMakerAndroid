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
import com.example.semakerandroid.FeventsView
import com.example.semakerandroid.Models.RecyclerAdapter
import com.example.semakerandroid.Presenters.FeventsPresenter
import com.example.semakerandroid.R
import kotlinx.android.synthetic.main.fragment_fevents.view.*


class FeventsFragment : MvpAppCompatFragment(), FeventsView {

    @InjectPresenter
    lateinit var feventsPresenter: FeventsPresenter

    @ProvidePresenterTag(presenterClass = FeventsPresenter::class)
    fun provideFeventsPresenterTag(): String = "Hello"

    @ProvidePresenter
    fun provideFeventsPresenter() = FeventsPresenter()

    private var root: View? = null
    private lateinit var adapter: RecyclerAdapter


    companion object {
        fun newInstance(): FeventsFragment =
            FeventsFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.fragment_fevents, null)

        feventsPresenter.getAllEvents()

        return root
    }

    override fun initRecycler() {
        adapter = RecyclerAdapter()
        adapter.events = feventsPresenter.listEvents
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

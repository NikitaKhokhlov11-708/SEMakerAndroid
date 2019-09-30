package com.example.semakerandroid.UI

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.arellomobile.mvp.MvpAppCompatFragment
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.arellomobile.mvp.presenter.ProvidePresenterTag
import com.example.semakerandroid.Models.TakepartTabsAdapter
import com.example.semakerandroid.Presenters.TakepartPresenter
import com.example.semakerandroid.R
import com.example.semakerandroid.TakepartView
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.fragment_takepart.view.*


class TakepartFragment : MvpAppCompatFragment(), TakepartView {

    @InjectPresenter
    lateinit var takepartPresenter: TakepartPresenter

    @ProvidePresenterTag(presenterClass = TakepartPresenter::class)
    fun provideTakepartPresenterTag(): String = "Hello"

    @ProvidePresenter
    fun provideTakepartPresenter() = TakepartPresenter()

    private var root: View? = null

    companion object {
        fun newInstance(): TakepartFragment =
            TakepartFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.fragment_takepart, null)

        root!!.tab_layout.addTab(root!!.tab_layout.newTab().setText("События"))
        root!!.tab_layout.addTab(root!!.tab_layout.newTab().setText("Заявки"))
        root!!.tab_layout.tabGravity = TabLayout.GRAVITY_FILL
        val tabsAdapter = TakepartTabsAdapter(
            activity!!.supportFragmentManager,
            root!!.tab_layout.tabCount
        )
        root!!.view_pager.adapter = tabsAdapter
        root!!.view_pager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(root!!.tab_layout))
        root!!.tab_layout.setOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                root!!.view_pager.currentItem = tab.position
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {

            }

            override fun onTabReselected(tab: TabLayout.Tab) {

            }
        })

        return root
    }
}

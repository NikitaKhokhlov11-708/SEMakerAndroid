package com.example.semakerandroid.UI

import android.os.Bundle
import android.view.*
import com.arellomobile.mvp.MvpAppCompatFragment
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.arellomobile.mvp.presenter.ProvidePresenterTag
import com.example.semakerandroid.Models.OrganizationTabsAdapter
import com.example.semakerandroid.OrganizationView
import com.example.semakerandroid.Presenters.OrganizationPresenter
import com.example.semakerandroid.R
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.fragment_takepart.view.*


class OrganizationFragment : MvpAppCompatFragment(), OrganizationView {

    @InjectPresenter
    lateinit var organizationPresenter: OrganizationPresenter

    @ProvidePresenterTag(presenterClass = OrganizationPresenter::class)
    fun provideOrganizationPresenterTag(): String = "Hello"

    @ProvidePresenter
    fun provideOrganizationPresenter() = OrganizationPresenter()

    private var root: View? = null

    companion object {
        fun newInstance(): OrganizationFragment =
            OrganizationFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.fragment_organization, null)

        root!!.tab_layout.addTab(root!!.tab_layout.newTab().setText("Активные соревнования"))
        root!!.tab_layout.addTab(root!!.tab_layout.newTab().setText("Прошедшие соревнования"))
        root!!.tab_layout.tabGravity = TabLayout.GRAVITY_FILL
        val tabsAdapter = OrganizationTabsAdapter(
            activity!!.supportFragmentManager,
            root!!.tab_layout.tabCount
        )
        root!!.view_pager.adapter = tabsAdapter
        root!!.view_pager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(root!!.tab_layout))
        root!!.tab_layout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.fragment_organize, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        when (item.itemId) {
            R.id.frag1_item -> {
                val addeventFragment = AddeventFragment()
                val fragmentTransaction = activity!!.supportFragmentManager.beginTransaction()
                fragmentTransaction.replace(R.id.container, addeventFragment)
                fragmentTransaction.addToBackStack(null)
                fragmentTransaction.commit()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }
}

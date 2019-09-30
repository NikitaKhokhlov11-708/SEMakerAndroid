package com.example.semakerandroid.UI

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.arellomobile.mvp.MvpAppCompatActivity
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.arellomobile.mvp.presenter.ProvidePresenterTag
import com.example.semakerandroid.MainView
import com.example.semakerandroid.Presenters.MainPresenter
import com.example.semakerandroid.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : MvpAppCompatActivity(), MainView {

    @InjectPresenter
    lateinit var mainPresenter: MainPresenter

    @ProvidePresenterTag(presenterClass = MainPresenter::class)
    fun provideMainPresenterTag(): String = "Hello"

    @ProvidePresenter
    fun provideMainPresenter() = MainPresenter()

    private val mOnNavigationItemSelectedListener =
        BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.action_awards -> {
                    title = "Награды"
                    val awardsFragment =
                        AwardsFragment.newInstance()
                    openFragment(awardsFragment)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.action_organize -> {
                    title = "Организация"
                    val organizationFragment =
                        OrganizationFragment.newInstance()
                    openFragment(organizationFragment)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.action_takepart -> {
                    title = "Участие"
                    val takepartFragment =
                        TakepartFragment.newInstance()
                    openFragment(takepartFragment)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.action_teams -> {
                    title = "Команды"
                    val teamsFragment = TeamsFragment.newInstance()
                    openFragment(teamsFragment)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.action_profile -> {
                    title = "Профиль"
                    val profileFragment =
                        ProfileFragment.newInstance()
                    openFragment(profileFragment)
                    return@OnNavigationItemSelectedListener true
                }
            }
            false
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        title = "Участие"
        val takepartFragment = TakepartFragment.newInstance()
        openFragment(takepartFragment)
        bottom_navigation.selectedItemId = R.id.action_takepart

        bottom_navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
    }

    override fun openFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

}

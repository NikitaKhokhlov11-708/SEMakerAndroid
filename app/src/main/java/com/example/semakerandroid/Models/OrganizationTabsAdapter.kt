package com.example.semakerandroid.Models

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.example.semakerandroid.UI.ActiveFragment
import com.example.semakerandroid.UI.PastFragment

class OrganizationTabsAdapter(fm: FragmentManager, private var mNumOfTabs: Int) :
    FragmentStatePagerAdapter(fm) {
    override fun getCount(): Int {
        return mNumOfTabs
    }

    override fun getItem(position: Int): Fragment? {
        return when (position) {
            0 -> {
                ActiveFragment()
            }
            1 -> {
                PastFragment()
            }
            else -> null
        }
    }
}
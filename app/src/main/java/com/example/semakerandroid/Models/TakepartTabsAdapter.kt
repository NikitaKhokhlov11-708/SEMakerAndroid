package com.example.semakerandroid.Models

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.example.semakerandroid.UI.ApplicationsFragment
import com.example.semakerandroid.UI.FeventsFragment


class TakepartTabsAdapter(fm: FragmentManager, private var mNumOfTabs: Int) :
    FragmentStatePagerAdapter(fm) {
    override fun getCount(): Int {
        return mNumOfTabs
    }

    override fun getItem(position: Int): Fragment? {
        return when (position) {
            0 -> {
                FeventsFragment()
            }
            1 -> {
                ApplicationsFragment()
            }
            else -> null
        }
    }
}
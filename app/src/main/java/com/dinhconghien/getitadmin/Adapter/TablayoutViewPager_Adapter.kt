package com.dinhconghien.getitadmin.Adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import java.util.ArrayList

class TablayoutViewPager_Adapter(fm: FragmentManager) :
    FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    var arrayListFragment = ArrayList<Fragment>()
    var arrayListTitle = ArrayList<String>()

    fun addFragment(fragment: Fragment, title: String) {
        arrayListFragment.add(fragment)
        arrayListTitle.add(title)
    }

    override fun getItem(position: Int): Fragment {
        return arrayListFragment[position]
    }

    override fun getCount(): Int {
        return arrayListTitle.size
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return arrayListTitle[position]
    }
}
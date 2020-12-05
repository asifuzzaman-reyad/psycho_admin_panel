package com.reyad.psycheadminpanel.main.teacher.ui.main

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import java.util.ArrayList

class TeacherPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

    private val fragmentList : MutableList<Fragment> = ArrayList()
    private val titleList : MutableList<String> = ArrayList()

    override fun getItem(position: Int): Fragment {

        return fragmentList[position]
    }

    override fun getPageTitle(position: Int): CharSequence {
        return titleList[position]
    }

    override fun getCount(): Int {
        // Show 2 total pages.
        return titleList.size
    }

    fun addFragment(fragment : Fragment , title:String){
        fragmentList.add(fragment)
        titleList.add(title)
    }
}
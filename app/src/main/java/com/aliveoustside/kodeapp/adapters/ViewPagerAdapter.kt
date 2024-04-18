package com.aliveoustside.kodeapp.adapters

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.aliveoustside.kodeapp.ui.fragments.ListFragment
import com.aliveoustside.kodeapp.ui.fragments.MainFragment
import com.aliveoustside.kodeapp.utility.Util

class ViewPagerAdapter(fragment: MainFragment): FragmentStateAdapter(fragment) {
    override fun getItemCount() = 13

    override fun createFragment(position: Int): Fragment {
        val fragment  = ListFragment()
        fragment.arguments = Bundle().apply {
            when(position){
                0  -> putSerializable("dep",Util.Dep.ALL          )
                1  -> putSerializable("dep",Util.Dep.DESIGN       )
                2  -> putSerializable("dep",Util.Dep.ANALYTICS    )
                3  -> putSerializable("dep",Util.Dep.MANAGEMENT   )
                4  -> putSerializable("dep",Util.Dep.BACK_OFFICE  )
                5  -> putSerializable("dep",Util.Dep.BACKEND      )
                6  -> putSerializable("dep",Util.Dep.PR           )
                7  -> putSerializable("dep",Util.Dep.HR           )
                8  -> putSerializable("dep",Util.Dep.FRONTEND     )
                9  -> putSerializable("dep",Util.Dep.SUPPORT      )
                10 -> putSerializable("dep",Util.Dep.QA           )
                11 -> putSerializable("dep",Util.Dep.ANDROID      )
                12 -> putSerializable("dep",Util.Dep.IOS          )
            }
        }
        return fragment
    }
}
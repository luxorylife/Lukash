package com.example.lukash.pagerView

import android.util.Log
import androidx.fragment.app.*
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.lukash.MainActivity
import com.example.lukash.entityes.GifPrototype

class SampleFragmentPagerAdapter(fa: MainActivity) : FragmentStateAdapter(fa) {
    val PAGE_COUNT = 3

    override fun getItemCount(): Int {
        return PAGE_COUNT
    }

    override fun createFragment(position: Int): Fragment {
        Log.d("TEST", "tab position is $position")
        var section = when(position){
            0 -> {
                "latest"
            }
            1 -> {
                "top"
            }
            2 -> {
                "hot"
            }
            else -> {
                "latest"
            }
        }
        return PageFragment.newInstance(section)
    }

}
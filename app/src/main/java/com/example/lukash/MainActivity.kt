package com.example.lukash

import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.viewpager2.widget.ViewPager2
import com.example.lukash.databinding.ActivityMainBinding
import com.example.lukash.entityes.GifPrototype
import com.example.lukash.pagerView.SampleFragmentPagerAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator


class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    companion object {
        var gifLatestList = mutableListOf<GifPrototype>()
        var gifBestList = mutableListOf<GifPrototype>()
        var gifHotList = mutableListOf<GifPrototype>()

        var curLatestPage = 0
        var curlatestGif = 0

        var curBestPage = 0
        var curBestGif = 0

        var curHotPage = 0
        var curHotGif = 0

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val viewPager: ViewPager2 = binding.viewpager
        viewPager.adapter = SampleFragmentPagerAdapter(this@MainActivity)

        // Передаём ViewPager в TabLayout
        val tabLayout: TabLayout = binding.slidingTabs

        TabLayoutMediator(tabLayout, viewPager,
            TabLayoutMediator.TabConfigurationStrategy{
                    tab, position ->
                when(position){
                    0 -> {
                        tab.text = "Последние"
                    }
                    1 -> {
                        tab.text = "Лучшие"
                    }
                    2 -> {
                        tab.text = "Горячие"
                    }
                }
            }).attach()


    }

}
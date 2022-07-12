package com.rsmnm.Fragments.passenger

import android.os.Bundle
import com.rsmnm.Adapters.ViewPagerAdapter
import com.rsmnm.BaseClasses.BaseFragment
import com.rsmnm.R
import com.rsmnm.Views.TitleBar
import com.rsmnm.Views.ZoomOutPageTransformer
import kotlinx.android.synthetic.main.fragment_trips.*

/**
 * Created by saqib on 9/11/2018.
 */
class MyTripsFragment : BaseFragment() {

    lateinit var adapter: ViewPagerAdapter

    override fun getLayout(): Int = R.layout.fragment_trips

    override fun getTitleBar(titleBar: TitleBar) {
        titleBar.resetTitleBar().enableBack().setTitle("Your Trips")
    }

    override fun activityCreated(savedInstanceState: Bundle?) {
    }

    override fun inits() {

        adapter = ViewPagerAdapter(childFragmentManager)
        adapter.addFragment(TripListingFragment.newInstance("past"), "Past Trips")
        adapter.addFragment(TripListingFragment.newInstance("upcoming"), "Upcoming Trips")
        viewpager.setPageTransformer(true, ZoomOutPageTransformer())
        viewpager.setAdapter(adapter)
        tabLayout.setupWithViewPager(viewpager)

    }

    override fun setEvents() {

    }

}
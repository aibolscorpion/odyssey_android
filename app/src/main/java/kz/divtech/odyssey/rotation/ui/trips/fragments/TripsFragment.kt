package kz.divtech.odyssey.rotation.ui.trips.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayoutMediator
import kz.divtech.odyssey.rotation.R
import kz.divtech.odyssey.rotation.databinding.FragmentTripsBinding
import kz.divtech.odyssey.rotation.ui.trips.adapters.ViewPagerAdapter

class TripsFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle? ): View {
        val binding = FragmentTripsBinding.inflate(inflater)

        val adapter = ViewPagerAdapter(this)
        adapter.addFragment(ActiveTripsFragment(), getString(R.string.active_trips))
        adapter.addFragment(ArchiveTripsFragment(), getString(R.string.archive_trips))
        binding.viewPager.adapter = adapter

        TabLayoutMediator(binding.tabLayout, binding.viewPager){ tab, position ->
            tab.text = adapter.getPageTitle(position)
            binding.viewPager.setCurrentItem(tab.position, true)
        }.attach()

        return binding.root
    }
}
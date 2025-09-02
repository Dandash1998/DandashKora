package com.example.dandashkora

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.Group
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.gms.ads.MobileAds
import com.google.android.material.navigation.NavigationView
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.Tab

class MainActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var mainContentGroup: Group
    private lateinit var noInternetGroup: Group
    private lateinit var retryButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        MobileAds.initialize(this) {}

        // 1. Set up the Toolbar
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        // 2. Set up the Navigation Drawer
        drawerLayout = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.nav_view)

        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )

        // Remove the default gray tint so icons keep their own colors
        navigationView.itemIconTintList = null
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        // 3. Initialize Views for connection check
        mainContentGroup = findViewById(R.id.main_content_group)
        noInternetGroup = findViewById(R.id.no_internet_group)
        retryButton = findViewById(R.id.retry_button)

        retryButton.setOnClickListener {
            checkInternetAndLoadContent()
        }

        // 4. Perform the initial check
        checkInternetAndLoadContent()
    }

    private fun checkInternetAndLoadContent() {
        if (isNetworkAvailable()) {
            // Internet is available, show main content and hide error message
            mainContentGroup.visibility = View.VISIBLE
            noInternetGroup.visibility = View.GONE
            setupTabsAndFragment()
        } else {
            // No internet, hide main content and show error message
            mainContentGroup.visibility = View.GONE
            noInternetGroup.visibility = View.VISIBLE
        }
    }

    private fun setupTabsAndFragment() {
        val tabLayout = findViewById<TabLayout>(R.id.tabLayout)

        // Clear tabs to prevent duplicates on retry
        if (tabLayout.tabCount == 0) {
            tabLayout.addTab(tabLayout.newTab().setText("Yesterday"))
            tabLayout.addTab(tabLayout.newTab().setText("Today"))
            tabLayout.addTab(tabLayout.newTab().setText("Tomorrow"))
            tabLayout.addTab(tabLayout.newTab().setText("All Matches"))
        }

        // Select the 'Today' tab and load the fragment
        tabLayout.getTabAt(1)?.select()
        loadMatchFragment("Today")

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: Tab?) {
                when (tab?.position) {
                    0 -> loadMatchFragment("Yesterday")
                    1 -> loadMatchFragment("Today")
                    2 -> loadMatchFragment("Tomorrow")
                    3 -> loadMatchFragment("All Matches")
                }
            }

            override fun onTabUnselected(tab: Tab?) {}
            override fun onTabReselected(tab: Tab?) {}
        })
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false
        return when {
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
    }

    private fun loadMatchFragment(matchDay: String) {
        val fragment = MatchListFragment.newInstance(matchDay)
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainerView, fragment)
            .commit()
    }
}

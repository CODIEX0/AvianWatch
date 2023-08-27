package com.example.avianwatch

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.avianwatch.fragments.BirdFactsFragment
import com.example.avianwatch.fragments.GoBirdingFragment
import com.example.avianwatch.fragments.HomeFragment
import com.example.avianwatch.fragments.ObservationFragment
import com.example.avianwatch.fragments.ObservationListFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity(), OnCardClickListener{
    //private lateinit var auth: FirebaseAuth
    private lateinit var bottomNav: BottomNavigationView
    lateinit var drawerLayout: DrawerLayout
    lateinit var actionBarDrawerToggle: ActionBarDrawerToggle
    lateinit var navView: NavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragment_container) as NavHostFragment
        val navController = navHostFragment.navController
        bottomNav = findViewById(R.id.bottom_navigation)
        bottomNav.setupWithNavController(navController)

        // as soon as the application opens the first fragment should
        // be shown to the user in this case it is the Home Fragment
        val fragment = HomeFragment()
        replaceFragment(fragment)

        bottomNav.setOnNavigationItemSelectedListener { menuItem ->
            // By using switch we can easily get the
            // selected fragment by using the id
            lateinit var selectedFragment: Fragment

            when (menuItem.itemId) {

                R.id.nav_go_birding -> {
                    selectedFragment = GoBirdingFragment()
                }

                R.id.nav_identify_bird -> {
                    selectedFragment = ObservationFragment()
                }

                R.id.nav_home -> {
                    selectedFragment = HomeFragment()
                }

                R.id.nav_view_birds -> {
                    selectedFragment = ObservationListFragment()
                }

                R.id.nav_bird_facts -> {
                    selectedFragment = BirdFactsFragment()
                }

            }
            // replace the current fragment
            // to the selected fragment.
            replaceFragment(selectedFragment)

            true


        }
    }

    private fun replaceFragment(fragment : Fragment){

        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragment_container,fragment)
        fragmentTransaction.commit()
    }

    override fun onCardClick(fragment: Fragment) {
        replaceFragment(fragment)
    }
}
interface OnCardClickListener {
    public fun onCardClick(fragment: Fragment)
}
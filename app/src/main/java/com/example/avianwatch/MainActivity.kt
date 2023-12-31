package com.example.avianwatch

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.avianwatch.databinding.ActivityMainBinding
import com.example.avianwatch.fragments.BirdFactsFragment
import com.example.avianwatch.fragments.PostsFragment
import com.example.avianwatch.fragments.GoBirdingFragment
import com.example.avianwatch.fragments.HomeFragment
import com.example.avianwatch.fragments.ObservationListFragment

class MainActivity : AppCompatActivity(), OnCardClickListener{
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.txtTitle.setText("Dashboard")

        //setup the bottom navigation bar
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragment_container) as NavHostFragment
        val navController = navHostFragment.navController

        binding.bottomNavigation.setupWithNavController(navController)

        // as soon as the application opens the first fragment should
        // be shown to the user in this case it is the Home Fragment
        val fragment = HomeFragment()
        replaceFragment(fragment)

        //change the fragment every time the user presses a different bottom navigation button
        binding.bottomNavigation.setOnNavigationItemSelectedListener { menuItem ->
            // By using switch we can easily get the
            // selected fragment by using the id
            lateinit var selectedFragment: Fragment

            when (menuItem.itemId) {
                R.id.nav_home -> {
                    updateTitle("Dashboard")
                    selectedFragment = HomeFragment()
                }

                R.id.nav_go_birding -> {
                    updateTitle("Go Birding")
                    selectedFragment = GoBirdingFragment()
                }

                R.id.nav_view_birds -> {
                    updateTitle("My Observations")
                    selectedFragment = ObservationListFragment()
                }

                R.id.nav_blogs -> {
                    updateTitle("Community")
                    selectedFragment = PostsFragment()
                }

                R.id.nav_bird_facts -> {
                    updateTitle("Random Bird Facts")
                    selectedFragment = BirdFactsFragment()
                }

            }

            // replace the current fragment
            // to the selected fragment.
            replaceFragment(selectedFragment)

            true
        }
    }

    fun updateTitle(newText: String) {
        binding.txtTitle.setText(newText)
    }

    //method used to replace the current fragment with the fragment passed in the parameters
    private fun replaceFragment(fragment : Fragment){

        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragment_container,fragment)
        fragmentTransaction.addToBackStack(null) // add to back stack
        fragmentTransaction.commit()
    }

    override fun onCardClick(fragment: Fragment) {
        replaceFragment(fragment)
    }
}
interface OnCardClickListener {
    public fun onCardClick(fragment: Fragment)
}
package com.sempatpanick.githubuserlookup

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.tabs.TabLayoutMediator
import com.sempatpanick.githubuserlookup.databinding.ActivityDetailUserBinding
import com.sempatpanick.githubuserlookup.fragment.TabFragment
import com.sempatpanick.githubuserlookup.model.MainViewModel
import com.sempatpanick.githubuserlookup.model.UserSearchItems

class DetailUserActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailUserBinding
    private lateinit var mainViewModel: MainViewModel

    companion object {
        const val EXTRA_DATA = "extra_data"
        private val TAB_TITLES = ArrayList<String>()
        var username: String? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val actionBar = supportActionBar

        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24)
            actionBar.setDisplayHomeAsUpEnabled(true)
        }

        actionBar?.title = resources.getString(R.string.detail_user)

        mainViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(
            MainViewModel::class.java)

        val dataIntent = intent.getParcelableExtra<UserSearchItems>(EXTRA_DATA) as UserSearchItems

        username = dataIntent.username
        true.showLoading()

        if (isConnected()) {
            mainViewModel.userDetail(this, username.toString())
            mainViewModel.getDetailUser().observe(this, { userItems ->
                if (userItems != null) {
                    false.showLoading()
                    true.showPage()

                    binding.tvUsername.text = userItems[0].username
                    if (userItems[0].name != "null") {
                        binding.tvName.text = userItems[0].name
                    } else {
                        binding.tvName.visibility = View.GONE
                    }

                    Glide.with(applicationContext)
                        .load(userItems[0].avatarUrl)
                        .apply(RequestOptions().override(60, 60))
                        .into(binding.imgPhotoProfile)

                    if (userItems[0].company != "null") {
                        binding.tvCompany.text = userItems[0].company
                    } else {
                        binding.tvCompany.visibility = View.GONE
                    }

                    if (userItems[0].location != "null") {
                        binding.tvLocation.text = userItems[0].location
                    } else {
                        binding.tvLocation.visibility = View.GONE
                    }

                    binding.tvRepository.text = userItems[0].repository.toString()
                    TabFragment.FOLLOWERS = userItems[0].followers
                    TabFragment.FOLLOWING = userItems[0].following
                }
                TAB_TITLES.clear()
                showTab(userItems[0].followers, userItems[0].following)
            })
        } else {
            Toast.makeText(this, getString(R.string.no_connection), Toast.LENGTH_SHORT).show()
            false.showLoading()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_change_settings -> {
                val mIntent = Intent(Settings.ACTION_LOCALE_SETTINGS)
                startActivity(mIntent)
            }
            android.R.id.home -> {
                onBackPressed()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showTab(followers: Int?, following: Int?) {
        TAB_TITLES.add(resources.getString(R.string.tab_followers, followers))
        TAB_TITLES.add(resources.getString(R.string.tab_following, following))

        val sectionsPagerAdapter = SectionsPagerAdapter(this)
        binding.viewPager.adapter = sectionsPagerAdapter

        TabLayoutMediator(binding.tabs, binding.viewPager) { tab, position ->
            tab.text = TAB_TITLES[position]
        }.attach()
    }

    private fun Boolean.showLoading() {
        if (this) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }

    private fun Boolean.showPage() {
        if (this) {
            binding.layoutPage.visibility = View.VISIBLE
        } else {
            binding.layoutPage.visibility = View.GONE
        }
    }

    private fun isConnected() : Boolean {
        val cm = applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = cm.activeNetworkInfo
        return activeNetwork?.isConnectedOrConnecting == true
    }
}
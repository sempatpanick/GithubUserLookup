package com.sempatpanick.githubuserlookup

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.database.ContentObserver
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.tabs.TabLayoutMediator
import com.sempatpanick.githubuserlookup.databinding.ActivityDetailUserBinding
import com.sempatpanick.githubuserlookup.db.DatabaseContract.UserColumns.Companion.AVATAR_URL
import com.sempatpanick.githubuserlookup.db.DatabaseContract.UserColumns.Companion.CONTENT_URI
import com.sempatpanick.githubuserlookup.db.DatabaseContract.UserColumns.Companion.FOLLOWERS
import com.sempatpanick.githubuserlookup.db.DatabaseContract.UserColumns.Companion.FOLLOWING
import com.sempatpanick.githubuserlookup.db.DatabaseContract.UserColumns.Companion.NAME
import com.sempatpanick.githubuserlookup.db.DatabaseContract.UserColumns.Companion.REPOSITORY
import com.sempatpanick.githubuserlookup.db.DatabaseContract.UserColumns.Companion.USERNAME
import com.sempatpanick.githubuserlookup.entity.UserDetailItems
import com.sempatpanick.githubuserlookup.fragment.TabFragment
import com.sempatpanick.githubuserlookup.model.MainViewModel
import com.sempatpanick.githubuserlookup.entity.UserSearchItems
import com.sempatpanick.githubuserlookup.helper.MappingHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class DetailUserActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivityDetailUserBinding
    private lateinit var mainViewModel: MainViewModel
    private lateinit var uriWithId: Uri
    private var dataDetail: UserDetailItems = UserDetailItems()
    private var dataIntent: UserSearchItems = UserSearchItems()
    private var isFavorite: Boolean = false

    companion object {
        private const val EXTRA_STATE = "EXTRA_STATE"
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

        dataIntent = intent.getParcelableExtra<UserSearchItems>(EXTRA_DATA) as UserSearchItems

        username = dataIntent.username
        true.showLoading()

        if (isConnected()) {
            mainViewModel.userDetail(this, username.toString())
            mainViewModel.getDetailUser().observe(this, { userItems ->
                if (userItems != null) {
                    false.showLoading()
                    true.showPage()
                    true.showFavoriteButton()
                    dataDetail = userItems[0]

                    binding.tvUsername.text = dataDetail.username
                    if (dataDetail.name != "null") {
                        binding.tvName.text = dataDetail.name
                    } else {
                        binding.tvName.visibility = View.GONE
                    }

                    Glide.with(applicationContext)
                        .load(dataDetail.avatarUrl)
                        .apply(RequestOptions().override(60, 60))
                        .into(binding.imgPhotoProfile)

                    if (dataDetail.company != "null") {
                        binding.tvCompany.text = dataDetail.company
                    } else {
                        binding.tvCompany.visibility = View.GONE
                    }

                    if (dataDetail.location != "null") {
                        binding.tvLocation.text = dataDetail.location
                    } else {
                        binding.tvLocation.visibility = View.GONE
                    }

                    binding.tvRepository.text = dataDetail.repository.toString()
                    TabFragment.FOLLOWERS = dataDetail.followers
                    TabFragment.FOLLOWING = dataDetail.following
                }

                TAB_TITLES.clear()
                showTab(dataDetail.followers, dataDetail.following)
            })
        } else {
            Toast.makeText(this, getString(R.string.no_connection), Toast.LENGTH_SHORT).show()
            false.showLoading()
        }

        val handlerThread = HandlerThread("DataObserver")
        handlerThread.start()
        val handler = Handler(handlerThread.looper)

        val myObserver = object : ContentObserver(handler) {
            override fun onChange(self: Boolean) {
                loadDataFavorite()
            }
        }

        contentResolver.registerContentObserver(CONTENT_URI, true, myObserver)

        if (savedInstanceState == null) {
            loadDataFavorite()
        } else {
            savedInstanceState.getBoolean(EXTRA_STATE)?.also { isFavorite = it }
            loadDataFavorite()
        }

        binding.fabFavorite.setOnClickListener(this)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.setting_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_settings -> {
                val mIntent = Intent(this, SettingsActivity::class.java)
                startActivity(mIntent)
            }
            android.R.id.home -> {
                onBackPressed()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            binding.fabFavorite.id -> {
                if (isFavorite) {
                    contentResolver.delete(uriWithId, null, null)
                    isFavorite = false
                    Toast.makeText(this, resources.getString(R.string.has_removed_favorite, dataIntent.username), Toast.LENGTH_SHORT).show()
                } else {
                    val values = ContentValues()
                    values.put(USERNAME, dataDetail.username)
                    values.put(NAME, dataDetail.name)
                    values.put(AVATAR_URL, dataDetail.avatarUrl)
                    values.put(REPOSITORY, dataDetail.repository)
                    values.put(FOLLOWERS, dataDetail.followers)
                    values.put(FOLLOWING, dataDetail.following)
                    contentResolver.insert(CONTENT_URI, values)
                    isFavorite = true
                    Toast.makeText(this, resources.getString(R.string.has_added_favorite, dataDetail.username), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(EXTRA_STATE, isFavorite)
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

    private fun loadDataFavorite() {
        GlobalScope.launch(Dispatchers.Main) {
            val deferredNotes = async(Dispatchers.IO) {
                val cursor = contentResolver.query(CONTENT_URI, null, null, null, null)
                MappingHelper.mapCursorToArrayList(cursor)
            }
            val favorites = deferredNotes.await()

            for (favorite in favorites) {
                if (favorite.username == dataIntent.username) {
                    uriWithId = Uri.parse("${CONTENT_URI}/${favorite.id}")
                    isFavorite = true
                    break
                } else {
                    isFavorite = false
                }
            }

            changeIconFab()
        }
    }

    private fun changeIconFab() {
        if (isFavorite) {
            binding.fabFavorite.setImageResource(R.drawable.ic_baseline_favorite_24)
        } else {
            binding.fabFavorite.setImageResource(R.drawable.ic_baseline_favorite_border_24)
        }
    }

    private fun Boolean.showPage() {
        if (this) {
            binding.layoutPage.visibility = View.VISIBLE
        } else {
            binding.layoutPage.visibility = View.GONE
        }
    }

    private fun Boolean.showLoading() {
        if (this) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }

    private fun Boolean.showFavoriteButton() {
        if (this) {
            binding.fabFavorite.visibility = View.VISIBLE
        } else {
            binding.fabFavorite.visibility = View.GONE
        }
    }

    private fun isConnected() : Boolean {
        val connectivityManager = applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val nw      = connectivityManager.activeNetwork ?: return false
            val actNw = connectivityManager.getNetworkCapabilities(nw) ?: return false
            return when {
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH) -> true
                else -> false
            }
        } else {
            return connectivityManager.activeNetworkInfo?.isConnected ?: false
        }
    }
}
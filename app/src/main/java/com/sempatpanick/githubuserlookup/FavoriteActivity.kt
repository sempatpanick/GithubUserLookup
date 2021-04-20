package com.sempatpanick.githubuserlookup

import android.content.Intent
import android.database.ContentObserver
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.sempatpanick.githubuserlookup.adapter.FavoriteAdapter
import com.sempatpanick.githubuserlookup.databinding.ActivityFavoriteBinding
import com.sempatpanick.githubuserlookup.db.DatabaseContract.UserColumns.Companion.CONTENT_URI
import com.sempatpanick.githubuserlookup.entity.UserFavoriteItems
import com.sempatpanick.githubuserlookup.entity.UserSearchItems
import com.sempatpanick.githubuserlookup.helper.MappingHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class FavoriteActivity : AppCompatActivity() {
    companion object {
        private const val EXTRA_STATE = "EXTRA_STATE"
    }

    private lateinit var adapter: FavoriteAdapter
    private lateinit var binding: ActivityFavoriteBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFavoriteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = resources.getString(R.string.favorite)

        binding.rvListFavorite.layoutManager = LinearLayoutManager(this)
        binding.rvListFavorite.setHasFixedSize(true)
        adapter = FavoriteAdapter()
        binding.rvListFavorite.adapter = adapter

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
            savedInstanceState.getParcelableArrayList<UserFavoriteItems>(EXTRA_STATE)?.also { adapter.mData = it }
        }

        adapter.setOnItemClickCallback(object : FavoriteAdapter.OnItemClickCallback {
            override fun onItemClicked(data: UserFavoriteItems) {
                showSelectedUser(data)
            }
        })
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

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelableArrayList(EXTRA_STATE, adapter.mData)
    }

    private fun loadDataFavorite() {
        GlobalScope.launch(Dispatchers.Main) {
            binding.progressBar.visibility = View.VISIBLE
            val deferredNotes = async(Dispatchers.IO) {
                val cursor = contentResolver.query(CONTENT_URI, null, null, null, null)
                MappingHelper.mapCursorToArrayList(cursor)
            }
            val favorites = deferredNotes.await()

            binding.progressBar.visibility = View.GONE

            if (favorites.size > 0) {
                adapter.setData(favorites)
                binding.tvNodata.visibility = View.GONE
            } else {
                adapter.clearData()
                binding.tvNodata.visibility = View.VISIBLE
            }
        }
    }

    private fun showSelectedUser(data: UserFavoriteItems) {
        val forDetail = UserSearchItems(
                data.username,
                data.avatarUrl
        )

        val moveIntent = Intent(this, DetailUserActivity::class.java)
        moveIntent.putExtra(DetailUserActivity.EXTRA_DATA, forDetail)
        startActivity(moveIntent)
    }
}
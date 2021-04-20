package com.sempatpanick.consumerapp

import android.database.ContentObserver
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.sempatpanick.consumerapp.databinding.ActivityMainBinding
import com.sempatpanick.consumerapp.adapter.FavoriteAdapter
import com.sempatpanick.consumerapp.db.DatabaseContract.UserColumns.Companion.CONTENT_URI
import com.sempatpanick.consumerapp.entity.UserFavoriteItems
import com.sempatpanick.consumerapp.helper.MappingHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var adapter: FavoriteAdapter
    private lateinit var binding: ActivityMainBinding

    companion object {
        private const val EXTRA_STATE = "EXTRA_STATE"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
}
package com.sempatpanick.githubuserlookup

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.SearchView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.sempatpanick.githubuserlookup.adapter.UserAdapter
import com.sempatpanick.githubuserlookup.databinding.ActivityMainBinding
import com.sempatpanick.githubuserlookup.model.MainViewModel
import com.sempatpanick.githubuserlookup.entity.UserSearchItems
import com.sempatpanick.githubuserlookup.utils.ConnectionStatus.Companion.isConnected

class MainActivity : AppCompatActivity(), SearchView.OnQueryTextListener,
    UserAdapter.OnItemClickCallback {
    private lateinit var adapter: UserAdapter
    private lateinit var binding: ActivityMainBinding
    private lateinit var mainViewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = UserAdapter()
        adapter.notifyDataSetChanged()
        binding.rvSearchUser.layoutManager = LinearLayoutManager(this)
        binding.rvSearchUser.adapter = adapter
        adapter.setOnItemClickCallback(this)

        mainViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(MainViewModel::class.java)

        binding.edSearch.setOnQueryTextListener(this)

        mainViewModel.getSearchUsers().observe(this, { userItems ->
            observeData(userItems)
        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                val mIntent = Intent(this, SettingsActivity::class.java)
                startActivity(mIntent)
                true
            }
            R.id.action_favorite -> {
                val mIntent = Intent(this, FavoriteActivity::class.java)
                startActivity(mIntent)
                true
            }
            else -> true
        }
    }

    private fun showLoading(state: Boolean) {
        if (state) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }

    override fun onQueryTextSubmit(query: String): Boolean {
        adapter.clearData()
        showLoading(true)

        if (isConnected(this)) {
            mainViewModel.searchUsers(this, query)
        } else {
            Toast.makeText(this, getString(R.string.no_connection), Toast.LENGTH_SHORT).show()
            showLoading(false)
        }
        return true
    }

    override fun onQueryTextChange(newText: String): Boolean {
        if (newText.isEmpty()) {
            showLoading(false)
            binding.tvTotalUsers.visibility = View.GONE
            adapter.clearData()
        }
        return true
    }

    override fun onItemClicked(data: UserSearchItems) {
        val moveIntent = Intent(this, DetailUserActivity::class.java)
        moveIntent.putExtra(DetailUserActivity.EXTRA_DATA, data)
        startActivity(moveIntent)
    }

    private fun observeData(userItems: ArrayList<UserSearchItems>) {
        if (MainViewModel.numberOfUser == 0) {
            userItems.add(UserSearchItems(
                resources.getString(R.string.no_data),
                null
            ))
            binding.tvTotalUsers.visibility = View.GONE
        } else {
            binding.tvTotalUsers.visibility = View.VISIBLE
            binding.tvTotalUsers.text = resources.getString(R.string.total_users, MainViewModel.numberOfUser)
        }
        adapter.setData(userItems)
        showLoading(false)
    }
}
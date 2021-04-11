package com.sempatpanick.githubuserlookup

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.sempatpanick.githubuserlookup.adapter.UserAdapter
import com.sempatpanick.githubuserlookup.databinding.ActivityMainBinding
import com.sempatpanick.githubuserlookup.model.MainViewModel
import com.sempatpanick.githubuserlookup.model.UserSearchItems

class MainActivity : AppCompatActivity(), View.OnKeyListener {
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

        mainViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(
            MainViewModel::class.java)

        binding.edSearch.setOnKeyListener(this)
        adapter.setOnItemClickCallback(object : UserAdapter.OnItemClickCallback {
            override fun onItemClicked(data: UserSearchItems) {
                showSelectedUser(data)
            }
        })

        mainViewModel.getSearchUsers().observe(this, { userItems ->
            if (userItems != null) {
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
        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_change_settings) {
            val mIntent = Intent(Settings.ACTION_LOCALE_SETTINGS)
            startActivity(mIntent)
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showLoading(state: Boolean) {
        if (state) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }

    private fun showSelectedUser(data: UserSearchItems) {
        val moveIntent = Intent(this, DetailUserActivity::class.java)
        moveIntent.putExtra(DetailUserActivity.EXTRA_DATA, data)
        startActivity(moveIntent)
    }

    override fun onKey(v: View?, keyCode: Int, event: KeyEvent?): Boolean {
        if((event?.action == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
            if (binding.edSearch.text.toString().isEmpty()) {
                showLoading(false)
                binding.tvTotalUsers.visibility = View.GONE
                adapter.clearData()
            } else {
                adapter.clearData()
                showLoading(true)

                if (isConnected()) {
                    mainViewModel.searchUsers(this, binding.edSearch.text.toString())
                } else {
                    Toast.makeText(this, getString(R.string.no_connection), Toast.LENGTH_SHORT).show()
                    showLoading(false)
                }
            }
        }
        return true
    }

    private fun isConnected() : Boolean {
        val cm = applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = cm.activeNetworkInfo
        return activeNetwork?.isConnectedOrConnecting == true
    }
}
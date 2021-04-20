package com.sempatpanick.githubuserlookup.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.sempatpanick.githubuserlookup.DetailUserActivity
import com.sempatpanick.githubuserlookup.R
import com.sempatpanick.githubuserlookup.adapter.TabAdapter
import com.sempatpanick.githubuserlookup.databinding.FragmentTabBinding
import com.sempatpanick.githubuserlookup.model.MainViewModel
import com.sempatpanick.githubuserlookup.entity.UserSearchItems

class TabFragment : Fragment() {
    private lateinit var fragmentTabBinding: FragmentTabBinding
    private lateinit var mainViewModel: MainViewModel

    private fun showLoading(state: Boolean) {
        if (state) {
            fragmentTabBinding.progressBar.visibility = View.VISIBLE
        } else {
            fragmentTabBinding.progressBar.visibility = View.GONE
        }
    }

    private fun showSelectedUser(data: UserSearchItems) {
        val moveIntent = Intent(context, DetailUserActivity::class.java)
        moveIntent.putExtra(DetailUserActivity.EXTRA_DATA, data)
        startActivity(moveIntent)
    }

    companion object {
        private const val ARG_SECTION_NUMBER = "section_number"
        var FOLLOWERS: Int? = 0
        var FOLLOWING: Int? = 0

        @JvmStatic
        fun newInstance(index: Int) =
            TabFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_SECTION_NUMBER, index)
                }
            }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,  savedInstanceState: Bundle?): View {
        fragmentTabBinding = FragmentTabBinding.inflate(layoutInflater)

        val adapter = TabAdapter()
        fragmentTabBinding.rvListUser.layoutManager = LinearLayoutManager(context)
        fragmentTabBinding.rvListUser.setHasFixedSize(true)
        fragmentTabBinding.rvListUser.adapter = adapter

        mainViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(
            MainViewModel::class.java)

        val index = arguments?.getInt(ARG_SECTION_NUMBER, 0)

        showLoading(true)

        when (index) {
            1 -> {
                adapter.setOnItemClickCallback(object : TabAdapter.OnItemClickCallback {
                    override fun onItemClicked(data: UserSearchItems) {
                        showSelectedUser(data)
                    }
                })
                context?.let { mainViewModel.userFollowers(it, DetailUserActivity.username) }
                mainViewModel.getUserFollowers().observe(viewLifecycleOwner, { userItems ->
                    if (userItems != null) {
                        if (FOLLOWERS == 0) {
                            userItems.add(UserSearchItems(
                                resources.getString(R.string.no_data),
                                null
                            ))
                        } else if (FOLLOWERS?: 0 > 30) {
                            val moreData = UserSearchItems(
                                resources.getString(R.string.many_more, FOLLOWERS?.minus(30)),
                                null
                            )
                            userItems.add(moreData)
                        }
                        adapter.setData(userItems)
                        showLoading(false)
                    }
                })
            }
            2 -> {
                adapter.setOnItemClickCallback(object : TabAdapter.OnItemClickCallback {
                    override fun onItemClicked(data: UserSearchItems) {
                        showSelectedUser(data)
                    }
                })
                context?.let { mainViewModel.userFollowing(it, DetailUserActivity.username.toString()) }
                mainViewModel.getUserFollowing().observe(viewLifecycleOwner, { userItems ->
                    if (userItems != null) {
                        if (FOLLOWING == 0) {
                            userItems.add(UserSearchItems(
                                resources.getString(R.string.no_data),
                                null
                            ))
                        } else if (FOLLOWING?: 0 > 30) {
                            val moreData = UserSearchItems(
                                resources.getString(R.string.many_more, FOLLOWING?.minus(30)),
                                null
                            )
                            userItems.add(moreData)
                        }
                        adapter.setData(userItems)
                        showLoading(false)
                    }
                })
            }
        }

        return fragmentTabBinding.root
    }
}
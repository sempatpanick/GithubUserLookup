package com.sempatpanick.githubuserlookup.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.sempatpanick.githubuserlookup.R
import com.sempatpanick.githubuserlookup.model.UserSearchItems
import com.sempatpanick.githubuserlookup.databinding.ItemUsersBinding

class TabAdapter : RecyclerView.Adapter<TabAdapter.TabViewHolder>() {
    private val mData = ArrayList<UserSearchItems>()

    fun setData(items: ArrayList<UserSearchItems>) {
        mData.clear()
        mData.addAll(items)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TabViewHolder {
        val mView = LayoutInflater.from(parent.context).inflate(R.layout.item_users, parent, false)
        return TabViewHolder(mView)
    }

    override fun onBindViewHolder(holder: TabViewHolder, position: Int) {
        holder.bind(mData[position])
    }

    override fun getItemCount(): Int = mData.size

    inner class TabViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = ItemUsersBinding.bind(itemView)

        fun bind(userSearchItems: UserSearchItems) {
            binding.tvUsername.text = userSearchItems.username
            Glide.with(itemView.context)
                .load(userSearchItems.avatarUrl)
                .apply(RequestOptions().override(60, 60))
                .into(binding.imgPhotoProfile)
        }
    }
}
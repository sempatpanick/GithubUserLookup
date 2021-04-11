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

class UserAdapter : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {
    private val mData = ArrayList<UserSearchItems>()
    private lateinit var onItemClickCallback: OnItemClickCallback

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    interface OnItemClickCallback {
        fun onItemClicked(data: UserSearchItems)
    }

    fun setData(items: ArrayList<UserSearchItems>) {
        mData.clear()
        mData.addAll(items)
        notifyDataSetChanged()
    }

    fun clearData() {
        mData.clear()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val mView = LayoutInflater.from(parent.context).inflate(R.layout.item_users, parent, false)
        return UserViewHolder(mView)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.bind(mData[position])
    }

    override fun getItemCount(): Int = mData.size

    inner class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = ItemUsersBinding.bind(itemView)

        fun bind(userSearchItems: UserSearchItems) {
            with(itemView) {
                binding.tvUsername.text = userSearchItems.username
                Glide.with(itemView.context)
                        .load(userSearchItems.avatarUrl)
                        .apply(RequestOptions().override(60, 60))
                        .into(binding.imgPhotoProfile)
                setOnClickListener {
                    onItemClickCallback.onItemClicked(mData[adapterPosition])
                }
            }
        }
    }
}
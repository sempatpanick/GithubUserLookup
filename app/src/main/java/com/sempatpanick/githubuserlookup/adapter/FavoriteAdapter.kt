package com.sempatpanick.githubuserlookup.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.sempatpanick.githubuserlookup.R
import com.sempatpanick.githubuserlookup.databinding.ItemFavoriteBinding
import com.sempatpanick.githubuserlookup.entity.UserFavoriteItems

class FavoriteAdapter : RecyclerView.Adapter<FavoriteAdapter.FavoriteViewHolder>() {
    private lateinit var onItemClickCallback: OnItemClickCallback

    var mData = ArrayList<UserFavoriteItems>()
        set(listNotes) {
            if (listNotes.size > 0) {
                this.mData.clear()
            }
            this.mData.addAll(listNotes)
            notifyDataSetChanged()
        }

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    interface OnItemClickCallback {
        fun onItemClicked(data: UserFavoriteItems)
    }

    fun setData(items: ArrayList<UserFavoriteItems>) {
        mData.clear()
        mData.addAll(items)
        notifyDataSetChanged()
    }

    fun clearData() {
        mData.clear()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteViewHolder {
        val mView = LayoutInflater.from(parent.context).inflate(R.layout.item_favorite, parent, false)
        return FavoriteViewHolder(mView)
    }

    override fun onBindViewHolder(holder: FavoriteViewHolder, position: Int) {
        holder.bind(mData[position])
    }

    override fun getItemCount(): Int = mData.size

    inner class FavoriteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = ItemFavoriteBinding.bind(itemView)

        fun bind(userFavoriteItems: UserFavoriteItems) {
            with(itemView) {
                Glide.with(context)
                    .load(userFavoriteItems.avatarUrl)
                    .apply(RequestOptions().override(60, 60))
                    .into(binding.imgPhotoProfile)
                if (userFavoriteItems.name != "null") {
                    binding.tvName.visibility = View.VISIBLE
                    binding.tvName.text = userFavoriteItems.name
                } else {
                    binding.tvName.visibility = View.GONE
                }
                binding.tvUsername.text = userFavoriteItems.username
                binding.tvRepository.text = userFavoriteItems.repository.toString()
                binding.tvFollowersFollowing.text = resources.getString(R.string.followers_following, userFavoriteItems.followers, userFavoriteItems.following)

                setOnClickListener {
                    onItemClickCallback.onItemClicked(mData[adapterPosition])
                }
            }
        }
    }
}
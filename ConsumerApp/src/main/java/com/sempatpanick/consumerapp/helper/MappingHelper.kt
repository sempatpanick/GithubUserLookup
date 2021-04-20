package com.sempatpanick.consumerapp.helper

import android.database.Cursor
import com.sempatpanick.consumerapp.db.DatabaseContract.UserColumns.Companion.AVATAR_URL
import com.sempatpanick.consumerapp.db.DatabaseContract.UserColumns.Companion.FOLLOWERS
import com.sempatpanick.consumerapp.db.DatabaseContract.UserColumns.Companion.FOLLOWING
import com.sempatpanick.consumerapp.db.DatabaseContract.UserColumns.Companion.NAME
import com.sempatpanick.consumerapp.db.DatabaseContract.UserColumns.Companion.REPOSITORY
import com.sempatpanick.consumerapp.db.DatabaseContract.UserColumns.Companion.USERNAME
import com.sempatpanick.consumerapp.db.DatabaseContract.UserColumns.Companion._ID
import com.sempatpanick.consumerapp.entity.UserFavoriteItems

object MappingHelper {
    fun mapCursorToArrayList(userCursor: Cursor?): ArrayList<UserFavoriteItems> {
        val favoriteList = ArrayList<UserFavoriteItems>()
        userCursor?.apply {
            while (moveToNext()) {
                val id = getInt(getColumnIndexOrThrow(_ID))
                val username = getString(getColumnIndexOrThrow(USERNAME))
                val name = getString(getColumnIndexOrThrow(NAME))
                val avatarUrl = getString(getColumnIndexOrThrow(AVATAR_URL))
                val repository = getInt(getColumnIndexOrThrow(REPOSITORY))
                val followers = getInt(getColumnIndexOrThrow(FOLLOWERS))
                val following = getInt(getColumnIndexOrThrow(FOLLOWING))
                favoriteList.add(UserFavoriteItems(id, username, name, avatarUrl, repository, followers, following))
            }
        }
        return favoriteList
    }
}
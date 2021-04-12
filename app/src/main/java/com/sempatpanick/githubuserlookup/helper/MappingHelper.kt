package com.sempatpanick.githubuserlookup.helper

import android.database.Cursor
import com.sempatpanick.githubuserlookup.db.DatabaseContract.UserColumns.Companion.AVATAR_URL
import com.sempatpanick.githubuserlookup.db.DatabaseContract.UserColumns.Companion.FOLLOWERS
import com.sempatpanick.githubuserlookup.db.DatabaseContract.UserColumns.Companion.FOLLOWING
import com.sempatpanick.githubuserlookup.db.DatabaseContract.UserColumns.Companion.NAME
import com.sempatpanick.githubuserlookup.db.DatabaseContract.UserColumns.Companion.REPOSITORY
import com.sempatpanick.githubuserlookup.db.DatabaseContract.UserColumns.Companion.USERNAME
import com.sempatpanick.githubuserlookup.db.DatabaseContract.UserColumns.Companion._ID
import com.sempatpanick.githubuserlookup.entity.UserFavoriteItems

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

    fun mapCursorToObject(userCursor: Cursor?): UserFavoriteItems {
        var user = UserFavoriteItems()
        userCursor?.apply {
            moveToFirst()
            val id = getInt(getColumnIndexOrThrow(_ID))
            val username = getString(getColumnIndexOrThrow(USERNAME))
            val name = getString(getColumnIndexOrThrow(NAME))
            val avatarUrl = getString(getColumnIndexOrThrow(AVATAR_URL))
            val repository = getInt(getColumnIndexOrThrow(REPOSITORY))
            val followers = getInt(getColumnIndexOrThrow(FOLLOWERS))
            val following = getInt(getColumnIndexOrThrow(FOLLOWING))
            user = UserFavoriteItems(id, username, name, avatarUrl, repository, followers, following)
        }
        return user
    }
}
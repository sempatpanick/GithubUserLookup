package com.sempatpanick.githubuserlookup.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserDetailItems (
        var username: String? = null,
        var name: String? = null,
        var avatarUrl: String? = null,
        var company: String? = null,
        var location: String? = null,
        var repository: Int? = 0,
        var followers: Int? = 0,
        var following: Int? = 0
) : Parcelable
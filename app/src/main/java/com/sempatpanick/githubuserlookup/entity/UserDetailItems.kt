package com.sempatpanick.githubuserlookup.entity

import android.os.Parcelable
import androidx.annotation.NonNull
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserDetailItems (
        @NonNull var username: String,
        var name: String? = null,
        var avatarUrl: String? = null,
        var company: String? = null,
        var location: String? = null,
        var repository: Int? = 0,
        var followers: Int? = 0,
        var following: Int? = 0
) : Parcelable
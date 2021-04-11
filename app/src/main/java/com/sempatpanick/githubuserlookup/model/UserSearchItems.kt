package com.sempatpanick.githubuserlookup.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserSearchItems (
    var username: String? = null,
    var avatarUrl: String? = null
) : Parcelable
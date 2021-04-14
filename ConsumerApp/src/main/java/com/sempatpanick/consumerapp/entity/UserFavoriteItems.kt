package com.sempatpanick.consumerapp.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class UserFavoriteItems (
        var id: Int = 0,
        var username: String? = null,
        var name: String? = null,
        var avatarUrl: String? = null,
        var repository: Int? = 0,
        var followers: Int? = 0,
        var following: Int? = 0
) : Parcelable
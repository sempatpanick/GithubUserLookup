package com.sempatpanick.githubuserlookup.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build

class ConnectionStatus {
    companion object {
        fun isConnected(context: Context) : Boolean {
            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val network = connectivityManager.activeNetwork ?: return false
            val actionNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false
            return when {
                actionNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                actionNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                actionNetwork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                actionNetwork.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH) -> true
                else -> false
            }
        }
    }
}
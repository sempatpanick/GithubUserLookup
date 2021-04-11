package com.sempatpanick.githubuserlookup.model

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import com.sempatpanick.githubuserlookup.R
import cz.msebera.android.httpclient.Header
import org.json.JSONArray
import org.json.JSONObject

class MainViewModel : ViewModel() {
    private val listUsers = MutableLiveData<ArrayList<UserSearchItems>>()
    private val detailUser = MutableLiveData<ArrayList<UserDetailItems>>()
    private val listFollowers = MutableLiveData<ArrayList<UserSearchItems>>()
    private val listFollowing = MutableLiveData<ArrayList<UserSearchItems>>()
    private val mainUrl = "https://api.github.com/"
    /*
        Your Github Access Token
     */
    private val accessToken = "8568eb0c7c3e06fd76adbe73c316844c4c9380a3"

    companion object {
        var numberOfUser: Int? = 0
    }

    fun searchUsers(context: Context, username: String?){
        val listItems = ArrayList<UserSearchItems>()

        val url = "${mainUrl}search/users?q=${username}"

        val client = AsyncHttpClient()
        client.addHeader("Authorization", "token $accessToken")
        client.addHeader("User-Agent", "request")
        client.setTimeout(50000)
        client.get(url, object : AsyncHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Array<Header>, responseBody: ByteArray) {
                try {
                    //parsing json
                    val result = String(responseBody)
                    val responseObject = JSONObject(result)
                    val list = responseObject.getJSONArray("items")
                    numberOfUser = responseObject.getInt("total_count")

                    for (i in 0 until list.length()) {
                        val user = list.getJSONObject(i)
                        val userSearchItems = UserSearchItems()
                        userSearchItems.username = user.getString("login")
                        userSearchItems.avatarUrl = user.getString("avatar_url")
                        listItems.add(userSearchItems)
                    }

                    listUsers.postValue(listItems)
                } catch (e: Exception) {
                    val errorMessage = "${context.getString(R.string.failed)} : ${e.message}"
                    Toast.makeText(context, context.getString(R.string.failed, errorMessage), Toast.LENGTH_SHORT).show()
                }

            }

            override fun onFailure(statusCode: Int, headers: Array<Header>, responseBody: ByteArray, error: Throwable) {
                // Jika koneksi gagal
                val errorMessage = when (statusCode) {
                    401 -> "$statusCode - Bad Request"
                    403 -> "$statusCode - Forbidden"
                    404 -> "$statusCode - Not Found"
                    else -> "$statusCode - ${error.message}"
                }
                Toast.makeText(context, context.getString(R.string.failed, errorMessage), Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun userDetail(context: Context, username: String?){
        val dataUser = ArrayList<UserDetailItems>()

        val url = "${mainUrl}users/${username}"

        val client = AsyncHttpClient()
        client.addHeader("Authorization", "token $accessToken")
        client.addHeader("User-Agent", "request")
        client.setTimeout(50000)
        client.get(url, object : AsyncHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Array<Header>, responseBody: ByteArray) {
                try {
                    //parsing json
                    val result = String(responseBody)
                    val responseObject = JSONObject(result)
                    val userDetailItems = UserDetailItems()
                    userDetailItems.username = responseObject.getString("login")
                    userDetailItems.name = responseObject.getString("name")
                    userDetailItems.avatarUrl = responseObject.getString("avatar_url")
                    userDetailItems.company = responseObject.getString("company")
                    userDetailItems.location = responseObject.getString("location")
                    userDetailItems.repository = responseObject.getInt("public_repos")
                    userDetailItems.followers = responseObject.getInt("followers")
                    userDetailItems.following = responseObject.getInt("following")
                    dataUser.add(userDetailItems)
                    detailUser.postValue(dataUser)
                } catch (e: Exception) {
                    val errorMessage = "${context.getString(R.string.failed)} : ${e.message}"
                    Toast.makeText(context, context.getString(R.string.failed, errorMessage), Toast.LENGTH_SHORT).show()
                }

            }

            override fun onFailure(statusCode: Int, headers: Array<Header>, responseBody: ByteArray, error: Throwable) {
                // Jika koneksi gagal
                val errorMessage = when (statusCode) {
                    401 -> "$statusCode - Bad Request"
                    403 -> "$statusCode - Forbidden"
                    404 -> "$statusCode - Not Found"
                    else -> "$statusCode - ${error.message}"
                }
                Toast.makeText(context, context.getString(R.string.failed, errorMessage), Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun userFollowers(context: Context, username: String?){
        val listItems = ArrayList<UserSearchItems>()

        val url = "${mainUrl}users/${username}/followers"

        val client = AsyncHttpClient()
        client.addHeader("Authorization", "token $accessToken")
        client.addHeader("User-Agent", "request")
        client.setTimeout(50000)
        client.get(url, object : AsyncHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Array<Header>, responseBody: ByteArray) {
                try {
                    //parsing json
                    val result = String(responseBody)
                    val list = JSONArray(result)

                    for (i in 0 until list.length()) {
                        val user = list.getJSONObject(i)
                        val userSearchItems = UserSearchItems()
                        userSearchItems.username = user.getString("login")
                        userSearchItems.avatarUrl = user.getString("avatar_url")
                        listItems.add(userSearchItems)
                    }

                    listFollowers.postValue(listItems)
                } catch (e: Exception) {
                    val errorMessage = "${context.getString(R.string.failed)} : ${e.message}"
                    Toast.makeText(context, context.getString(R.string.failed, errorMessage), Toast.LENGTH_SHORT).show()
                }

            }

            override fun onFailure(statusCode: Int, headers: Array<Header>, responseBody: ByteArray, error: Throwable) {
                // Jika koneksi gagal
                val errorMessage = when (statusCode) {
                    401 -> "$statusCode - Bad Request"
                    403 -> "$statusCode - Forbidden"
                    404 -> "$statusCode - Not Found"
                    else -> "$statusCode - ${error.message}"
                }
                Toast.makeText(context, context.getString(R.string.failed, errorMessage), Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun userFollowing(context: Context, username: String?){
        val listItems = ArrayList<UserSearchItems>()

        val url = "${mainUrl}users/${username}/following"

        val client = AsyncHttpClient()
        client.addHeader("Authorization", "token $accessToken")
        client.addHeader("User-Agent", "request")
        client.setTimeout(50000)
        client.get(url, object : AsyncHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Array<Header>, responseBody: ByteArray) =
                    try {
                        //parsing json
                        val result = String(responseBody)
                        val list = JSONArray(result)

                        for (i in 0 until list.length()) {
                            val user = list.getJSONObject(i)
                            val userSearchItems = UserSearchItems()
                            userSearchItems.username = user.getString("login")
                            userSearchItems.avatarUrl = user.getString("avatar_url")
                            listItems.add(userSearchItems)
                        }

                        listFollowing.postValue(listItems)
                    } catch (e: Exception) {
                        val errorMessage = "${context.getString(R.string.failed)} : ${e.message}"
                        Toast.makeText(context, context.getString(R.string.failed, errorMessage), Toast.LENGTH_SHORT).show()
                    }

            override fun onFailure(statusCode: Int, headers: Array<Header>, responseBody: ByteArray, error: Throwable) {
                // Jika koneksi gagal
                val errorMessage = when (statusCode) {
                    401 -> "$statusCode - Bad Request"
                    403 -> "$statusCode - Forbidden"
                    404 -> "$statusCode - Not Found"
                    else -> "$statusCode - ${error.message}"
                }
                Toast.makeText(context, context.getString(R.string.failed, errorMessage), Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun getSearchUsers() : LiveData<ArrayList<UserSearchItems>> {
        return listUsers
    }

    fun getDetailUser() : LiveData<ArrayList<UserDetailItems>> {
        return detailUser
    }

    fun getUserFollowers() : LiveData<ArrayList<UserSearchItems>> {
        return listFollowers
    }

    fun getUserFollowing() : LiveData<ArrayList<UserSearchItems>> {
        return listFollowing
    }
}
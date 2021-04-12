package com.sempatpanick.githubuserlookup.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.sempatpanick.githubuserlookup.db.DatabaseContract.UserColumns.Companion.AVATAR_URL
import com.sempatpanick.githubuserlookup.db.DatabaseContract.UserColumns.Companion.FOLLOWERS
import com.sempatpanick.githubuserlookup.db.DatabaseContract.UserColumns.Companion.FOLLOWING
import com.sempatpanick.githubuserlookup.db.DatabaseContract.UserColumns.Companion.NAME
import com.sempatpanick.githubuserlookup.db.DatabaseContract.UserColumns.Companion.REPOSITORY
import com.sempatpanick.githubuserlookup.db.DatabaseContract.UserColumns.Companion.TABLE_NAME
import com.sempatpanick.githubuserlookup.db.DatabaseContract.UserColumns.Companion.USERNAME
import com.sempatpanick.githubuserlookup.db.DatabaseContract.UserColumns.Companion._ID

internal class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "dbgithub"
        private const val DATABASE_VERSION = 1

        private const val SQL_CREATE_TABLE_NOTE = "CREATE TABLE $TABLE_NAME" +
                " (${_ID} INTEGER PRIMARY KEY AUTOINCREMENT," +
                " $USERNAME TEXT NOT NULL," +
                " $NAME TEXT NOT NULL," +
                " $AVATAR_URL TEXT NOT NULL," +
                " $REPOSITORY INTEGER NOT NULL," +
                " $FOLLOWERS INTEGER NOT NULL," +
                " $FOLLOWING INTEGER NOT NULL)"
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(SQL_CREATE_TABLE_NOTE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }
}
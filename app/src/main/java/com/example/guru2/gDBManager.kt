package com.example.guru2

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class gDBManager(context: Context?,
                 name: String?,
                 factory: SQLiteDatabase.CursorFactory?,
                 version: Int
) : SQLiteOpenHelper(context, name, factory, version) {
    override fun onCreate(db: SQLiteDatabase?) {
        db!!.execSQL("CREATE TABLE groupDB (gName text, gNumber INTEGER, gText text, gStep String, gCount INTEGER, gMember1 text, gMember2 text, gMember3 text, gMember4 text)")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        TODO("Not yet implemented")
    }
}
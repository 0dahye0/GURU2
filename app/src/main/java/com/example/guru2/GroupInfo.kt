package com.example.guru2

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast

class GroupInfo : AppCompatActivity() {

    lateinit var dbManager: DBManager
    lateinit var sqlitedb: SQLiteDatabase

    lateinit var tvName: TextView
    lateinit var tvNumber: TextView
    lateinit var tvStep: TextView
    lateinit var tvText: TextView
    lateinit var btnEnjoy: Button

    lateinit var str_gName: String
    var gNumber: Int = 0
    lateinit var str_gStep: String
    var gCount: Int = 1
    lateinit var str_gText: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group_info)

        tvName = findViewById(R.id.gName)
        tvNumber = findViewById(R.id.gNumber)
        tvStep = findViewById(R.id.gStep)
        tvText = findViewById(R.id.gText)
        btnEnjoy = findViewById(R.id.btnEnjoy)

        var id: String = intent.getStringExtra("userId").toString()

        var myHelper: myDBHelper = myDBHelper(this)

        val intent = intent
        str_gName = intent.getStringExtra("intent_name").toString()

        dbManager = DBManager(this, "groupDB", null, 1)
        sqlitedb = dbManager.readableDatabase

        var cursor: Cursor
        cursor = sqlitedb.rawQuery("SELECT * FROM groupDB WHERE gName = '" + str_gName + "';", null)

        if (cursor.moveToNext()) {
            gNumber = cursor.getInt((cursor.getColumnIndex("gNumber")))
            str_gStep = cursor.getString((cursor.getColumnIndex("gStep")))
            str_gText = cursor.getString((cursor.getColumnIndex("gText"))).toString()
            gCount = cursor.getInt((cursor.getColumnIndex("gCount")))
        }

        cursor.close()
        sqlitedb.close()
        dbManager.close()

        tvName.text = str_gName
        tvNumber.text ="$gCount / $gNumber"
        tvStep.text = str_gStep
        tvText.text = str_gText + "\n"

        btnEnjoy.setOnClickListener {
            sqlitedb = myHelper.writableDatabase
            // 멤버 업데이트
            sqlitedb.execSQL("UPDATE groupDB SET gMember${gCount+1} = '" + id + "' WHERE gName ='" + str_gName + "';")
            gCount++
            // 현재 인원수 업데이트
            sqlitedb.execSQL("UPDATE groupDB SET gCount = " + gCount.toString() + " WHERE gName = '" + str_gName + "';")
            sqlitedb.close()
            Toast.makeText(applicationContext, "참여 완료!", Toast.LENGTH_SHORT).show()
        }
    }

    inner class myDBHelper(context: Context) : SQLiteOpenHelper(context, "groupDB", null, 1) {
        // 테이블 생성
        override fun onCreate(db: SQLiteDatabase?) {
            db!!.execSQL("CREATE TABLE groupDB (gName String, gNumber INTEGER, gText String, gStep String, gCount INTEGER, gMember1 String, gMember2 String, gMember3 String, gMember4 String);")
        }

        // 테이블 삭제 후 다시 생성
        override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
            db!!.execSQL("DROP TABLE IF EXISTS groupDB")
            onCreate(db)
        }
    }
}
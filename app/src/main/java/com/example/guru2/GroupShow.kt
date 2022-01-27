package com.example.guru2

import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView

class GroupShow : AppCompatActivity() {
    lateinit var gDbManager: gDBManager
    lateinit var sqlitedb: SQLiteDatabase
    lateinit var layout: LinearLayout
    lateinit var btnMaking: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group_show)

        gDbManager = gDBManager(this, "groupDB", null, 1)
        sqlitedb = gDbManager.readableDatabase

        layout = findViewById(R.id.group)

        btnMaking = findViewById(R.id.btnMaking)

        var cursor: Cursor
        cursor = sqlitedb.rawQuery("SELECT * FROM groupDB;", null)

        var num: Int = 0
        while (cursor.moveToNext()) {
            var str_gName = cursor.getString((cursor.getColumnIndex("gName"))).toString()
            var str_gText = cursor.getString((cursor.getColumnIndex("gText"))).toString()

            var layout_item: LinearLayout = LinearLayout(this)
            layout_item.orientation = LinearLayout.VERTICAL
            layout_item.setPadding(20, 10, 20, 10)
            layout_item.id = num
            layout_item.setTag(str_gName)

            // 그룹 이름 보여 주기
            var tvName: TextView = TextView(this)
            tvName.text = str_gName
            tvName.textSize = 30F
            tvName.setBackgroundColor(Color.LTGRAY)
            layout_item.addView(tvName)

            // 그룹 한줄 소개 보여주기
            var tvText: TextView = TextView(this)
            tvText.text = str_gText
            layout_item.addView(tvText)

            // 줄 클릭했을 때
            layout_item.setOnClickListener {
                val intent = Intent(this, GroupInfo::class.java)
                intent.putExtra("intent_name", str_gName)
                startActivity(intent)
            }
            layout.addView(layout_item)
            num++
        }

        cursor.close()
        sqlitedb.close()
        gDbManager.close()

        btnMaking.setOnClickListener {
            val intent = Intent(this, GroupMaking::class.java)
            startActivity(intent)
        }
    }
}
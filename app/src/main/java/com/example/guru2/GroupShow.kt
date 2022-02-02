package com.example.guru2

import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class GroupShow : AppCompatActivity() {
    lateinit var gDbManager: gDBManager
    lateinit var sqlitedb: SQLiteDatabase
    lateinit var layout: LinearLayout
    lateinit var btnMaking: FloatingActionButton
    lateinit var mainBtn2: ImageButton
    lateinit var myPageBtn2: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group_show)

        gDbManager = gDBManager(this, "groupDB", null, 1)
        sqlitedb = gDbManager.readableDatabase

        layout = findViewById(R.id.group)

        btnMaking = findViewById(R.id.btnMaking)

        mainBtn2 = findViewById(R.id.mainBtn2)
        myPageBtn2 = findViewById(R.id.myPageBtn2)

        // 사용자 아이디 가져오기
        var id: String = intent.getStringExtra("userId").toString()

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
                // 사용자 아이디 데이터 보내기
                intent.putExtra("userId", id)
                // 그룹 이름 보내기
                intent.putExtra("intent_name", str_gName)
                startActivity(intent)
            }
            layout.addView(layout_item)
            num++
        }

        cursor.close()
        sqlitedb.close()
        gDbManager.close()

        // 그룹 생성 페이지로 이동
        btnMaking.setOnClickListener {
            val intent = Intent(this, GroupMaking::class.java)
            // 사용자 아이디 데이터
            //intent.putExtra("userId", id)
            startActivity(intent)
        }

        // 메인 페이지로 이동
        mainBtn2.setOnClickListener {
            val intent = Intent(this, StepCounter::class.java)
            startActivity(intent)
        }

        // 마이 페이지로 이동
        myPageBtn2.setOnClickListener {
            val intent = Intent(this, MyPage::class.java)
            startActivity(intent)
        }
    }
}
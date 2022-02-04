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
    lateinit var gDBManager: gDBManager
    lateinit var sqlitedb: SQLiteDatabase
    lateinit var layout: LinearLayout
    lateinit var btnMaking: FloatingActionButton
    lateinit var mainBtn2: ImageButton
    lateinit var myPageBtn2: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group_show)

        gDBManager = gDBManager(this, "groupDB", null, 1)
        sqlitedb = gDBManager.readableDatabase // 읽기 전용

        layout = findViewById(R.id.group)

        btnMaking = findViewById(R.id.btnMaking)

        mainBtn2 = findViewById(R.id.mainBtn2)
        myPageBtn2 = findViewById(R.id.myPageBtn2)

        // 유저 아이디 갖고 오기
        var userID = intent.getStringExtra("id").toString()

        var cursor: Cursor
        // groupDB 테이블 정보 모두 가져오기
        cursor = sqlitedb.rawQuery("SELECT * FROM groupDB;", null)

        var num: Int = 0
        while (cursor.moveToNext()) {
            var str_gName = cursor.getString((cursor.getColumnIndex("gName"))).toString() // column 이름이 gName인 데이터 (그룹명)
            var str_gText = cursor.getString((cursor.getColumnIndex("gText"))).toString() // column 이름이 gText인 데이터 (한 줄 소개)

            var layout_item: LinearLayout = LinearLayout(this)
            layout_item.orientation = LinearLayout.VERTICAL // 수직으로
            layout_item.setPadding(20, 10, 20, 20) // 패딩 크기 설정
            layout_item.id = num
            layout_item.setTag(str_gName)

            // 그룹 이름 보여 주기
            var tvName: TextView = TextView(this)
            tvName.text = str_gName
            tvName.textSize = 27F
            tvName.setBackgroundColor(Color.parseColor("#8EC9A4"))
            layout_item.addView(tvName)

            // 그룹 한줄 소개 보여주기
            var tvText: TextView = TextView(this)
            tvText.text = str_gText
            tvText.textSize = 18F
            layout_item.addView(tvText)

            // 줄 클릭했을 때
            layout_item.setOnClickListener {
                val intent = Intent(this, GroupInfo::class.java)
                // 그룹 이름 보내기
                intent.putExtra("intent_name", str_gName)
                startActivity(intent)
            }
            layout.addView(layout_item)
            num++
        }

        cursor.close()
        sqlitedb.close()
        gDBManager.close()

        // 그룹 생성 페이지로 이동
        btnMaking.setOnClickListener {
            val intent = Intent(this, GroupMaking::class.java)
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
            intent.putExtra("id", userID)
            startActivity(intent)
        }
    }
}
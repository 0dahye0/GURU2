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

    // groupDB 테이블을 사용하기 위해서
    lateinit var dbManager: DBManager
    lateinit var sqlitedb: SQLiteDatabase // groupDB에 사용
    lateinit var sqlDB: SQLiteDatabase // personnelDB에 사용

    lateinit var tvName: TextView // 그룹명
    lateinit var tvNumber: TextView // 그룹 인원수
    lateinit var tvStep: TextView // 그룹 목표 걸음수
    lateinit var tvText: TextView // 그룹 한 줄 소개
    lateinit var btnEnjoy: Button // 참여하기 버튼

    lateinit var str_gName: String // 그룹 리스트에서 누른 그룹 이름
    var gNumber: Int = 0 // 해당 그룹 인원수
    lateinit var str_gStep: String // 해당 그룹 목표 걸음수
    var gCount: Int = 1 // 해당 그룹 현재 참여 중 인원수
    lateinit var str_gText: String // 해당 그룹 한 줄 소개

    var id: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group_info)

        // 변수 id와 연결
        tvName = findViewById(R.id.gName)
        tvNumber = findViewById(R.id.gNumber)
        tvStep = findViewById(R.id.gStep)
        tvText = findViewById(R.id.gText)
        btnEnjoy = findViewById(R.id.btnEnjoy)

        // helper 선언
        var myHelper: myDBHelper = myDBHelper(this)
        var myHelper2: myDBHelper2 = myDBHelper2(this)

        // 리스트에서 선택한 그룹의 이름을 가져오기
        val intent = intent
        str_gName = intent.getStringExtra("intent_name").toString()

        dbManager = DBManager(this, "groupDB", null, 1)
        sqlitedb = dbManager.readableDatabase // 읽기 전용

        // groupDB에서 선택한 그룹의 데이터를 조회함
        var cursor: Cursor
        cursor = sqlitedb.rawQuery("SELECT * FROM groupDB WHERE gName = '" + str_gName + "';", null)

        // 각 데이터들을 변수에 담음
        if (cursor.moveToNext()) {
            gNumber = cursor.getInt((cursor.getColumnIndex("gNumber")))
            str_gStep = cursor.getString((cursor.getColumnIndex("gStep")))
            str_gText = cursor.getString((cursor.getColumnIndex("gText"))).toString()
            gCount = cursor.getInt((cursor.getColumnIndex("gCount")))
        }

        cursor.close()
        sqlitedb.close()
        dbManager.close()

        // 데이터 표기
        tvName.text = str_gName
        tvNumber.text ="$gCount / $gNumber"
        tvStep.text = str_gStep
        tvText.text = str_gText + "\n"

        // 참여하기 버튼을 눌렀을 때
        btnEnjoy.setOnClickListener {
            sqlitedb = myHelper.writableDatabase // 읽고 쓰기 가능

            // 멤버 업데이트
            sqlDB = myHelper2.writableDatabase // 읽고 쓰기 가능
            sqlDB.execSQL("UPDATE groupDB SET gMember${gCount+1} = '" + id + "' WHERE gName ='" + str_gName + "';")
            gCount++ // 인원수 한 명 증가
            sqlDB.close()

            // 현재 인원수 groupDB에 업데이트
            sqlitedb.execSQL("UPDATE groupDB SET gCount = " + gCount.toString() + " WHERE gName = '" + str_gName + "';")
            sqlitedb.close()
            Toast.makeText(applicationContext, "참여 완료!", Toast.LENGTH_SHORT).show()
        }
    }

    // groupDB
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

    // personnelDB
    inner class myDBHelper2(context: Context) : SQLiteOpenHelper(context, "personnelDB", null, 1) {
        override fun onCreate(db: SQLiteDatabase?) {
            db!!.execSQL("CREATE TABLE personnel (id text, pwd text, nickname text)")
        }

        override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
            db!!.execSQL("DROP TABLE IF EXISTS personnelDB")
            onCreate(db)
        }
    }
}
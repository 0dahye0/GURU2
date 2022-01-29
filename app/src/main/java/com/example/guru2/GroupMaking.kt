package com.example.guru2

import android.content.Context
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class GroupMaking : AppCompatActivity() {

    lateinit var myHelper: myDBHelper
    lateinit var edtName: EditText
    lateinit var edtNumber: EditText
    lateinit var edtTextBox: EditText
    lateinit var btnOk: Button
    lateinit var sqlDB: SQLiteDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group_making)

        edtName = findViewById(R.id.edtName)
        edtNumber = findViewById(R.id.edtNumber)
        edtTextBox = findViewById(R.id.edtTextBox)
        btnOk = findViewById(R.id.btnOk)

        // 사용자 아이디 가져오기
        var id: String = intent.getStringExtra("userId").toString()

        myHelper = myDBHelper(this)

        btnOk.setOnClickListener {
            if (edtNumber.text.toString().toInt() <= 4) {
                sqlDB = myHelper.writableDatabase
                // 그룹 생성하기
                sqlDB.execSQL("INSERT INTO groupDB VALUES ('" + edtName.text.toString() + "', " +
                        edtNumber.text.toString() + ", '" + edtTextBox.text.toString() + "', " + 1.toString() + ", ' ', ' ', ' ', ' ');")
                // 만든 사람 그룹 멤버로 추가하기
                sqlDB.execSQL("UPDATE groupDB SET gMember1 = " + id + " WHERE gName = '" + edtName.text.toString() + "';")
                sqlDB.close()
                Toast.makeText(applicationContext, "${edtName.text} 그룹이 생성되었습니다!", Toast.LENGTH_SHORT).show()
                
                // 만들어졌으면 그룹 목록으로 화면 전환
                val intent = Intent(this, GroupShow::class.java)
                startActivity(intent)
            } else {
                Toast.makeText(applicationContext, "인원수를 4명 이하로 설정해 주세요!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    inner class myDBHelper(context: Context) : SQLiteOpenHelper(context, "groupDB", null, 1) {
        // 테이블 생성
        override fun onCreate(db: SQLiteDatabase?) {
            db!!.execSQL("CREATE TABLE groupDB (gName String, gNumber INTEGER, gText String, gCount INTEGER, gMember1 String, gMember2 String, gMember3 String, gMember4 String);")
        }

        // 테이블 삭제 후 다시 생성
        override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
            db!!.execSQL("DROP TABLE IF EXISTS groupDB")
            onCreate(db)
        }
    }
}
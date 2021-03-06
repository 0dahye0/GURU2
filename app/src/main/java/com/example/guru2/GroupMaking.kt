package com.example.guru2

import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class GroupMaking : AppCompatActivity() {

    lateinit var myHelper: myDBHelper // groupDB 헬퍼
    lateinit var edtName: EditText
    lateinit var btnGname: Button
    lateinit var edtNumber: EditText
    lateinit var edtStep: EditText
    lateinit var edtTextBox: EditText
    lateinit var btnOk: Button
    lateinit var sqlgDB: SQLiteDatabase // groupDB

    lateinit var name: String
    lateinit var id: String
    var available: Boolean = false // 중복 확인 위한 변수

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group_making)

        // 변수 id와 연결
        edtName = findViewById(R.id.edtName)
        edtNumber = findViewById(R.id.edtNumber)
        edtStep = findViewById(R.id.edtStep)
        edtTextBox = findViewById(R.id.edtTextBox)
        btnGname = findViewById(R.id.btnGname)
        btnOk = findViewById(R.id.btnOk)

        var userID = intent.getStringExtra("id").toString()

        // groupDB
        myHelper = myDBHelper(this)

        // 그룹명 중복확인
        btnGname.setOnClickListener {
            var gName = edtName.text.toString() // 그룹명

            sqlgDB = myHelper.readableDatabase // 읽기 전용

            var cursor : Cursor
            name = "SELECT gName FROM groupDB WHERE gName = '" + gName + "'"
            cursor = sqlgDB.rawQuery(name, null)

            // 해당 이름이 데이터 테이블에 있는지 확인
            if(cursor.getCount() != 0) {
                available = false // 사용 불가능 (중복)
                Toast.makeText(applicationContext, "존재하는 그룹명입니다!", Toast.LENGTH_SHORT).show()
            }
            else {
                available = true // 사용 가능
                Toast.makeText(applicationContext, "사용 가능한 그룹명입니다!", Toast.LENGTH_SHORT).show()
            }
        }

        btnOk.setOnClickListener {
            // 인원수가 4 이하이고 그룹명 중복 아닐 때
            if (edtNumber.text.toString().toInt() <= 4 && available) {
                sqlgDB = myHelper.writableDatabase // groupDB
                // 그룹 생성하기
                sqlgDB.execSQL("INSERT INTO groupDB VALUES ('" + edtName.text.toString() + "', " +
                        edtNumber.text.toString() + ", '" + edtTextBox.text.toString() + "', '" + edtStep.text.toString() + "', " + 1.toString() + ", ' ', ' ', ' ', ' ');")

                // 만든 사람 그룹 멤버로 추가하기
                sqlgDB.execSQL("UPDATE groupDB SET gMember1 = '" + userID + "' WHERE gName = '" + edtName.text.toString() + "';")
                sqlgDB.close()

                // 그룹 생성됨 메시지
                Toast.makeText(applicationContext, "${edtName.text} 그룹이 생성되었습니다!", Toast.LENGTH_SHORT).show()

                // 만들어졌으면 그룹 목록으로 화면 전환
                val intent = Intent(this, GroupShow::class.java)
                intent.putExtra("id", userID) // 유저 아이디 전달
                startActivity(intent)
            } else {
                // 그룹명 중복 아니지만 인원수 4 초과할 때
                if (edtNumber.text.toString().toInt() > 4 && available) {
                    Toast.makeText(applicationContext, "인원수를 4명 이하로 설정해 주세요!", Toast.LENGTH_SHORT).show()
                }
                // 인원수가 4 이하이지만 그룹명이 중복일 때
                else if (edtNumber.text.toString().toInt() <= 4 && !available) {
                    Toast.makeText(applicationContext, "그룹명을 바꿔 주세요!", Toast.LENGTH_SHORT).show()
                }
            }
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
}
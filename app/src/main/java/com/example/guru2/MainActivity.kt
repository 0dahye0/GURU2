package com.example.guru2

import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class MainActivity : AppCompatActivity() {

    lateinit var dbManager: DBManager
    lateinit var sqlitedb : SQLiteDatabase

    lateinit var editTextId : EditText //아이디 editText
    lateinit var editTextPassword : EditText //패스워드 editText
    lateinit var loginBtn : Button //로그인 버튼
    lateinit var signUpBtn : Button //회원가입 버튼

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //위젯 연결
        editTextId = findViewById(R.id.editTextId)
        editTextPassword = findViewById(R.id.editTextPassword)
        loginBtn = findViewById(R.id.LoginBtn)
        signUpBtn = findViewById(R.id.SignUpBtn)

        dbManager = DBManager(this, "personnelDB", null, 1)

        //로그인버튼 이벤트처리
        loginBtn.setOnClickListener {
            var edid = editTextId.text.toString()
            var edpwd = editTextPassword.text.toString()
            var idFlag = 0
            var pwdFlag = 0

            //아이디 또는 패스워드를 입력하지 않았을 때
            if(edid.length == 0 || edpwd.length == 0){
                Toast.makeText(applicationContext, "아이디와 패스워드는 필수 입력사항입니다.", Toast.LENGTH_LONG).show()
            }
            else{//아이디와 패스워드를 모두 입력했을 때
                var dbid : String //db에서 받아온 id
                var dbpwd : String //db에서 받아온 pwd

                sqlitedb=dbManager.readableDatabase //데이터베이스를 읽기모드로
                var cursor : Cursor
                cursor = sqlitedb.rawQuery("SELECT id, pwd FROM personnel", null)

                while(cursor.moveToNext()){
                    dbid = cursor.getString(0)
                    dbpwd = cursor.getString(1)

                    if(dbid.equals(edid)){ //사용자가 editTextId 칸에 적은 아이디가 dbid와 동일한 경우
                        idFlag=1

                        if(dbpwd.equals(edpwd)){ //사용자가 editTextPassword칸에 적은 패스워드가 dbpwd와 동일한 경우
                            pwdFlag=1
                            Toast.makeText(applicationContext, "로그인 성공", Toast.LENGTH_LONG).show() //아이디, 패스워드가 둘 다 일치했음으로 로그인 성공
                            var intent = Intent(this, StepCounter::class.java)
                            intent.putExtra("id", edid)
                            startActivity(intent)
                        }
                        else{
                            Toast.makeText(applicationContext, "아이디 또는 패스워드가 틀렸습니다.", Toast.LENGTH_LONG).show() //아이디는 일치, 패스워드는 불일치한 경우

                        }
                    }
                }
                cursor.close()
                sqlitedb.close()
                if(idFlag==0 && pwdFlag==0){//아이디와 패스워드 모두 불일치한 경우
                    Toast.makeText(applicationContext, "아이디 또는 패스워드가 틀렸습니다.", Toast.LENGTH_LONG).show()
                }

            }
        }

        //로그인 버튼 이벤트 처리
        loginBtn.setOnClickListener {
            var intent = Intent(this, StepCounter::class.java)
            // 사용자 아이디 넘기기
            intent.putExtra("userId", editTextId.text.toString())
            startActivity(intent)
        }

        //회원가입 버튼 이벤트 처리
        signUpBtn.setOnClickListener { //회원가입 액티비티로 넘어감
            var intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }
    }
}
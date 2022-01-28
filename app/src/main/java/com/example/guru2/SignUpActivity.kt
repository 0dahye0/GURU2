package com.example.guru2

import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class SignUpActivity : AppCompatActivity() {

    lateinit var dbManager:DBManager
    lateinit var sqlitedb: SQLiteDatabase

    lateinit var SignUpId : EditText
    lateinit var SignUpNickName : EditText
    lateinit var SignUpStepValue  :EditText
    lateinit var SignUpPassword : EditText
    lateinit var SignUpPasswordCheck : EditText
    lateinit var SignUpIdCheckBtn : Button
    lateinit var SignUpNickNameCheckBtn : Button
    lateinit var SignUpFinishBtn : Button

    lateinit var sql:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        SignUpId = findViewById(R.id.SingUpId)
        SignUpNickName = findViewById(R.id.SignUpNickName)
        SignUpStepValue = findViewById(R.id.SignUpStepValue)
        SignUpPassword = findViewById(R.id.SignUpPassword)
        SignUpPasswordCheck = findViewById(R.id.SignUpPasswordCheck)
        SignUpIdCheckBtn = findViewById(R.id.SignUpIdCheckBtn)
        SignUpNickNameCheckBtn = findViewById(R.id.SignUpNickNameCheckBtn)
        SignUpFinishBtn = findViewById(R.id.SignUpFinishBtn)

        dbManager = DBManager(this, "personnelDB", null, 1)

        //아이디 중복확인
        SignUpIdCheckBtn.setOnClickListener {
            var sid = SignUpId.text.toString()

            sqlitedb = dbManager.readableDatabase

            var cursor : Cursor
            sql = "SELECT id FROM personnel WHERE id='"+sid+"'"
            cursor = sqlitedb.rawQuery(sql, null)

            if(cursor.getCount()!=0){
                Toast.makeText(applicationContext, "존재하는 아이디입니다.", Toast.LENGTH_LONG).show()
            }
            else{
                Toast.makeText(applicationContext, "사용가능한 아이디입니다.", Toast.LENGTH_LONG).show()
            }
        }

        //닉네임 중복확인
        SignUpNickNameCheckBtn.setOnClickListener {
            var snickname = SignUpNickName.text.toString()

            sqlitedb = dbManager.readableDatabase

            var cursor : Cursor
            sql = "SELECT nickname FROM personnel WHERE nickname='"+snickname+"'"
            cursor = sqlitedb.rawQuery(sql, null)

            if(cursor.getCount()!=0){
                Toast.makeText(applicationContext, "존재하는 닉네임입니다.", Toast.LENGTH_SHORT).show()
            }
            else{
                Toast.makeText(applicationContext, "사용가능한 닉네임입니다.", Toast.LENGTH_SHORT).show()
            }
        }

        SignUpFinishBtn.setOnClickListener {
            var sid = SignUpId.text.toString()
            var snickname = SignUpNickName.text.toString()
            var sstepvalue = SignUpStepValue.text.toString()
            var spwd = SignUpPassword.text.toString()
            var spwdcheck = SignUpPasswordCheck.text.toString()

            //아이디, 패스워드, 닉네임 빈칸일 때
            if(sid.length==0 || snickname.length==0 || sstepvalue.length==0 ||spwd.length==0 || spwdcheck.length==0){
                Toast.makeText(applicationContext, "아이디와 닉네임, 목표 걸음 수와 패스워드는 필수 입력사항입니다.", Toast.LENGTH_SHORT).show()
            }
            else{
                if(!spwd.equals(spwdcheck)){
                    Toast.makeText(applicationContext, "패스워드가 일치하지 않습니다.", Toast.LENGTH_LONG).show()
                }
                else{
                    sqlitedb = dbManager.writableDatabase
                    sqlitedb.execSQL("INSERT INTO personnel VALUES ('"+ sid +"','"+spwd+"', '"+ snickname +"');")//아이디, 비번, 닉네임
                    sqlitedb.close()
                    Toast.makeText(applicationContext, "가입이 완료되었습니다", Toast.LENGTH_LONG).show()
                    //인텐트 로그인 홈으로 이동.
                    var intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                }
            }
        }
    }
}
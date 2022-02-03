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

    lateinit var SignUpId : EditText //아이디
    lateinit var SignUpNickName : EditText //닉네임
    lateinit var SignUpStepValue  :EditText //목표걸음수
    lateinit var SignUpPassword : EditText //패스워드
    lateinit var SignUpPasswordCheck : EditText //패스워드확인
    lateinit var SignUpIdCheckBtn : Button //아이디 중복확인 버튼
    lateinit var SignUpNickNameCheckBtn : Button //닉네임 중복확인 버튼
    lateinit var SignUpFinishBtn : Button //회원가입완료 버튼

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

        //아이디 중복확인 버튼 이벤트 처리
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
                Toast.makeText(applicationContext, "사용 가능한 아이디입니다.", Toast.LENGTH_LONG).show()
            }
        }

        //닉네임 중복확인 버튼 이벤트 처리
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
                Toast.makeText(applicationContext, "사용 가능한 닉네임입니다.", Toast.LENGTH_SHORT).show()
            }
        }

        //회원가입 완료 버튼 이벤트처리
        SignUpFinishBtn.setOnClickListener {
            var sid = SignUpId.text.toString()
            var snickname = SignUpNickName.text.toString()
            var sstepvalue = SignUpStepValue.text.toString()
            var spwd = SignUpPassword.text.toString() //패스워드
            var spwdcheck = SignUpPasswordCheck.text.toString() //패스워드 확인

            //아이디, 닉네임, 목표걸음수, 패스워드, 패스워드 확인 칸이 빈칸일 때 나타나는 이벤트 처리
            if(sid.length==0 || snickname.length==0 || sstepvalue.length==0 ||spwd.length==0 || spwdcheck.length==0){
                Toast.makeText(applicationContext, "아이디와 닉네임, 목표 걸음 수와 패스워드는 필수 입력사항입니다.", Toast.LENGTH_SHORT).show()
            }
            else{//모든 빈칸을 입력했을 때 이벤트 처리
                if(!spwd.equals(spwdcheck)){ //패스워드와 패스워드 확인 칸의 문자열이 일치하지 않을 경우
                    Toast.makeText(applicationContext, "패스워드가 일치하지 않습니다.", Toast.LENGTH_LONG).show()
                }
                else{//모든 빈칸이 올바르게 입력된 경우
                    sqlitedb = dbManager.writableDatabase
                    sqlitedb.execSQL("INSERT INTO personnel VALUES ('"+ sid +"','"+spwd+"', '"+ snickname +"');")//db에 정보 삽입
                    sqlitedb.close()
                    Toast.makeText(applicationContext, "가입이 완료되었습니다", Toast.LENGTH_LONG).show()

                    //로그인 홈으로 이동.
                    var intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                }
            }
        }
    }
}
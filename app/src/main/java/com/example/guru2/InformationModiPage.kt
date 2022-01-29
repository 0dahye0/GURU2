package com.example.guru2

import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast

class InformationModiPage : AppCompatActivity()  {

    lateinit var dbManager:DBManager
    lateinit var sqlitedb: SQLiteDatabase

    lateinit var originalNickName : TextView //기존 닉네임
    lateinit var newNickName : EditText //변경할 닉네임
    lateinit var newNickNameCheckBtn : Button //닉네임 중복 확인 버튼
    lateinit var orginalPassWord : EditText //기존 패스워드
    lateinit var newPassWord : EditText //새로운 패스워드
    lateinit var newPassWordCheck : EditText //새로운 패스워드 재확인
    lateinit var ModiBtn : Button //수정하기 버튼
    lateinit var sql:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_information_modi_page)

        originalNickName = findViewById(R.id.originalNickName) //기존닉네임
        newNickName = findViewById(R.id.newNickName) //새로운 닉네임
        newNickNameCheckBtn = findViewById(R.id.newNickNameCheckBtn) //닉네임중복버튼

        orginalPassWord = findViewById(R.id.orginalPassWord) //기존 패스워드
        newPassWord = findViewById(R.id.newPassWord) //새로운 패스워드
        newPassWordCheck = findViewById(R.id.newPassWordCheck) //새로운 패스워드 확인

        ModiBtn = findViewById(R.id.ModiBtn) //최종 수정버튼


        var id = intent.getStringExtra("id") //mypage에서 보낸 인텐트
        var nick = intent.getStringExtra("nick")//MyPage에서 받아온 닉네임
        originalNickName.setText(nick)

        dbManager = DBManager(this, "personnelDB", null, 1)


        //닉네임중복확인버튼
        newNickNameCheckBtn.setOnClickListener {
            sqlitedb = dbManager.readableDatabase
            var cursor: Cursor
            sql="SELECT nickname FROM personnel WHERE nickname='"+newNickName.text+"'"
            cursor = sqlitedb.rawQuery(sql, null)//디비에서 해당 닉네임 가져오기

            if(cursor.getCount()!=0){
                Toast.makeText(applicationContext, "존재하는 닉네임입니다.", Toast.LENGTH_SHORT).show()
            }
            else{
                Toast.makeText(applicationContext, "사용가능한 닉네임입니다.", Toast.LENGTH_SHORT).show()
                sqlitedb = dbManager.writableDatabase
                sql = "UPDATE personnel SET nickname='"+ newNickName.text+"' WHERE id =" +id
                sqlitedb.execSQL(sql)//계속 오류 발생
                sqlitedb.close()
            }
        }


        //수정하기버튼->대화상자 경고문... 현재 비밀번호가 틀린 경우와, 새 비밀번호와 새 비밀번호 확인이 다른 경우우
        ModiBtn.setOnClickListener {
            var oripwd = orginalPassWord.text.toString() //기존패스워드
            var newpwd = newPassWord.text.toString() //새로운 패스워드
            var newpwdch = newPassWordCheck.text.toString() //새로운 패스워드 확인
            var pwdFlag1 = 0
            var pwdFlag2 = 0
            var dbpwd=""

            sqlitedb = dbManager.readableDatabase
            var cursor: Cursor
            sql = "SELECT pwd FROM personnel WHERE id='"+id+"'"//id에 저장된 password를 가져온다
            cursor = sqlitedb.rawQuery(sql, null)
            while(cursor.moveToNext()){
                dbpwd = cursor.getString(0)
            }
            Toast.makeText(applicationContext, "비밀번호는"+dbpwd, Toast.LENGTH_LONG).show()

            if(!oripwd.equals(dbpwd)){//비밀번호가 동일하지 않으면 -> pwdFlage=0 으로 고정
                //Toast.makeText(applicationContext, "기존 비밀번호가 틀렸습니다.", Toast.LENGTH_LONG).show()
            }
            else{//기존 비밀번호 동일
                pwdFlag1=1

                if(!newpwd.equals(newpwdch)){//비밀번호 입력과 비밀번호 확인 칸의 문자열이 동일하지 않을 때
                    Toast.makeText(applicationContext, "새로운 비밀번호 확인을 다시 해주세요.", Toast.LENGTH_SHORT).show()
                }
                else{//기존 비밀번호, 새로운 비밀번호 동일
                    pwdFlag2=1

                    if(pwdFlag1==1&&pwdFlag2==1){
                        sqlitedb = dbManager.writableDatabase
                        sql = "UPDATE personnel SET pwd ='"+ newPassWord.text+"' WHERE id ="+ id
                        sqlitedb.execSQL(sql)
                        sqlitedb.close()

                        var intent = Intent(this, MyPage::class.java)
                        intent.putExtra("nick", newNickName.text)//MyPage에서 닉네임 변경된 내용 보여주기
                        setResult(RESULT_OK, intent)
                        finish()
                    }
                    else{
                        Toast.makeText(applicationContext, "기존 비밀번호 혹은 새로운 비밀번호 확인을 다시해주세요", Toast.LENGTH_SHORT).show()
                    }
                }

            }

        }
    }
}
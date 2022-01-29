package com.example.guru2

import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MyPage : AppCompatActivity() {

    lateinit var dbManager:DBManager
    lateinit var sqlitedb: SQLiteDatabase

    lateinit var personId : TextView
    lateinit var personNickName : TextView
    lateinit var personWalk : TextView
    lateinit var personTeam : TextView
    lateinit var informationmodiBtn : Button
    lateinit var logout : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_page)

        personId = findViewById(R.id.personId)
        personNickName = findViewById(R.id.personNickName)
        personWalk = findViewById(R.id.personWalk)
        personTeam = findViewById(R.id.personTeam)
        informationmodiBtn = findViewById(R.id.informationmodiBtn)
        logout = findViewById(R.id.logout)


        dbManager = DBManager(this, "personnelDB", null, 1)

        var id = ""
        var nickNa = ""
        var walk: String
        var team: String

        sqlitedb = dbManager.readableDatabase
        var cursor: Cursor
        cursor = sqlitedb.rawQuery("SELECT id, nickname FROM personnel", null) // 디비에서 해당 로그인 아이디,닉네임 가져오기

        // 마이페이지에서 닉네임, 아이디 안 뜨는 거 수정해야 할 것 같아요!!
        while (cursor.moveToNext()) {
            id = cursor.getString(0) //id
            nickNa = cursor.getString(1) //nickname
        }
        personId.setText(id)
        personNickName.setText(nickNa)

        cursor.close()
        sqlitedb.close()
        //총 걸음 수 가져오기
        //소속 팀 가져오기

        //개인정보수정버튼 누르면 페이지 이동
        informationmodiBtn.setOnClickListener {
            var intent1 = Intent(this, InformationModiPage::class.java)
            intent1.putExtra("id", personId.text.toString()) //인텐트로 id를 정보수정페이지로 넘김
            startActivity(intent1)

            var intent2 = Intent(this, InformationModiPage::class.java)
            intent2.putExtra("nick", personNickName.text.toString())
            startActivityForResult(intent2,0)
        }

        //로그아웃버튼 누르면 로그아웃
        logout.setOnClickListener {
            var intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
        }

    }

    @Override
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(resultCode == RESULT_OK){
            var nick = data?.getStringExtra("nick")
            personNickName.text = nick
            Toast.makeText(applicationContext, "수정이 완료되었습니다.", Toast.LENGTH_SHORT).show()
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

}
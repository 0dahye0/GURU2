package com.example.guru2

import android.app.Activity
import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.lang.Exception

class MyPage : AppCompatActivity() {

    lateinit var dbManager:DBManager
    lateinit var sqlitedb: SQLiteDatabase

    lateinit var MyImage : ImageView
    lateinit var MYImageChangeBtn : Button
    lateinit var personId : TextView
    lateinit var personNickName : TextView
    lateinit var personWalk : TextView
    lateinit var personTeam : TextView
    lateinit var informationmodiBtn : Button
    lateinit var logout : Button
    private val GALLERY = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_page)

        MyImage = findViewById(R.id.MyImage)
        MYImageChangeBtn = findViewById(R.id.MyImageChangeBtn)
        personId = findViewById(R.id.personId)
        personNickName = findViewById(R.id.personNickName)
        personWalk = findViewById(R.id.personWalk)
        personTeam = findViewById(R.id.personTeam)
        informationmodiBtn = findViewById(R.id.informationmodiBtn)
        logout = findViewById(R.id.logout)

        //새로운 닉네임 받는다면
        var newnickname = intent.getStringExtra("nick")
        if(newnickname != null){
            personNickName.setText(newnickname)
        }


        dbManager = DBManager(this, "personnelDB", null, 1)

        var id = ""
        var nickNa = ""
        var walk: String
        var team: String

        //프로필 사진 변경버튼
        MYImageChangeBtn.setOnClickListener {
            val intent:Intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.setType("image/*")
            startActivityForResult(intent, GALLERY)
        }

        sqlitedb = dbManager.readableDatabase
        var cursor: Cursor
        cursor = sqlitedb.rawQuery("SELECT id, nickname FROM personnel", null) // 디비에서 해당 로그인 아이디,닉네임 가져오기

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

            var intent = Intent(this, InformationModiPage::class.java)
            intent.putExtra("id", personId.text.toString()) //인텐트로 id를 정보수정페이지로 넘김
            intent.putExtra("nick", personNickName.text.toString())
            startActivity(intent)
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
        super.onActivityResult(requestCode, resultCode, data)

        if(resultCode== Activity.RESULT_OK){
            if(requestCode==GALLERY){
                var ImnageData: Uri? = data?.data
                //Toast.makeText(this, ImnageData.toString(), Toast.LENGTH_SHORT).show()
                try{
                    val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, ImnageData)
                    MyImage.setImageBitmap(bitmap)
                }
                catch (e: Exception){
                    e.printStackTrace()
                }
            }
        }
    }

}
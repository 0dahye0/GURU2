

package com.example.guru2

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
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

    lateinit var myHelper: myDBHelper
    lateinit var sqlgDB: SQLiteDatabase

    lateinit var MyImage : ImageView //프로필 사진
    lateinit var MYImageChangeBtn : Button //프로필 사진 변경 버튼
    lateinit var personId : TextView //사용자 아이디
    lateinit var personNickName : TextView //사용자 닉네임
    lateinit var personWalk : TextView //사용자 걸음 수
    lateinit var personTeam : TextView //사용자 소속 팀
    lateinit var informationmodiBtn : Button //정보 수정 버튼
    lateinit var logout : Button //로그아웃 버튼

    private val GALLERY = 1 //프로필 사진 이벤트 처리와 관련된 변수

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

        //새로운 닉네임을 인텐트로 받을 경우 이벤트처리
        var newnickname = intent.getStringExtra("nick")
        if(newnickname != null){
            personNickName.setText(newnickname)
        }

        dbManager = DBManager(this, "personnelDB", null, 1)

        //변수설정
        var id = ""
        var nickNa = ""
        var walk=""
        var team=""

        //프로필 사진 변경버튼 이벤트 처리
        MYImageChangeBtn.setOnClickListener {
            val intent:Intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.setType("image/*")
            startActivityForResult(intent, GALLERY)
        }

        sqlitedb = dbManager.readableDatabase
        var cursor: Cursor
        cursor = sqlitedb.rawQuery("SELECT id, nickname, walk FROM personnel", null) // 디비에서 해당 로그인 아이디,닉네임 가져오기

        while (cursor.moveToNext()) {
            id = cursor.getString(0) //id 가져오기
            nickNa = cursor.getString(1) //nickname 가져오기
            walk = cursor.getString(2)
        }
        personId.setText(id) //사용자 아이디로 세팅
        personNickName.setText(nickNa)//사용자 닉네임으로 세팅
        personWalk.setText(walk)//목표 걸음 수 세팅

        cursor.close()
        sqlitedb.close()


        //소속 팀 가져오기
        myHelper = myDBHelper(this)
        sqlgDB = myHelper.readableDatabase
        var sql = "SELECT gName FROM groupDB WHERE gMember1 = '" + id + "'" + "OR gMember2 = '" + id + "'" + "OR gMember3= '" + id + "'"+
                "OR gMember4 = '" + id +"'"
        cursor = sqlgDB.rawQuery(sql, null)
        while (cursor.moveToNext()) {
            team = cursor.getString(0)
        }

        if(cursor.getCount() != 0){
            personTeam.setText(team)//소속그룹있다면, 소속팀 세팅
        }
        else {
            //소속그룹없다면, 아무런 결과 나타나지 않음
        }

        //개인정보수정버튼 이벤트 처리
        informationmodiBtn.setOnClickListener {

            var intent = Intent(this, InformationModiPage::class.java)
            //intent.putExtra("id", personId.text.toString()) //인텐트로 id를 정보수정페이지로 넘김
            intent.putExtra("id", id) //인텐트로 id를 정보수정페이지로 넘김
            intent.putExtra("nick", personNickName.text.toString())//인텐트로 닉네임을 정보수정페이지로 넘김
            startActivity(intent)
        }

        //로그아웃버튼 이벤트 처리
        logout.setOnClickListener {//로그아웃되어 메인페이지로 돌아감
            var intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
        }

    }

    //프로필 사진 변경과 관련된 함수
    @Override
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(resultCode== Activity.RESULT_OK){
            if(requestCode==GALLERY){
                var ImnageData: Uri? = data?.data
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
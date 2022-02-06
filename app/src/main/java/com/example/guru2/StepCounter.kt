package com.example.guru2

import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.floatingactionbutton.FloatingActionButton

class StepCounter : AppCompatActivity(), SensorEventListener {
    private var running = false
    private var sensorManager: SensorManager? = null
    private lateinit var progressBar: ProgressBar
    private lateinit var stepsValue: TextView
    private lateinit var playFab: FloatingActionButton
    private lateinit var stopFab: FloatingActionButton
    private lateinit var groupBtn: ImageButton
    private lateinit var myPageBtn: ImageButton
    private lateinit var goal : TextView
    private lateinit var progressBar2: ProgressBar
    private lateinit var teamStep: TextView
    private lateinit var tvPercent: TextView

    // personnelDB용
    lateinit var dbManager: DBManager
    lateinit var sqlitedb: SQLiteDatabase

    // groupDB용
    lateinit var myHelper: myDBHelper
    lateinit var sqlgDB: SQLiteDatabase

    private var currentStep = 0
    private var percent = 0.0
    lateinit var walk: String // 현재 유저의 걸음수
    lateinit var step: String // 팀 목표 걸음 수
    lateinit var team: String // 팀 이름


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_step_counter)
        progressBar = findViewById(R.id.progressBar)
        stepsValue = findViewById(R.id.stepsValue)
        playFab = findViewById(R.id.playFab)
        stopFab = findViewById(R.id.stopFab)
        groupBtn = findViewById(R.id.groupBtn)
        myPageBtn = findViewById(R.id.myPageBtn)
        goal = findViewById(R.id.goal)
        progressBar2 = findViewById(R.id.progressBar2)
        teamStep = findViewById(R.id.teamStep)
        tvPercent = findViewById(R.id.tvPercent)

        // 유저 아이디 갖고 오기
        var userID = intent.getStringExtra("id").toString()

        // 목표칸에 들어갈 걸음 수
        dbManager = DBManager(this, "personnelDB", null, 1)
        sqlitedb = dbManager.readableDatabase

        var cursor: Cursor
        cursor = sqlitedb.rawQuery("SELECT * FROM personnel WHERE id = '" + userID + "';", null) // 디비에서 걸음수 가져오기

        while (cursor.moveToNext()) {
            walk = cursor.getString((cursor.getColumnIndex("walk")))
        }

        // 목표 걸음수 텍스트 설정
        goal.text = "$walk 걸음"

        // 우리 팀 목표 걸음 수 가져오기
        myHelper = myDBHelper(this)
        sqlgDB = myHelper.readableDatabase

        // 유저의 그룹 가져오기
        var sql = "SELECT gName FROM groupDB WHERE gMember1 = '" + userID + "'" + " OR gMember2 = '" + userID + "'" + " OR gMember3= '" + userID + "'"+
                " OR gMember4 = '" + userID +"'"

        cursor = sqlgDB.rawQuery(sql, null)

        while (cursor.moveToNext()) {
            team = cursor.getString(0)
        }

        cursor = sqlgDB.rawQuery("SELECT * FROM groupDB WHERE gName = '" + team + "';", null)

        while (cursor.moveToNext()) {
            step = cursor.getString((cursor.getColumnIndex("gStep")))
        }

        if (cursor.count != 0) {
            teamStep.text = "$step 걸음" // 소속 그룹 있다면, 프로그레스바에 팀 목표 걸음 수 세팅
        }
        else {
            teamStep.text = "그룹에 가입해 보세요!" // 소속 그룹이 현재 없다면, 텍스트 표시
        }

        // 프로그레스바 맥스 설정
        progressBar.max = walk.toInt()
        progressBar2.max = step.toInt()

        // play(pause) 버튼 클릭 이벤트
        playFab.setOnClickListener {
            if (!running) {
                sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
                onResume()
            }
            else {
                onPause()
            }
        }

        // stop 버튼 클릭 이벤트 (초기화)
        stopFab.setOnClickListener {
            sensorManager?.unregisterListener(this)
            progressBar.setProgress(0, true)
            stepsValue.text = "0"
            tvPercent.text = "0%"
            currentStep = 0
            playFab.setImageResource(R.drawable.ic_baseline_play_circle_24)
        }

        // 그룹 페이지로 이동
        groupBtn.setOnClickListener {
            var intent = Intent(this, GroupShow::class.java)
            intent.putExtra("id", userID) // 유저 아이디 보내기
            startActivity(intent)
        }

        // 마이 페이지로 이동
        myPageBtn.setOnClickListener {
            var intent = Intent(this, MyPage::class.java)
            intent.putExtra("id", userID) // 유저 아이디 보내기
            startActivity(intent)
        }
    }

    // motion 인식 작동 함수
    override fun onResume() {
        super.onResume()
        var stepsSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

        if (stepsSensor == null) {
            if (running) {
                Toast.makeText(this, "No Step Counter Sensor!", Toast.LENGTH_SHORT).show()
            }
        }
        else {
            running = true
            if (currentStep != 0) {
                currentStep--
            }
            sensorManager?.registerListener(this, stepsSensor, SensorManager.SENSOR_DELAY_UI)
            playFab.setImageResource(R.drawable.ic_baseline_pause_circle_24)
        }
    }

    // motion 인식 멈춤 함수
    override fun onPause() {
        super.onPause()
        running = false
        sensorManager?.unregisterListener(this)
        playFab.setImageResource(R.drawable.ic_baseline_play_circle_24)
    }

    // 센서 감지 함수
    override fun onSensorChanged(p0: SensorEvent?) {
        if (running) {
            progressBar.setProgress(currentStep, true) // 내 걸음수
            progressBar2.setProgress(currentStep, true) // 기여도
            stepsValue.text = currentStep.toString()
            percent = currentStep * 100 / step.toDouble()
            tvPercent.text = "$percent%" // 백분율 계산해서 보여주기
            currentStep++
        }
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {

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
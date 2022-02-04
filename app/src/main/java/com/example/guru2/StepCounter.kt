package com.example.guru2

import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
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
    lateinit var dbManager:DBManager
    lateinit var sqlitedb: SQLiteDatabase

    private var currentStep = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_step_counter)
        progressBar = findViewById(R.id.progressBar)
        stepsValue = findViewById(R.id.stepsValue)
        playFab = findViewById(R.id.playFab)
        stopFab = findViewById(R.id.stopFab)
        groupBtn = findViewById(R.id.groupBtn)
        myPageBtn = findViewById(R.id.myPageBtn)
        goal=findViewById(R.id.goal)

        //목표칸에 들어갈 걸음 수
        dbManager = DBManager(this, "personnelDB", null, 1)
        sqlitedb = dbManager.readableDatabase
        var cursor: Cursor
        cursor = sqlitedb.rawQuery("SELECT walk FROM personnel", null) // 디비에서 해당 로그인 아이디,닉네임 가져오기
        var walk=""
        while (cursor.moveToNext()) {
            walk = cursor.getString(0)
        }

        if(cursor.getCount() != 0){
            goal.setText(walk)
        }



        // play(pause) 버튼 클릭 이벤트
        playFab.setOnClickListener {
            if (!running) {
                sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
                onResume()
            }
            else {
                onPause()
            }
        }

        // stop 버튼 클릭 이벤트
        stopFab.setOnClickListener {
            sensorManager?.unregisterListener(this)
            progressBar.setProgress(0, true)
            stepsValue.text = "0"
            currentStep = 0
            playFab.setImageResource(R.drawable.ic_baseline_play_circle_24)
        }

        // 그룹 페이지로 이동
        groupBtn.setOnClickListener {
            var intent = Intent(this, GroupShow::class.java)
            startActivity(intent)
        }

        // 마이 페이지로 이동
        myPageBtn.setOnClickListener {
            intent = Intent(this, MyPage::class.java)
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
            progressBar.setProgress(currentStep, true)
            stepsValue.text = currentStep.toString()
            currentStep++
        }
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {

    }
}
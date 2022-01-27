package com.example.guru2

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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
    private var currentStep = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_step_counter)
        progressBar = findViewById(R.id.progressBar)
        stepsValue = findViewById(R.id.stepsValue)
        playFab = findViewById(R.id.playFab)
        stopFab = findViewById(R.id.stopFab)

        playFab.setOnClickListener {
            if (!running) {
                sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
                onResume()
            }
            else {
                onPause()
            }
        }

        stopFab.setOnClickListener {
            sensorManager?.unregisterListener(this)
            progressBar.setProgress(0, true)
            stepsValue.text = "0"
            currentStep = 0
            playFab.setImageResource(R.drawable.ic_baseline_play_circle_24)
        }
    }

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

    override fun onPause() {
        super.onPause()
        running = false
        sensorManager?.unregisterListener(this)
        playFab.setImageResource(R.drawable.ic_baseline_play_circle_24)
    }

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
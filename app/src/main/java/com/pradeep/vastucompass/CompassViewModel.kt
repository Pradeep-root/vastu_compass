package com.pradeep.vastucompass

import android.app.Application
import android.content.Context.SENSOR_SERVICE
import android.hardware.Sensor
import android.hardware.Sensor.TYPE_ACCELEROMETER
import android.hardware.Sensor.TYPE_MAGNETIC_FIELD
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.hardware.SensorManager.SENSOR_DELAY_GAME
import android.util.Log
import android.util.MutableInt
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import java.lang.Math.toDegrees

class CompassViewModel(application: Application) : AndroidViewModel(application), SensorEventListener {


    private val sensorManager = application.getSystemService(SENSOR_SERVICE) as SensorManager
    private val accelerometer = sensorManager.getDefaultSensor(TYPE_ACCELEROMETER)
    private val magnetometer = sensorManager.getDefaultSensor(TYPE_MAGNETIC_FIELD)

    var currentDegree = 0.0f
    private var lastAccelerometer = FloatArray(3)
    private var lastMagnetometer = FloatArray(3)
    private var lastAccelerometerSet = false
    private var lastMagnetometerSet = false

    var angleDegreeLiveData = MutableLiveData<Float>()

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor === accelerometer) {
            event?.values?.let { lowPass(it, lastAccelerometer) }
            lastAccelerometerSet = true
        } else if (event?.sensor === magnetometer) {
            event?.values?.let { lowPass(it, lastMagnetometer) }
            lastMagnetometerSet = true
        }

        if (lastAccelerometerSet && lastMagnetometerSet) {
            val rMatrix = FloatArray(9)
            if (SensorManager.getRotationMatrix(rMatrix, null, lastAccelerometer, lastMagnetometer)) {
                val orientation = FloatArray(3)
                SensorManager.getOrientation(rMatrix, orientation)
                val degree = (toDegrees(orientation[0].toDouble()) + 360).toFloat() % 360
                //Log.d("CompassViewModel", "degree: $degree")
                angleDegreeLiveData.postValue(degree)
                currentDegree = -degree
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }

    fun registerSensorManager(){
        sensorManager.registerListener(this, accelerometer, SENSOR_DELAY_GAME)
        sensorManager.registerListener(this, magnetometer, SENSOR_DELAY_GAME)
    }

    fun unRegisterSensorManager(){
        sensorManager.unregisterListener(this, accelerometer)
        sensorManager.unregisterListener(this, magnetometer)
    }

    private fun lowPass(input: FloatArray, output: FloatArray) {
        val alpha = 0.05f
        for (i in input.indices) {
            output[i] = output[i] + alpha * (input[i] - output[i])
        }
    }
}
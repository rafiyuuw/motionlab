package com.example.motionlab.sensor

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import com.example.motionlab.model.LightCategory
import com.example.motionlab.model.LightData
import com.example.motionlab.model.MotionData
import com.example.motionlab.model.MotionSensorType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.math.atan2
import kotlin.math.sqrt

class MotionSensorManager(context: Context) : SensorEventListener {

    private val sensorManager: SensorManager =
        context.applicationContext.getSystemService(Context.SENSOR_SERVICE) as SensorManager

    private val _motionData = MutableStateFlow(MotionData())
    val motionData: StateFlow<MotionData> = _motionData.asStateFlow()

    private val _lightData = MutableStateFlow(LightData())
    val lightData: StateFlow<LightData> = _lightData.asStateFlow()

    private var activeMotionSensor: Sensor? = null
    private var activeLightSensor: Sensor? = null
    private var isMotionUsingGravity = false
    private var isListening = false

    // Exponential moving average filter state
    private var smoothX = 0f
    private var smoothY = 0f
    private var smoothZ = 0f
    private var isFirstMotionReading = true

    private val alpha = 0.20f // Low pass filter factor (0.2 = smooth yet responsive)

    init {
        detectSensors()
    }

    private fun detectSensors() {
        val gravity = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY)
        val accel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        val light = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)

        if (gravity != null) {
            activeMotionSensor = gravity
            isMotionUsingGravity = true
        } else if (accel != null) {
            activeMotionSensor = accel
            isMotionUsingGravity = false
        } else {
            activeMotionSensor = null
        }

        activeLightSensor = light

        // Initialize state availability
        _motionData.value = MotionData(
            isAvailable = activeMotionSensor != null,
            sensorType = when {
                activeMotionSensor == null -> MotionSensorType.UNAVAILABLE
                isMotionUsingGravity -> MotionSensorType.GRAVITY
                else -> MotionSensorType.ACCELEROMETER
            }
        )

        _lightData.value = LightData(
            isAvailable = activeLightSensor != null
        )
    }

    fun startListening() {
        if (isListening) return
        isListening = true
        isFirstMotionReading = true

        activeMotionSensor?.let { sensor ->
            sensorManager.registerListener(
                this,
                sensor,
                SensorManager.SENSOR_DELAY_GAME
            )
        }

        activeLightSensor?.let { sensor ->
            sensorManager.registerListener(
                this,
                sensor,
                SensorManager.SENSOR_DELAY_UI
            )
        }
    }

    fun stopListening() {
        if (!isListening) return
        isListening = false
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event == null) return

        when (event.sensor.type) {
            Sensor.TYPE_GRAVITY, Sensor.TYPE_ACCELEROMETER -> {
                val rawX = event.values[0]
                val rawY = event.values[1]
                val rawZ = event.values[2]

                if (isFirstMotionReading) {
                    smoothX = rawX
                    smoothY = rawY
                    smoothZ = rawZ
                    isFirstMotionReading = false
                } else {
                    smoothX += alpha * (rawX - smoothX)
                    smoothY += alpha * (rawY - smoothY)
                    smoothZ += alpha * (rawZ - smoothZ)
                }

                val magnitude = sqrt(smoothX * smoothX + smoothY * smoothY + smoothZ * smoothZ)

                // Calculate tilt angles (Pitch & Roll)
                // Pitch: Rotation around X axis (front/back tilt)
                // Roll: Rotation around Y axis (left/right tilt)
                val pitchRad = atan2(smoothY, sqrt(smoothX * smoothX + smoothZ * smoothZ))
                val rollRad = atan2(-smoothX, smoothZ)

                val pitchDeg = Math.toDegrees(pitchRad.toDouble()).toFloat()
                val rollDeg = Math.toDegrees(rollRad.toDouble()).toFloat()

                val sensorType = if (isMotionUsingGravity) {
                    MotionSensorType.GRAVITY
                } else {
                    MotionSensorType.ACCELEROMETER
                }

                _motionData.value = MotionData(
                    x = smoothX,
                    y = smoothY,
                    z = smoothZ,
                    pitch = pitchDeg,
                    roll = rollDeg,
                    magnitude = magnitude,
                    sensorType = sensorType,
                    isAvailable = true
                )
            }

            Sensor.TYPE_LIGHT -> {
                val lux = event.values[0]
                val category = LightCategory.fromLux(lux)

                _lightData.value = LightData(
                    lux = lux,
                    category = category,
                    isAvailable = true
                )
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // No-op
    }
}

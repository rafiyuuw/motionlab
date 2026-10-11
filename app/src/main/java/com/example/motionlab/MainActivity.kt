package com.example.motionlab

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.DisposableEffect
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.motionlab.sensor.MotionSensorManager
import com.example.motionlab.ui.MotionLabScreen
import com.example.motionlab.ui.theme.MotionLabTheme

class MainActivity : ComponentActivity() {

    private lateinit var sensorManager: MotionSensorManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        sensorManager = MotionSensorManager(this)

        setContent {
            val lifecycleOwner = LocalLifecycleOwner.current

            DisposableEffect(lifecycleOwner) {
                val observer = LifecycleEventObserver { _, event ->
                    when (event) {
                        Lifecycle.Event.ON_RESUME -> sensorManager.startListening()
                        Lifecycle.Event.ON_PAUSE -> sensorManager.stopListening()
                        else -> {}
                    }
                }

                lifecycleOwner.lifecycle.addObserver(observer)

                onDispose {
                    lifecycleOwner.lifecycle.removeObserver(observer)
                    sensorManager.stopListening()
                }
            }

            MotionLabTheme {
                MotionLabScreen(sensorManager = sensorManager)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (::sensorManager.isInitialized) {
            sensorManager.startListening()
        }
    }

    override fun onPause() {
        super.onPause()
        if (::sensorManager.isInitialized) {
            sensorManager.stopListening()
        }
    }
}

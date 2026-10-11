package com.example.motionlab.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.motionlab.model.MotionSensorType
import com.example.motionlab.sensor.MotionSensorManager
import com.example.motionlab.ui.components.AmbientLightSection
import com.example.motionlab.ui.components.AngleMetricCard
import com.example.motionlab.ui.components.PerspectiveCanvas
import com.example.motionlab.ui.components.RawVectorSection
import com.example.motionlab.ui.components.StatusBadge
import com.example.motionlab.ui.theme.CharcoalText
import com.example.motionlab.ui.theme.MutedGreen
import com.example.motionlab.ui.theme.MutedGreenContainer
import com.example.motionlab.ui.theme.MutedText
import com.example.motionlab.ui.theme.SubtleBorder
import com.example.motionlab.ui.theme.WarmBackground

@Composable
fun MotionLabScreen(
    sensorManager: MotionSensorManager,
    modifier: Modifier = Modifier,
) {
    val motionData by sensorManager.motionData.collectAsState()
    val lightData by sensorManager.lightData.collectAsState()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = WarmBackground
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header Section
            HeaderBar(
                motionSensorType = motionData.sensorType,
                isMotionAvailable = motionData.isAvailable,
                isLightAvailable = lightData.isAvailable
            )

            // Main Section: Device Orientation & Perspective Plane
            OrientationMainSection(
                pitch = motionData.pitch,
                roll = motionData.roll,
                sensorType = motionData.sensorType,
                isAvailable = motionData.isAvailable
            )

            // Secondary Section 1: Ambient Light Sensor
            AmbientLightSection(lightData = lightData)

            // Secondary Section 2: Raw Linear Vector Components
            RawVectorSection(motionData = motionData)

            // Footer / Metadata
            FooterInfo()
        }
    }
}

@Composable
fun HeaderBar(
    motionSensorType: MotionSensorType,
    isMotionAvailable: Boolean,
    isLightAvailable: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "MOTION LAB",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = MutedGreen,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.8.sp
                    )
                )
                Text(
                    text = "Sensor Instrument",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            val motionBadgeLabel = when (motionSensorType) {
                MotionSensorType.GRAVITY -> "TYPE_GRAVITY Active"
                MotionSensorType.ACCELEROMETER -> "TYPE_ACCEL Fallback"
                MotionSensorType.UNAVAILABLE -> "Motion Unavailable"
            }

            StatusBadge(
                label = motionBadgeLabel,
                isActive = isMotionAvailable,
                isWarning = motionSensorType == MotionSensorType.ACCELEROMETER
            )

            StatusBadge(
                label = if (isLightAvailable) "TYPE_LIGHT Active" else "Light Missing",
                isActive = isLightAvailable,
                isWarning = !isLightAvailable
            )
        }
    }
}

@Composable
fun OrientationMainSection(
    pitch: Float,
    roll: Float,
    sensorType: MotionSensorType,
    isAvailable: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
            .border(1.dp, SubtleBorder, RoundedCornerShape(12.dp))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "DEVICE ORIENTATION",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = MutedText,
                        fontSize = 10.sp
                    )
                )
                Text(
                    text = "Perspective Tilt Plane",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }

            Text(
                text = "UNIT: DEGREES (°)",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 10.sp,
                    color = MutedGreen,
                    fontWeight = FontWeight.Bold
                )
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Custom Canvas Perspective Visualization
        PerspectiveCanvas(
            pitch = pitch,
            roll = roll,
            isAvailable = isAvailable
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Angle Metrics Cards Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            AngleMetricCard(
                label = "PITCH (FRONT/BACK)",
                degrees = pitch,
                isAvailable = isAvailable,
                modifier = Modifier.weight(1f)
            )
            AngleMetricCard(
                label = "ROLL (LEFT/RIGHT)",
                degrees = roll,
                isAvailable = isAvailable,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Source Sensor Notice
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MutedGreenContainer)
                .clip(RoundedCornerShape(6.dp))
                .padding(horizontal = 10.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "PRIMARY ORIENTATION SOURCE",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 9.sp,
                    color = MutedGreen
                )
            )
            Text(
                text = sensorType.sensorTypeName,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = MutedGreen,
                    fontSize = 11.sp
                )
            )
        }
    }
}

@Composable
fun FooterInfo() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Motion Lab • Android Hardware Sensor Exploration",
            style = MaterialTheme.typography.labelSmall.copy(
                color = MutedText,
                fontSize = 10.sp
            )
        )
    }
}

package com.example.motionlab.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.motionlab.model.LightCategory
import com.example.motionlab.model.LightData
import com.example.motionlab.model.MotionData
import com.example.motionlab.model.MotionSensorType
import com.example.motionlab.ui.theme.AccentAmber
import com.example.motionlab.ui.theme.AmberContainer
import com.example.motionlab.ui.theme.CharcoalSecondary
import com.example.motionlab.ui.theme.CharcoalText
import com.example.motionlab.ui.theme.MutedGreen
import com.example.motionlab.ui.theme.MutedGreenContainer
import com.example.motionlab.ui.theme.MutedText
import com.example.motionlab.ui.theme.SubtleBorder
import com.example.motionlab.ui.theme.SurfaceCard
import com.example.motionlab.ui.theme.WarmBackground
import java.util.Locale

@Composable
fun StatusBadge(
    label: String,
    isActive: Boolean,
    isWarning: Boolean = false,
    modifier: Modifier = Modifier
) {
    val bgColor = when {
        !isActive -> Color(0xFFF3F4F6)
        isWarning -> AmberContainer
        else -> MutedGreenContainer
    }

    val textColor = when {
        !isActive -> MutedText
        isWarning -> AccentAmber
        else -> MutedGreen
    }

    val dotColor = when {
        !isActive -> MutedText
        isWarning -> AccentAmber
        else -> MutedGreen
    }

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(bgColor)
            .border(1.dp, textColor.copy(alpha = 0.25f), RoundedCornerShape(16.dp))
            .padding(horizontal = 10.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(dotColor)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = label.uppercase(Locale.US),
            style = MaterialTheme.typography.labelSmall.copy(
                color = textColor,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.8.sp
            )
        )
    }
}

@Composable
fun AngleMetricCard(
    label: String,
    degrees: Float,
    isAvailable: Boolean,
    modifier: Modifier = Modifier
) {
    val formattedDeg = if (isAvailable) {
        String.format(Locale.US, "%+.1f°", degrees)
    } else {
        "--.-°"
    }

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(SurfaceCard)
            .border(1.dp, SubtleBorder, RoundedCornerShape(10.dp))
            .padding(vertical = 10.dp, horizontal = 12.dp)
    ) {
        Text(
            text = label.uppercase(Locale.US),
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 10.sp,
                color = MutedText
            )
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = formattedDeg,
            style = MaterialTheme.typography.headlineMedium.copy(
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                color = CharcoalText
            )
        )
    }
}

@Composable
fun AmbientLightSection(
    lightData: LightData,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(SurfaceCard)
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
                    text = "AMBIENT LIGHT",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = MutedText,
                        fontSize = 10.sp
                    )
                )
                Text(
                    text = "Illuminance Monitor",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }

            StatusBadge(
                label = if (lightData.isAvailable) "TYPE_LIGHT Active" else "Sensor Missing",
                isActive = lightData.isAvailable,
                isWarning = !lightData.isAvailable
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        if (lightData.isAvailable) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = String.format(Locale.US, "%.1f lx", lightData.lux),
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 32.sp,
                            color = CharcoalText
                        )
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(MutedGreenContainer)
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = lightData.category.label,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MutedGreen,
                            fontSize = 12.sp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Qualitative intensity indicator bar
            LightIntensityMeter(
                currentLux = lightData.lux,
                category = lightData.category
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "${lightData.category.description}. Qualitative threshold indicator (uncalibrated).",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 11.sp,
                    color = MutedText
                )
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(WarmBackground)
                    .border(1.dp, SubtleBorder, RoundedCornerShape(8.dp))
                    .padding(12.dp)
            ) {
                Text(
                    text = "Ambient Light Sensor (TYPE_LIGHT) is not detected on this hardware. Standard light readings are unavailable.",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 12.sp,
                        color = CharcoalSecondary
                    )
                )
            }
        }
    }
}

@Composable
fun LightIntensityMeter(
    currentLux: Float,
    category: LightCategory,
    modifier: Modifier = Modifier
) {
    // Map lux on logarithmic scale 0 to 100,000 for smooth visual meter
    val maxLog = kotlin.math.log10(100000f)
    val currentLog = if (currentLux > 0f) kotlin.math.log10(currentLux).coerceIn(0f, maxLog) else 0f
    val fraction = (currentLog / maxLog).coerceIn(0.02f, 1.0f)

    Column(modifier = modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(WarmBackground)
                .border(0.5.dp, SubtleBorder, RoundedCornerShape(4.dp))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(fraction)
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(MutedGreen)
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "0 lx", style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp))
            Text(text = "100 lx", style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp))
            Text(text = "1K lx", style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp))
            Text(text = "10K+ lx", style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp))
        }
    }
}

@Composable
fun RawVectorSection(
    motionData: MotionData,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(SurfaceCard)
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
                    text = "LINEAR ACCELERATION VECTOR",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = MutedText,
                        fontSize = 10.sp
                    )
                )
                Text(
                    text = "Raw Tri-Axis Components",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }

            Text(
                text = "UNIT: m/s²",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 10.sp,
                    color = MutedGreen,
                    fontWeight = FontWeight.Bold
                )
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            VectorAxisItem(
                axisLabel = "X-AXIS",
                value = motionData.x,
                isAvailable = motionData.isAvailable,
                modifier = Modifier.weight(1f)
            )
            VectorAxisItem(
                axisLabel = "Y-AXIS",
                value = motionData.y,
                isAvailable = motionData.isAvailable,
                modifier = Modifier.weight(1f)
            )
            VectorAxisItem(
                axisLabel = "Z-AXIS",
                value = motionData.z,
                isAvailable = motionData.isAvailable,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(WarmBackground)
                .clip(RoundedCornerShape(6.dp))
                .padding(horizontal = 10.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "VECTOR MAGNITUDE |g|",
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp)
            )
            Text(
                text = if (motionData.isAvailable) String.format(Locale.US, "%.2f m/s²", motionData.magnitude) else "-- m/s²",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = CharcoalText,
                    fontSize = 12.sp
                )
            )
        }
    }
}

@Composable
fun VectorAxisItem(
    axisLabel: String,
    value: Float,
    isAvailable: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .background(WarmBackground)
            .border(1.dp, SubtleBorder, RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 8.dp)
    ) {
        Text(
            text = axisLabel,
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 9.sp,
                color = MutedText
            )
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = if (isAvailable) String.format(Locale.US, "%+.2f", value) else "--",
            style = MaterialTheme.typography.bodyLarge.copy(
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = CharcoalText
            )
        )
    }
}

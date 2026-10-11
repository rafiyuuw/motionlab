package com.example.motionlab.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.motionlab.ui.theme.GridLineColor
import com.example.motionlab.ui.theme.GridLineMain
import com.example.motionlab.ui.theme.HorizonLineColor
import com.example.motionlab.ui.theme.MutedGreen
import com.example.motionlab.ui.theme.MutedGreenContainer
import com.example.motionlab.ui.theme.SubtleBorder
import com.example.motionlab.ui.theme.WarmBackground
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

@Composable
fun PerspectiveCanvas(
    pitch: Float,
    roll: Float,
    isAvailable: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(320.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(WarmBackground)
            .border(1.dp, SubtleBorder, RoundedCornerShape(12.dp)),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height
            val cx = width / 2f
            val cy = height / 2f

            if (!isAvailable) {
                drawUnavailableGrid(width, height)
                return@Canvas
            }

            // Convert pitch and roll to constrained radians
            val safePitch = pitch.coerceIn(-80f, 80f)
            val safeRoll = roll.coerceIn(-80f, 80f)

            val pitchRad = Math.toRadians(safePitch.toDouble()).toFloat()
            val rollRad = Math.toRadians(safeRoll.toDouble()).toFloat()

            // Base tilt angle (camera viewing angle from top-front)
            val baseElevationRad = Math.toRadians(55.0).toFloat()
            val effectivePitch = baseElevationRad + (pitchRad * 0.75f)
            val effectiveRoll = rollRad * 0.75f

            val gridRange = 3
            val cellSize = min(width, height) / 8.0f
            val cameraDist = min(width, height) * 1.8f

            // Helper 3D point projection to 2D screen coordinates
            fun project3D(gx: Float, gy: Float): Offset {
                val px = gx * cellSize
                val py = gy * cellSize
                val pz = 0f

                // Pitch rotation around X axis
                val y1 = (py * cos(effectivePitch)) - (pz * sin(effectivePitch))
                val z1 = (py * sin(effectivePitch)) + (pz * cos(effectivePitch))

                // Roll rotation around Y axis
                val x2 = (px * cos(effectiveRoll)) + (z1 * sin(effectiveRoll))
                val y2 = y1
                val z2 = (-px * sin(effectiveRoll)) + (z1 * cos(effectiveRoll))

                val k = cameraDist / (cameraDist + z2)
                val screenX = cx + x2 * k
                val screenY = cy + y2 * k

                return Offset(screenX, screenY)
            }

            // 1. Draw 3D plane filled background polygon
            val topLeft = project3D(-gridRange.toFloat(), -gridRange.toFloat())
            val topRight = project3D(gridRange.toFloat(), -gridRange.toFloat())
            val bottomRight = project3D(gridRange.toFloat(), gridRange.toFloat())
            val bottomLeft = project3D(-gridRange.toFloat(), gridRange.toFloat())

            val planePath = Path().apply {
                moveTo(topLeft.x, topLeft.y)
                lineTo(topRight.x, topRight.y)
                lineTo(bottomRight.x, bottomRight.y)
                lineTo(bottomLeft.x, bottomLeft.y)
                close()
            }

            drawPath(
                path = planePath,
                color = MutedGreenContainer.copy(alpha = 0.35f)
            )

            // 2. Draw Grid Lines along X and Y axes
            for (i in -gridRange..gridRange) {
                val fi = i.toFloat()

                // Line along Y (constant X)
                val pStart1 = project3D(fi, -gridRange.toFloat())
                val pEnd1 = project3D(fi, gridRange.toFloat())
                val isCenter1 = i == 0

                drawLine(
                    color = if (isCenter1) GridLineMain else GridLineColor,
                    start = pStart1,
                    end = pEnd1,
                    strokeWidth = if (isCenter1) 2.5f else 1.2f,
                    cap = StrokeCap.Round
                )

                // Line along X (constant Y)
                val pStart2 = project3D(-gridRange.toFloat(), fi)
                val pEnd2 = project3D(gridRange.toFloat(), fi)
                val isCenter2 = i == 0

                drawLine(
                    color = if (isCenter2) GridLineMain else GridLineColor,
                    start = pStart2,
                    end = pEnd2,
                    strokeWidth = if (isCenter2) 2.5f else 1.2f,
                    cap = StrokeCap.Round
                )
            }

            // 3. Draw Outer Rim of Plane
            drawPath(
                path = planePath,
                color = MutedGreen.copy(alpha = 0.5f),
                style = Stroke(
                    width = 2.0f,
                    cap = StrokeCap.Round
                )
            )

            // 4. Draw Horizon Line across canvas center
            drawLine(
                color = HorizonLineColor,
                start = Offset(0f, cy),
                end = Offset(width, cy),
                strokeWidth = 1.0f,
                pathEffect = PathEffect.dashPathEffect(
                    floatArrayOf(10f, 10f), 0f
                )
            )

            // 5. Draw Center Crosshair Target
            val crosshairSize = 14f
            drawLine(
                color = MutedGreen,
                start = Offset(cx - crosshairSize, cy),
                end = Offset(cx + crosshairSize, cy),
                strokeWidth = 1.5f
            )
            drawLine(
                color = MutedGreen,
                start = Offset(cx, cy - crosshairSize),
                end = Offset(cx, cy + crosshairSize),
                strokeWidth = 1.5f
            )

            // 6. Draw Level Bubble / Plumb Ball responding to physical tilt
            val bubbleMaxOffset = cellSize * 2.2f
            val bubbleOffsetX = -(safeRoll / 80f) * bubbleMaxOffset
            val bubbleOffsetY = (safePitch / 80f) * bubbleMaxOffset
            val bubbleCenter = Offset(cx + bubbleOffsetX, cy + bubbleOffsetY)

            // Outer pulse ring for bubble
            drawCircle(
                color = MutedGreen.copy(alpha = 0.25f),
                radius = 16f,
                center = bubbleCenter
            )
            // Solid center bubble
            drawCircle(
                color = MutedGreen,
                radius = 7f,
                center = bubbleCenter
            )
            drawCircle(
                color = Color.White,
                radius = 2.5f,
                center = Offset(bubbleCenter.x - 2f, bubbleCenter.y - 2f)
            )
        }

        if (!isAvailable) {
            Box(
                modifier = Modifier
                    .background(Color.White.copy(alpha = 0.85f))
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "MOTION SENSOR UNAVAILABLE",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = Color.Red,
                        letterSpacing = 1.2.sp
                    )
                )
            }
        }
    }
}

private fun DrawScope.drawUnavailableGrid(width: Float, height: Float) {
    val step = 40f
    var x = 0f
    while (x < width) {
        drawLine(
            color = SubtleBorder,
            start = Offset(x, 0f),
            end = Offset(x, height),
            strokeWidth = 1f
        )
        x += step
    }
    var y = 0f
    while (y < height) {
        drawLine(
            color = SubtleBorder,
            start = Offset(0f, y),
            end = Offset(width, y),
            strokeWidth = 1f
        )
        y += step
    }
}

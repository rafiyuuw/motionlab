package com.example.motionlab

import com.example.motionlab.model.LightCategory
import org.junit.Assert.assertEquals
import org.junit.Test
import kotlin.math.atan2
import kotlin.math.sqrt

class SensorLogicUnitTest {

    @Test
    fun lightCategory_fromLux_returnsCorrectThresholds() {
        assertEquals(LightCategory.DARK, LightCategory.fromLux(0f))
        assertEquals(LightCategory.DARK, LightCategory.fromLux(5f))
        assertEquals(LightCategory.LOW_LIGHT, LightCategory.fromLux(15f))
        assertEquals(LightCategory.INDOOR, LightCategory.fromLux(250f))
        assertEquals(LightCategory.DAYLIGHT, LightCategory.fromLux(2500f))
        assertEquals(LightCategory.BRIGHT, LightCategory.fromLux(15000f))
    }

    @Test
    fun tiltCalculation_flatPosition_returnsZeroPitchAndRoll() {
        // Standard gravity vector flat on table: X=0, Y=0, Z=9.81
        val x = 0.0
        val y = 0.0
        val z = 9.81

        val pitchRad = atan2(y, sqrt(x * x + z * z))
        val rollRad = atan2(-x, z)

        val pitchDeg = Math.toDegrees(pitchRad)
        val rollDeg = Math.toDegrees(rollRad)

        assertEquals(0.0, pitchDeg, 0.01)
        assertEquals(0.0, rollDeg, 0.01)
    }

    @Test
    fun tiltCalculation_tiltedForward_returnsPositivePitch() {
        // Phone tilted front forward: positive Y gravity component
        val x = 0.0
        val y = 4.905
        val z = 8.4957 // sqrt(9.81^2 - 4.905^2)

        val pitchRad = atan2(y, sqrt(x * x + z * z))
        val pitchDeg = Math.toDegrees(pitchRad)

        assertEquals(30.0, pitchDeg, 0.5)
    }

    @Test
    fun tiltCalculation_tiltedRight_returnsNegativeRoll() {
        // Phone tilted right: positive X gravity component -> roll should be negative
        val x = 4.905
        val y = 0.0
        val z = 8.4957

        val rollRad = atan2(-x, z)
        val rollDeg = Math.toDegrees(rollRad)

        assertEquals(-30.0, rollDeg, 0.5)
    }
}

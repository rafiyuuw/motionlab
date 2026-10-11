package com.example.motionlab.model

enum class MotionSensorType(val displayName: String, val sensorTypeName: String) {
    GRAVITY("Gravity Sensor", "TYPE_GRAVITY"),
    ACCELEROMETER("Accelerometer (Fallback)", "TYPE_ACCELEROMETER"),
    UNAVAILABLE("Unavailable", "NONE")
}

data class MotionData(
    val x: Float = 0f,
    val y: Float = 0f,
    val z: Float = 0f,
    val pitch: Float = 0f, // Front/back tilt angle in degrees (-90° to +90°)
    val roll: Float = 0f,  // Left/right tilt angle in degrees (-180° to +180°)
    val magnitude: Float = 0f, // Vector magnitude sqrt(x^2 + y^2 + z^2) in m/s²
    val sensorType: MotionSensorType = MotionSensorType.UNAVAILABLE,
    val isAvailable: Boolean = false
)

enum class LightCategory(
    val label: String,
    val description: String,
    val minLux: Float,
    val maxLux: Float
) {
    DARK("Dark", "Pitch dark or sensor covered", 0f, 10f),
    LOW_LIGHT("Dim Light", "Dim indoor environment", 10f, 100f),
    INDOOR("Indoor Light", "Standard artificial indoor illumination", 100f, 1000f),
    DAYLIGHT("Daylight", "Bright room or shaded outdoors", 1000f, 10000f),
    BRIGHT("Direct Sunlight", "Direct sunlight or high intensity light source", 10000f, Float.MAX_VALUE);

    companion object {
        fun fromLux(lux: Float): LightCategory {
            return when {
                lux < 10f -> DARK
                lux < 100f -> LOW_LIGHT
                lux < 1000f -> INDOOR
                lux < 10000f -> DAYLIGHT
                else -> BRIGHT
            }
        }
    }
}

data class LightData(
    val lux: Float = 0f,
    val category: LightCategory = LightCategory.DARK,
    val isAvailable: Boolean = false
)

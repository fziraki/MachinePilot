package com.github.fziraki.machinepilot.domain.model

data class MachineTelemetry(
    val rpm: Int,
    val fuelPercent: Int,
    val engineTempCelsius: Double,
    val speedKmph: Double,
    val hydraulicPressureBar: Double,
    val engineHours: Double,
    val batteryVoltage: Double,
    val latitude: Double,
    val longitude: Double,
    val gpsConnected: Boolean,
)

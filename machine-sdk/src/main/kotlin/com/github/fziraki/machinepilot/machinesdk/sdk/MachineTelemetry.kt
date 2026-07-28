package com.github.fziraki.machinepilot.machinesdk.sdk

data class MachineTelemetry(
    val powertrain: PowertrainTelemetry,
    val hydraulics: HydraulicTelemetry,
    val electrical: ElectricalTelemetry,
    val location: LocationTelemetry,
)

data class PowertrainTelemetry(
    val engineRpm: Int,
    val groundSpeedKmph: Double,
    val fuelLevelPercent: Int,
    val coolantTemperatureCelsius: Double,
    val engineHours: Double,
)

data class HydraulicTelemetry(
    val pressureBar: Double,
)

data class ElectricalTelemetry(
    val batteryVoltage: Double,
)

data class LocationTelemetry(
    val latitude: Double,
    val longitude: Double,
    val gpsConnected: Boolean,
)

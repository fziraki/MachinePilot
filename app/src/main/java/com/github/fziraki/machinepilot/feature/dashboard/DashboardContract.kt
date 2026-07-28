package com.github.fziraki.machinepilot.feature.dashboard

import com.github.fziraki.machinepilot.domain.model.AlertSeverity

data class DashboardState(
    val rpm: String = "2 450",
    val fuel: String = "73",
    val engineTemp: String = "88",
    val speed: String = "12.4",
    val hydraulicPressure: String = "180",
    val engineHours: String = "12 450",
    val latitude: String = "35.6895",
    val longitude: String = "51.3890",
    val gpsConnected: Boolean = true,
    val batteryLevel: String = "85",
    val batteryVoltage: String = "12.4",
    val batteryStatus: String = "Normal",
    val canStatus: String = "OK",
    val ecuStatus: String = "OK",
    val alerts: List<AlertUi> = emptyList(),
    val healthStatus: String = "OPERATIONAL",
    val isEmergencyStopped: Boolean = false,
)

data class AlertUi(
    val title: String = "",
    val message: String,
    val severity: AlertSeverity,
    val timestamp: Long = 0L,
)

sealed interface DashboardAction {
    data object EmergencyStop : DashboardAction
    data object ReleaseEmergencyStop : DashboardAction
}

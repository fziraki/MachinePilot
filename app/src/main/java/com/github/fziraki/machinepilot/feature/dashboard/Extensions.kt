package com.github.fziraki.machinepilot.feature.dashboard

import com.github.fziraki.machinepilot.domain.model.HealthStatus

internal fun Int.formatRpm() = toString().reversed().chunked(3).joinToString(" ").reversed()
internal fun Int.formatFuel() = toString()
internal fun Double.formatTemp() = "%.0f".format(this)
internal fun Double.formatSpeed() = "%.1f".format(this)
internal fun Double.formatPressure() = "%.0f".format(this)
internal fun Double.formatEngineHours() = "%.0f".format(this).reversed().chunked(3).joinToString(" ").reversed()
internal fun Double.formatBattery() = ((this / 12.6) * 100).toInt().coerceIn(0, 100).toString()
internal fun HealthStatus.deriveBusStatus() = when (this) {
    HealthStatus.CRITICAL -> "ERROR"
    HealthStatus.WARNING -> "WARN"
    HealthStatus.NOMINAL -> "OK"
}
internal fun HealthStatus.formatHealthStatus() = when (this) {
    HealthStatus.NOMINAL -> "OPERATIONAL"
    HealthStatus.WARNING -> "WARNING"
    HealthStatus.CRITICAL -> "CRITICAL"
}

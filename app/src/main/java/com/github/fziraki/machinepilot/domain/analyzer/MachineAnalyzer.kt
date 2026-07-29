package com.github.fziraki.machinepilot.domain.analyzer

import com.github.fziraki.machinepilot.domain.model.Alert
import com.github.fziraki.machinepilot.domain.model.AlertSeverity
import com.github.fziraki.machinepilot.domain.model.Diagnostic
import com.github.fziraki.machinepilot.domain.model.HealthStatus
import com.github.fziraki.machinepilot.domain.model.MachineAnalysis
import com.github.fziraki.machinepilot.domain.model.MachineTelemetry

object MachineAnalyzer {

    fun analyze(telemetry: MachineTelemetry): MachineAnalysis {
        val alerts = mutableListOf<Alert>()
        val diagnostics = mutableListOf<Diagnostic>()

        if (telemetry.fuelPercent < 15) {
            alerts.add(Alert(message = "Low fuel level (${telemetry.fuelPercent}%)", severity = AlertSeverity.WARNING))
        }
        if (telemetry.engineTempCelsius > 100.0) {
            alerts.add(Alert(message = "Engine temperature above normal range", severity = AlertSeverity.WARNING))
            diagnostics.add(Diagnostic("TEMP_HIGH", "Engine temperature at ${"%.1f".format(telemetry.engineTempCelsius)}°C"))
        }
        if (telemetry.hydraulicPressureBar < 80.0) {
            alerts.add(Alert(message = "Hydraulic pressure below minimum (${"%.0f".format(telemetry.hydraulicPressureBar)} bar)", severity = AlertSeverity.WARNING))
        }
        if (telemetry.hydraulicPressureBar > 220.0) {
            alerts.add(Alert(message = "Hydraulic pressure fluctuation detected", severity = AlertSeverity.CRITICAL))
            diagnostics.add(Diagnostic("HYD_HIGH", "Hydraulic pressure at ${"%.0f".format(telemetry.hydraulicPressureBar)} bar"))
        }
        if (telemetry.rpm > 3500) {
            diagnostics.add(Diagnostic("RPM_HIGH", "Engine RPM at ${telemetry.rpm}"))
        }

        val healthStatus = when {
            alerts.any { it.severity == AlertSeverity.CRITICAL } -> HealthStatus.CRITICAL
            alerts.isNotEmpty() -> HealthStatus.WARNING
            else -> HealthStatus.NORMAL
        }

        return MachineAnalysis(alerts, diagnostics, healthStatus)
    }
}

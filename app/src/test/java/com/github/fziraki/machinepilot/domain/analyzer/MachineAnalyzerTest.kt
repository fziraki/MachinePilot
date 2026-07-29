package com.github.fziraki.machinepilot.domain.analyzer

import com.github.fziraki.machinepilot.domain.model.AlertSeverity
import com.github.fziraki.machinepilot.domain.model.HealthStatus
import com.github.fziraki.machinepilot.domain.model.MachineTelemetry
import org.junit.Assert.assertEquals
import org.junit.Test

class MachineAnalyzerTest {

    @Test
    fun `nominal telemetry returns NORMAL health`() {
        val result = MachineAnalyzer.analyze(nominalTelemetry())
        assertEquals(HealthStatus.NORMAL, result.healthStatus)
        assertEquals(0, result.alerts.size)
    }

    @Test
    fun `low fuel below 15 percent triggers warning`() {
        val t = nominalTelemetry().copy(fuelPercent = 10)
        val result = MachineAnalyzer.analyze(t)
        assertEquals(HealthStatus.WARNING, result.healthStatus)
        val fuelAlert = result.alerts.find { it.message.contains("fuel", ignoreCase = true) }
        assertEquals(AlertSeverity.WARNING, fuelAlert?.severity)
    }

    @Test
    fun `high engine temp over 100 triggers warning and diagnostic`() {
        val t = nominalTelemetry().copy(engineTempCelsius = 105.0)
        val result = MachineAnalyzer.analyze(t)
        assertEquals(HealthStatus.WARNING, result.healthStatus)
        assertEquals(1, result.diagnostics.size)
        assertEquals("TEMP_HIGH", result.diagnostics[0].code)
    }

    @Test
    fun `low hydraulic pressure under 80 triggers warning`() {
        val t = nominalTelemetry().copy(hydraulicPressureBar = 60.0)
        val result = MachineAnalyzer.analyze(t)
        assertEquals(HealthStatus.WARNING, result.healthStatus)
    }

    @Test
    fun `high hydraulic pressure over 220 triggers CRITICAL`() {
        val t = nominalTelemetry().copy(hydraulicPressureBar = 230.0)
        val result = MachineAnalyzer.analyze(t)
        assertEquals(HealthStatus.CRITICAL, result.healthStatus)
        val critical = result.alerts.find { it.severity == AlertSeverity.CRITICAL }
        assertEquals("Hydraulic pressure fluctuation detected", critical?.message)
    }

    @Test
    fun `rpm over 3500 adds diagnostic`() {
        val t = nominalTelemetry().copy(rpm = 3800)
        val result = MachineAnalyzer.analyze(t)
        assertEquals("RPM_HIGH", result.diagnostics.find { it.code == "RPM_HIGH" }?.code)
    }

    @Test
    fun `boundary fuel at exactly 15 is not warning`() {
        val t = nominalTelemetry().copy(fuelPercent = 15)
        val result = MachineAnalyzer.analyze(t)
        val fuelAlert = result.alerts.find { it.message.contains("fuel", ignoreCase = true) }
        assertEquals(null, fuelAlert)
    }

    @Test
    fun `boundary temp at exactly 100 is not warning`() {
        val t = nominalTelemetry().copy(engineTempCelsius = 100.0)
        val result = MachineAnalyzer.analyze(t)
        val tempAlert = result.alerts.find { it.message.contains("temperature", ignoreCase = true) }
        assertEquals(null, tempAlert)
    }

    private fun nominalTelemetry() = MachineTelemetry(
        rpm = 1500,
        fuelPercent = 50,
        engineTempCelsius = 85.0,
        speedKmph = 30.0,
        hydraulicPressureBar = 150.0,
        engineHours = 1000.0,
        batteryVoltage = 12.6,
        latitude = 0.0,
        longitude = 0.0,
        gpsConnected = true,
    )
}

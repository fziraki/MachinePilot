package com.github.fziraki.machinepilot.feature.dashboard

import com.github.fziraki.machinepilot.domain.model.HealthStatus
import org.junit.Assert.assertEquals
import org.junit.Test

class FormattingExtensionsTest {

    @Test
    fun `formatRpm groups digits with spaces`() {
        assertEquals("2 450", 2450.formatRpm())
        assertEquals("800", 800.formatRpm())
        assertEquals("12 000", 12000.formatRpm())
    }

    @Test
    fun `formatFuel returns plain string`() {
        assertEquals("73", 73.formatFuel())
        assertEquals("0", 0.formatFuel())
    }

    @Test
    fun `formatTemp removes decimals`() {
        assertEquals("88", 88.0.formatTemp())
        assertEquals("101", 100.5.formatTemp())
    }

    @Test
    fun `formatSpeed one decimal`() {
        assertEquals("12.4", 12.4.formatSpeed())
        assertEquals("0.0", 0.0.formatSpeed())
    }

    @Test
    fun `formatPressure removes decimals`() {
        assertEquals("180", 180.0.formatPressure())
        assertEquals("76", 75.8.formatPressure())
    }

    @Test
    fun `formatEngineHours groups with spaces`() {
        assertEquals("12 450", 12450.0.formatEngineHours())
        assertEquals("1 000", 1000.0.formatEngineHours())
    }

    @Test
    fun `formatBattery converts voltage to percentage`() {
        assertEquals("100", 12.6.formatBattery())
        assertEquals("50", (12.6 / 2).formatBattery())
        assertEquals("0", 0.0.formatBattery())
    }

    @Test
    fun `deriveBusStatus maps correctly`() {
        assertEquals("ERROR", HealthStatus.CRITICAL.deriveBusStatus())
        assertEquals("WARN", HealthStatus.WARNING.deriveBusStatus())
        assertEquals("OK", HealthStatus.NORMAL.deriveBusStatus())
    }

    @Test
    fun `formatHealthStatus maps correctly`() {
        assertEquals("CRITICAL", HealthStatus.CRITICAL.formatHealthStatus())
        assertEquals("WARNING", HealthStatus.WARNING.formatHealthStatus())
        assertEquals("OPERATIONAL", HealthStatus.NORMAL.formatHealthStatus())
    }
}

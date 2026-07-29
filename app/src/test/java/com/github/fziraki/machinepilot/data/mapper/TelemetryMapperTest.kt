package com.github.fziraki.machinepilot.data.mapper

import com.github.fziraki.machinepilot.machinesdk.sdk.ElectricalTelemetry
import com.github.fziraki.machinepilot.machinesdk.sdk.HydraulicTelemetry
import com.github.fziraki.machinepilot.machinesdk.sdk.LocationTelemetry
import com.github.fziraki.machinepilot.machinesdk.sdk.MachineTelemetry as SdkTelemetry
import com.github.fziraki.machinepilot.machinesdk.sdk.PowertrainTelemetry
import org.junit.Assert.assertEquals
import org.junit.Test

class TelemetryMapperTest {

    @Test
    fun `maps all SDK fields to domain correctly`() {
        val sdk = SdkTelemetry(
            powertrain = PowertrainTelemetry(
                engineRpm = 2450,
                groundSpeedKmph = 12.4,
                fuelLevelPercent = 73,
                coolantTemperatureCelsius = 88.0,
                engineHours = 12450.0,
            ),
            hydraulics = HydraulicTelemetry(pressureBar = 180.0),
            electrical = ElectricalTelemetry(batteryVoltage = 12.4),
            location = LocationTelemetry(latitude = 35.6895, longitude = 51.3890, gpsConnected = true),
        )

        val domain = TelemetryMapper.toDomain(sdk)

        assertEquals(2450, domain.rpm)
        assertEquals(12.4, domain.speedKmph, 0.001)
        assertEquals(73, domain.fuelPercent)
        assertEquals(88.0, domain.engineTempCelsius, 0.001)
        assertEquals(12450.0, domain.engineHours, 0.001)
        assertEquals(180.0, domain.hydraulicPressureBar, 0.001)
        assertEquals(12.4, domain.batteryVoltage, 0.001)
        assertEquals(35.6895, domain.latitude, 0.001)
        assertEquals(51.3890, domain.longitude, 0.001)
        assertEquals(true, domain.gpsConnected)
    }

    @Test
    fun `maps GPS disconnected state`() {
        val sdk = SdkTelemetry(
            powertrain = PowertrainTelemetry(engineRpm = 0, groundSpeedKmph = 0.0, fuelLevelPercent = 0, coolantTemperatureCelsius = 0.0, engineHours = 0.0),
            hydraulics = HydraulicTelemetry(pressureBar = 0.0),
            electrical = ElectricalTelemetry(batteryVoltage = 0.0),
            location = LocationTelemetry(latitude = 0.0, longitude = 0.0, gpsConnected = false),
        )
        val domain = TelemetryMapper.toDomain(sdk)
        assertEquals(false, domain.gpsConnected)
    }
}

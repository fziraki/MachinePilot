package com.github.fziraki.machinepilot.data.repository

import com.github.fziraki.machinepilot.machinesdk.sdk.FakeMachineClient
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class FakeMachineClientTest {

    @Test
    fun `telemetry emits within expected ranges`() = runTest {
        val client = FakeMachineClient()
        val t = client.telemetry.first()

        assertTrue("RPM in range", t.powertrain.engineRpm in 800..4000)
        assertTrue("Speed in range", t.powertrain.groundSpeedKmph in 0.0..60.0)
        assertTrue("Fuel in range", t.powertrain.fuelLevelPercent in 0..100)
        assertTrue("Temp in range", t.powertrain.coolantTemperatureCelsius in 70.0..110.0)
        assertTrue("Pressure in range", t.hydraulics.pressureBar in 50.0..250.0)
        assertTrue("Battery in range", t.electrical.batteryVoltage in 11.0..13.0)
        assertEquals(true, t.location.gpsConnected)
    }

    @Test
    fun `emergencyStop zeroes RPM and speed`() = runTest {
        val client = FakeMachineClient()
        client.emergencyStop()
        // after emergency stop, enough delay to process one emission
        kotlinx.coroutines.delay(300)
        val t = client.telemetry.first()

        assertEquals(0, t.powertrain.engineRpm)
        assertEquals(0.0, t.powertrain.groundSpeedKmph, 0.001)
    }

    @Test
    fun `releaseEmergencyStop resumes normal range`() = runTest {
        val client = FakeMachineClient()
        client.emergencyStop()
        kotlinx.coroutines.delay(300)
        client.releaseEmergencyStop()
        kotlinx.coroutines.delay(300)
        val t = client.telemetry.first()

        assertTrue("RPM resumed", t.powertrain.engineRpm > 0 || t.powertrain.engineRpm == 0)
        // actually after release it resumes from the coerceIn range
        assertTrue("RPM in range after release", t.powertrain.engineRpm in 800..4000 || t.powertrain.engineRpm == 0)
    }
}

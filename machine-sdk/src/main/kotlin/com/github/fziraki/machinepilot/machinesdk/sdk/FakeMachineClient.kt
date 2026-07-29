package com.github.fziraki.machinepilot.machinesdk.sdk

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.random.Random
import kotlin.time.Duration.Companion.milliseconds

class FakeMachineClient : MachineClient {

    @Volatile
    private var emergencyStopActive = false

    override val telemetry: Flow<MachineTelemetry> = flow {
        var rpm = 2450
        var speed = 12.4
        var fuel = 73
        var temp = 88.0
        var pressure = 180.0
        var hours = 12450.0
        var lat = 35.6895
        var lon = 51.3890

        while (true) {
            delay(200.milliseconds)
            if (emergencyStopActive) {
                rpm = 0
                speed = 0.0
                pressure = (pressure - 20.0).coerceAtLeast(0.0)
            } else {
                rpm = (rpm + Random.nextInt(-50, 50)).coerceIn(800, 4000)
                speed = (speed + Random.nextDouble(-2.0, 2.0)).coerceIn(0.0, 60.0)
                fuel = (fuel + Random.nextInt(-1, 0)).coerceIn(0, 100)
                temp = (temp + Random.nextDouble(-1.0, 1.0)).coerceIn(70.0, 110.0)
                pressure = (pressure + Random.nextDouble(-5.0, 5.0)).coerceIn(50.0, 250.0)
                hours += Random.nextDouble(0.0, 0.001)
                lat += Random.nextDouble(-0.001, 0.001)
                lon += Random.nextDouble(-0.001, 0.001)
            }
            emit(
                MachineTelemetry(
                    powertrain = PowertrainTelemetry(
                        engineRpm = rpm,
                        groundSpeedKmph = speed,
                        fuelLevelPercent = fuel,
                        coolantTemperatureCelsius = temp,
                        engineHours = hours,
                    ),
                    hydraulics = HydraulicTelemetry(pressureBar = pressure),
                    electrical = ElectricalTelemetry(batteryVoltage = 12.4 + Random.nextDouble(-0.3, 0.3)),
                    location = LocationTelemetry(
                        latitude = lat,
                        longitude = lon,
                        gpsConnected = true,
                    ),
                )
            )
        }
    }

    override suspend fun emergencyStop() {
        emergencyStopActive = true
    }

    override suspend fun releaseEmergencyStop() {
        emergencyStopActive = false
    }
}

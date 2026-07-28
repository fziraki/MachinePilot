package com.github.fziraki.machinepilot.data.mapper

import com.github.fziraki.machinepilot.domain.model.MachineTelemetry as DomainTelemetry
import com.github.fziraki.machinepilot.machinesdk.sdk.MachineTelemetry as SdkTelemetry

object TelemetryMapper {

    fun toDomain(sdk: SdkTelemetry): DomainTelemetry = DomainTelemetry(
        rpm = sdk.powertrain.engineRpm,
        fuelPercent = sdk.powertrain.fuelLevelPercent,
        engineTempCelsius = sdk.powertrain.coolantTemperatureCelsius,
        speedKmph = sdk.powertrain.groundSpeedKmph,
        hydraulicPressureBar = sdk.hydraulics.pressureBar,
        engineHours = sdk.powertrain.engineHours,
        batteryVoltage = sdk.electrical.batteryVoltage,
        latitude = sdk.location.latitude,
        longitude = sdk.location.longitude,
        gpsConnected = sdk.location.gpsConnected,
    )
}

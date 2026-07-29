package com.github.fziraki.machinepilot.data.repository

import com.github.fziraki.machinepilot.data.mapper.TelemetryMapper
import com.github.fziraki.machinepilot.domain.analyzer.MachineAnalyzer
import com.github.fziraki.machinepilot.domain.model.Alert
import com.github.fziraki.machinepilot.domain.model.AlertSeverity
import com.github.fziraki.machinepilot.domain.model.MachineOperatingState
import com.github.fziraki.machinepilot.domain.repository.MachineRepository
import com.github.fziraki.machinepilot.domain.repository.MachineState
import com.github.fziraki.machinepilot.machinesdk.sdk.MachineClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MachineRepositoryImpl @Inject constructor(
    private val machineClient: MachineClient,
) : MachineRepository {

    private val _operatingState = MutableStateFlow(MachineOperatingState.OPERATIONAL)
    private var emergencyStopTimestamp: Long = 0L

    override fun observe(): Flow<MachineState> =
        combine(
            machineClient.telemetry,
            _operatingState,
        ) { sdkTelemetry, operatingState ->
            val domainTelemetry = TelemetryMapper.toDomain(sdkTelemetry)
            val analysis = MachineAnalyzer.analyze(domainTelemetry)
            val alerts = if (operatingState == MachineOperatingState.EMERGENCY_STOPPED) {
                analysis.alerts + Alert(
                    title = "Emergency Stop Activated",
                    message = "Machine operation has been stopped manually.",
                    severity = AlertSeverity.CRITICAL,
                    timestamp = emergencyStopTimestamp,
                )
            } else {
                analysis.alerts
            }
            MachineState(
                telemetry = domainTelemetry,
                analysis = analysis.copy(alerts = alerts),
                operatingState = operatingState,
            )
        }

    override suspend fun emergencyStop() {
        emergencyStopTimestamp = System.currentTimeMillis()
        _operatingState.value = MachineOperatingState.EMERGENCY_STOPPED
        machineClient.emergencyStop()
    }

    override suspend fun releaseEmergencyStop() {
        _operatingState.value = MachineOperatingState.OPERATIONAL
        machineClient.releaseEmergencyStop()
    }
}

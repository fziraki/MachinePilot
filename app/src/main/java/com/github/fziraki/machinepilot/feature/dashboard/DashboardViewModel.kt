package com.github.fziraki.machinepilot.feature.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.fziraki.machinepilot.domain.model.HealthStatus
import com.github.fziraki.machinepilot.domain.model.MachineOperatingState
import com.github.fziraki.machinepilot.domain.repository.MachineRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val machineRepository: MachineRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(DashboardState())
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            machineRepository.observe().collect { machineState ->
                val t = machineState.telemetry
                val a = machineState.analysis
                _state.value = _state.value.copy(
                    rpm = formatRpm(t.rpm),
                    fuel = formatFuel(t.fuelPercent),
                    engineTemp = formatTemp(t.engineTempCelsius),
                    speed = formatSpeed(t.speedKmph),
                    hydraulicPressure = formatPressure(t.hydraulicPressureBar),
                    engineHours = formatEngineHours(t.engineHours),
                    latitude = "%.4f".format(t.latitude),
                    longitude = "%.4f".format(t.longitude),
                    gpsConnected = t.gpsConnected,
                    batteryLevel = formatBattery(t.batteryVoltage),
                    batteryVoltage = "%.1f".format(t.batteryVoltage),
                    canStatus = deriveBusStatus(a.healthStatus),
                    ecuStatus = deriveBusStatus(a.healthStatus),
                    alerts = a.alerts.map {
                        AlertUi(
                            title = it.title,
                            message = it.message,
                            severity = it.severity,
                            timestamp = it.timestamp,
                        )
                    },
                    healthStatus = formatHealthStatus(a.healthStatus),
                    isEmergencyStopped = machineState.operatingState == MachineOperatingState.EMERGENCY_STOPPED,
                )
            }
        }
    }

    fun onAction(action: DashboardAction) {
        when (action) {
            is DashboardAction.EmergencyStop -> {
                viewModelScope.launch { machineRepository.emergencyStop() }
            }
            is DashboardAction.ReleaseEmergencyStop -> {
                viewModelScope.launch { machineRepository.releaseEmergencyStop() }
            }
        }
    }

    private fun formatRpm(v: Int) = v.toString()
        .chunked(3).joinToString(" ")

    private fun formatFuel(v: Int) = v.toString()

    private fun formatTemp(v: Double) = "%.0f".format(v)

    private fun formatSpeed(v: Double) = "%.1f".format(v)

    private fun formatPressure(v: Double) = "%.0f".format(v)

    private fun formatEngineHours(v: Double) = "%.0f".format(v)
        .chunked(3).joinToString(" ")

    private fun formatBattery(v: Double) = ((v / 12.6) * 100).toInt().coerceIn(0, 100).toString()

    private fun deriveBusStatus(health: HealthStatus) = when (health) {
        HealthStatus.CRITICAL -> "ERROR"
        HealthStatus.WARNING -> "WARN"
        HealthStatus.NOMINAL -> "OK"
    }

    private fun formatHealthStatus(h: HealthStatus) = when (h) {
        HealthStatus.NOMINAL -> "OPERATIONAL"
        HealthStatus.WARNING -> "WARNING"
        HealthStatus.CRITICAL -> "CRITICAL"
    }
}

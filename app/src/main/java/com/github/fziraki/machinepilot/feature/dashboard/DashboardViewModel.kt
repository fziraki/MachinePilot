package com.github.fziraki.machinepilot.feature.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
                    rpm = t.rpm.formatRpm(),
                    fuel = t.fuelPercent.formatFuel(),
                    engineTemp = t.engineTempCelsius.formatTemp(),
                    speed = t.speedKmph.formatSpeed(),
                    hydraulicPressure = t.hydraulicPressureBar.formatPressure(),
                    engineHours = t.engineHours.formatEngineHours(),
                    latitude = "%.4f".format(t.latitude),
                    longitude = "%.4f".format(t.longitude),
                    gpsConnected = t.gpsConnected,
                    batteryLevel = t.batteryVoltage.formatBattery(),
                    batteryVoltage = "%.1f".format(t.batteryVoltage),
                    canStatus = a.healthStatus.deriveBusStatus(),
                    ecuStatus = a.healthStatus.deriveBusStatus(),
                    alerts = a.alerts.map {
                        AlertUi(
                            title = it.title,
                            message = it.message,
                            severity = it.severity,
                            timestamp = it.timestamp,
                        )
                    },
                    healthStatus = a.healthStatus.formatHealthStatus(),
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
}

package com.github.fziraki.machinepilot.domain.repository

import com.github.fziraki.machinepilot.domain.model.MachineAnalysis
import com.github.fziraki.machinepilot.domain.model.MachineOperatingState
import com.github.fziraki.machinepilot.domain.model.MachineTelemetry
import kotlinx.coroutines.flow.Flow

data class MachineState(
    val telemetry: MachineTelemetry,
    val analysis: MachineAnalysis,
    val operatingState: MachineOperatingState = MachineOperatingState.OPERATIONAL,
)

interface MachineRepository {
    fun observe(): Flow<MachineState>
    suspend fun emergencyStop()
    suspend fun releaseEmergencyStop()
}

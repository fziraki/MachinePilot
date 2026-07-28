package com.github.fziraki.machinepilot.machinesdk.sdk

import kotlinx.coroutines.flow.Flow

interface MachineClient {
    val telemetry: Flow<MachineTelemetry>
    suspend fun emergencyStop()
    suspend fun releaseEmergencyStop()
}

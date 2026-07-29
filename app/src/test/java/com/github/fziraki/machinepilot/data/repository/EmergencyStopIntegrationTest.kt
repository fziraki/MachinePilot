package com.github.fziraki.machinepilot.data.repository

import com.github.fziraki.machinepilot.domain.model.MachineOperatingState
import com.github.fziraki.machinepilot.machinesdk.sdk.FakeMachineClient
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class EmergencyStopIntegrationTest {

    @Test
    fun `emergency stop sets state and appends critical alert`() = runTest {
        val client = FakeMachineClient()
        val repo = MachineRepositoryImpl(client)

        repo.emergencyStop()

        val state = repo.observe().first()
        assertEquals(MachineOperatingState.EMERGENCY_STOPPED, state.operatingState)
        val alert = state.analysis.alerts.find { it.title == "Emergency Stop Activated" }
        assertEquals("Emergency Stop Activated", alert?.title)
        assertEquals(true, alert?.severity?.name == "CRITICAL")
    }

    @Test
    fun `release emergency stop restores operational state`() = runTest {
        val client = FakeMachineClient()
        val repo = MachineRepositoryImpl(client)

        repo.emergencyStop()
        repo.releaseEmergencyStop()

        val state = repo.observe().first()
        assertEquals(MachineOperatingState.OPERATIONAL, state.operatingState)
    }

    @Test
    fun `double emergency stop is idempotent`() = runTest {
        val client = FakeMachineClient()
        val repo = MachineRepositoryImpl(client)

        repo.emergencyStop()
        repo.emergencyStop() // second call

        val state = repo.observe().first()
        assertEquals(MachineOperatingState.EMERGENCY_STOPPED, state.operatingState)
    }
}

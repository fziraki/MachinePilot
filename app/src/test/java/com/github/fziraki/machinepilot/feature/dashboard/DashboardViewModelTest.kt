package com.github.fziraki.machinepilot.feature.dashboard

import com.github.fziraki.machinepilot.domain.model.Alert
import com.github.fziraki.machinepilot.domain.model.AlertSeverity
import com.github.fziraki.machinepilot.domain.model.Diagnostic
import com.github.fziraki.machinepilot.domain.model.HealthStatus
import com.github.fziraki.machinepilot.domain.model.MachineAnalysis
import com.github.fziraki.machinepilot.domain.model.MachineOperatingState
import com.github.fziraki.machinepilot.domain.model.MachineTelemetry
import com.github.fziraki.machinepilot.domain.repository.MachineRepository
import com.github.fziraki.machinepilot.domain.repository.MachineState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DashboardViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `state maps telemetry fields from repository`() = runTest(testDispatcher) {
        val repo = FakeRepository()
        val vm = DashboardViewModel(repo)

        val state = vm.state.value
        assertEquals("1 500", state.rpm)
        assertEquals("50", state.fuel)
        assertEquals("OPERATIONAL", state.healthStatus)
        assertEquals(false, state.isEmergencyStopped)
    }

    @Test
    fun `emergency stop sets isEmergencyStopped true`() = runTest(testDispatcher) {
        val repo = FakeRepository()
        val vm = DashboardViewModel(repo)

        vm.onAction(DashboardAction.EmergencyStop)

        val state = vm.state.value
        assertTrue(state.isEmergencyStopped)
        assertEquals("CRITICAL", state.healthStatus)
    }

    @Test
    fun `release emergency stop restores state`() = runTest(testDispatcher) {
        val repo = FakeRepository()
        val vm = DashboardViewModel(repo)

        vm.onAction(DashboardAction.EmergencyStop)
        vm.onAction(DashboardAction.ReleaseEmergencyStop)

        val state = vm.state.value
        assertFalse(state.isEmergencyStopped)
    }

    @Test
    fun `alerts from analysis appear in state`() = runTest(testDispatcher) {
        val alerts = listOf(
            Alert(message = "Low fuel", severity = AlertSeverity.WARNING, timestamp = 1000L),
        )
        val analysis = MachineAnalysis(
            alerts = alerts,
            diagnostics = listOf(Diagnostic("CODE", "msg")),
            healthStatus = HealthStatus.WARNING,
        )
        val telemetry = MachineTelemetry(0, 0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, true)
        val repo = FakeRepository(
            initial = MachineState(telemetry = telemetry, analysis = analysis, operatingState = MachineOperatingState.OPERATIONAL),
        )
        val vm = DashboardViewModel(repo)

        val state = vm.state.value
        assertEquals(1, state.alerts.size)
        assertEquals("Low fuel", state.alerts[0].message)
        assertEquals(AlertSeverity.WARNING, state.alerts[0].severity)
    }
}

private class FakeRepository(
    initial: MachineState = MachineState(
        telemetry = MachineTelemetry(1500, 50, 85.0, 30.0, 150.0, 1000.0, 12.6, 0.0, 0.0, true),
        analysis = MachineAnalysis(
            alerts = emptyList(),
            diagnostics = emptyList(),
            healthStatus = HealthStatus.NOMINAL,
        ),
        operatingState = MachineOperatingState.OPERATIONAL,
    ),
) : MachineRepository {

    private val _state = MutableStateFlow(initial)

    override fun observe(): Flow<MachineState> = _state

    override suspend fun emergencyStop() {
        val current = _state.value
        _state.value = current.copy(
            operatingState = MachineOperatingState.EMERGENCY_STOPPED,
            analysis = current.analysis.copy(
                alerts = current.analysis.alerts + Alert(
                    title = "Emergency Stop Activated",
                    message = "Machine operation has been stopped manually.",
                    severity = AlertSeverity.CRITICAL,
                ),
                healthStatus = HealthStatus.CRITICAL,
            ),
        )
    }

    override suspend fun releaseEmergencyStop() {
        val current = _state.value
        _state.value = current.copy(
            operatingState = MachineOperatingState.OPERATIONAL,
            analysis = current.analysis.copy(
                alerts = current.analysis.alerts.filter { it.title != "Emergency Stop Activated" },
                healthStatus = HealthStatus.NOMINAL,
            ),
        )
    }
}

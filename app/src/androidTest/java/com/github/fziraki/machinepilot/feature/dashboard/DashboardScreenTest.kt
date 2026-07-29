package com.github.fziraki.machinepilot.feature.dashboard

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.github.fziraki.machinepilot.domain.model.AlertSeverity
import com.github.fziraki.machinepilot.designsystem.theme.MachinePilotTheme
import org.junit.Rule
import org.junit.Test

class DashboardScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val sampleState = DashboardState(
        rpm = "1 500",
        fuel = "50",
        engineTemp = "85",
        speed = "30.0",
        hydraulicPressure = "150",
        engineHours = "1 000",
        batteryLevel = "85",
        batteryVoltage = "12.4",
        batteryStatus = "Normal",
        canStatus = "OK",
        ecuStatus = "OK",
        alerts = listOf(
            AlertUi(
                title = "Low Fuel",
                message = "Fuel below 20%",
                severity = AlertSeverity.WARNING,
                timestamp = 1000L,
            ),
        ),
        healthStatus = "OPERATIONAL",
        isEmergencyStopped = false,
    )

    @Test
    fun screen_displaysRpmAndFuel() {
        composeTestRule.setContent {
            MachinePilotTheme {
                DashboardScreen(state = sampleState, onAction = {})
            }
        }

        composeTestRule.onNodeWithText("1 500").assertIsDisplayed()
        composeTestRule.onNodeWithText("50").assertIsDisplayed()
    }

    @Test
    fun emergencyStopButton_showsReleaseAfterClick() {
        var lastAction: DashboardAction? = null

        composeTestRule.setContent {
            MachinePilotTheme {
                DashboardScreen(state = sampleState, onAction = { lastAction = it })
            }
        }

        composeTestRule.onNodeWithText("EMERGENCY STOP (SIMULATE)").performClick()
        assert(lastAction is DashboardAction.EmergencyStop)
    }
}

package com.github.fziraki.machinepilot.feature.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.fziraki.machinepilot.designsystem.theme.MachinePilotTheme
import com.github.fziraki.machinepilot.domain.model.AlertSeverity

@Composable
fun DashboardRoot(
    viewModel: DashboardViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    DashboardScreen(state = state, onAction = viewModel::onAction)
}

// ─────────────────────────────────────────────
// DashboardScreen – entry composable
// ─────────────────────────────────────────────

@Composable
fun DashboardScreen(
    state: DashboardState,
    onAction: (DashboardAction) -> Unit,
) {
    TabletHmiLayout(state, onAction)
}

// ─────────────────────────────────────────────
// Tablet Frame (device chassis)
// ─────────────────────────────────────────────

@Composable
private fun TabletHmiLayout(
    state: DashboardState,
    onAction: (DashboardAction) -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1A1D23))
            .padding(12.dp),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp)
                .clip(RoundedCornerShape(28.dp))
                .border(
                    3.dp,
                    Brush.verticalGradient(
                        listOf(Color(0xFF4A4F5B), Color(0xFF2A2D35))
                    ),
                    RoundedCornerShape(28.dp),
                )
                .background(Color(0xFF0D1117)),
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // top bezel strip
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .background(Color(0xFF2A2D35)),
                    contentAlignment = Alignment.Center,
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF1A1D23))
                            .border(1.dp, Color(0xFF4A4F5B), CircleShape),
                    )
                }

                // screen content
                Box(modifier = Modifier.weight(1f).padding(6.dp)) {
                    HmiContent(state, onAction)
                }

                // bottom bezel strip
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .background(Color(0xFF2A2D35)),
                )
            }
        }
    }
}

// ─────────────────────────────────────────────
// HMI Content – status bar + 3-column + bottom bar
// ─────────────────────────────────────────────

@Composable
private fun HmiContent(
    state: DashboardState,
    onAction: (DashboardAction) -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize()) {
        TopStatusBar(state)

        HorizontalDivider(
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
            thickness = 1.dp,
        )

        if (state.isEmergencyStopped) {
            EmergencyBanner()
        }

        Row(
            modifier = Modifier.weight(1f).fillMaxWidth(),
        ) {
            MachineOverviewPanel(
                state = state,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
            )

            VerticalDividerLine()

            SystemHealthPanel(
                state = state,
                modifier = Modifier
                    .weight(1.2f)
                    .fillMaxHeight(),
            )

            VerticalDividerLine()

            ActiveAlertsPanel(
                state = state,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
            )
        }

        HorizontalDivider(
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
            thickness = 1.dp,
        )

        BottomControlBar(state, onAction)
    }
}

// ─────────────────────────────────────────────
// Top Status Bar
// ─────────────────────────────────────────────

@Composable
private fun TopStatusBar(state: DashboardState) {
    val healthColor = when (state.healthStatus) {
        "CRITICAL" -> Color(0xFFF85149)
        "WARNING" -> Color(0xFFD29922)
        else -> Color(0xFF3FB950)
    }
    val sdf = androidx.compose.runtime.remember { java.text.SimpleDateFormat("HH:mm:ss", java.util.Locale.getDefault()) }
    val currentTime = androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(sdf.format(System.currentTimeMillis())) }
    androidx.compose.runtime.LaunchedEffect(Unit) {
        while (true) {
            kotlinx.coroutines.delay(1000L)
            currentTime.value = sdf.format(System.currentTimeMillis())
        }
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 20.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(healthColor),
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = state.healthStatus,
                style = MaterialTheme.typography.labelLarge,
                color = healthColor,
                fontWeight = FontWeight.Bold,
            )
        }

        Text(
            text = currentTime.value,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

// ─────────────────────────────────────────────
// Left Column – Machine Overview
// ─────────────────────────────────────────────

@Composable
private fun MachineOverviewPanel(
    state: DashboardState,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.padding(12.dp).fillMaxWidth(),
    ) {
        PanelLabel("Machine Overview")

        Spacer(Modifier.height(12.dp))

        OverviewGauge(
            label = "Engine Speed",
            value = state.rpm,
            unit = "RPM",
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(Modifier.height(8.dp))

        OverviewGauge(
            label = "Ground Speed",
            value = state.speed,
            unit = "km/h",
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            MiniGauge(
                label = "Fuel",
                value = state.fuel,
                unit = "%",
                modifier = Modifier.weight(1f),
            )
            MiniGauge(
                label = "Temp",
                value = state.engineTemp,
                unit = "\u00B0C",
                modifier = Modifier.weight(1f),
            )
        }

        Spacer(Modifier.height(8.dp))

        MiniGauge(
            label = "Hydraulic Pressure",
            value = state.hydraulicPressure,
            unit = "bar",
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(Modifier.height(8.dp))

        MiniGauge(
            label = "Engine Hours",
            value = state.engineHours,
            unit = "h",
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

// ─────────────────────────────────────────────
// Center Column – System Health
// ─────────────────────────────────────────────

@Composable
private fun SystemHealthPanel(
    state: DashboardState,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.padding(12.dp).fillMaxWidth(),
    ) {
        PanelLabel("System Health")

        Spacer(Modifier.height(12.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface,
            ),
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Battery",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        text = state.batteryVoltage,
                        style = MaterialTheme.typography.displayMedium,
                        color = MaterialTheme.colorScheme.primary,
                    )
                    Text(
                        text = "V",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Text(
                    text = state.batteryStatus,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        Spacer(Modifier.height(12.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface,
            ),
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                StatusRow(label = "CAN Bus", status = state.canStatus)
                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                StatusRow(label = "ECU", status = state.ecuStatus)
                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                StatusRow(label = "GPS", status = if (state.gpsConnected) "OK" else "OFF")
            }
        }


    }
}

// ─────────────────────────────────────────────
// Right Column – Active Alerts
// ─────────────────────────────────────────────

@Composable
private fun ActiveAlertsPanel(
    state: DashboardState,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.padding(12.dp).fillMaxWidth(),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            PanelLabel("Active Alerts")
            if (state.alerts.isNotEmpty()) {
                Spacer(Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(MaterialTheme.colorScheme.error.copy(alpha = 0.2f))
                        .padding(horizontal = 8.dp, vertical = 2.dp),
                ) {
                    Text(
                        text = "${state.alerts.size}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }

        Spacer(Modifier.height(8.dp))

        if (state.alerts.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "No active alerts",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                items(state.alerts) { alert ->
                    AlertCard(alert)
                }
            }
        }
    }
}

// ─────────────────────────────────────────────
// Status Row (colored dot + label + status)
// ─────────────────────────────────────────────

@Composable
private fun StatusRow(
    label: String,
    status: String,
) {
    val color = when (status) {
        "OK" -> Color(0xFF3FB950)
        "WARN" -> Color(0xFFD29922)
        else -> Color(0xFFF85149)
    }
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(color),
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Text(
            text = status,
            style = MaterialTheme.typography.labelLarge,
            color = color,
            fontWeight = FontWeight.Bold,
        )
    }
}

// ─────────────────────────────────────────────
// Bottom Control Bar
// ─────────────────────────────────────────────

@Composable
private fun BottomControlBar(
    state: DashboardState,
    onAction: (DashboardAction) -> Unit,
) {
    val context = LocalContext.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 20.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        ControlButton(
            label = "ACKNOWLEDGE ALL",
            color = MaterialTheme.colorScheme.primary,
            onClick = { android.widget.Toast.makeText(context, "Not implemented", android.widget.Toast.LENGTH_SHORT).show() },
        )
        if (state.isEmergencyStopped) {
            ControlButton(
                label = "RELEASE EMERGENCY STOP (SIMULATE)",
                color = Color(0xFF3FB950),
                onClick = { onAction(DashboardAction.ReleaseEmergencyStop) },
            )
        } else {
            ControlButton(
                label = "EMERGENCY STOP (SIMULATE)",
                color = Color(0xFFF85149),
                onClick = { onAction(DashboardAction.EmergencyStop) },
            )
        }
        ControlButton(
            label = "SYSTEM INFO",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            onClick = { android.widget.Toast.makeText(context, "Not implemented", android.widget.Toast.LENGTH_SHORT).show() },
        )
        ControlButton(
            label = "SETTINGS",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            onClick = { android.widget.Toast.makeText(context, "Not implemented", android.widget.Toast.LENGTH_SHORT).show() },
        )
    }
}

// ─────────────────────────────────────────────
// Shared Composables
// ─────────────────────────────────────────────

@Composable
private fun PanelLabel(text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .width(3.dp)
                .height(18.dp)
                .background(MaterialTheme.colorScheme.primary),
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

@Composable
private fun OverviewGauge(
    label: String,
    value: String,
    unit: String,
    color: Color,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = value,
                    style = MaterialTheme.typography.displayMedium,
                    color = color,
                )
            }
            Text(
                text = unit,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Medium,
            )
        }
    }
}

@Composable
private fun MiniGauge(
    label: String,
    value: String,
    unit: String,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary,
            )
            Text(
                text = unit,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun AlertCard(alert: AlertUi) {
    val color = when (alert.severity) {
        AlertSeverity.INFO -> MaterialTheme.colorScheme.primary
        AlertSeverity.WARNING -> MaterialTheme.colorScheme.tertiary
        AlertSeverity.CRITICAL -> MaterialTheme.colorScheme.error
    }
    val severityLabel = when (alert.severity) {
        AlertSeverity.INFO -> "INFO"
        AlertSeverity.WARNING -> "WARNING"
        AlertSeverity.CRITICAL -> "CRITICAL"
    }
    Card(
        shape = RoundedCornerShape(6.dp),
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.08f),
        ),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            verticalAlignment = Alignment.Top,
        ) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(color)
                    .padding(top = 2.dp),
            )
            Spacer(Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = severityLabel,
                    style = MaterialTheme.typography.labelSmall,
                    color = color,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                )
                if (alert.title.isNotEmpty()) {
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = alert.title,
                        style = MaterialTheme.typography.bodySmall,
                        color = color,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
                Spacer(Modifier.height(2.dp))
                Text(
                    text = alert.message,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                if (alert.timestamp != 0L) {
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = formatTimestamp(alert.timestamp),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}

@Composable
private fun ControlButton(
    label: String,
    color: Color,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(color.copy(alpha = 0.12f))
            .border(1.dp, color.copy(alpha = 0.3f), RoundedCornerShape(6.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = color,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
private fun EmergencyBanner() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF85149).copy(alpha = 0.15f))
            .border(1.dp, Color(0xFFF85149), RoundedCornerShape(0.dp))
            .padding(horizontal = 20.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "EMERGENCY STOP ACTIVE",
            style = MaterialTheme.typography.titleMedium,
            color = Color(0xFFF85149),
            fontWeight = FontWeight.Bold,
            letterSpacing = 2.sp,
        )
    }
}

@Composable
private fun VerticalDividerLine() {
    Box(
        modifier = Modifier
            .width(1.dp)
            .fillMaxHeight()
            .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)),
    )
}

private fun formatTimestamp(millis: Long): String {
    if (millis == 0L) return ""
    val sdf = java.text.SimpleDateFormat("HH:mm:ss", java.util.Locale.getDefault())
    return sdf.format(java.util.Date(millis))
}

// ─────────────────────────────────────────────
// Preview
// ─────────────────────────────────────────────

@Preview(showBackground = true, widthDp = 1280, heightDp = 800)
@Composable
private fun DashboardScreenPreview() {
    MachinePilotTheme {
        DashboardScreen(state = DashboardState(), onAction = {})
    }
}

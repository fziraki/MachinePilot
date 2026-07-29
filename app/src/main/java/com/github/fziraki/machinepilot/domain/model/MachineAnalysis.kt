package com.github.fziraki.machinepilot.domain.model

data class MachineAnalysis(
    val alerts: List<Alert>,
    val diagnostics: List<Diagnostic>,
    val healthStatus: HealthStatus,
)

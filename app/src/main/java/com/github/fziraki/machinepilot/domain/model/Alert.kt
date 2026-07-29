package com.github.fziraki.machinepilot.domain.model

data class Alert(
    val title: String = "",
    val message: String,
    val severity: AlertSeverity,
    val timestamp: Long = 0L,
)

enum class AlertSeverity { INFO, WARNING, CRITICAL }

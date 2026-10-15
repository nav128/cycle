package com.cycletracker.app

enum class CyclePhase {
    FOLLICULAR,      // Pre-ovulatory phase
    OVULATION,       // Ovulation detected
    LUTEAL,          // Post-ovulatory phase
    INSUFFICIENT_DATA // Not enough data to determine phase
}

data class CycleInsight(
    val phase: CyclePhase,
    val coverline: Double?,
    val ovulationDay: Int?, // Day index where ovulation was detected
    val message: String
)

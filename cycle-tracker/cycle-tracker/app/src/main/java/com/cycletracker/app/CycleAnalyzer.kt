package com.cycletracker.app

import kotlin.math.abs

class CycleAnalyzer {

    companion object {
        private const val COVERLINE_DAYS = 6
        private const val TEMP_RISE_THRESHOLD = 0.2 // Celsius
        private const val OVULATION_CONFIRMATION_DAYS = 3
    }

    fun analyzeCycle(entries: List<TemperatureEntry>): CycleInsight {
        if (entries.size < COVERLINE_DAYS) {
            return CycleInsight(
                phase = CyclePhase.INSUFFICIENT_DATA,
                coverline = null,
                ovulationDay = null,
                message = "נדרשים לפחות $COVERLINE_DAYS מדידות לניתוח"
            )
        }

        // Group entries by date and take average temperature per day
        val dailyTemperatures = groupByDay(entries)

        if (dailyTemperatures.size < COVERLINE_DAYS) {
            return CycleInsight(
                phase = CyclePhase.INSUFFICIENT_DATA,
                coverline = null,
                ovulationDay = null,
                message = "נדרשים לפחות $COVERLINE_DAYS ימים של מדידות"
            )
        }

        // Apply 3-over-6 rule
        return detectPhaseWithRule(dailyTemperatures)
    }

    private fun groupByDay(entries: List<TemperatureEntry>): List<Double> {
        val grouped = entries.groupBy {
            // Normalize to day start (midnight)
            val calendar = java.util.Calendar.getInstance()
            calendar.timeInMillis = it.date
            calendar.set(java.util.Calendar.HOUR_OF_DAY, 0)
            calendar.set(java.util.Calendar.MINUTE, 0)
            calendar.set(java.util.Calendar.SECOND, 0)
            calendar.set(java.util.Calendar.MILLISECOND, 0)
            calendar.timeInMillis
        }

        return grouped.entries
            .sortedBy { it.key }
            .map { entry -> entry.value.map { it.temperature }.average() }
    }

    private fun detectPhaseWithRule(dailyTemps: List<Double>): CycleInsight {
        // Need at least 6 days for baseline + 3 days for confirmation
        if (dailyTemps.size < COVERLINE_DAYS + OVULATION_CONFIRMATION_DAYS) {
            // Calculate coverline from available data
            val coverline = dailyTemps.take(COVERLINE_DAYS.coerceAtMost(dailyTemps.size)).maxOrNull() ?: 0.0

            return CycleInsight(
                phase = CyclePhase.FOLLICULAR,
                coverline = coverline,
                ovulationDay = null,
                message = "שלב פוליקולרי - טמפרטורה בסיסית: %.2f°C".format(coverline)
            )
        }

        // Scan for ovulation using sliding window
        for (startIdx in 0..(dailyTemps.size - COVERLINE_DAYS - OVULATION_CONFIRMATION_DAYS)) {
            val baselineDays = dailyTemps.subList(startIdx, startIdx + COVERLINE_DAYS)
            val coverline = baselineDays.maxOrNull() ?: continue

            // Check next 3 days for sustained rise
            val nextThreeDays = dailyTemps.subList(
                startIdx + COVERLINE_DAYS,
                startIdx + COVERLINE_DAYS + OVULATION_CONFIRMATION_DAYS
            )

            // All three days must be above coverline
            val allAboveCoverline = nextThreeDays.all { it > coverline }

            // Third day must be at least 0.2°C above coverline
            val thirdDayHighEnough = nextThreeDays[2] >= (coverline + TEMP_RISE_THRESHOLD)

            if (allAboveCoverline && thirdDayHighEnough) {
                val ovulationDay = startIdx + COVERLINE_DAYS
                val currentDay = dailyTemps.size - 1

                return if (currentDay == ovulationDay + 2) {
                    // Just detected ovulation
                    CycleInsight(
                        phase = CyclePhase.OVULATION,
                        coverline = coverline,
                        ovulationDay = ovulationDay,
                        message = "זוהתה ביוץ! קו בסיס: %.2f°C, עלייה של %.2f°C".format(
                            coverline,
                            nextThreeDays[2] - coverline
                        )
                    )
                } else {
                    // In luteal phase
                    val daysAfterOvulation = currentDay - ovulationDay
                    CycleInsight(
                        phase = CyclePhase.LUTEAL,
                        coverline = coverline,
                        ovulationDay = ovulationDay,
                        message = "שלב לוטאלי - יום %d לאחר ביוץ. קו בסיס: %.2f°C".format(
                            daysAfterOvulation,
                            coverline
                        )
                    )
                }
            }
        }

        // No ovulation detected - still in follicular phase
        val recentCoverline = dailyTemps.takeLast(COVERLINE_DAYS).maxOrNull() ?: 0.0
        return CycleInsight(
            phase = CyclePhase.FOLLICULAR,
            coverline = recentCoverline,
            ovulationDay = null,
            message = "שלב פוליקולרי - ממתין לעלייה בטמפרטורה. קו בסיס: %.2f°C".format(recentCoverline)
        )
    }

    fun getMovingAverage(dailyTemps: List<Double>, windowSize: Int = 3): List<Double> {
        if (dailyTemps.size < windowSize) return dailyTemps

        return dailyTemps.windowed(windowSize, 1) { window ->
            window.average()
        }
    }
}

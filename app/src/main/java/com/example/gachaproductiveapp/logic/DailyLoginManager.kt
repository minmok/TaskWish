package com.example.gachaproductiveapp.logic

import com.example.gachaproductiveapp.data.UserState
import java.time.LocalDate
import java.time.ZoneOffset

object DailyLoginManager {

    // index 0..6 = streak day 1..7
    private val WEEKLY_BONUS = intArrayOf(10, 15, 20, 25, 30, 40, 60)
    private const val WELCOME_BACK_BONUS = 15 // bonus sparks for returning after streak break

    private fun todayEpochDay(): Long = LocalDate.now(ZoneOffset.UTC).toEpochDay()

    /** True if the user hasn't claimed today's bonus yet. */
    fun canClaimToday(state: UserState): Boolean = state.lastLoginEpochDay != todayEpochDay()

    /**
     * Claims today's bonus and returns (bonusGranted, updatedState).
     * 1-day grace period: missing exactly one day holds the streak instead of resetting it;
     * missing two or more days resets to day 1 with a welcome-back bonus.
     */
    fun claim(state: UserState): Pair<Int, UserState> {
        val today = todayEpochDay()
        val daysSinceLast =
            if (state.lastLoginEpochDay < 0) Long.MAX_VALUE else today - state.lastLoginEpochDay

        val brokeStreak = daysSinceLast > 2L && state.lastLoginEpochDay >= 0

        val newStreak = when {
            daysSinceLast == 1L -> (state.loginStreak % 7) + 1 // consecutive day
            daysSinceLast == 2L -> state.loginStreak.coerceAtLeast(1) // grace day, streak held
            else -> 1 // broke streak, or first ever login
        }

        val baseBonus = WEEKLY_BONUS[(newStreak - 1).coerceIn(0, 6)]
        val bonus = baseBonus + if (brokeStreak) WELCOME_BACK_BONUS else 0

        val updated = state.copy(
            currency = state.currency + bonus,
            lastLoginEpochDay = today,
            loginStreak = newStreak
        )
        return bonus to updated
    }
}

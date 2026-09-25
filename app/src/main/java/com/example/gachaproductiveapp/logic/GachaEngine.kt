package com.example.gachaproductiveapp.logic

import com.example.gachaproductiveapp.data.Rarity
import com.example.gachaproductiveapp.data.Reward
import com.example.gachaproductiveapp.data.UserState
import kotlin.random.Random

data class PullResult(
    val rarity: Rarity,
    val reward: Reward?,
    val isFeatured: Boolean = false
)

/** Read-only view of the user's reward pool, built fresh for each roll. */
class RewardPool(
    private val rewardsByRarity: Map<Rarity, List<Reward>>,
    val featuredFiveStar: Reward?
) {
    fun randomFrom(rarity: Rarity, excludeFeatured: Boolean = false): Reward? {
        val list = rewardsByRarity[rarity].orEmpty()
            .let { if (excludeFeatured) it.filterNot { r -> r.isFeatured } else it }
        return if (list.isEmpty()) null else list.random()
    }
}

object GachaEngine {

    const val PULL_COST = 10 // 10 Favor = 1 Pull
    const val MULTI_PULL_COST = 50 // 5 pulls

    private const val BASE_5_STAR = 0.005 // 0.5% early chance
    private const val SOFT_PITY_START = 25
    private const val SOFT_PITY_STEP = 0.15
    const val HARD_PITY = 30 // Hard pity at 30

    private const val RATIO_4_OF_NON5 = 0.1327 // 13% for 4-star, 86.5% for 3-star
    private const val PITY_4_HARD = 10

    /** Probability of a 5-star on the NEXT pull, given pulls since the last 5-star. */
    fun fiveStarChance(pity5Counter: Int): Double {
        val n = pity5Counter + 1
        return if (n < SOFT_PITY_START) BASE_5_STAR
        else minOf(1.0, BASE_5_STAR + SOFT_PITY_STEP * (n - (SOFT_PITY_START - 1)))
    }

    /** One pull. Currency is NOT deducted here — the repository handles that. */
    fun rollOnce(state: UserState, pool: RewardPool): Pair<PullResult, UserState> {
        val p5 = fiveStarChance(state.pity5Counter)
        val remaining = 1.0 - p5
        val p4 = remaining * RATIO_4_OF_NON5

        val roll = Random.nextDouble()
        var rarity = when {
            roll < p5 -> Rarity.FIVE_STAR
            roll < p5 + p4 -> Rarity.FOUR_STAR
            else -> Rarity.THREE_STAR
        }

        var newPity4 = state.pity4Counter + 1
        var newPity5 = state.pity5Counter + 1

        // 4-star hard pity: force an upgrade if 10 pulls passed with nothing 4-star+
        if (rarity == Rarity.THREE_STAR && newPity4 >= PITY_4_HARD) {
            rarity = Rarity.FOUR_STAR
        }

        when (rarity) {
            Rarity.FIVE_STAR -> { newPity5 = 0; newPity4 = 0 }
            Rarity.FOUR_STAR -> { newPity4 = 0 }
            Rarity.THREE_STAR -> { /* counters already incremented above */ }
        }

        var guaranteed = state.guaranteed5
        var isFeatured = false
        val reward: Reward? = when (rarity) {
            Rarity.FIVE_STAR -> {
                val wonFeatured = guaranteed || Random.nextBoolean()
                guaranteed = !wonFeatured
                isFeatured = wonFeatured
                if (wonFeatured) {
                    pool.featuredFiveStar
                } else {
                    pool.randomFrom(Rarity.FIVE_STAR, excludeFeatured = true) ?: pool.featuredFiveStar
                }
            }
            Rarity.FOUR_STAR -> pool.randomFrom(Rarity.FOUR_STAR)
            Rarity.THREE_STAR -> pool.randomFrom(Rarity.THREE_STAR)
        }

        val updatedState = state.copy(
            pity5Counter = newPity5,
            pity4Counter = newPity4,
            guaranteed5 = guaranteed
        )

        return PullResult(rarity, reward, isFeatured) to updatedState
    }

    /** 5 sequential rolls so pity carries correctly from one pull to the next. */
    fun rollFive(state: UserState, pool: RewardPool): Pair<List<PullResult>, UserState> {
        var currentState = state
        val results = mutableListOf<PullResult>()
        repeat(5) {
            val (result, next) = rollOnce(currentState, pool)
            results.add(result)
            currentState = next
        }
        return results to currentState
    }
}

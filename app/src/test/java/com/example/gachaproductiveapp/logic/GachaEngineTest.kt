package com.example.gachaproductiveapp.logic

import com.example.gachaproductiveapp.data.Rarity
import com.example.gachaproductiveapp.data.Reward
import com.example.gachaproductiveapp.data.UserState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Test

class GachaEngineTest {

    @Test
    fun fallbackReward_usesAnotherRarityWhenTargetPoolMissing() {
        val fourStarReward = Reward(id = 42, name = "Coffee Break", rarity = Rarity.FOUR_STAR)
        val pool = RewardPool(
            rewardsByRarity = mapOf(
                Rarity.THREE_STAR to emptyList(),
                Rarity.FOUR_STAR to listOf(fourStarReward),
                Rarity.FIVE_STAR to emptyList()
            ),
            featuredFiveStar = null
        )

        val fallback = pool.fallbackReward(Rarity.THREE_STAR)

        assertNotNull(fallback)
        assertEquals(Rarity.FOUR_STAR, fallback!!.rarity)
        assertEquals(42, fallback.id)
    }

    @Test
    fun hasAnyReward_returnsFalseWhenAllPoolsEmpty() {
        val pool = RewardPool(
            rewardsByRarity = mapOf(
                Rarity.THREE_STAR to emptyList(),
                Rarity.FOUR_STAR to emptyList(),
                Rarity.FIVE_STAR to emptyList()
            ),
            featuredFiveStar = null
        )

        assertFalse(pool.hasAnyReward())
    }

    @Test
    fun rollOnce_fiveStarFallbackStillReturnsReward() {
        val fourStarReward = Reward(id = 7, name = "Movie Night", rarity = Rarity.FOUR_STAR)
        val pool = RewardPool(
            rewardsByRarity = mapOf(
                Rarity.THREE_STAR to emptyList(),
                Rarity.FOUR_STAR to listOf(fourStarReward),
                Rarity.FIVE_STAR to emptyList()
            ),
            featuredFiveStar = null
        )
        val state = UserState(pity5Counter = GachaEngine.HARD_PITY - 1)

        val (result, updated) = GachaEngine.rollOnce(state, pool)

        assertNotNull(result.reward)
        assertEquals(Rarity.FOUR_STAR, result.rarity)
        assertEquals(0, updated.pity5Counter)
        assertEquals(0, updated.pity4Counter)
    }
}

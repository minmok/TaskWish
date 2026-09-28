package com.example.gachaproductiveapp.repository

import com.example.gachaproductiveapp.data.Rarity
import com.example.gachaproductiveapp.data.Reward
import com.example.gachaproductiveapp.data.RewardDao
import com.example.gachaproductiveapp.data.Task
import com.example.gachaproductiveapp.data.TaskDao
import com.example.gachaproductiveapp.data.TaskTier
import com.example.gachaproductiveapp.data.UserState
import com.example.gachaproductiveapp.data.UserStateDao
import com.example.gachaproductiveapp.logic.DailyLoginManager
import com.example.gachaproductiveapp.logic.GachaEngine
import com.example.gachaproductiveapp.logic.PullResult
import com.example.gachaproductiveapp.logic.RewardPool
import kotlinx.coroutines.flow.Flow

class GameRepository(
    private val taskDao: TaskDao,
    private val rewardDao: RewardDao,
    private val userStateDao: UserStateDao
) {
    val tasks: Flow<List<Task>> = taskDao.observeTasks()
    val rewards: Flow<List<Reward>> = rewardDao.observeAll()
    val userState: Flow<UserState?> = userStateDao.observeState()

    suspend fun ensureUserState(): UserState {
        return userStateDao.getState() ?: UserState(id = 0).also { userStateDao.upsert(it) }
    }

    suspend fun setUserName(username: String) {
        val state = ensureUserState()
        userStateDao.upsert(state.copy(username = username))
    }

    suspend fun addTask(title: String, description: String, tier: TaskTier) {
        taskDao.insert(Task(title = title, description = description, tier = tier))
    }

    suspend fun deleteTask(task: Task) {
        taskDao.delete(task)
    }

    suspend fun deleteAllTasks() {
        taskDao.deleteAll()
    }

    suspend fun deleteCompletedTasks() {
        taskDao.deleteCompleted()
    }

    suspend fun completeTask(task: Task) {
        if (task.isCompleted) return
        taskDao.update(task.copy(isCompleted = true, completedAt = System.currentTimeMillis()))
        val state = ensureUserState()
        userStateDao.upsert(state.copy(currency = state.currency + task.tier.currencyReward))
    }

    suspend fun addReward(name: String, description: String, rarity: Rarity, isFeatured: Boolean, imageUrl: String) {
        val normalizedFeatured = rarity == Rarity.FIVE_STAR && isFeatured
        if (normalizedFeatured) {
            // only one featured 5-star at a time
            rewardDao.getByRarity(Rarity.FIVE_STAR).forEach {
                if (it.isFeatured) rewardDao.update(it.copy(isFeatured = false))
            }
        }
        rewardDao.insert(
            Reward(
                name = name,
                description = description,
                rarity = rarity,
                isFeatured = normalizedFeatured,
                imageUrl = imageUrl
            )
        )
    }

    suspend fun updateReward(reward: Reward) {
        val normalizedReward = if (reward.rarity == Rarity.FIVE_STAR) reward else reward.copy(isFeatured = false)
        if (normalizedReward.rarity == Rarity.FIVE_STAR && normalizedReward.isFeatured) {
            rewardDao.getByRarity(Rarity.FIVE_STAR).forEach {
                if (it.id != normalizedReward.id && it.isFeatured) rewardDao.update(it.copy(isFeatured = false))
            }
        }
        rewardDao.update(normalizedReward)
    }

    suspend fun canClaimDailyLogin(): Boolean = DailyLoginManager.canClaimToday(ensureUserState())

    suspend fun claimDailyLogin(): Int {
        val state = ensureUserState()
        val (bonus, updated) = DailyLoginManager.claim(state)
        userStateDao.upsert(updated)
        return bonus
    }

    private suspend fun buildPool(): RewardPool {
        val pools = mapOf(
            Rarity.THREE_STAR to rewardDao.getByRarity(Rarity.THREE_STAR),
            Rarity.FOUR_STAR to rewardDao.getByRarity(Rarity.FOUR_STAR),
            Rarity.FIVE_STAR to rewardDao.getByRarity(Rarity.FIVE_STAR)
        )
        return RewardPool(rewardsByRarity = pools, featuredFiveStar = rewardDao.getFeaturedFiveStar())
    }

    /** Returns null if the user can't afford the pull. */
    suspend fun pullSingle(): PullResult? {
        val state = ensureUserState()
        if (state.currency < GachaEngine.PULL_COST) return null
        val pool = buildPool()
        val (result, updatedState) = GachaEngine.rollOnce(state, pool)
        userStateDao.upsert(updatedState.copy(currency = state.currency - GachaEngine.PULL_COST))
        bumpTimesWon(result)
        return result
    }

    suspend fun pullFive(): List<PullResult>? {
        val state = ensureUserState()
        if (state.currency < GachaEngine.MULTI_PULL_COST) return null
        val pool = buildPool()
        val (results, updatedState) = GachaEngine.rollFive(state, pool)
        userStateDao.upsert(updatedState.copy(currency = state.currency - GachaEngine.MULTI_PULL_COST))
        results.forEach { bumpTimesWon(it) }
        return results
    }

    suspend fun devAddCurrency(amount: Int = 5000) {
        val state = ensureUserState()
        userStateDao.upsert(state.copy(currency = state.currency + amount))
    }

    suspend fun devResetPity() {
        val state = ensureUserState()
        userStateDao.upsert(state.copy(pity5Counter = 0, pity4Counter = 0, guaranteed5 = false))
    }

    suspend fun devFreePullSingle(): PullResult {
        val state = ensureUserState()
        val pool = buildPool()
        val (result, updatedState) = GachaEngine.rollOnce(state, pool)
        userStateDao.upsert(updatedState)
        bumpTimesWon(result)
        return result
    }

    suspend fun devFreePullFive(): List<PullResult> {
        val state = ensureUserState()
        val pool = buildPool()
        val (results, updatedState) = GachaEngine.rollFive(state, pool)
        userStateDao.upsert(updatedState)
        results.forEach { bumpTimesWon(it) }
        return results
    }

    private suspend fun bumpTimesWon(result: PullResult) {
        result.reward?.let { rewardDao.update(it.copy(timesWon = it.timesWon + 1)) }
    }

    suspend fun useReward(reward: Reward) {
        if (reward.remainingCount > 0) {
            rewardDao.update(reward.copy(timesUsed = reward.timesUsed + 1))
        }
    }
}

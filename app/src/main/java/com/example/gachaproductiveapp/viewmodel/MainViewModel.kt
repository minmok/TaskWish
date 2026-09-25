package com.example.gachaproductiveapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.gachaproductiveapp.data.AppDatabase
import com.example.gachaproductiveapp.data.Rarity
import com.example.gachaproductiveapp.data.Reward
import com.example.gachaproductiveapp.data.Task
import com.example.gachaproductiveapp.data.TaskTier
import com.example.gachaproductiveapp.data.UserState
import com.example.gachaproductiveapp.logic.PullResult
import com.example.gachaproductiveapp.repository.GameRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getInstance(application)
    private val repository = GameRepository(db.taskDao(), db.rewardDao(), db.userStateDao())

    val tasks: StateFlow<List<Task>> = repository.tasks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val rewards: StateFlow<List<Reward>> = repository.rewards
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val userState: StateFlow<UserState?> = repository.userState
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private val _lastPullResult = MutableStateFlow<List<PullResult>?>(null)
    val lastPullResult: StateFlow<List<PullResult>?> = _lastPullResult

    // null = hidden. -1 = claimable, not yet claimed. Any other value = amount just claimed.
    private val _dailyLoginBonus = MutableStateFlow<Int?>(null)
    val dailyLoginBonus: StateFlow<Int?> = _dailyLoginBonus

    init {
        viewModelScope.launch {
            repository.ensureUserState()
            if (repository.canClaimDailyLogin()) {
                _dailyLoginBonus.value = -1
            }
        }
    }

    fun setUserName(username: String) = viewModelScope.launch {
        repository.setUserName(username)
    }

    fun addTask(title: String, description: String, tier: TaskTier) = viewModelScope.launch {
        repository.addTask(title, description, tier)
    }

    fun deleteTask(task: Task) = viewModelScope.launch {
        repository.deleteTask(task)
    }

    fun deleteAllTasks() = viewModelScope.launch {
        repository.deleteAllTasks()
    }

    fun deleteCompletedTasks() = viewModelScope.launch {
        repository.deleteCompletedTasks()
    }

    fun completeTask(task: Task) = viewModelScope.launch {
        repository.completeTask(task)
    }

    fun addReward(name: String, description: String, rarity: Rarity, isFeatured: Boolean, imageUrl: String) = viewModelScope.launch {
        repository.addReward(name, description, rarity, isFeatured, imageUrl)
    }

    fun updateReward(reward: Reward) = viewModelScope.launch {
        repository.updateReward(reward)
    }

    fun claimDailyLogin() = viewModelScope.launch {
        val bonus = repository.claimDailyLogin()
        _dailyLoginBonus.value = bonus
    }

    fun dismissDailyLoginBanner() {
        _dailyLoginBonus.value = null
    }

    fun pullSingle() = viewModelScope.launch {
        repository.pullSingle()?.let { _lastPullResult.value = listOf(it) }
    }

    fun pullFive() = viewModelScope.launch {
        repository.pullFive()?.let { _lastPullResult.value = it }
    }

    fun devAddFavor() = viewModelScope.launch {
        repository.devAddCurrency(5000)
    }

    fun devResetPity() = viewModelScope.launch {
        repository.devResetPity()
    }

    fun devFreePullSingle() = viewModelScope.launch {
        _lastPullResult.value = listOf(repository.devFreePullSingle())
    }

    fun devFreePullFive() = viewModelScope.launch {
        _lastPullResult.value = repository.devFreePullFive()
    }

    fun clearPullResult() {
        _lastPullResult.value = null
    }
}
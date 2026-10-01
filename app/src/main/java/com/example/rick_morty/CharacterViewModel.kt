package com.example.rick_morty

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

sealed interface SyncState {
    object Idle : SyncState
    object Loading : SyncState
    object Success : SyncState
    data class Error(val message: String) : SyncState
}

class CharacterViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: CharacterRepository
    val charactersState: StateFlow<List<CharacterEntity>>

    private val _syncState = MutableStateFlow<SyncState>(SyncState.Idle)
    val syncState: StateFlow<SyncState> = _syncState.asStateFlow()

    init {
        val database = AppDatabase.getDatabase(application)
        repository = CharacterRepository(database.characterDao(), RetrofitClient.api)

        charactersState = repository.allCharacters.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        syncData()
    }

    fun syncData() {
        viewModelScope.launch {
            _syncState.value = SyncState.Loading
            try {
                repository.syncCharacters()
                _syncState.value = SyncState.Success
            } catch (e: Exception) {
                _syncState.value = SyncState.Error("Falha na conexão. Exibindo dados locais.")
            }
        }
    }

    fun resetSyncState() {
        _syncState.value = SyncState.Idle
    }
}
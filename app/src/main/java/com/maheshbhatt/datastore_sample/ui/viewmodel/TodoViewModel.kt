package com.maheshbhatt.datastore_sample.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.maheshbhatt.datastore_sample.data.model.Todo
import com.maheshbhatt.datastore_sample.data.repository.DataStoreType
import com.maheshbhatt.datastore_sample.data.repository.TodoRepository
import com.maheshbhatt.datastore_sample.ui.mvi.TodoUiIntent
import com.maheshbhatt.datastore_sample.ui.mvi.TodoUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.util.UUID

/**
 * ViewModel for Todo screen implementing MVI (Model-View-Intent) pattern.
 * 
 * Manages UI state and processes user intents to update the state.
 * Observes repository Flow for reactive data updates.
 */
class TodoViewModel(application: Application) : AndroidViewModel(application) {

    private var repository: TodoRepository = TodoRepository.create(
        application.applicationContext,
        DataStoreType.KEY_VALUE
    )

    private val _uiState = MutableStateFlow(
        TodoUiState(
            dataStoreType = DataStoreType.KEY_VALUE
        )
    )
    val uiState: StateFlow<TodoUiState> = _uiState.asStateFlow()

    init {
        // Start observing todos from repository
        observeTodos()
    }

    /**
     * Processes user intents and updates the UI state accordingly.
     * 
     * @param intent The user intent to process
     */
    fun processIntent(intent: TodoUiIntent) {
        when (intent) {
            is TodoUiIntent.AddTodo -> handleAddTodo(intent.title)
            is TodoUiIntent.ToggleTodo -> handleToggleTodo(intent.id)
            is TodoUiIntent.DeleteTodo -> handleDeleteTodo(intent.id)
            is TodoUiIntent.SwitchDataStore -> handleSwitchDataStore(intent.type)
            is TodoUiIntent.LoadTodos -> handleLoadTodos()
        }
    }

    /**
     * Observes todos from the repository and updates the UI state.
     */
    private fun observeTodos() {
        repository.getTodos()
            .onEach { todos ->
                _uiState.value = _uiState.value.copy(
                    todos = todos,
                    isLoading = false,
                    error = null
                )
            }
            .catch { exception ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = exception.message ?: "An error occurred"
                )
            }
            .launchIn(viewModelScope)
    }

    /**
     * Handles adding a new todo.
     */
    private fun handleAddTodo(title: String) {
        if (title.isBlank()) {
            return
        }

        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true, error = null)
                val newTodo = Todo(
                    id = UUID.randomUUID().toString(),
                    title = title.trim(),
                    completed = false
                )
                repository.addTodo(newTodo)
                // State will be updated automatically via observeTodos()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to add todo"
                )
            }
        }
    }

    /**
     * Handles toggling the completion status of a todo.
     */
    private fun handleToggleTodo(id: String) {
        viewModelScope.launch {
            try {
                val todo = _uiState.value.todos.find { it.id == id }
                if (todo != null) {
                    val updatedTodo = todo.copy(completed = !todo.completed)
                    repository.updateTodo(updatedTodo)
                    // State will be updated automatically via observeTodos()
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message ?: "Failed to toggle todo"
                )
            }
        }
    }

    /**
     * Handles deleting a todo.
     */
    private fun handleDeleteTodo(id: String) {
        viewModelScope.launch {
            try {
                repository.deleteTodo(id)
                // State will be updated automatically via observeTodos()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message ?: "Failed to delete todo"
                )
            }
        }
    }

    /**
     * Handles switching between DataStore types.
     * Recreates the repository with the new DataStore type.
     */
    private fun handleSwitchDataStore(type: DataStoreType) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(
                    isLoading = true,
                    error = null,
                    dataStoreType = type
                )

                // Recreate repository with new DataStore type
                repository = TodoRepository.create(
                    getApplication<Application>().applicationContext,
                    type
                )

                // Restart observing todos from the new repository
                observeTodos()

                // Load todos from the new DataStore
                handleLoadTodos()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to switch DataStore"
                )
            }
        }
    }

    /**
     * Handles loading todos from the DataStore.
     * This is typically called on initialization or after switching DataStore types.
     */
    private fun handleLoadTodos() {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(
                    isLoading = true,
                    error = null
                )
                // Todos will be loaded automatically via observeTodos()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to load todos"
                )
            }
        }
    }
}

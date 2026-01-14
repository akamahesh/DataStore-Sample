package com.maheshbhatt.datastore_sample.ui.mvi

import com.maheshbhatt.datastore_sample.data.model.Todo
import com.maheshbhatt.datastore_sample.data.repository.DataStoreType

/**
 * Represents the current UI state for the Todo screen.
 * 
 * This is an immutable data class that holds all the state needed
 * to render the UI in the MVI pattern.
 * 
 * @param todos The current list of todos
 * @param dataStoreType The currently selected DataStore type
 * @param isLoading Whether a data operation is in progress
 * @param error Error message if an error occurred, null otherwise
 */
data class TodoUiState(
    val todos: List<Todo> = emptyList(),
    val dataStoreType: DataStoreType = DataStoreType.KEY_VALUE,
    val isLoading: Boolean = false,
    val error: String? = null
)

package com.maheshbhatt.datastore_sample.ui.mvi

import com.maheshbhatt.datastore_sample.data.repository.DataStoreType

/**
 * Sealed class representing all user intents/actions in the MVI pattern.
 * 
 * Each intent represents a user action that should be processed by the ViewModel.
 * The ViewModel will handle these intents and update the UI state accordingly.
 */
sealed class TodoUiIntent {
    /**
     * Intent to add a new todo item.
     * 
     * @param title The title/description of the todo to add
     */
    data class AddTodo(val title: String) : TodoUiIntent()

    /**
     * Intent to toggle the completion status of a todo.
     * 
     * @param id The unique identifier of the todo to toggle
     */
    data class ToggleTodo(val id: String) : TodoUiIntent()

    /**
     * Intent to delete a todo item.
     * 
     * @param id The unique identifier of the todo to delete
     */
    data class DeleteTodo(val id: String) : TodoUiIntent()

    /**
     * Intent to switch between DataStore types.
     * 
     * @param type The DataStore type to switch to (KEY_VALUE or PROTOBUF)
     */
    data class SwitchDataStore(val type: DataStoreType) : TodoUiIntent()

    /**
     * Intent to load todos from the DataStore.
     * This is typically called on initialization or after switching DataStore types.
     */
    object LoadTodos : TodoUiIntent()
}

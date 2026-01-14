package com.maheshbhatt.datastore_sample.data.datasource

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.maheshbhatt.datastore_sample.data.model.Todo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * KeyValue DataStore implementation for Todo storage.
 * 
 * Stores todos as a JSON string in Preferences DataStore.
 * Key: "todos", Value: JSON array of todos
 */
class KeyValueTodoDataSource(
    private val context: Context
) : TodoDataSource {

    companion object {
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "todo_preferences")
        private val TODOS_KEY = stringPreferencesKey("todos")
    }

    private val dataStore = context.dataStore

    override fun getTodos(): Flow<List<Todo>> {
        return dataStore.data.map { preferences ->
            val todosJson = preferences[TODOS_KEY] ?: ""
            Todo.fromJsonString(todosJson)
        }
    }

    override suspend fun saveTodos(todos: List<Todo>) {
        dataStore.edit { preferences ->
            val todosJson = Todo.toJsonString(todos)
            preferences[TODOS_KEY] = todosJson
        }
    }

    override suspend fun addTodo(todo: Todo) {
        dataStore.edit { preferences ->
            val currentTodosJson = preferences[TODOS_KEY] ?: ""
            val currentTodos = Todo.fromJsonString(currentTodosJson).toMutableList()
            currentTodos.add(todo)
            preferences[TODOS_KEY] = Todo.toJsonString(currentTodos)
        }
    }

    override suspend fun updateTodo(todo: Todo) {
        dataStore.edit { preferences ->
            val currentTodosJson = preferences[TODOS_KEY] ?: ""
            val currentTodos = Todo.fromJsonString(currentTodosJson).toMutableList()
            val index = currentTodos.indexOfFirst { it.id == todo.id }
            if (index != -1) {
                currentTodos[index] = todo
                preferences[TODOS_KEY] = Todo.toJsonString(currentTodos)
            }
        }
    }

    override suspend fun deleteTodo(id: String) {
        dataStore.edit { preferences ->
            val currentTodosJson = preferences[TODOS_KEY] ?: ""
            val currentTodos = Todo.fromJsonString(currentTodosJson).toMutableList()
            currentTodos.removeAll { it.id == id }
            preferences[TODOS_KEY] = Todo.toJsonString(currentTodos)
        }
    }
}

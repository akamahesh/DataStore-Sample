package com.maheshbhatt.datastore_sample.data.datasource

import com.maheshbhatt.datastore_sample.data.model.Todo
import kotlinx.coroutines.flow.Flow

/**
 * Interface defining CRUD operations for Todo data storage.
 * 
 * Implementations can use either KeyValue or Protobuf DataStore.
 * All operations return Flow for reactive updates.
 */
interface TodoDataSource {
    /**
     * Gets all todos as a Flow that emits updates whenever the data changes.
     * 
     * @return Flow emitting the current list of todos
     */
    fun getTodos(): Flow<List<Todo>>

    /**
     * Adds a new todo item.
     * 
     * @param todo The todo item to add
     */
    suspend fun addTodo(todo: Todo)

    /**
     * Updates an existing todo item.
     * 
     * @param todo The todo item with updated values
     */
    suspend fun updateTodo(todo: Todo)

    /**
     * Deletes a todo item by its ID.
     * 
     * @param id The unique identifier of the todo to delete
     */
    suspend fun deleteTodo(id: String)

    /**
     * Saves a complete list of todos, replacing all existing todos.
     * 
     * @param todos The list of todos to save
     */
    suspend fun saveTodos(todos: List<Todo>)
}

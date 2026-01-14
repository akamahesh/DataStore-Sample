package com.maheshbhatt.datastore_sample.data.repository

import android.content.Context
import com.maheshbhatt.datastore_sample.data.datasource.KeyValueTodoDataSource
import com.maheshbhatt.datastore_sample.data.datasource.ProtobufTodoDataSource
import com.maheshbhatt.datastore_sample.data.datasource.TodoDataSource
import com.maheshbhatt.datastore_sample.data.model.Todo
import kotlinx.coroutines.flow.Flow

/**
 * Enum representing the type of DataStore to use.
 */
enum class DataStoreType {
    KEY_VALUE,
    PROTOBUF
}

/**
 * Repository for managing Todo data.
 * 
 * Delegates all operations to the active DataSource implementation.
 * Provides a factory method to create repository with selected DataStore type.
 */
class TodoRepository(
    private val dataSource: TodoDataSource
) {
    /**
     * Gets all todos as a Flow that emits updates whenever the data changes.
     * 
     * @return Flow emitting the current list of todos
     */
    fun getTodos(): Flow<List<Todo>> = dataSource.getTodos()

    /**
     * Adds a new todo item.
     * 
     * @param todo The todo item to add
     */
    suspend fun addTodo(todo: Todo) = dataSource.addTodo(todo)

    /**
     * Updates an existing todo item.
     * 
     * @param todo The todo item with updated values
     */
    suspend fun updateTodo(todo: Todo) = dataSource.updateTodo(todo)

    /**
     * Deletes a todo item by its ID.
     * 
     * @param id The unique identifier of the todo to delete
     */
    suspend fun deleteTodo(id: String) = dataSource.deleteTodo(id)

    /**
     * Saves a complete list of todos, replacing all existing todos.
     * 
     * @param todos The list of todos to save
     */
    suspend fun saveTodos(todos: List<Todo>) = dataSource.saveTodos(todos)

    companion object {
        /**
         * Factory method to create a TodoRepository with the specified DataStore type.
         * 
         * @param context The application context
         * @param type The type of DataStore to use (KEY_VALUE or PROTOBUF)
         * @return A new TodoRepository instance with the selected DataSource
         */
        fun create(context: Context, type: DataStoreType): TodoRepository {
            val dataSource: TodoDataSource = when (type) {
                DataStoreType.KEY_VALUE -> KeyValueTodoDataSource(context)
                DataStoreType.PROTOBUF -> ProtobufTodoDataSource(context)
            }
            return TodoRepository(dataSource)
        }
    }
}

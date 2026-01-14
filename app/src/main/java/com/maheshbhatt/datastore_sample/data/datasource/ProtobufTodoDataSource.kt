package com.maheshbhatt.datastore_sample.data.datasource

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.Serializer
import androidx.datastore.dataStore
import com.maheshbhatt.datastore_sample.TodoList
import com.maheshbhatt.datastore_sample.data.model.Todo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.io.InputStream
import java.io.OutputStream

/**
 * Protobuf DataStore implementation for Todo storage.
 * 
 * Stores todos using Protobuf DataStore with type safety.
 * Uses TodoList protobuf message to store repeated Todo items.
 */
class ProtobufTodoDataSource(
    private val context: Context
) : TodoDataSource {

    companion object {
        private val Context.todoDataStore: DataStore<TodoList> by dataStore(
            fileName = "todos.pb",
            serializer = TodoListSerializer
        )

        /**
         * Serializer for TodoList protobuf message.
         */
        private object TodoListSerializer : Serializer<TodoList> {
            override val defaultValue: TodoList = TodoList.getDefaultInstance()

            override suspend fun readFrom(input: InputStream): TodoList {
                return TodoList.parseFrom(input)
            }

            override suspend fun writeTo(t: TodoList, output: OutputStream) {
                t.writeTo(output)
            }
        }
    }

    private val dataStore = context.todoDataStore

    override fun getTodos(): Flow<List<Todo>> {
        return dataStore.data.map { todoList ->
            todoList.todosList.map { protoTodo ->
                Todo(
                    id = protoTodo.id,
                    title = protoTodo.title,
                    completed = protoTodo.completed
                )
            }
        }
    }

    override suspend fun saveTodos(todos: List<Todo>) {
        dataStore.updateData { currentList ->
            val todoListBuilder = TodoList.newBuilder()
            todos.forEach { todo ->
                todoListBuilder.addTodos(
                    com.maheshbhatt.datastore_sample.Todo.newBuilder()
                        .setId(todo.id)
                        .setTitle(todo.title)
                        .setCompleted(todo.completed)
                        .build()
                )
            }
            todoListBuilder.build()
        }
    }

    override suspend fun addTodo(todo: Todo) {
        dataStore.updateData { currentList ->
            val todoListBuilder = currentList.toBuilder()
            todoListBuilder.addTodos(
                com.maheshbhatt.datastore_sample.Todo.newBuilder()
                    .setId(todo.id)
                    .setTitle(todo.title)
                    .setCompleted(todo.completed)
                    .build()
            )
            todoListBuilder.build()
        }
    }

    override suspend fun updateTodo(todo: Todo) {
        dataStore.updateData { currentList ->
            val todoListBuilder = currentList.toBuilder()
            val todosList = todoListBuilder.todosList.toMutableList()
            val index = todosList.indexOfFirst { it.id == todo.id }
            if (index != -1) {
                todosList[index] = com.maheshbhatt.datastore_sample.Todo.newBuilder()
                    .setId(todo.id)
                    .setTitle(todo.title)
                    .setCompleted(todo.completed)
                    .build()
                todoListBuilder.clearTodos()
                todoListBuilder.addAllTodos(todosList)
            }
            todoListBuilder.build()
        }
    }

    override suspend fun deleteTodo(id: String) {
        dataStore.updateData { currentList ->
            val todoListBuilder = currentList.toBuilder()
            val todosList = todoListBuilder.todosList.toMutableList()
            todosList.removeAll { it.id == id }
            todoListBuilder.clearTodos()
            todoListBuilder.addAllTodos(todosList)
            todoListBuilder.build()
        }
    }
}

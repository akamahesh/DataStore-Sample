package com.maheshbhatt.datastore_sample.data.model

import org.json.JSONArray
import org.json.JSONObject

/**
 * Data class representing a Todo item.
 * 
 * @param id Unique identifier for the todo
 * @param title The title/description of the todo
 * @param completed Whether the todo is completed or not
 */
data class Todo(
    val id: String,
    val title: String,
    val completed: Boolean
) {
    companion object {
        /**
         * Serializes a list of todos to a JSON string for KeyValue DataStore.
         */
        fun toJsonString(todos: List<Todo>): String {
            val jsonArray = JSONArray()
            todos.forEach { todo ->
                val jsonObject = JSONObject().apply {
                    put("id", todo.id)
                    put("title", todo.title)
                    put("completed", todo.completed)
                }
                jsonArray.put(jsonObject)
            }
            return jsonArray.toString()
        }

        /**
         * Deserializes a JSON string to a list of todos from KeyValue DataStore.
         */
        fun fromJsonString(jsonString: String): List<Todo> {
            if (jsonString.isBlank()) {
                return emptyList()
            }
            
            return try {
                val jsonArray = JSONArray(jsonString)
                val todos = mutableListOf<Todo>()
                
                for (i in 0 until jsonArray.length()) {
                    val jsonObject = jsonArray.getJSONObject(i)
                    todos.add(
                        Todo(
                            id = jsonObject.getString("id"),
                            title = jsonObject.getString("title"),
                            completed = jsonObject.getBoolean("completed")
                        )
                    )
                }
                
                todos
            } catch (e: Exception) {
                emptyList()
            }
        }
    }
}

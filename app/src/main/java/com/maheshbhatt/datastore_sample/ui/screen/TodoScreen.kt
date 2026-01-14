package com.maheshbhatt.datastore_sample.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.maheshbhatt.datastore_sample.data.model.Todo
import com.maheshbhatt.datastore_sample.data.repository.DataStoreType
import com.maheshbhatt.datastore_sample.ui.mvi.TodoUiIntent
import com.maheshbhatt.datastore_sample.ui.viewmodel.TodoViewModel

/**
 * Main Todo screen Composable implementing MVI pattern.
 * 
 * Observes UI state from ViewModel and sends intents on user actions.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoScreen(
    viewModel: TodoViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Title
        Text(
            text = "Todo App",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // DataStore Type Toggle
        DataStoreTypeSelector(
            selectedType = uiState.dataStoreType,
            onTypeSelected = { type ->
                viewModel.processIntent(TodoUiIntent.SwitchDataStore(type))
            }
        )

        // Error Message
        uiState.error?.let { error ->
            ErrorMessage(
                message = error,
                onDismiss = {
                    viewModel.processIntent(TodoUiIntent.LoadTodos)
                }
            )
        }

        // Add Todo Section
        AddTodoSection(
            onAddTodo = { title ->
                viewModel.processIntent(TodoUiIntent.AddTodo(title))
            }
        )

        // Loading Indicator
        if (uiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(modifier = Modifier.size(48.dp))
            }
        }

        // Todo List
        TodoList(
            todos = uiState.todos,
            onToggleTodo = { id ->
                viewModel.processIntent(TodoUiIntent.ToggleTodo(id))
            },
            onDeleteTodo = { id ->
                viewModel.processIntent(TodoUiIntent.DeleteTodo(id))
            },
            modifier = Modifier.weight(1f)
        )

        // Empty State
        if (uiState.todos.isEmpty() && !uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No todos yet. Add one above!",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

/**
 * DataStore type selector using SegmentedButton.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DataStoreTypeSelector(
    selectedType: DataStoreType,
    onTypeSelected: (DataStoreType) -> Unit,
    modifier: Modifier = Modifier
) {
    SingleChoiceSegmentedButtonRow(
        modifier = modifier.fillMaxWidth()
    ) {
        SegmentedButton(
            selected = selectedType == DataStoreType.KEY_VALUE,
            onClick = { onTypeSelected(DataStoreType.KEY_VALUE) },
            shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2)
        ) {
            Text("KeyValue")
        }
        SegmentedButton(
            selected = selectedType == DataStoreType.PROTOBUF,
            onClick = { onTypeSelected(DataStoreType.PROTOBUF) },
            shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2)
        ) {
            Text("Protobuf")
        }
    }
}

/**
 * Error message display.
 */
@Composable
private fun ErrorMessage(
    message: String,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.errorContainer,
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onErrorContainer,
                modifier = Modifier.weight(1f)
            )
            TextButton(onClick = onDismiss) {
                Text("Dismiss")
            }
        }
    }
}

/**
 * Add todo input section.
 */
@Composable
private fun AddTodoSection(
    onAddTodo: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var todoTitle by remember { mutableStateOf("") }

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = todoTitle,
            onValueChange = { todoTitle = it },
            label = { Text("Add a todo") },
            modifier = Modifier.weight(1f),
            singleLine = true
        )
        OutlinedButton(
            onClick = {
                if (todoTitle.isNotBlank()) {
                    onAddTodo(todoTitle)
                    todoTitle = ""
                }
            },
            enabled = todoTitle.isNotBlank()
        ) {
            Text("Add")
        }
    }
}

/**
 * Todo list display.
 */
@Composable
private fun TodoList(
    todos: List<Todo>,
    onToggleTodo: (String) -> Unit,
    onDeleteTodo: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(
            items = todos,
            key = { it.id }
        ) { todo ->
            TodoItem(
                todo = todo,
                onToggle = { onToggleTodo(todo.id) },
                onDelete = { onDeleteTodo(todo.id) }
            )
        }
    }
}

/**
 * Individual todo item card.
 */
@Composable
private fun TodoItem(
    todo: Todo,
    onToggle: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = todo.completed,
                onCheckedChange = { onToggle() }
            )
            Text(
                text = todo.title,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f),
                textDecoration = if (todo.completed) {
                    TextDecoration.LineThrough
                } else {
                    null
                },
                color = if (todo.completed) {
                    MaterialTheme.colorScheme.onSurfaceVariant
                } else {
                    MaterialTheme.colorScheme.onSurface
                }
            )
            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete todo",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

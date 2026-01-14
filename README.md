# Android DataStore Todo App

A sample Android application demonstrating both **KeyValue** and **Protobuf** DataStore implementations using the **MVI (Model-View-Intent)** architecture pattern. Users can seamlessly switch between the two DataStore types to see both implementations in action.

## Features

- ✅ **Dual DataStore Support**: Toggle between KeyValue and Protobuf DataStore implementations
- 📝 **CRUD Operations**: Create, read, update, and delete todos
- 🔄 **Reactive Updates**: Real-time UI updates using Kotlin Flow
- 🎨 **Modern UI**: Built with Jetpack Compose and Material 3
- 🏗️ **MVI Architecture**: Clean separation of concerns with unidirectional data flow
- 💾 **Persistent Storage**: Data persists across app restarts using DataStore

## Architecture

The app follows the **MVI (Model-View-Intent)** pattern with the following data flow:

```
Composable (View) 
  ↓ (sends Intent)
ViewModel (processes Intent, updates State)
  ↓
Repository 
  ↓
LocalDataSource (KeyValue/Protobuf)
  ↓
DataStore
```

### MVI Components

- **Model (State)**: `TodoUiState` - Immutable data class representing UI state
- **View**: `TodoScreen` - Jetpack Compose UI that observes state and sends intents
- **Intent**: `TodoUiIntent` - Sealed class representing all user actions

## Technologies Used

- **Kotlin**: Modern Android development language
- **Jetpack Compose**: Declarative UI framework
- **DataStore**: Modern data storage solution (Preferences & Protobuf)
- **ViewModel**: Lifecycle-aware component for managing UI-related data
- **Kotlin Coroutines & Flow**: Asynchronous programming and reactive streams
- **Material 3**: Modern Material Design components

## Project Structure

```
app/src/main/
├── java/com/maheshbhatt/datastore_sample/
│   ├── data/
│   │   ├── model/
│   │   │   └── Todo.kt                    # Todo data model
│   │   ├── datasource/
│   │   │   ├── TodoDataSource.kt          # DataSource interface
│   │   │   ├── KeyValueTodoDataSource.kt  # KeyValue DataStore implementation
│   │   │   └── ProtobufTodoDataSource.kt  # Protobuf DataStore implementation
│   │   └── repository/
│   │       └── TodoRepository.kt          # Repository with factory method
│   ├── ui/
│   │   ├── mvi/
│   │   │   ├── TodoUiState.kt             # UI state model
│   │   │   └── TodoUiIntent.kt            # User intent sealed class
│   │   ├── viewmodel/
│   │   │   └── TodoViewModel.kt          # ViewModel with MVI pattern
│   │   └── screen/
│   │       └── TodoScreen.kt              # Main UI Composable
│   └── MainActivity.kt                    # Main activity
└── proto/
    └── todo.proto                         # Protobuf schema definition
```

## Setup

### Prerequisites

- Android Studio Hedgehog (2023.1.1) or later
- JDK 11 or later
- Android SDK with API level 24+ (Android 7.0+)

### Building the Project

1. Clone the repository:
   ```bash
   git clone <repository-url>
   cd DataStore-Sample
   ```

2. Open the project in Android Studio

3. Sync Gradle files (File → Sync Project with Gradle Files)

4. Build and run the app:
   - Click the "Run" button, or
   - Use the command line: `./gradlew installDebug`

## Usage

### Adding a Todo

1. Enter a todo title in the text field at the top
2. Click the "Add" button
3. The todo will appear in the list below

### Toggling Todo Completion

- Click the checkbox next to any todo to mark it as completed or incomplete
- Completed todos are displayed with a strikethrough

### Deleting a Todo

- Click the delete icon (trash can) next to any todo to remove it

### Switching DataStore Types

- Use the segmented button at the top to switch between:
  - **KeyValue**: Stores todos as JSON string in Preferences DataStore
  - **Protobuf**: Stores todos using type-safe Protobuf DataStore

**Note**: When switching DataStore types, the app will load todos from the newly selected DataStore. Each DataStore type maintains its own separate data storage.

## DataStore Implementation Details

### KeyValue DataStore

- Uses `PreferencesDataStore` to store todos as a JSON string
- Key: `"todos"`, Value: JSON array of todos
- Simple serialization using Android's JSON classes
- Suitable for simple key-value storage needs

### Protobuf DataStore

- Uses `DataStore<TodoList>` with custom protobuf serializer
- Type-safe storage using generated protobuf classes
- Better performance and type safety
- Suitable for complex data structures

## Key Concepts

### MVI Pattern

The app implements the MVI (Model-View-Intent) pattern:

1. **Intent**: User actions are represented as intents (e.g., `AddTodo`, `ToggleTodo`)
2. **ViewModel**: Processes intents and updates state
3. **State**: Immutable state object that the UI observes
4. **View**: Composable UI that renders based on state

### Reactive Data Flow

- Repository exposes `Flow<List<Todo>>` for reactive updates
- ViewModel observes the Flow and updates UI state
- UI automatically recomposes when state changes

### Error Handling

- Errors are captured and displayed in the UI
- Loading states are shown during data operations
- User-friendly error messages with dismiss functionality

## Dependencies

Key dependencies used in this project:

- `androidx.datastore:datastore-preferences` - KeyValue DataStore
- `androidx.datastore:datastore-core` - Core DataStore functionality
- `com.google.protobuf:protobuf-kotlin-lite` - Protobuf support
- `androidx.lifecycle:lifecycle-viewmodel-compose` - ViewModel for Compose
- `androidx.lifecycle:lifecycle-runtime-compose` - Lifecycle-aware Compose
- `org.jetbrains.kotlinx:kotlinx-coroutines-android` - Coroutines support

## License

This project is a sample application for educational purposes.

## Contributing

This is a sample project. Feel free to use it as a reference or starting point for your own projects.

## Author

Created as a demonstration of Android DataStore and MVI architecture patterns.

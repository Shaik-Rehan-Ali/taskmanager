package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.model.Note
import com.example.data.model.Task
import com.example.data.model.TaskCategory
import com.example.data.model.TaskPriority
import com.example.data.repository.TaskRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate

enum class AppTab(val label: String) {
    DAILY_TASKS("daily tasks"),
    DAILY_NOTES("daily notes"),
    SETTINGS("settings"),
    ANALYSIS("analysis")
}

enum class TaskStatusFilter(val label: String) {
    ALL("All"),
    ACTIVE("Active"),
    COMPLETED("Completed")
}

enum class AppThemeMode {
    DARK,
    LIGHT,
    SYSTEM
}

data class TaskStats(
    val total: Int = 0,
    val completed: Int = 0,
    val pending: Int = 0,
    val completionPercentage: Float = 0f
)

data class TaskUiState(
    val activeTab: AppTab = AppTab.DAILY_TASKS,
    val tasks: List<Task> = emptyList(),
    val filteredTasks: List<Task> = emptyList(),
    val notes: List<Note> = emptyList(),
    val selectedDate: String? = LocalDate.now().toString(), // YYYY-MM-DD or null for All
    val selectedCategory: TaskCategory = TaskCategory.ALL,
    val statusFilter: TaskStatusFilter = TaskStatusFilter.ALL,
    val searchQuery: String = "",
    val stats: TaskStats = TaskStats(),
    val isAddEditDialogOpen: Boolean = false,
    val taskToEdit: Task? = null,
    val taskToDelete: Task? = null,
    val isClearCompletedDialogOpen: Boolean = false,
    val isSettingsDialogOpen: Boolean = false,
    val isExportDialogOpen: Boolean = false,
    val isImportDialogOpen: Boolean = false,
    val themeMode: AppThemeMode = AppThemeMode.DARK
)

class TaskViewModel(private val repository: TaskRepository) : ViewModel() {

    private val _activeTab = MutableStateFlow(AppTab.DAILY_TASKS)
    private val _selectedDate = MutableStateFlow<String?>(LocalDate.now().toString())
    private val _selectedCategory = MutableStateFlow(TaskCategory.ALL)
    private val _statusFilter = MutableStateFlow(TaskStatusFilter.ALL)
    private val _searchQuery = MutableStateFlow("")

    private val _isAddEditDialogOpen = MutableStateFlow(false)
    private val _taskToEdit = MutableStateFlow<Task?>(null)
    private val _taskToDelete = MutableStateFlow<Task?>(null)
    private val _isClearCompletedDialogOpen = MutableStateFlow(false)
    private val _isSettingsDialogOpen = MutableStateFlow(false)
    private val _isExportDialogOpen = MutableStateFlow(false)
    private val _isImportDialogOpen = MutableStateFlow(false)
    private val _themeMode = MutableStateFlow(AppThemeMode.DARK)

    // Combine flows to produce single cohesive UI State
    val uiState: StateFlow<TaskUiState> = combine(
        repository.getAllTasks(),
        repository.getAllNotes(),
        _activeTab,
        _selectedDate,
        _selectedCategory,
        _statusFilter,
        _searchQuery,
        combine(
            _isAddEditDialogOpen,
            _taskToEdit,
            _taskToDelete,
            _isClearCompletedDialogOpen,
            _isSettingsDialogOpen,
            _isExportDialogOpen,
            _isImportDialogOpen,
            _themeMode
        ) { dialogsAndTheme ->
            dialogsAndTheme
        }
    ) { params ->
        @Suppress("UNCHECKED_CAST")
        val tasksList = params[0] as List<Task>
        @Suppress("UNCHECKED_CAST")
        val notesList = params[1] as List<Note>
        val tab = params[2] as AppTab
        val date = params[3] as String?
        val category = params[4] as TaskCategory
        val status = params[5] as TaskStatusFilter
        val query = params[6] as String

        val dialogs = params[7] as Array<*>
        val isAddEditDialogOpen = dialogs[0] as Boolean
        val taskToEdit = dialogs[1] as Task?
        val taskToDelete = dialogs[2] as Task?
        val isClearCompletedDialogOpen = dialogs[3] as Boolean
        val isSettingsDialogOpen = dialogs[4] as Boolean
        val isExportDialogOpen = dialogs[5] as Boolean
        val isImportDialogOpen = dialogs[6] as Boolean
        val themeMode = dialogs[7] as AppThemeMode

        val filtered = tasksList.filter { task ->
            val matchesDate = date == null || task.date == date
            val matchesCategory = category == TaskCategory.ALL || task.category == category
            val matchesStatus = when (status) {
                TaskStatusFilter.ALL -> true
                TaskStatusFilter.ACTIVE -> !task.isCompleted
                TaskStatusFilter.COMPLETED -> task.isCompleted
            }
            val matchesSearch = query.isBlank() ||
                    task.title.contains(query, ignoreCase = true) ||
                    task.description.contains(query, ignoreCase = true)

            matchesDate && matchesCategory && matchesStatus && matchesSearch
        }

        // Daily Progress calculation: Count tasks for today
        val todayStr = LocalDate.now().toString()
        val todayTasks = tasksList.filter { it.date == todayStr }
        val total = todayTasks.size
        val completed = todayTasks.count { it.isCompleted }
        val pending = total - completed
        val percentage = if (total > 0) completed.toFloat() / total.toFloat() else 0f
        val stats = TaskStats(
            total = total,
            completed = completed,
            pending = pending,
            completionPercentage = percentage
        )

        TaskUiState(
            activeTab = tab,
            tasks = tasksList,
            filteredTasks = filtered,
            notes = notesList,
            selectedDate = date,
            selectedCategory = category,
            statusFilter = status,
            searchQuery = query,
            stats = stats,
            isAddEditDialogOpen = isAddEditDialogOpen,
            taskToEdit = taskToEdit,
            taskToDelete = taskToDelete,
            isClearCompletedDialogOpen = isClearCompletedDialogOpen,
            isSettingsDialogOpen = isSettingsDialogOpen,
            isExportDialogOpen = isExportDialogOpen,
            isImportDialogOpen = isImportDialogOpen,
            themeMode = themeMode
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = TaskUiState()
    )

    fun onTabSelected(tab: AppTab) {
        _activeTab.value = tab
    }

    fun onDateSelected(date: String?) {
        _selectedDate.value = date
    }

    fun onCategorySelected(category: TaskCategory) {
        _selectedCategory.value = category
    }

    fun onStatusFilterSelected(status: TaskStatusFilter) {
        _statusFilter.value = status
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun openCreateTaskDialog() {
        _taskToEdit.value = null
        _isAddEditDialogOpen.value = true
    }

    fun openEditTaskDialog(task: Task) {
        _taskToEdit.value = task
        _isAddEditDialogOpen.value = true
    }

    fun dismissAddEditDialog() {
        _isAddEditDialogOpen.value = false
        _taskToEdit.value = null
    }

    fun openSettingsDialog() {
        _isSettingsDialogOpen.value = true
    }

    fun dismissSettingsDialog() {
        _isSettingsDialogOpen.value = false
    }

    fun openExportDialog() {
        _isExportDialogOpen.value = true
    }

    fun dismissExportDialog() {
        _isExportDialogOpen.value = false
    }

    fun openImportDialog() {
        _isImportDialogOpen.value = true
    }

    fun dismissImportDialog() {
        _isImportDialogOpen.value = false
    }

    fun setThemeMode(mode: AppThemeMode) {
        _themeMode.value = mode
    }

    fun saveTask(
        id: Long = 0,
        title: String,
        description: String,
        date: String,
        time: String,
        priority: TaskPriority,
        category: TaskCategory,
        repeatEveryDayOfMonth: Boolean = false
    ) {
        if (title.isBlank()) return

        viewModelScope.launch {
            val currentEditing = _taskToEdit.value
            if (currentEditing != null) {
                val updatedTask = currentEditing.copy(
                    title = title.trim(),
                    description = description.trim(),
                    date = date,
                    time = time.trim(),
                    priority = priority,
                    category = category,
                    updatedAt = System.currentTimeMillis()
                )
                repository.updateTask(updatedTask)
            } else {
                if (repeatEveryDayOfMonth) {
                    val parsedDate = try {
                        LocalDate.parse(date)
                    } catch (e: Exception) {
                        LocalDate.now()
                    }
                    val year = parsedDate.year
                    val month = parsedDate.month
                    val lengthOfMonth = parsedDate.lengthOfMonth()
                    val tasksForMonth = (1..lengthOfMonth).map { day ->
                        val dayDate = LocalDate.of(year, month, day).toString()
                        Task(
                            title = title.trim(),
                            description = description.trim(),
                            date = dayDate,
                            time = time.trim(),
                            priority = priority,
                            category = category
                        )
                    }
                    repository.insertTasks(tasksForMonth)
                } else {
                    val newTask = Task(
                        title = title.trim(),
                        description = description.trim(),
                        date = date,
                        time = time.trim(),
                        priority = priority,
                        category = category
                    )
                    repository.insertTask(newTask)
                }
            }
            dismissAddEditDialog()
        }
    }

    fun toggleTaskCompletion(task: Task) {
        viewModelScope.launch {
            repository.toggleTaskCompletion(task)
        }
    }

    fun promptDeleteTask(task: Task) {
        _taskToDelete.value = task
    }

    fun confirmDeleteTask() {
        val task = _taskToDelete.value ?: return
        viewModelScope.launch {
            repository.deleteTask(task)
            _taskToDelete.value = null
        }
    }

    fun dismissDeletePrompt() {
        _taskToDelete.value = null
    }

    fun promptClearCompleted() {
        _isClearCompletedDialogOpen.value = true
    }

    fun confirmClearCompleted() {
        viewModelScope.launch {
            repository.deleteCompletedTasks()
            _isClearCompletedDialogOpen.value = false
        }
    }

    fun dismissClearCompletedPrompt() {
        _isClearCompletedDialogOpen.value = false
    }

    // Notes management
    fun saveNote(
        id: Long,
        title: String,
        content: String,
        date: String,
        colorTag: Long,
        isPinned: Boolean
    ) {
        if (title.isBlank() && content.isBlank()) return

        viewModelScope.launch {
            if (id > 0) {
                val existing = repository.getNoteById(id)
                if (existing != null) {
                    repository.updateNote(
                        existing.copy(
                            title = title.trim(),
                            content = content.trim(),
                            date = date,
                            colorTag = colorTag,
                            isPinned = isPinned,
                            updatedAt = System.currentTimeMillis()
                        )
                    )
                }
            } else {
                val newNote = Note(
                    title = title.trim(),
                    content = content.trim(),
                    date = date,
                    colorTag = colorTag,
                    isPinned = isPinned
                )
                repository.insertNote(newNote)
            }
        }
    }

    fun deleteNote(note: Note) {
        viewModelScope.launch {
            repository.deleteNote(note)
        }
    }

    // Data Backup / Migration (Import & Export & Reset)
    fun importData(tasks: List<Task>, notes: List<Note>, replaceExisting: Boolean) {
        viewModelScope.launch {
            if (replaceExisting) {
                repository.deleteAllTasks()
                repository.deleteAllNotes()
            }
            if (tasks.isNotEmpty()) {
                repository.insertTasks(tasks)
            }
            if (notes.isNotEmpty()) {
                repository.insertNotes(notes)
            }
            dismissImportDialog()
        }
    }

    fun resetAllData() {
        viewModelScope.launch {
            repository.deleteAllTasks()
            repository.deleteAllNotes()
        }
    }

    class Factory(private val repository: TaskRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(TaskViewModel::class.java)) {
                return TaskViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}

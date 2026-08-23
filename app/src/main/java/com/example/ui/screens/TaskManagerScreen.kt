package com.example.ui.screens

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material.icons.outlined.Analytics
import androidx.compose.material.icons.outlined.EditNote
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.TaskAlt
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.ExportDataDialog
import com.example.ui.components.ImportDataDialog
import com.example.ui.components.TaskEditDialog
import com.example.ui.viewmodel.AppTab
import com.example.ui.viewmodel.TaskViewModel

@Composable
fun TaskManagerScreen(
    viewModel: TaskViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.testTag("main_navigation_bar")
            ) {
                // Tab 1: daily tasks
                val isTasksSelected = uiState.activeTab == AppTab.DAILY_TASKS
                NavigationBarItem(
                    selected = isTasksSelected,
                    onClick = { viewModel.onTabSelected(AppTab.DAILY_TASKS) },
                    icon = {
                        Icon(
                            imageVector = if (isTasksSelected) Icons.Filled.TaskAlt else Icons.Outlined.TaskAlt,
                            contentDescription = "Daily Tasks"
                        )
                    },
                    label = {
                        Text(
                            text = "daily tasks",
                            fontWeight = if (isTasksSelected) FontWeight.Bold else FontWeight.Medium
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        indicatorColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    modifier = Modifier.testTag("tab_daily_tasks")
                )

                // Tab 2: daily notes
                val isNotesSelected = uiState.activeTab == AppTab.DAILY_NOTES
                NavigationBarItem(
                    selected = isNotesSelected,
                    onClick = { viewModel.onTabSelected(AppTab.DAILY_NOTES) },
                    icon = {
                        Icon(
                            imageVector = if (isNotesSelected) Icons.Filled.EditNote else Icons.Outlined.EditNote,
                            contentDescription = "Daily Notes"
                        )
                    },
                    label = {
                        Text(
                            text = "daily notes",
                            fontWeight = if (isNotesSelected) FontWeight.Bold else FontWeight.Medium
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        indicatorColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    modifier = Modifier.testTag("tab_daily_notes")
                )

                // Tab 3: settings
                val isSettingsSelected = uiState.activeTab == AppTab.SETTINGS
                NavigationBarItem(
                    selected = isSettingsSelected,
                    onClick = { viewModel.onTabSelected(AppTab.SETTINGS) },
                    icon = {
                        Icon(
                            imageVector = if (isSettingsSelected) Icons.Filled.Settings else Icons.Outlined.Settings,
                            contentDescription = "Settings"
                        )
                    },
                    label = {
                        Text(
                            text = "settings",
                            fontWeight = if (isSettingsSelected) FontWeight.Bold else FontWeight.Medium
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        indicatorColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    modifier = Modifier.testTag("tab_settings")
                )

                // Tab 4: analysis
                val isAnalysisSelected = uiState.activeTab == AppTab.ANALYSIS
                NavigationBarItem(
                    selected = isAnalysisSelected,
                    onClick = { viewModel.onTabSelected(AppTab.ANALYSIS) },
                    icon = {
                        Icon(
                            imageVector = if (isAnalysisSelected) Icons.Filled.Analytics else Icons.Outlined.Analytics,
                            contentDescription = "Analysis"
                        )
                    },
                    label = {
                        Text(
                            text = "analysis",
                            fontWeight = if (isAnalysisSelected) FontWeight.Bold else FontWeight.Medium
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        indicatorColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    modifier = Modifier.testTag("tab_analysis")
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Crossfade(
                targetState = uiState.activeTab,
                animationSpec = tween(durationMillis = 200),
                label = "tab_crossfade"
            ) { tab ->
                when (tab) {
                    AppTab.DAILY_TASKS -> {
                        DailyTasksScreen(
                            tasks = uiState.tasks,
                            filteredTasks = uiState.filteredTasks,
                            selectedDate = uiState.selectedDate,
                            selectedCategory = uiState.selectedCategory,
                            statusFilter = uiState.statusFilter,
                            searchQuery = uiState.searchQuery,
                            stats = uiState.stats,
                            onDateSelected = { viewModel.onDateSelected(it) },
                            onCategorySelected = { viewModel.onCategorySelected(it) },
                            onStatusFilterSelected = { viewModel.onStatusFilterSelected(it) },
                            onSearchQueryChange = { viewModel.onSearchQueryChange(it) },
                            onToggleTaskComplete = { viewModel.toggleTaskCompletion(it) },
                            onEditTask = { viewModel.openEditTaskDialog(it) },
                            onDeleteTask = { viewModel.promptDeleteTask(it) },
                            onAddNewTask = { viewModel.openCreateTaskDialog() }
                        )
                    }

                    AppTab.DAILY_NOTES -> {
                        DailyNotesScreen(
                            notes = uiState.notes,
                            onSaveNote = { id, title, content, date, colorTag, isPinned ->
                                viewModel.saveNote(id, title, content, date, colorTag, isPinned)
                            },
                            onDeleteNote = { note ->
                                viewModel.deleteNote(note)
                            }
                        )
                    }

                    AppTab.SETTINGS -> {
                        SettingsScreen(
                            currentThemeMode = uiState.themeMode,
                            onThemeModeChanged = { viewModel.setThemeMode(it) },
                            onClearCompletedTasks = { viewModel.confirmClearCompleted() },
                            onResetAllData = { viewModel.resetAllData() },
                            onExportClick = { viewModel.openExportDialog() },
                            onImportClick = { viewModel.openImportDialog() }
                        )
                    }

                    AppTab.ANALYSIS -> {
                        AnalysisScreen(
                            tasks = uiState.tasks,
                            notes = uiState.notes,
                            onExportClick = { viewModel.openExportDialog() },
                            onImportClick = { viewModel.openImportDialog() }
                        )
                    }
                }
            }
        }
    }

    // Task Add / Edit Dialog
    if (uiState.isAddEditDialogOpen) {
        TaskEditDialog(
            task = uiState.taskToEdit,
            onDismiss = { viewModel.dismissAddEditDialog() },
            onSave = { title, description, date, time, priority, category, repeatEveryDayOfMonth ->
                viewModel.saveTask(
                    title = title,
                    description = description,
                    date = date,
                    time = time,
                    priority = priority,
                    category = category,
                    repeatEveryDayOfMonth = repeatEveryDayOfMonth
                )
            }
        )
    }

    // Delete Single Task Confirmation Dialog
    uiState.taskToDelete?.let { task ->
        AlertDialog(
            onDismissRequest = { viewModel.dismissDeletePrompt() },
            title = { Text("Delete Task") },
            text = { Text("Are you sure you want to delete \"${task.title}\"?") },
            confirmButton = {
                TextButton(
                    onClick = { viewModel.confirmDeleteTask() },
                    modifier = Modifier.testTag("confirm_delete_button")
                ) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.dismissDeletePrompt() }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Export Data Dialog
    if (uiState.isExportDialogOpen) {
        ExportDataDialog(
            tasks = uiState.tasks,
            notes = uiState.notes,
            onDismiss = { viewModel.dismissExportDialog() }
        )
    }

    // Import Data Dialog
    if (uiState.isImportDialogOpen) {
        ImportDataDialog(
            onDismiss = { viewModel.dismissImportDialog() },
            onImportConfirmed = { tasks, notes, replaceExisting ->
                viewModel.importData(tasks, notes, replaceExisting)
            }
        )
    }
}

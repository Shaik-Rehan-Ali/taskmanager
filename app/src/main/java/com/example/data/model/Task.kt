package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

enum class TaskPriority(val label: String, val colorHex: Long) {
    LOW("Low", 0xFF10B981),
    MEDIUM("Medium", 0xFFF59E0B),
    HIGH("High", 0xFFEF4444)
}

enum class TaskCategory(val label: String, val iconName: String) {
    ALL("All", "Category"),
    WORK("Work", "BusinessCenter"),
    PERSONAL("Personal", "Person"),
    STUDY("Study", "School"),
    HEALTH("Health", "FitnessCenter"),
    SHOPPING("Shopping", "ShoppingCart"),
    OTHER("Other", "Assignment")
}

@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String = "",
    val date: String = LocalDate.now().toString(), // YYYY-MM-DD
    val time: String = "", // e.g. "10:00 AM" or ""
    val priority: TaskPriority = TaskPriority.MEDIUM,
    val category: TaskCategory = TaskCategory.PERSONAL,
    val isCompleted: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

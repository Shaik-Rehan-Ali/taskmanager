package com.example.data.repository

import com.example.data.local.NoteDao
import com.example.data.local.TaskDao
import com.example.data.model.Note
import com.example.data.model.Task
import kotlinx.coroutines.flow.Flow

class TaskRepository(
    private val taskDao: TaskDao,
    private val noteDao: NoteDao
) {

    // Tasks
    fun getAllTasks(): Flow<List<Task>> = taskDao.getAllTasks()

    fun getTasksByDate(date: String): Flow<List<Task>> = taskDao.getTasksByDate(date)

    suspend fun getTaskById(id: Long): Task? = taskDao.getTaskById(id)

    suspend fun insertTask(task: Task): Long = taskDao.insertTask(task)

    suspend fun insertTasks(tasks: List<Task>): List<Long> = taskDao.insertTasks(tasks)

    suspend fun updateTask(task: Task) = taskDao.updateTask(task.copy(updatedAt = System.currentTimeMillis()))

    suspend fun toggleTaskCompletion(task: Task) {
        val newStatus = !task.isCompleted
        taskDao.updateTaskCompletion(task.id, newStatus, System.currentTimeMillis())
    }

    suspend fun deleteTask(task: Task) = taskDao.deleteTask(task)

    suspend fun deleteTaskById(id: Long) = taskDao.deleteTaskById(id)

    suspend fun deleteCompletedTasks() = taskDao.deleteCompletedTasks()

    suspend fun deleteAllTasks() = taskDao.deleteAllTasks()

    suspend fun getAllTasksDirect(): List<Task> = taskDao.getAllTasksDirect()

    // Notes
    fun getAllNotes(): Flow<List<Note>> = noteDao.getAllNotes()

    fun getNotesByDate(date: String): Flow<List<Note>> = noteDao.getNotesByDate(date)

    suspend fun getNoteById(id: Long): Note? = noteDao.getNoteById(id)

    suspend fun insertNote(note: Note): Long = noteDao.insertNote(note)

    suspend fun insertNotes(notes: List<Note>): List<Long> = noteDao.insertNotes(notes)

    suspend fun updateNote(note: Note) = noteDao.updateNote(note.copy(updatedAt = System.currentTimeMillis()))

    suspend fun deleteNote(note: Note) = noteDao.deleteNote(note)

    suspend fun deleteAllNotes() = noteDao.deleteAllNotes()
}

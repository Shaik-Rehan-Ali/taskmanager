package com.example.data.util

import com.example.data.model.Note
import com.example.data.model.Task
import com.example.data.model.TaskCategory
import com.example.data.model.TaskPriority
import org.json.JSONArray
import org.json.JSONObject
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object DataBackupManager {

    fun exportToJson(tasks: List<Task>, notes: List<Note>): String {
        val root = JSONObject()
        root.put("version", 1)
        root.put("appName", "Task Manager")
        root.put("exportDate", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
        root.put("tasksCount", tasks.size)
        root.put("notesCount", notes.size)

        val tasksArray = JSONArray()
        for (task in tasks) {
            val tObj = JSONObject()
            tObj.put("id", task.id)
            tObj.put("title", task.title)
            tObj.put("description", task.description)
            tObj.put("date", task.date)
            tObj.put("time", task.time)
            tObj.put("priority", task.priority.name)
            tObj.put("category", task.category.name)
            tObj.put("isCompleted", task.isCompleted)
            tObj.put("createdAt", task.createdAt)
            tObj.put("updatedAt", task.updatedAt)
            tasksArray.put(tObj)
        }
        root.put("tasks", tasksArray)

        val notesArray = JSONArray()
        for (note in notes) {
            val nObj = JSONObject()
            nObj.put("id", note.id)
            nObj.put("title", note.title)
            nObj.put("content", note.content)
            nObj.put("date", note.date)
            nObj.put("colorTag", note.colorTag)
            nObj.put("isPinned", note.isPinned)
            nObj.put("createdAt", note.createdAt)
            nObj.put("updatedAt", note.updatedAt)
            notesArray.put(nObj)
        }
        root.put("notes", notesArray)

        return root.toString(2)
    }

    data class ImportResult(
        val tasks: List<Task>,
        val notes: List<Note>,
        val message: String? = null
    )

    fun parseFromJson(jsonString: String): ImportResult {
        val cleanJson = jsonString.trim()
        val root = JSONObject(cleanJson)

        val parsedTasks = mutableListOf<Task>()
        val parsedNotes = mutableListOf<Note>()

        if (root.has("tasks")) {
            val tasksArray = root.getJSONArray("tasks")
            for (i in 0 until tasksArray.length()) {
                val tObj = tasksArray.getJSONObject(i)
                val title = tObj.optString("title", "").trim()
                if (title.isNotEmpty()) {
                    val description = tObj.optString("description", "")
                    val date = tObj.optString("date", "")
                    val time = tObj.optString("time", "")
                    val priorityStr = tObj.optString("priority", "MEDIUM")
                    val categoryStr = tObj.optString("category", "PERSONAL")
                    val isCompleted = tObj.optBoolean("isCompleted", false)
                    val createdAt = tObj.optLong("createdAt", System.currentTimeMillis())
                    val updatedAt = tObj.optLong("updatedAt", System.currentTimeMillis())

                    val priority = try {
                        TaskPriority.valueOf(priorityStr.uppercase())
                    } catch (e: Exception) {
                        TaskPriority.MEDIUM
                    }

                    val category = try {
                        TaskCategory.valueOf(categoryStr.uppercase())
                    } catch (e: Exception) {
                        TaskCategory.PERSONAL
                    }

                    parsedTasks.add(
                        Task(
                            id = 0, // Reset for insertion/re-index
                            title = title,
                            description = description,
                            date = if (date.isNotBlank()) date else java.time.LocalDate.now().toString(),
                            time = time,
                            priority = priority,
                            category = category,
                            isCompleted = isCompleted,
                            createdAt = createdAt,
                            updatedAt = updatedAt
                        )
                    )
                }
            }
        }

        if (root.has("notes")) {
            val notesArray = root.getJSONArray("notes")
            for (i in 0 until notesArray.length()) {
                val nObj = notesArray.getJSONObject(i)
                val title = nObj.optString("title", "").trim()
                val content = nObj.optString("content", "")
                if (title.isNotEmpty() || content.isNotEmpty()) {
                    val date = nObj.optString("date", java.time.LocalDate.now().toString())
                    val colorTag = nObj.optLong("colorTag", 0xFF6750A4)
                    val isPinned = nObj.optBoolean("isPinned", false)
                    val createdAt = nObj.optLong("createdAt", System.currentTimeMillis())
                    val updatedAt = nObj.optLong("updatedAt", System.currentTimeMillis())

                    parsedNotes.add(
                        Note(
                            id = 0,
                            title = title,
                            content = content,
                            date = date,
                            colorTag = colorTag,
                            isPinned = isPinned,
                            createdAt = createdAt,
                            updatedAt = updatedAt
                        )
                    )
                }
            }
        }

        return ImportResult(
            tasks = parsedTasks,
            notes = parsedNotes,
            message = "Parsed ${parsedTasks.size} tasks and ${parsedNotes.size} notes."
        )
    }
}

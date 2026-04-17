package com.example.todoapp.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todoapp.data.Task
import com.example.todoapp.data.TaskRepository
import kotlinx.coroutines.launch

class TasksViewModel(private val repository: TaskRepository) : ViewModel() {

    val allTasks = repository.allTasks

    fun addTask(title: String, category: String) {
        viewModelScope.launch {
            repository.insert(Task(title = title, category = category))
        }
    }

    fun toggleCompletion(task: Task) {
        viewModelScope.launch {
            repository.update(task.copy(isCompleted = !task.isCompleted))
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            repository.delete(task)
        }
    }

    fun clearCompleted() {
        viewModelScope.launch {
            repository.deleteCompleted()
        }
    }
}

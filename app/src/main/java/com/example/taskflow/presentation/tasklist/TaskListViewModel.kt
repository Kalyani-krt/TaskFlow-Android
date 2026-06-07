package com.example.taskflow.presentation.tasklist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskflow.data.local.db.Task
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

class TaskListViewModel(private val taskRepository: TaskRepository) : ViewModel() {
//    val _uiState = MutableStateFlow<UiState>(UiState.Loading)
//    val uiState =_uiState.asStateFlow()

    init {
        viewModelScope.launch {
            taskRepository.syncTasks()
        }
    }
    //converts raw data to UI state
    var tasksUiState = taskRepository.observeTaskRepo().map {
        UiState.Success(it)
    }.stateIn( //converts flow to stateflow
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        UiState.Loading
    )

    private val _selectedTask = MutableStateFlow<Task?>(null)
    val selectedTask = _selectedTask.asStateFlow()

    fun editTask(id: Int) {
        viewModelScope.launch {
            val task = tasksUiState.value

        }
    }

    fun getTaskById(id: String): Flow<Task?> {
        return taskRepository.getTaskByIDRepo(id)
    }

    fun updateTask(task: Task) {
        viewModelScope.launch {
            taskRepository.upsertTaskRepo(task)
        }
    }

    fun deleteTask(id: String){
        viewModelScope.launch {
            taskRepository.deleteRepo(id)
        }
    }

    fun toggleStatus(task: Task){
        viewModelScope.launch {

            val updatedTask = if (task.status == "Pending") {
                task.copy(status = "Completed")
            } else {
                task.copy(status = "Pending")
            }

            taskRepository.upsertTaskRepo(updatedTask)
        }
    }
    fun addTask(title: String) {
        viewModelScope.launch {
            var newTask = Task(
                //   id = tasksUiState.value.tasks.size+1,
                id = UUID.randomUUID().toString(),
                title = title, status = "Pending"

            )
            taskRepository.upsertTaskRepo(newTask)

        }

    }

    sealed class UiState {
        object Loading : UiState()
        data class Success(val tasks: List<Task>) : UiState()
        data class Error(val message: String) : UiState()
    }


}
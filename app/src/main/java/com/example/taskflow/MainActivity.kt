package com.example.taskflow

import android.os.Bundle
import androidx.compose.material3.CircularProgressIndicator
import androidx.navigation.compose.rememberNavController
import androidx.compose.runtime.getValue
import androidx.compose.foundation.lazy.items
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxState
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.taskflow.data.local.db.AppDatabase
import com.example.taskflow.presentation.tasklist.TaskListViewModel
import com.example.taskflow.presentation.tasklist.TaskListViewModel.UiState
import com.example.taskflow.presentation.tasklist.TaskListViewModelFactory
import com.example.taskflow.presentation.tasklist.TaskRepository

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val roomdb = AppDatabase.getDatabase(this)
        val dao = roomdb.getTaskDao()
        val repo = TaskRepository(dao)
        val factory = TaskListViewModelFactory(repo)

        val viewmodel = ViewModelProvider(this, factory)[TaskListViewModel::class.java]
////        dummy data inserted for testing
////        lifecycleScope.launch(Dispatchers.IO){
////            dao.upsertTask(Task("1","Task1","Pending"))
////
////        }
//        enableEdgeToEdge()
        setContent {
            App(viewmodel)
        }
    }
}

@Composable
fun App(viewModel: TaskListViewModel) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = RouteNames.ListScreen.route) {

        composable(RouteNames.ListScreen.route) {
            ShowTaskList(
                viewModel, gotoAddTaskScreen = {
                    navController.navigate(RouteNames.AddTask.route)
                },
                gotoAddEditScreen = { id ->
                    navController.navigate("add_task?taskId=$id")
                })
        }
        composable(RouteNames.AddTask.route) { backStackEntry ->
            val taskId = backStackEntry.arguments?.getString("taskId")
            AddTaskScreen(viewModel, taskId = taskId) {
                navController.popBackStack()
            }
        }
    }
}

sealed class RouteNames(val route: String) {
    object ListScreen : RouteNames("task_list")
    object AddTask : RouteNames("add_task?taskId={taskId}")
}

@Composable
fun ShowTaskList(
    viewModel: TaskListViewModel,
    gotoAddTaskScreen: () -> Unit = {},
    gotoAddEditScreen: (String) -> Unit
) {
    val uiState by viewModel.tasksUiState.collectAsState()

    when (uiState) {
        is UiState.Loading -> {
            Column(
                modifier =
                    Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator()
                Spacer(
                    modifier = Modifier.height(8.dp)
                )
                Text(text = "Loading tasks...", fontSize = 14.sp)
            }
        }

        is UiState.Success -> {
            val list = (uiState as UiState.Success).tasks
            if (list.isEmpty()) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("No tasks yet")
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    LazyColumn(
                        modifier =
                            Modifier.weight(1f)
                    ) {
                        items(
                            items=list,
                            key = {it.id}
                        ) { task ->
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 6.dp),
                                        shape = RoundedCornerShape(16.dp),
                                        colors = CardDefaults.cardColors(
                                            containerColor = Color.White,
                                        ),
                                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(12.dp)
                                                .clickable {
                                                    gotoAddEditScreen(task.id.toString())
                                                },
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = task.title,
                                                modifier = Modifier.weight(1f),
                                                maxLines = 2,
                                                fontWeight = FontWeight.Medium
                                            )

                                            Spacer(modifier = Modifier.width(12.dp))

                                            Box(
                                                modifier = Modifier
//                                                    .background(
//                                                        color = if (task.status == "Completed") Color(
//                                                            0xFFE8F5E9
//                                                        ) else Color(
//                                                            0xFFF1F1F1
//                                                        ),
//                                                        shape = RoundedCornerShape(8.dp)
//                                                    )
                                                    .padding(horizontal = 8.dp, vertical = 2.dp),
                                                contentAlignment = Alignment.CenterStart
                                            )
                                            {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Checkbox(
                                                        checked = task.status == "Completed",
                                                        onCheckedChange = {
                                                            viewModel.toggleStatus(task)
                                                        }
                                                    )
                                                    Text(
                                                        text = if (task.status == "Completed") "Done" else "Pending",
                                                        fontSize = 12.sp
                                                    )
                                                }
                                            }
                                            Spacer(modifier = Modifier.width(8.dp))

                                            IconButton(onClick = {
                                                viewModel.deleteTask(task.id)
                                            }) {
                                                Icon(
                                                    imageVector = Icons.Default.Delete,
                                                    contentDescription = "Delete",
                                                    tint = Color.DarkGray
                                                )
                                            }
                                        }
                                    }
                        }
                    }

                    Button(
                        onClick = { gotoAddTaskScreen() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(text = "Add Task")
                    }
                }
            }
        }

        is UiState.Error -> {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Something went wrong")
            }
        }
    }
}

@Composable
fun AddTaskScreen(
    viewModel: TaskListViewModel, taskId: String? = null, save: () -> Unit = {}
) {
    val existingTask by taskId?.let {
        viewModel.getTaskById(it).collectAsState(initial = null)
    } ?: remember { mutableStateOf(null) }

    var isError by remember { mutableStateOf(false) }
    var titleText by remember { mutableStateOf("") }
    LaunchedEffect(existingTask) {
        if (existingTask != null) {
            titleText = existingTask!!.title
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (existingTask != null) "Edit Task" else "Add New Task",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF333333)
                )
                Spacer(modifier = Modifier.height(16.dp))

                TextField(
                    leadingIcon = {
                        Icon(Icons.Default.Edit, contentDescription = null)
                    },
                    value = titleText,
                    onValueChange = { newText ->
                        titleText = newText
                        isError = false
                    },

                    isError = isError,
                    label = { Text(text = "Enter Title") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)

                )
                if (isError) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Title cannot be empty",
                        color = Color.Red,
                        fontSize = 12.sp
                    )
                }
                Spacer(modifier = Modifier.height(20.dp))
                Button(
                    onClick = {
                        if (titleText.isBlank()) {
                            isError = true
                            return@Button
                        }


                        existingTask?.let {
                            viewModel.updateTask(it.copy(title = titleText))
                            //viewModel.updateTask(it.id,titleText)
                        } ?: run {
                            viewModel.addTask(titleText)
                        }
                        //above is the same way as below just a different way of writing it

//            if(existingTask!=null){
//                viewModel.updateTask(existingTask!!.id,titleText)
//            }else{
//                viewModel.addTask(titleText)
//            }
                        save()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(text = "Save")
                }

            }
        }
    }
}

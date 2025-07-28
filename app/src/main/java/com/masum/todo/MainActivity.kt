package com.masum.todo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import com.masum.todo.data.database.TodoDatabase
import com.masum.todo.data.repository.TodoRepositoryImpl
import com.masum.todo.presentation.TodoScreen
import com.masum.todo.presentation.viewmodel.TodoViewModelFactory
import com.masum.todo.ui.theme.TodoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        
        val database = TodoDatabase.getDatabase(this)
        val repository = TodoRepositoryImpl(database.todoTaskDao())
        val viewModelFactory = TodoViewModelFactory(repository)
        
        setContent {
            TodoTheme {
                TodoScreen(
                    viewModel = viewModel(factory = viewModelFactory)
                )
            }
        }
    }
}
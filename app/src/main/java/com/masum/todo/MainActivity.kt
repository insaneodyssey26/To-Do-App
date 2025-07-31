package com.masum.todo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.FirebaseApp
import com.masum.todo.data.auth.AuthService
import com.masum.todo.data.database.TodoDatabase
import com.masum.todo.data.repository.TodoRepositoryImpl
import com.masum.todo.data.repository.TodoRepositoryWithSync
import com.masum.todo.data.sync.FirestoreSyncRepository
import com.masum.todo.presentation.TodoScreen
import com.masum.todo.presentation.auth.LoginScreen
import com.masum.todo.presentation.viewmodel.TodoViewModelFactory
import com.masum.todo.ui.theme.TodoTheme

class MainActivity : ComponentActivity() {
    private lateinit var authService: AuthService
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        FirebaseApp.initializeApp(this)
        authService = AuthService(this)
        
        val database = TodoDatabase.getDatabase(this)
        val localRepository = TodoRepositoryImpl(database.todoTaskDao())
        val syncRepository = FirestoreSyncRepository()
        val repository = TodoRepositoryWithSync(localRepository, syncRepository, authService)
        val viewModelFactory = TodoViewModelFactory(repository)
        
        setContent {
            TodoTheme {
                var showLogin by remember { mutableStateOf(!authService.isUserLoggedIn()) }
                var loginError by remember { mutableStateOf<String?>(null) }
                
                if (showLogin) {
                    LoginScreen(
                        onLoginSuccess = { 
                            showLogin = false
                            loginError = null
                        },
                        onLoginError = { error ->
                            loginError = error
                        },
                        authService = authService
                    )
                    
                    loginError?.let { error ->
                        LaunchedEffect(error) {
                            // You could show a snackbar or dialog here
                        }
                    }
                } else {
                    TodoScreen(
                        viewModel = viewModel(factory = viewModelFactory)
                    )
                }
            }
        }
    }
}
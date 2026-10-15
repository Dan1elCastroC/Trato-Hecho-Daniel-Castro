package com.example.tratohecho

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tratohecho.navigation.AppNavigation
import com.example.tratohecho.ui.theme.TratoHechoTheme
import com.example.tratohecho.viewmodel.AppViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TratoHechoTheme {
                val appViewModel: AppViewModel = viewModel()
                AppNavigation(viewModel = appViewModel)
            }
        }
    }
}

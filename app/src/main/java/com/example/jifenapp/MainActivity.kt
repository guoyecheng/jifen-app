package com.example.jifenapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.jifenapp.navigation.JifenNavHost
import com.example.jifenapp.ui.theme.JifenTheme

/**
 * 唯一的 Activity。Compose Navigation 接管全部页面切换。
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            JifenApp()
        }
    }
}

@Composable
private fun JifenApp() {
    JifenTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            val navController = rememberNavController()
            JifenNavHost(navController = navController)
        }
    }
}

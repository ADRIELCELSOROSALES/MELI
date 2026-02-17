package com.example.appchallengemeli

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.example.appchallengemeli.ui.navigation.MeliNavGraph
import com.example.appchallengemeli.ui.theme.AppChallengeMELITheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppChallengeMELITheme {
                val navController = rememberNavController()
                MeliNavGraph(navController = navController)
            }
        }
    }
}

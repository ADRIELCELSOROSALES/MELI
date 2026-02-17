package com.example.appchallengemeli.ui.navigation

import android.net.Uri
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.appchallengemeli.ui.detail.DetailScreen
import com.example.appchallengemeli.ui.search.SearchScreen

object Routes {
    const val SEARCH = "search"
    const val DETAIL = "detail/{itemId}"

    fun detail(itemId: String) = "detail/${Uri.encode(itemId)}"
}

private const val ANIM_DURATION = 300

@Composable
fun MeliNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Routes.SEARCH
    ) {
        composable(Routes.SEARCH) {
            SearchScreen(
                onProductClick = { itemId ->
                    navController.navigate(Routes.detail(itemId))
                }
            )
        }

        composable(
            route = Routes.DETAIL,
            arguments = listOf(
                navArgument("itemId") { type = NavType.StringType }
            ),
            enterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Start,
                    animationSpec = tween(ANIM_DURATION)
                ) + fadeIn(animationSpec = tween(ANIM_DURATION))
            },
            exitTransition = {
                fadeOut(animationSpec = tween(ANIM_DURATION))
            },
            popEnterTransition = {
                fadeIn(animationSpec = tween(ANIM_DURATION))
            },
            popExitTransition = {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.End,
                    animationSpec = tween(ANIM_DURATION)
                ) + fadeOut(animationSpec = tween(ANIM_DURATION))
            }
        ) {
            DetailScreen(
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}

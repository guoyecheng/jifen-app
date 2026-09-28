package com.example.jifenapp.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.jifenapp.ui.child.ChildDetailScreen
import com.example.jifenapp.ui.child.ChildDetailViewModel
import com.example.jifenapp.ui.home.HomeScreen
import com.example.jifenapp.ui.home.HomeViewModel
import com.example.jifenapp.ui.record.AddRecordScreen
import com.example.jifenapp.ui.record.AddRecordViewModel
import com.example.jifenapp.ui.rules.RulesScreen
import com.example.jifenapp.ui.rules.RulesViewModel
import com.example.jifenapp.ui.statistics.StatisticsScreen
import com.example.jifenapp.ui.statistics.StatisticsViewModel
import com.example.jifenapp.di.viewModelFactory

/**
 * 全 App 路由拓扑。每个 composable 对应一个 Screen，
 * ViewModel 通过 [viewModelFactory] 工具函数从 AppContainer 注入依赖。
 */
@Composable
fun JifenNavHost(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Routes.Home.route
    ) {
        composable(Routes.Home.route) {
            val vm: HomeViewModel = viewModel(
                factory = viewModelFactory { HomeViewModel.create(it) }
            )
            HomeScreen(
                viewModel = vm,
                onChildClick = { childId ->
                    navController.navigate(Routes.ChildDetail.build(childId))
                },
                onAddRecord = { childId ->
                    navController.navigate(Routes.AddRecord.build(childId))
                },
                onOpenRules = {
                    navController.navigate(Routes.Rules.route)
                }
            )
        }

        composable(Routes.Rules.route) {
            val vm: RulesViewModel = viewModel(
                factory = viewModelFactory { RulesViewModel.create(it) }
            )
            RulesScreen(
                viewModel = vm,
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Routes.ChildDetail.route,
            arguments = listOf(
                navArgument(Routes.ChildDetail.ARG_CHILD_ID) { type = NavType.LongType }
            )
        ) { entry ->
            val childId = entry.arguments?.getLong(Routes.ChildDetail.ARG_CHILD_ID) ?: 0L
            val vm: ChildDetailViewModel = viewModel(
                factory = viewModelFactory { ChildDetailViewModel.create(it, childId) }
            )
            ChildDetailScreen(
                childId = childId,
                viewModel = vm,
                onBack = { navController.popBackStack() },
                onAddRecord = {
                    navController.navigate(Routes.AddRecord.build(childId))
                },
                onOpenStatistics = {
                    navController.navigate(Routes.Statistics.build(childId))
                }
            )
        }

        composable(
            route = Routes.AddRecord.route,
            arguments = listOf(
                navArgument(Routes.AddRecord.ARG_CHILD_ID) { type = NavType.LongType }
            )
        ) { entry ->
            val childId = entry.arguments?.getLong(Routes.AddRecord.ARG_CHILD_ID) ?: 0L
            val vm: AddRecordViewModel = viewModel(
                factory = viewModelFactory { AddRecordViewModel.create(it, childId) }
            )
            AddRecordScreen(
                childId = childId,
                viewModel = vm,
                onBack = { navController.popBackStack() },
                onSaved = { navController.popBackStack() }
            )
        }

        composable(
            route = Routes.Statistics.route,
            arguments = listOf(
                navArgument(Routes.Statistics.ARG_CHILD_ID) { type = NavType.LongType }
            )
        ) { entry ->
            val childId = entry.arguments?.getLong(Routes.Statistics.ARG_CHILD_ID) ?: 0L
            val vm: StatisticsViewModel = viewModel(
                factory = viewModelFactory { StatisticsViewModel.create(it, childId) }
            )
            StatisticsScreen(
                viewModel = vm,
                onBack = { navController.popBackStack() }
            )
        }
    }
}

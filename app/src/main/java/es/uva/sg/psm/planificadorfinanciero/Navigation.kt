package es.uva.sg.psm.planificadorfinanciero

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.colorResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import es.uva.sg.psm.planificadorfinanciero.viewModels.BudgetViewModel
import es.uva.sg.psm.planificadorfinanciero.viewModels.CategoryViewModel
import es.uva.sg.psm.planificadorfinanciero.viewModels.TransactionViewModel

@Composable
fun Navigation(
    transactionViewModel: TransactionViewModel = viewModel(),
    categoryViewModel: CategoryViewModel = viewModel(),
    budgetViewModel: BudgetViewModel = viewModel(),
    navController: NavHostController,
    onPlaySound: (Int) -> Unit
) {
    // Configuración del color de la barra de estado
    SetStatusBarColor(
        statusBarColor = colorResource(R.color.app_bar_color),
        navigationBarColor = colorResource(R.color.bold_from_palette)
    )

    NavHost(
        navController = navController,
        startDestination = Screen.DashboardScreen.route
    ) {
        composable(route = Screen.DashboardScreen.route) {
            DashboardScreen(navController, transactionViewModel)
        }
        composable(route = Screen.BudgetScreen.route) {
            BudgetScreen(navController, budgetViewModel, categoryViewModel, onPlaySound)
        }
        composable(route = Screen.AddEditBudgetScreen.route + "/{id}",
            arguments = listOf(
                navArgument("id") {
                    type = NavType.LongType
                    defaultValue = 0L
                    nullable = false
                }
            )) { entry ->
            val id = if (entry.arguments != null) entry.arguments!!.getLong("id") else 0L
            AddEditDetailBudgetView(
                id = id,
                budgetViewModel = budgetViewModel,
                categoryViewModel = categoryViewModel,
                transactionViewModel = transactionViewModel,
                navController = navController
            )
        }
        composable(route = Screen.AddEditTransactionScreen.route + "/{id}",
            arguments = listOf(
                navArgument("id") {
                    type = NavType.LongType
                    defaultValue = 0L
                    nullable = false
                }
            )) { entry ->
            val id = if (entry.arguments != null) entry.arguments!!.getLong("id") else 0L
            AddEditDetailTransactionView(
                id = id,
                transactionViewModel = transactionViewModel,
                categoryViewModel = categoryViewModel,
                navController = navController
            )
        }
        composable(route = Screen.HistoryScreen.route){
            HistoryScreen(
                navController = navController,
                transactionViewModel = transactionViewModel,
                categoryViewModel = categoryViewModel,
                budgetViewModel = budgetViewModel,
                onPlaySound = onPlaySound
            )
        }
        composable(route = Screen.StaticsScreen.route) {
            StaticsScreen(
                navController = navController,
                transactionViewModel = transactionViewModel
            )
        }
    }
}
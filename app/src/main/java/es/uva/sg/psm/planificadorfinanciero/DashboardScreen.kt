package es.uva.sg.psm.planificadorfinanciero

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.shape.RoundedCornerShape
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import es.uva.sg.psm.planificadorfinanciero.data.TransactionType
import es.uva.sg.psm.planificadorfinanciero.viewModels.TransactionViewModel
import java.util.Locale

@Composable
fun DashboardScreen(navController: NavHostController, transactionViewModel: TransactionViewModel) {
    Scaffold(
        topBar = {
            AppBarView(title = "Planificador Financiero", showBackArrow = false)
        },
        modifier = Modifier
            .fillMaxSize()
            .padding(WindowInsets.systemBars.asPaddingValues()), // Agregar espacio para evitar superposición
        bottomBar = { NavigationBar(navController = navController) }, // Barra inferior
        containerColor = MaterialTheme.colorScheme.background, // Fondo de la pantalla
        floatingActionButton = {
            FABCustom(navController = navController)
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Saldo total en la parte superior
            val listaTransacciones = transactionViewModel.getAllTransactions
                .collectAsState(initial = emptyList()).value
            var income = 0.0
            var expenses = 0.0

            listaTransacciones.forEach { transaction ->
                if(transaction.type == TransactionType.Ingreso) {
                    income += transaction.amount
                } else if (transaction.type == TransactionType.Gasto) {
                    expenses += transaction.amount
                }
            }

            val balance = income - expenses

            TotalBalanceCard(balance)

            // Sección Gasto Total e Ingreso Total
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically)
            ) {
                // Tarjeta Gasto Total
                DashboardCard(
                    title = "GASTO TOTAL",
                    value = expenses,
                    cardColor = colorResource(id = R.color.red_transaction)
                )

                // Tarjeta Ingreso Total
                DashboardCard(
                    title = "INGRESO TOTAL",
                    value = income,
                    cardColor = colorResource(id = R.color.green_transaction)
                )
            }
        }
    }
}

@Composable
fun TotalBalanceCard(balance: Double) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = 4.dp,
        shape = RoundedCornerShape(8.dp),
        backgroundColor = colorResource(R.color.blue_ultra_light)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Saldo Total",
                style = MaterialTheme.typography.titleLarge,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "%.2f€".format(Locale.getDefault(), balance),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )
        }
    }
}


@Composable
fun DashboardCard(title: String, value: Double, cardColor: Color) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(), // Asegura que ocupe todo el espacio asignado
        elevation = 4.dp,
        shape = RoundedCornerShape(8.dp),
        backgroundColor = colorResource(R.color.blue_white)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                color = cardColor,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "%.2f€".format(Locale.getDefault(), value),
                style = MaterialTheme.typography.bodyLarge,
                color = colorResource(R.color.bold_from_palette)
            )
        }
    }
}

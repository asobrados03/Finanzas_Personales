package es.uva.sg.psm.planificadorfinanciero

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.wrapContentHeight
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import es.uva.sg.psm.planificadorfinanciero.data.TransactionType
import es.uva.sg.psm.planificadorfinanciero.viewModels.TransactionViewModel
import kotlin.math.roundToInt

@Composable
fun StaticsScreen(navController: NavController, transactionViewModel: TransactionViewModel) {
    Scaffold(
        topBar = {
            AppBarView(title = "Estadísticas", showBackArrow = false) {
                navController.navigateUp()
            }
        },
        modifier = Modifier
            .fillMaxSize()
            .padding(WindowInsets.systemBars.asPaddingValues()), // Agregar espacio para evitar superposición
        bottomBar = { NavigationBar(navController = navController) }, // Barra inferior
        containerColor = MaterialTheme.colorScheme.background // Fondo de la pantalla
    ){ paddingValues ->
        // Operaciones para calcular los datos del gráfico comparación ingresos y gastos
        val transactions = transactionViewModel.getAllTransactions
            .collectAsState(initial = emptyList()).value

        var totalAmountExpenses = 0.0
        var totalAmountIncome = 0.0

        if(transactions.isNotEmpty()){
            transactions.forEach { transaction ->
                if (transaction.type == TransactionType.Gasto) {
                    totalAmountExpenses += transaction.amount
                } else {
                    totalAmountIncome += transaction.amount
                }
            }
        }

        if(transactions.isNotEmpty()){
            // Gráfico ingresos y gastos
            Card(modifier = Modifier
                .fillMaxWidth()
                .height(500.dp)
                .padding(paddingValues)) {
                BarChart(
                    income = totalAmountIncome,
                    expenses = totalAmountExpenses,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        } else { // Si no hay datos en el sistema para mostrar los gráficos aparece un mensaje
            Text(
                text = "No hay gráficos estadísticos disponibles. Por favor ingrese transacciones.",
                modifier = Modifier
                    .fillMaxSize()
                    .wrapContentHeight(Alignment.CenterVertically),
                textAlign = TextAlign.Center,
                fontSize = 20.sp
            )
        }
    }
}

// Comparación de Ingresos y Gastos
@Composable
fun BarChart(
    income: Double,
    expenses: Double,
    modifier: Modifier = Modifier
) {
    val max = maxOf(income, expenses).toFloat()
    val steps = 5
    val paint = remember {
        Paint().asFrameworkPaint().apply {
            isAntiAlias = true
            textSize = 35f
            color = android.graphics.Color.BLACK
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .height(350.dp)
    ) {
        Text(
            text = "Comparación de Ingresos y Gastos",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(16.dp),
            color = colorResource(R.color.bold_from_palette),
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold
        )
        // Los colores de las barras
        val colorGreen = colorResource(R.color.green_transaction)
        val colorRed = colorResource(R.color.red_transaction)

        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            val barWidth = size.width / 5
            val leftPadding = 120f  // Space for Y-axis labels
            val bottomPadding = 40f // Space for X-axis labels
            val chartWidth = size.width - leftPadding
            val chartHeight = size.height - bottomPadding

            // Dibujar líneas de cuadrícula y etiquetas
            for (i in 0..steps) {
                val y = chartHeight * (1 - i.toFloat() / steps)
                val value = (max * i / steps).roundToInt()

                // Líneas de cuadrícula
                drawLine(
                    color = Color.LightGray,
                    start = Offset(leftPadding, y),
                    end = Offset(size.width, y),
                    strokeWidth = 1f,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f))
                )

                // Etiquetas del eje Y
                drawIntoCanvas { canvas ->
                    canvas.nativeCanvas.drawText(
                        "$value€",
                        15f,
                        y + 12f,
                        paint
                    )
                }
            }

            // Dibuja las barras
            val incomeHeight = if (income > 0) (income.toFloat() / max) * chartHeight else 10f
            val expensesHeight = if (expenses > 0) (expenses.toFloat() / max) * chartHeight else 10f

            // Barra de ingresos
            drawRect(
                color = colorGreen,
                topLeft = Offset(
                    x = leftPadding + chartWidth / 4 - barWidth / 2,
                    y = chartHeight - incomeHeight
                ),
                size = Size(width = barWidth, height = incomeHeight),
                alpha = 0.8f
            )

            // Barra de gastos
            drawRect(
                color = colorRed,
                topLeft = Offset(
                    x = leftPadding + 3 * chartWidth / 4 - barWidth / 2,
                    y = chartHeight - expensesHeight
                ),
                size = Size(width = barWidth, height = expensesHeight),
                alpha = 0.8f
            )

            // Pinta los ejes
            drawLine(
                color = Color.Black,
                start = Offset(leftPadding, 0f),
                end = Offset(leftPadding, chartHeight),
                strokeWidth = 2f
            )
            drawLine(
                color = Color.Black,
                start = Offset(leftPadding, chartHeight),
                end = Offset(size.width, chartHeight),
                strokeWidth = 2f
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Text(
                text = "Ingresos",
                style = MaterialTheme.typography.bodyMedium,
                color = colorResource(R.color.bold_from_palette),
                textAlign = TextAlign.Center
            )
            Text(
                text = "Gastos",
                style = MaterialTheme.typography.bodyMedium,
                color = colorResource(R.color.bold_from_palette),
                textAlign = TextAlign.Center,
                modifier = Modifier.offset(x = 20.dp)
            )
        }
    }
}

/*
* A partir de aquí hay dos funciones composables que se implementaran más adelante cuando
* esta aplicación haya sido entregada al profesor.
*/

// Gastos por Categoría
@Composable
fun PieChart(
    data: Map<String, Float>,
    colors: List<Color>,
    modifier: Modifier = Modifier
) {
    val total = data.values.sum()
    var startAngle = 0f

    Canvas(modifier = modifier.fillMaxWidth()) {
        data.forEach { (category, value) ->
            val sweepAngle = (value / total) * 360f
            drawArc(
                color = colors[data.keys.indexOf(category) % colors.size],
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = true
            )
            startAngle += sweepAngle
        }
    }
}

// Tendencia de Saldo
@Composable
fun LineChart(
    dataPoints: List<Float>,
    modifier: Modifier = Modifier,
    lineColor: Color = Color.Blue,
    strokeWidth: Dp = 4.dp
) {
    if (dataPoints.isEmpty()) return

    val max = dataPoints.maxOrNull() ?: 1f

    Canvas(modifier = modifier.fillMaxSize()) {
        val stepX = size.width / (dataPoints.size - 1).coerceAtLeast(1)

        for (i in 0 until dataPoints.size - 1) {
            val startX = i * stepX
            val startY = size.height - (dataPoints[i] / max) * size.height
            val endX = (i + 1) * stepX
            val endY = size.height - (dataPoints[i + 1] / max) * size.height

            drawLine(
                color = lineColor,
                start = Offset(startX, startY),
                end = Offset(endX, endY),
                strokeWidth = strokeWidth.toPx()
            )
        }
    }
}
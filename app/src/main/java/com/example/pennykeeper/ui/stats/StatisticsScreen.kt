package com.example.pennykeeper.ui.stats

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.pennykeeper.data.model.ExpenseCategory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsScreen(statisticsViewModel: StatisticsViewModel) {
    val expensesByCategory by statisticsViewModel.expensesByCategory.collectAsState()
    val totalExpenses by statisticsViewModel.totalExpenses.collectAsState()
    val selectedTimeRange by statisticsViewModel.selectedTimeRange.collectAsState()
    val budget by statisticsViewModel.budgetFlow.collectAsState()
    val amountOverBudget by statisticsViewModel.amountOverBudget.collectAsState()
    val isOverBudget by statisticsViewModel.isOverBudget.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Expense Statistics",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Time range selector
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            TimeRange.values().forEach { timeRange ->
                FilterChip(
                    selected = timeRange == selectedTimeRange,
                    onClick = { statisticsViewModel.setTimeRange(timeRange) },
                    label = { Text(timeRange.name) }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Total expenses and current budget
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Total Expenses Section
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Total Expenses",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = "$${String.format("%.2f", totalExpenses)}",
                            style = MaterialTheme.typography.headlineMedium,
                            color = if (isOverBudget) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                        )
                    }

                    // Current Budget Section
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Current Budget",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = "$${String.format("%.2f", budget)}",
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                // Over Budget Indicator
                if (isOverBudget) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "You've exceeded your budget by $${String.format("%.2f", amountOverBudget)}",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        if (expensesByCategory.isNotEmpty()) {
            PieChart(
                data = expensesByCategory,
                modifier = Modifier.padding(vertical = 16.dp)
            )
            ExpenseSummary(expensesByCategory)
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No expenses recorded for this period",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun PieChart(
    data: Map<ExpenseCategory, Double>,
    modifier: Modifier = Modifier
) {
    val total = data.values.sum()
    var startAngle = 0f

    Canvas(modifier = modifier.size(200.dp)) {
        val radius = size.minDimension / 2
        val center = Offset(size.width / 2, size.height / 2)

        if (total > 0) {
            data.forEach { (category, amount) ->
                val sweepAngle = 360f * (amount / total).toFloat()
                drawArc(
                    color = getCategoryColor(category),
                    startAngle = startAngle,
                    sweepAngle = sweepAngle,
                    useCenter = true,
                    topLeft = Offset(center.x - radius, center.y - radius),
                    size = Size(radius * 2, radius * 2)
                )
                startAngle += sweepAngle
            }

            // Draw a white circle in the center for a donut effect
            drawCircle(
                color = Color.White,
                radius = radius * 0.6f,
                center = center
            )

            // Draw stroke around the pie chart
            drawCircle(
                color = Color.Black,
                radius = radius,
                center = center,
                style = Stroke(width = 2f)
            )
        }
    }
}

@Composable
fun ExpenseSummary(expensesByCategory: Map<ExpenseCategory, Double>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp)
    ) {
        expensesByCategory.forEach { (category, amount) ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                colors = CardDefaults.cardColors(
                    containerColor = getCategoryColor(category).copy(alpha = 0.1f)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = category.name,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = "$${String.format("%.2f", amount)}",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }
}


fun getCategoryColor(category: ExpenseCategory): Color {
    return when (category) {
        ExpenseCategory.GROCERIES -> Color(0xFFAED581)       // Pastel Green
        ExpenseCategory.SUBSCRIPTIONS -> Color(0xFF81D4FA)   // Pastel Blue
        ExpenseCategory.TAXES -> Color(0xFFFF8A80)           // Pastel Red
        ExpenseCategory.ENTERTAINMENT -> Color(0xFFCE93D8)   // Pastel Purple
        ExpenseCategory.UTILITIES -> Color(0xFFFFF176)       // Pastel Yellow
        ExpenseCategory.OTHER -> Color(0x8BEEEEEE)           // Light Grey
    }
}

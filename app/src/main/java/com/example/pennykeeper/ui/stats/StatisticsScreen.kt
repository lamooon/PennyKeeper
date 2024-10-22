package com.example.pennykeeper.ui.stats


import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pennykeeper.data.model.ExpenseCategory

@Composable
fun StatisticsScreen(statisticsViewModel: StatisticsViewModel = viewModel()) {
    val expensesByCategory by statisticsViewModel.expensesByCategory.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Expense Statistics",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        PieChart(data = expensesByCategory)
        ExpenseSummary(expensesByCategory)
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

@Composable
fun ExpenseSummary(expensesByCategory: Map<ExpenseCategory, Double>) {
    Column(modifier = Modifier.padding(top = 16.dp)) {
        expensesByCategory.forEach { (category, amount) ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = category.name, style = MaterialTheme.typography.bodyLarge)
                Text(text = "$${String.format("%.2f", amount)}", style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}

fun getCategoryColor(category: ExpenseCategory): Color {
    return when (category) {
        ExpenseCategory.GROCERIES -> Color.Green
        ExpenseCategory.SUBSCRIPTIONS -> Color.Blue
        ExpenseCategory.TAXES -> Color.Red
        ExpenseCategory.ENTERTAINMENT -> Color.Yellow
        ExpenseCategory.UTILITIES -> Color.Cyan
        ExpenseCategory.OTHER -> Color.Gray
    }
}
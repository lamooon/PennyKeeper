package com.example.pennykeeper.ui.stats

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun StatisticsScreen(viewModel: StatisticsViewModel) {
    val categoryExpenses by viewModel.categoryExpenses.collectAsState()
    val totalAmount by viewModel.totalAmount.collectAsState()
    val selectedPeriod by viewModel.selectedPeriod.collectAsState()
    val showPeriodPicker = remember { mutableStateOf(false) }
    val sheetState = rememberBottomSheetState()

    val sheetHeight by animateFloatAsState(
        targetValue = if (sheetState.isExpanded) 1f else 0.25f,
        animationSpec = tween(300, easing = FastOutSlowInEasing),
        label = "sheetHeight"
    )

    Box(modifier = Modifier.fillMaxSize()) {
        // Donut Chart Section
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(280.dp)
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        DonutChart(categoryExpenses)

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Spent on",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center,
                                modifier = Modifier.clickable { showPeriodPicker.value = true }
                            ) {
                                Text(
                                    text = getPeriodText(selectedPeriod),
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold
                                )
                                Icon(
                                    Icons.Default.ArrowDropDown,
                                    contentDescription = "Select period",
                                    modifier = Modifier.padding(start = 4.dp)
                                )
                            }
                            Text(
                                text = "₩${String.format("%,d", totalAmount.toLong())}",
                                style = MaterialTheme.typography.headlineMedium,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                    }

                    // Period selector
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp)
                    ) {
                        StatisticsViewModel.TimePeriod.values().forEach { period ->
                            Text(
                                text = period.name.lowercase()
                                    .replaceFirstChar { it.uppercase() },
                                modifier = Modifier
                                    .padding(horizontal = 12.dp)
                                    .alpha(if (selectedPeriod == period) 1f else 0.5f)
                                    .clickable {
                                        viewModel.setPeriod(period)
//                                        viewModel.updateStatistics(period)
                                    },
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = if (selectedPeriod == period)
                                    FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            }
        }

        // Bottom Sheet
        Surface(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .fillMaxHeight(sheetHeight)
                .draggable(
                    state = rememberDraggableState { delta ->
                        if (delta < 0 && !sheetState.isExpanded) {
                            sheetState.expand()
                        } else if (delta > 0 && sheetState.isExpanded) {
                            sheetState.collapse()
                        }
                    },
                    orientation = Orientation.Vertical,
                ),
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 2.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Drag Handle
                Box(
                    modifier = Modifier
                        .width(40.dp)
                        .height(4.dp)
                        .background(
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
                            RoundedCornerShape(2.dp)
                        )
                        .align(Alignment.CenterHorizontally)
                )

                Text(
                    text = "Spending Categories",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(vertical = 16.dp)
                )

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(categoryExpenses.chunked(2)) { rowItems ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            rowItems.forEach { expense ->
                                CategoryCard(
                                    category = expense,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            if (rowItems.size == 1) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CategoryCard(
    category: StatisticsViewModel.CategoryExpense,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
        tonalElevation = 4.dp
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "₩${String.format("%,d", category.amount.toLong())}",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${String.format("%.1f", category.percentage * 100)}%",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }

            Text(
                text = category.category.name,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 4.dp)
            )

            Box(
                modifier = Modifier
                    .padding(top = 8.dp)
                    .size(12.dp)
                    .background(category.color, CircleShape)
            )
        }
    }
}

@Composable
private fun DonutChart(categoryExpenses: List<StatisticsViewModel.CategoryExpense>) {
    Canvas(
        modifier = Modifier.fillMaxSize()
    ) {
        val donutThickness = size.width * 0.15f
        var startAngle = 0f

        categoryExpenses.forEach { categoryExpense ->
            val sweepAngle = categoryExpense.percentage * 360f
            drawArc(
                color = categoryExpense.color,
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = false,
                style = Stroke(
                    width = donutThickness,
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round
                )
            )
            startAngle += sweepAngle
        }
    }
}

@Composable
private fun rememberBottomSheetState(): BottomSheetState {
    return remember {
        BottomSheetState(
            isExpanded = false,
            offsetY = mutableStateOf(0f),
            isDragging = mutableStateOf(false)
        )
    }
}

class BottomSheetState(
    isExpanded: Boolean,
    val offsetY: MutableState<Float>,
    val isDragging: MutableState<Boolean>
) {
    var isExpanded by mutableStateOf(isExpanded)

    fun expand() {
        isExpanded = true
        offsetY.value = 0f
    }

    fun collapse() {
        isExpanded = false
        offsetY.value = 0f
    }
}

@Composable
private fun getPeriodText(period: StatisticsViewModel.TimePeriod): String {
    return when (period) {
        StatisticsViewModel.TimePeriod.WEEK -> "Week 1"
        StatisticsViewModel.TimePeriod.MONTH -> "January"
        StatisticsViewModel.TimePeriod.YEAR -> "2024"
    }
}
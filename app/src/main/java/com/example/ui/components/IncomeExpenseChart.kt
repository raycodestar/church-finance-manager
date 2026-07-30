package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ExpenseRed
import com.example.ui.theme.IncomeGreen
import com.example.utils.CurrencyFormatter

@Composable
fun IncomeExpenseChart(
    totalIncome: Long,
    totalExpense: Long,
    currencyCode: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Income vs Expenses",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(16.dp))

            val maxVal = maxOf(totalIncome, totalExpense, 1L).toFloat()
            val incomeRatio = (totalIncome.toFloat() / maxVal).coerceIn(0.05f, 1f)
            val expenseRatio = (totalExpense.toFloat() / maxVal).coerceIn(0.05f, 1f)

            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
            ) {
                val canvasWidth = size.width
                val canvasHeight = size.height

                val barWidth = canvasWidth * 0.28f
                val gap = canvasWidth * 0.15f
                val startX = (canvasWidth - (barWidth * 2 + gap)) / 2f

                // Draw background grid line
                drawLine(
                    color = Color.LightGray.copy(alpha = 0.3f),
                    start = Offset(0f, canvasHeight - 20f),
                    end = Offset(canvasWidth, canvasHeight - 20f),
                    strokeWidth = 2f
                )

                // Income Bar
                val incomeBarHeight = (canvasHeight - 30f) * incomeRatio
                drawRoundRect(
                    color = IncomeGreen,
                    topLeft = Offset(startX, canvasHeight - 20f - incomeBarHeight),
                    size = Size(barWidth, incomeBarHeight),
                    cornerRadius = CornerRadius(12f, 12f)
                )

                // Expense Bar
                val expenseBarHeight = (canvasHeight - 30f) * expenseRatio
                val expenseStartX = startX + barWidth + gap
                drawRoundRect(
                    color = ExpenseRed,
                    topLeft = Offset(expenseStartX, canvasHeight - 20f - expenseBarHeight),
                    size = Size(barWidth, expenseBarHeight),
                    cornerRadius = CornerRadius(12f, 12f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .background(IncomeGreen, CircleShape)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Income: ${CurrencyFormatter.format(totalIncome, currencyCode)}",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                        fontWeight = FontWeight.Medium
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .background(ExpenseRed, CircleShape)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Expense: ${CurrencyFormatter.format(totalExpense, currencyCode)}",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

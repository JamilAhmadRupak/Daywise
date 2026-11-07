package com.daywise.app.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.daywise.app.domain.util.CurrencyFormatter
import java.time.LocalDate

data class DaySpending(
    val dayOfMonth: Int,
    val amountPaisa: Long,
    val date: LocalDate
)

@Composable
fun DailySpendingBarChart(
    daysData: List<DaySpending>,
    selectedDay: Int?,
    onSelectDay: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    if (daysData.isEmpty()) {
        Text(
            text = "No daily spending recorded for this month.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(16.dp)
        )
        return
    }

    val maxAmountPaisa = (daysData.maxOfOrNull { it.amountPaisa } ?: 0L).coerceAtLeast(100L) // at least 1 BDT
    val primaryColor = MaterialTheme.colorScheme.primary
    val selectedColor = MaterialTheme.colorScheme.secondary
    val neutralBarColor = MaterialTheme.colorScheme.surfaceVariant
    val textColor = MaterialTheme.colorScheme.onSurfaceVariant

    val textMeasurer = rememberTextMeasurer()
    val scrollState = rememberScrollState()

    // Smooth entry animation
    val animProgress = remember { Animatable(0f) }
    LaunchedEffect(daysData) {
        animProgress.snapTo(0f)
        animProgress.animateTo(1f, animationSpec = tween(durationMillis = 650))
    }

    // Chart Dimensions
    val barWidthDp = 16.dp
    val barSpacingDp = 8.dp
    val totalChartWidthDp = (daysData.size * (16 + 8) + 24).dp
    val chartHeightDp = 180.dp

    Column(modifier = modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(scrollState)
        ) {
            Canvas(
                modifier = Modifier
                    .width(totalChartWidthDp)
                    .height(chartHeightDp)
                    .pointerInput(daysData) {
                        detectTapGestures { offset ->
                            val startX = 12.dp.toPx()
                            val barWidthPx = barWidthDp.toPx()
                            val barSpacingPx = barSpacingDp.toPx()
                            val step = barWidthPx + barSpacingPx

                            val clickedIndex = ((offset.x - startX) / step).toInt()
                            if (clickedIndex in daysData.indices) {
                                onSelectDay(daysData[clickedIndex].dayOfMonth)
                            }
                        }
                    }
            ) {
                val canvasWidth = size.width
                val canvasHeight = size.height

                val bottomLabelHeight = 28.dp.toPx()
                val topPadding = 20.dp.toPx()
                val availableHeight = canvasHeight - bottomLabelHeight - topPadding

                val startX = 12.dp.toPx()
                val barWidthPx = barWidthDp.toPx()
                val barSpacingPx = barSpacingDp.toPx()
                val step = barWidthPx + barSpacingPx

                // Baseline line
                drawLine(
                    color = neutralBarColor,
                    start = Offset(0f, canvasHeight - bottomLabelHeight),
                    end = Offset(canvasWidth, canvasHeight - bottomLabelHeight),
                    strokeWidth = 2.dp.toPx()
                )

                daysData.forEachIndexed { index, item ->
                    val x = startX + (index * step)
                    val fraction = (item.amountPaisa.toFloat() / maxAmountPaisa).coerceIn(0f, 1f)
                    val barHeight = (availableHeight * fraction * animProgress.value).coerceAtLeast(3.dp.toPx())
                    val topY = (canvasHeight - bottomLabelHeight) - barHeight

                    val isSelected = selectedDay == item.dayOfMonth
                    val barColor = when {
                        isSelected -> selectedColor
                        item.amountPaisa > 0 -> primaryColor
                        else -> neutralBarColor.copy(alpha = 0.5f)
                    }

                    // Draw bar
                    drawRoundRect(
                        color = barColor,
                        topLeft = Offset(x, topY),
                        size = Size(barWidthPx, barHeight),
                        cornerRadius = CornerRadius(6.dp.toPx(), 6.dp.toPx())
                    )

                    // Draw Day number label
                    val labelText = item.dayOfMonth.toString()
                    val textLayout = textMeasurer.measure(
                        text = labelText,
                        style = TextStyle(
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) primaryColor else textColor
                        )
                    )

                    drawText(
                        textLayoutResult = textLayout,
                        topLeft = Offset(
                            x + (barWidthPx - textLayout.size.width) / 2,
                            canvasHeight - bottomLabelHeight + 6.dp.toPx()
                        )
                    )
                }
            }
        }
    }
}

package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Icon
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import com.example.ui.theme.DarkSlatePrimary
import com.example.ui.theme.SlateBorder
import com.example.ui.theme.SlateTextSecondary
import com.example.utils.DateRange
import com.example.utils.DateUtils
import com.example.utils.PeriodFilter

@Composable
fun FilterChipsRow(
    selectedPeriod: PeriodFilter,
    onPeriodSelected: (PeriodFilter, Long?, Long?) -> Unit,
    customDateRange: DateRange? = null,
    modifier: Modifier = Modifier
) {
    var showCustomRangeDialog by remember { mutableStateOf(false) }

    Column(modifier = modifier.fillMaxWidth()) {
        // Full width rectangle container bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .border(width = 1.dp, color = SlateBorder)
                .padding(vertical = 4.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                PeriodFilter.entries.forEach { filter ->
                    val isSelected = filter == selectedPeriod

                    val chipLabel = if (filter == PeriodFilter.CUSTOM && isSelected && customDateRange != null) {
                        "Custom (${DateUtils.formatDate(customDateRange.startMillis)} - ${DateUtils.formatDate(customDateRange.endMillis)})"
                    } else if (filter == PeriodFilter.CUSTOM) {
                        "Custom Range"
                    } else {
                        filter.label
                    }

                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            if (filter == PeriodFilter.CUSTOM) {
                                showCustomRangeDialog = true
                            } else {
                                onPeriodSelected(filter, null, null)
                            }
                        },
                        label = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (filter == PeriodFilter.CUSTOM) {
                                    Icon(
                                        imageVector = Icons.Default.DateRange,
                                        contentDescription = null,
                                        tint = if (isSelected) Color.White else DarkSlatePrimary,
                                        modifier = Modifier
                                            .padding(end = 6.dp)
                                            .size(16.dp)
                                    )
                                }
                                Text(
                                    text = chipLabel,
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        fontSize = 12.sp
                                    )
                                )
                            }
                        },
                        shape = RoundedCornerShape(8.dp),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = DarkSlatePrimary,
                            selectedLabelColor = Color.White,
                            containerColor = Color(0xFFF1F5F9),
                            labelColor = SlateTextSecondary
                        ),
                        border = null
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
            }
        }

        // Active Custom Date Range Banner
        if (selectedPeriod == PeriodFilter.CUSTOM && customDateRange != null) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF6FF)),
                border = BorderStroke(1.dp, Color(0xFFBFDBFE))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = null,
                            tint = DarkSlatePrimary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "CUSTOM DATE RANGE APPLIED",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 9.sp,
                                    letterSpacing = 0.5.sp
                                ),
                                color = SlateTextSecondary
                            )
                            Text(
                                text = "${DateUtils.formatDate(customDateRange.startMillis)}  →  ${DateUtils.formatDate(customDateRange.endMillis)}",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                ),
                                color = DarkSlatePrimary
                            )
                        }
                    }

                    androidx.compose.material3.TextButton(
                        onClick = { showCustomRangeDialog = true }
                    ) {
                        Text(
                            text = "CHANGE",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = DarkSlatePrimary
                            )
                        )
                    }
                }
            }
        }
    }

    if (showCustomRangeDialog) {
        CustomDateRangeDialog(
            initialStartMillis = customDateRange?.startMillis,
            initialEndMillis = customDateRange?.endMillis,
            onDismiss = { showCustomRangeDialog = false },
            onDateRangeSelected = { start, end ->
                showCustomRangeDialog = false
                onPeriodSelected(PeriodFilter.CUSTOM, start, end)
            }
        )
    }
}


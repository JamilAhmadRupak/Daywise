package com.daywise.app.data.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * 8 Core Expense Categories for Daywise.
 * Clean, recognizable, distinct colors and icons.
 */
enum class ExpenseCategory(
    val displayName: String,
    val color: Color
) {
    FOOD("Food", Color(0xFFF97316)), // Warm Amber/Orange
    TRANSPORT("Transport", Color(0xFF0284C7)), // Vivid Blue
    SHOPPING("Shopping", Color(0xFFEC4899)), // Modern Pink
    EDUCATION("Education", Color(0xFF8B5CF6)), // Purple
    BILLS("Bills", Color(0xFFEAB308)), // Yellow Gold
    HEALTH("Health", Color(0xFFEF4444)), // Red
    ENTERTAINMENT("Entertainment", Color(0xFF10B981)), // Emerald Green
    OTHERS("Others", Color(0xFF64748B)); // Slate Gray

    val icon: ImageVector
        get() = when (this) {
            FOOD -> Icons.Filled.Fastfood
            TRANSPORT -> Icons.Filled.DirectionsCar
            SHOPPING -> Icons.Filled.ShoppingBag
            EDUCATION -> Icons.Filled.School
            BILLS -> Icons.Filled.Receipt
            HEALTH -> Icons.Filled.FitnessCenter
            ENTERTAINMENT -> Icons.Filled.CardGiftcard
            OTHERS -> Icons.Filled.MoreHoriz
        }

    companion object {
        fun fromName(name: String?): ExpenseCategory {
            return entries.firstOrNull { it.name.equals(name, ignoreCase = true) }
                ?: entries.firstOrNull { it.displayName.equals(name, ignoreCase = true) }
                ?: OTHERS
        }
    }
}

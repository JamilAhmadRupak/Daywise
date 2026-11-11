package com.daywise.app.data.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalCafe
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Work
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

data class CategoryModel(
    val name: String,
    val displayName: String,
    val color: Color,
    val icon: ImageVector,
    val isBuiltIn: Boolean = true,
    val iconName: String = "category"
) {
    companion object {
        val AVAILABLE_ICONS = listOf(
            "fastfood" to Icons.Filled.Fastfood,
            "transport" to Icons.Filled.DirectionsCar,
            "shopping" to Icons.Filled.ShoppingBag,
            "school" to Icons.Filled.School,
            "receipt" to Icons.Filled.Receipt,
            "fitness" to Icons.Filled.FitnessCenter,
            "gift" to Icons.Filled.CardGiftcard,
            "more" to Icons.Filled.MoreHoriz,
            "pets" to Icons.Filled.Pets,
            "computer" to Icons.Filled.Computer,
            "flight" to Icons.Filled.Flight,
            "home" to Icons.Filled.Home,
            "cafe" to Icons.Filled.LocalCafe,
            "work" to Icons.Filled.Work,
            "gaming" to Icons.Filled.SportsEsports,
            "music" to Icons.Filled.MusicNote,
            "hospital" to Icons.Filled.LocalHospital,
            "phone" to Icons.Filled.PhoneAndroid,
            "movie" to Icons.Filled.Movie,
            "star" to Icons.Filled.Star
        )

        val PRESET_COLORS = listOf(
            0xFFF97316, // Orange
            0xFF0284C7, // Blue
            0xFFEC4899, // Pink
            0xFF8B5CF6, // Purple
            0xFFEAB308, // Yellow
            0xFFEF4444, // Red
            0xFF10B981, // Emerald Green
            0xFF14B8A6, // Teal
            0xFF06B6D4, // Cyan
            0xFF3B82F6, // Indigo Blue
            0xFF6366F1, // Indigo
            0xFFA855F7, // Lavender
            0xFFD946EF, // Magenta
            0xFFF43F5E, // Rose
            0xFF84CC16, // Lime
            0xFF64748B  // Slate
        )

        private val customCache = java.util.concurrent.ConcurrentHashMap<String, CategoryModel>()

        fun updateCustomCategories(categories: List<CategoryModel>) {
            customCache.clear()
            for (cat in categories) {
                customCache[cat.name.lowercase()] = cat
                customCache[cat.displayName.lowercase()] = cat
            }
        }

        fun iconForName(iconName: String): ImageVector {
            return AVAILABLE_ICONS.firstOrNull { it.first.equals(iconName, ignoreCase = true) }?.second
                ?: Icons.Filled.Category
        }

        fun fromCustomEntity(entity: CustomCategoryEntity): CategoryModel {
            return CategoryModel(
                name = entity.name,
                displayName = entity.name,
                color = Color(entity.colorHex),
                icon = iconForName(entity.iconName),
                isBuiltIn = false,
                iconName = entity.iconName
            )
        }

        fun fromBuiltIn(category: ExpenseCategory): CategoryModel {
            return CategoryModel(
                name = category.name,
                displayName = category.displayName,
                color = category.color,
                icon = category.icon,
                isBuiltIn = true
            )
        }

        fun getAllBuiltIn(): List<CategoryModel> {
            return ExpenseCategory.entries.map { fromBuiltIn(it) }
        }

        fun resolve(name: String?, customCategories: List<CategoryModel> = emptyList()): CategoryModel {
            if (name.isNullOrBlank()) return fromBuiltIn(ExpenseCategory.OTHERS)

            // Check built-in first
            val builtIn = ExpenseCategory.entries.firstOrNull {
                it.name.equals(name, ignoreCase = true) || it.displayName.equals(name, ignoreCase = true)
            }
            if (builtIn != null) return fromBuiltIn(builtIn)

            // Check passed custom
            val custom = customCategories.firstOrNull {
                it.name.equals(name, ignoreCase = true)
            }
            if (custom != null) return custom

            // Check cached custom
            val cached = customCache[name.lowercase()]
            if (cached != null) return cached

            // Fallback: create dynamic category with slate color
            return CategoryModel(
                name = name,
                displayName = name,
                color = Color(0xFF64748B),
                icon = Icons.Filled.Category,
                isBuiltIn = false
            )
        }
    }
}

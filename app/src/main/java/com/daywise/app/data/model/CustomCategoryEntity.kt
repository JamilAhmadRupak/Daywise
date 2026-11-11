package com.daywise.app.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "custom_categories")
data class CustomCategoryEntity(
    @PrimaryKey
    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "color_hex")
    val colorHex: Long,

    @ColumnInfo(name = "icon_name")
    val iconName: String = "category",

    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis()
)


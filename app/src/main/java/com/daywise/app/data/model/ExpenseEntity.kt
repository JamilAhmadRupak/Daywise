package com.daywise.app.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

/**
 * Main expense record stored permanently in Room database.
 * Amount is stored as 64-bit integer paisa (100 paisa = 1 BDT ৳) to eliminate floating point issues.
 */
@Serializable
@Entity(
    tableName = "expenses",
    indices = [
        Index(value = ["date_millis"]),
        Index(value = ["category"])
    ]
)
data class ExpenseEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,

    @ColumnInfo(name = "amount_paisa")
    val amountPaisa: Long,

    @ColumnInfo(name = "category")
    val category: String,

    @ColumnInfo(name = "date_millis")
    val dateMillis: Long,

    @ColumnInfo(name = "note")
    val note: String = "",

    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis()
)

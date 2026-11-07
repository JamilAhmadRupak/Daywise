package com.daywise.app.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

enum class DebtType {
    LENT,      // You gave money to someone (They owe you / Receivable)
    BORROWED   // You took money from someone (You owe them / Payable)
}

/**
 * Entity for tracking lending and borrowing.
 * dateMillis is optional (nullable Long) per user requirements.
 */
@Serializable
@Entity(
    tableName = "debts",
    indices = [
        Index(value = ["person_name"]),
        Index(value = ["type"]),
        Index(value = ["is_settled"])
    ]
)
data class DebtEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,

    @ColumnInfo(name = "person_name")
    val personName: String,

    @ColumnInfo(name = "amount_paisa")
    val amountPaisa: Long,

    @ColumnInfo(name = "type")
    val type: String, // DebtType.LENT.name or DebtType.BORROWED.name

    @ColumnInfo(name = "date_millis")
    val dateMillis: Long? = null, // Optional date of the loan/borrowing

    @ColumnInfo(name = "due_date_millis")
    val dueDateMillis: Long? = null, // Optional due / repayment date

    @ColumnInfo(name = "note")
    val note: String = "",

    @ColumnInfo(name = "is_settled")
    val isSettled: Boolean = false,

    @ColumnInfo(name = "settled_at")
    val settledAt: Long? = null,

    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis()
)

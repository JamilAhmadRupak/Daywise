package com.daywise.app

import com.daywise.app.data.model.ExpenseEntity
import com.daywise.app.domain.util.BackupHelper
import org.junit.Assert.assertEquals
import org.junit.Test

class BackupDeduplicationTest {

    @Test
    fun testFindNonDuplicateExpenses() {
        val existing = listOf(
            ExpenseEntity(
                id = 1L,
                amountPaisa = 25000L,
                category = "Food",
                dateMillis = 1700000000000L,
                note = "Lunch"
            ),
            ExpenseEntity(
                id = 2L,
                amountPaisa = 5000L,
                category = "Transport",
                dateMillis = 1700000000000L,
                note = "Bus"
            )
        )

        val incoming = listOf(
            ExpenseEntity(
                id = 99L,
                amountPaisa = 25000L,
                category = "Food",
                dateMillis = 1700000000000L,
                note = "Lunch" // Duplicate
            ),
            ExpenseEntity(
                id = 100L,
                amountPaisa = 12000L,
                category = "Shopping",
                dateMillis = 1700000000000L,
                note = "Book" // New
            )
        )

        val nonDuplicates = BackupHelper.findNonDuplicateExpenses(existing, incoming)

        assertEquals(1, nonDuplicates.size)
        assertEquals(12000L, nonDuplicates[0].amountPaisa)
        assertEquals("Shopping", nonDuplicates[0].category)
        assertEquals(0L, nonDuplicates[0].id) // ID must be reset to 0 for Room insertion
    }
}

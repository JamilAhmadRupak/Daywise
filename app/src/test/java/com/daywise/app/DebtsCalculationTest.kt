package com.daywise.app

import com.daywise.app.data.model.DebtEntity
import com.daywise.app.data.model.DebtType
import com.daywise.app.domain.util.BackupHelper
import org.junit.Assert.assertEquals
import org.junit.Test

class DebtsCalculationTest {

    @Test
    fun testNetBalanceCalculation() {
        val lentTotal = 500000L // ৳5,000 lent out
        val borrowedTotal = 200000L // ৳2,000 borrowed
        val netBalance = lentTotal - borrowedTotal
        assertEquals(300000L, netBalance) // Net +৳3,000
    }

    @Test
    fun testDebtOptionalDateDeduplication() {
        val existing = listOf(
            DebtEntity(
                id = 1L,
                personName = "Tanvir",
                amountPaisa = 100000L,
                type = DebtType.LENT.name,
                dateMillis = null, // No date
                note = "Lunch split"
            )
        )

        val incoming = listOf(
            DebtEntity(
                id = 99L,
                personName = "Tanvir",
                amountPaisa = 100000L,
                type = DebtType.LENT.name,
                dateMillis = null,
                note = "Lunch split" // Duplicate
            ),
            DebtEntity(
                id = 101L,
                personName = "Rahim",
                amountPaisa = 50000L,
                type = DebtType.BORROWED.name,
                dateMillis = 1700000000000L,
                note = "Rickshaw" // New
            )
        )

        val nonDuplicates = BackupHelper.findNonDuplicateDebts(existing, incoming)

        assertEquals(1, nonDuplicates.size)
        assertEquals("Rahim", nonDuplicates[0].personName)
        assertEquals(50000L, nonDuplicates[0].amountPaisa)
        assertEquals(0L, nonDuplicates[0].id)
    }
}

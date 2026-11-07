package com.daywise.app

import com.daywise.app.domain.util.CurrencyFormatter
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class CurrencyFormatterTest {

    @Test
    fun testFormatPaisa_integerTaka() {
        assertEquals("৳1,500", CurrencyFormatter.formatPaisa(150000L))
        assertEquals("৳0", CurrencyFormatter.formatPaisa(0L))
        assertEquals("৳50", CurrencyFormatter.formatPaisa(5000L))
        assertEquals("৳100,000", CurrencyFormatter.formatPaisa(10000000L))
    }

    @Test
    fun testFormatPaisa_fractionalTaka() {
        assertEquals("৳1,500.50", CurrencyFormatter.formatPaisa(150050L))
        assertEquals("৳0.25", CurrencyFormatter.formatPaisa(25L))
    }

    @Test
    fun testParseAmountToPaisa() {
        assertEquals(50000L, CurrencyFormatter.parseAmountToPaisa("500"))
        assertEquals(50050L, CurrencyFormatter.parseAmountToPaisa("500.50"))
        assertEquals(50050L, CurrencyFormatter.parseAmountToPaisa("500.5"))
        assertEquals(150000L, CurrencyFormatter.parseAmountToPaisa("1,500"))
        assertEquals(150000L, CurrencyFormatter.parseAmountToPaisa("৳ 1,500"))
        assertNull(CurrencyFormatter.parseAmountToPaisa(""))
        assertNull(CurrencyFormatter.parseAmountToPaisa("abc"))
        assertNull(CurrencyFormatter.parseAmountToPaisa("-100"))
    }

    @Test
    fun testPaisaToEditableString() {
        assertEquals("500", CurrencyFormatter.paisaToEditableString(50000L))
        assertEquals("500.50", CurrencyFormatter.paisaToEditableString(50050L))
    }
}

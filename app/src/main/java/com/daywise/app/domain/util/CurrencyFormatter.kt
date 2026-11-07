package com.daywise.app.domain.util

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

/**
 * Monetary formatting for Bangladeshi Taka (৳).
 * All money is stored as Long paisa (100 paisa = 1 BDT).
 */
object CurrencyFormatter {

    private val symbols = DecimalFormatSymbols(Locale.US).apply {
        groupingSeparator = ','
        decimalSeparator = '.'
    }

    private val integerFormatter = DecimalFormat("#,##0", symbols)
    private val decimalFormatter = DecimalFormat("#,##0.00", symbols)

    /**
     * Formats paisa to standard Taka representation (e.g. 150000 -> "৳1,500" or "৳1,500.50").
     */
    fun formatPaisa(paisa: Long, includeSymbol: Boolean = true): String {
        val symbolPrefix = if (includeSymbol) "৳" else ""
        val isNegative = paisa < 0
        val absPaisa = Math.abs(paisa)
        val taka = absPaisa / 100
        val remainingPaisa = absPaisa % 100

        val formattedNumber = if (remainingPaisa == 0L) {
            integerFormatter.format(taka)
        } else {
            decimalFormatter.format(absPaisa / 100.0)
        }

        return if (isNegative) "-$symbolPrefix$formattedNumber" else "$symbolPrefix$formattedNumber"
    }

    /**
     * Formats paisa with 2 decimal places guaranteed (e.g. 150000 -> "৳1,500.00").
     */
    fun formatPaisaExact(paisa: Long, includeSymbol: Boolean = true): String {
        val symbolPrefix = if (includeSymbol) "৳" else ""
        val isNegative = paisa < 0
        val absPaisa = Math.abs(paisa)
        val formattedNumber = decimalFormatter.format(absPaisa / 100.0)
        return if (isNegative) "-$symbolPrefix$formattedNumber" else "$symbolPrefix$formattedNumber"
    }

    /**
     * Parses a user input string (e.g., "500", "500.50", " 1,200 ") to Long paisa.
     * Returns null if the string is empty or invalid.
     */
    fun parseAmountToPaisa(input: String): Long? {
        val cleaned = input.trim().replace(",", "").replace("৳", "").trim()
        if (cleaned.isEmpty()) return null

        return try {
            val parts = cleaned.split(".")
            if (parts.size > 2) return null

            val takaPart = parts[0].toLongOrNull() ?: return null
            if (takaPart < 0) return null

            val paisaPart = if (parts.size == 2) {
                val frac = parts[1]
                when {
                    frac.length == 1 -> frac.toLong() * 10
                    frac.length == 2 -> frac.toLong()
                    frac.length > 2 -> frac.substring(0, 2).toLong() // truncate to 2 decimals
                    else -> 0L
                }
            } else {
                0L
            }

            (takaPart * 100) + paisaPart
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Converts Long paisa to editable input text (e.g., 50050 -> "500.50", 50000 -> "500").
     */
    fun paisaToEditableString(paisa: Long): String {
        val taka = paisa / 100
        val remainingPaisa = paisa % 100
        return if (remainingPaisa == 0L) {
            taka.toString()
        } else {
            String.format(Locale.US, "%.2f", paisa / 100.0)
        }
    }
}

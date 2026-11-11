package com.daywise.app.domain.util

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale
import net.objecthunter.exp4j.ExpressionBuilder

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
     * Parses a user input string (e.g., "500", "500.50", " 100+50-20*2 ", "100+50-20×2") to Long paisa.
     * Returns null if the string is empty or invalid.
     */
    fun parseAmountToPaisa(input: String): Long? {
        val cleaned = input.trim()
            .replace(",", "")
            .replace("৳", "")
            .replace("×", "*")
            .replace("÷", "/")
            .trim()
        if (cleaned.isEmpty()) return null

        return try {
            val expression = ExpressionBuilder(cleaned).build()
            val result = expression.evaluate()

            if (result < 0 || result.isInfinite() || result.isNaN()) return null

            // Round to 2 decimal places and multiply by 100 to get paisa
            val paisa = Math.round(result * 100.0)
            paisa
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Evaluates math expressions on the fly and returns a formatted preview string (e.g. "= ৳110" or "= ৳110.50").
     * Returns null if input has no math operators or if expression is incomplete/invalid.
     */
    fun evaluateExpressionPreview(input: String): String? {
        val hasOperator = input.any { it in "+-*/×÷" }
        if (!hasOperator) return null

        val paisa = parseAmountToPaisa(input) ?: return null
        return "= " + formatPaisa(paisa)
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

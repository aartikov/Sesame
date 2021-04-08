package me.aartikov.sesame.form.control

/**
 * Allows to filter entered symbols.
 */
fun interface SymbolFilter {

    fun isSymbolAllowed(symbol: Char): Boolean
}
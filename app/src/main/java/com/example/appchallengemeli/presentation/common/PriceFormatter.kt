package com.example.appchallengemeli.presentation.common

import java.text.NumberFormat
import java.util.Locale

fun formatPrice(price: Double, currencyId: String): String {
    val locale = when (currencyId) {
        "ARS" -> Locale("es", "AR")
        "BRL" -> Locale("pt", "BR")
        "USD" -> Locale.US
        else -> Locale.getDefault()
    }
    val formatter = NumberFormat.getCurrencyInstance(locale)
    return formatter.format(price)
}

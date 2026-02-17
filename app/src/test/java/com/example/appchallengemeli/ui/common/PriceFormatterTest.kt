package com.example.appchallengemeli.ui.common

import org.junit.Assert.assertTrue
import org.junit.Test

class PriceFormatterTest {

    @Test
    fun `formatPrice with ARS formats correctly`() {
        val result = formatPrice(1500.0, "ARS")
        assertTrue("Expected ARS format but got: $result", result.contains("1.500") || result.contains("1,500"))
    }

    @Test
    fun `formatPrice with USD formats correctly`() {
        val result = formatPrice(99.99, "USD")
        assertTrue("Expected USD format but got: $result", result.contains("99.99") || result.contains("99,99"))
    }

    @Test
    fun `formatPrice with zero price`() {
        val result = formatPrice(0.0, "ARS")
        assertTrue("Expected zero price but got: $result", result.contains("0"))
    }

    @Test
    fun `formatPrice with unknown currency uses default locale`() {
        val result = formatPrice(100.0, "XYZ")
        assertTrue("Expected some formatted price but got: $result", result.isNotBlank())
    }

    @Test
    fun `formatPrice with BRL formats correctly`() {
        val result = formatPrice(250.50, "BRL")
        assertTrue("Expected BRL format but got: $result", result.contains("250"))
    }

    @Test
    fun `formatPrice with large number formats correctly`() {
        val result = formatPrice(1000000.0, "ARS")
        assertTrue("Expected formatted large number but got: $result", result.contains("1.000.000") || result.contains("1,000,000"))
    }
}

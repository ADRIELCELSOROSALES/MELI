package com.example.appchallengemeli.data.remote.mapper

import com.example.appchallengemeli.data.remote.dto.AttributeDto
import com.example.appchallengemeli.data.remote.dto.ItemDescriptionDto
import com.example.appchallengemeli.data.remote.dto.ItemDetailDto
import com.example.appchallengemeli.data.remote.dto.PagingDto
import com.example.appchallengemeli.data.remote.dto.PictureDto
import com.example.appchallengemeli.data.remote.dto.ProductDto
import com.example.appchallengemeli.data.remote.dto.SearchResponseDto
import com.example.appchallengemeli.data.remote.dto.ShippingDto
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ProductMapperTest {

    @Test
    fun `ProductDto toDomain maps all fields correctly`() {
        val dto = ProductDto(
            id = "MLA123",
            title = "iPhone 15",
            price = 999.99,
            currencyId = "ARS",
            thumbnail = "http://example.com/image.jpg",
            condition = "new",
            shipping = ShippingDto(freeShipping = true),
            availableQuantity = 5
        )

        val product = dto.toDomain()

        assertEquals("MLA123", product.id)
        assertEquals("iPhone 15", product.title)
        assertEquals(999.99, product.price, 0.01)
        assertEquals("ARS", product.currencyId)
        assertEquals("https://example.com/image.jpg", product.thumbnailUrl)
        assertEquals("new", product.condition)
        assertTrue(product.freeShipping)
        assertEquals(5, product.availableQuantity)
    }

    @Test
    fun `ProductDto toDomain handles null fields with defaults`() {
        val dto = ProductDto(
            id = "MLA123",
            title = "Test",
            price = null,
            currencyId = null,
            thumbnail = null,
            condition = null,
            shipping = null,
            availableQuantity = null
        )

        val product = dto.toDomain()

        assertEquals(0.0, product.price, 0.01)
        assertEquals("", product.currencyId)
        assertEquals("", product.thumbnailUrl)
        assertEquals("", product.condition)
        assertFalse(product.freeShipping)
        assertEquals(0, product.availableQuantity)
    }

    @Test
    fun `SearchResponseDto toDomain maps paging and products`() {
        val dto = SearchResponseDto(
            query = "celular",
            results = listOf(
                ProductDto("MLA1", "Product 1", 100.0, "ARS", null, "new", null, 1),
                ProductDto("MLA2", "Product 2", 200.0, "ARS", null, "used", null, 2)
            ),
            paging = PagingDto(total = 100, primaryResults = 2, offset = 0, limit = 20)
        )

        val result = dto.toDomain()

        assertEquals("celular", result.query)
        assertEquals(100, result.totalResults)
        assertEquals(2, result.products.size)
        assertEquals(0, result.offset)
        assertEquals(20, result.limit)
    }

    @Test
    fun `ItemDetailDto toDomain maps detail with description`() {
        val dto = ItemDetailDto(
            id = "MLA123",
            title = "iPhone 15",
            price = 999.99,
            currencyId = "ARS",
            pictures = listOf(PictureDto("1", "https://img.com/1.jpg")),
            condition = "new",
            soldQuantity = 50,
            availableQuantity = 10,
            shipping = ShippingDto(freeShipping = true),
            warranty = "12 meses",
            attributes = listOf(AttributeDto("Color", "Negro"))
        )

        val detail = dto.toDomain("Descripción del producto")

        assertEquals("MLA123", detail.id)
        assertEquals("iPhone 15", detail.title)
        assertEquals(1, detail.pictures.size)
        assertEquals("https://img.com/1.jpg", detail.pictures[0])
        assertEquals(50, detail.soldQuantity)
        assertEquals("12 meses", detail.warranty)
        assertEquals(1, detail.attributes.size)
        assertEquals("Color", detail.attributes[0].name)
        assertEquals("Negro", detail.attributes[0].value)
        assertEquals("Descripción del producto", detail.description)
    }

    @Test
    fun `ItemDescriptionDto toDomain returns plain text`() {
        val dto = ItemDescriptionDto(plainText = "Una descripción")
        assertEquals("Una descripción", dto.toDomain())
    }

    @Test
    fun `ItemDescriptionDto toDomain handles null`() {
        val dto = ItemDescriptionDto(plainText = null)
        assertEquals("", dto.toDomain())
    }

    @Test
    fun `ProductDto toDomain converts http to https in thumbnail`() {
        val dto = ProductDto(
            id = "MLA1",
            title = "Test",
            price = 100.0,
            currencyId = "ARS",
            thumbnail = "http://http2.mlstatic.com/image.jpg",
            condition = "new",
            shipping = null,
            availableQuantity = 1
        )

        val product = dto.toDomain()
        assertTrue(product.thumbnailUrl.startsWith("https://"))
    }
}

package org.jesperancinha.narwhals.rest

import org.jesperancinha.narwhals.Narwhal
import org.jesperancinha.narwhals.dao.CustomerOrder
import org.jesperancinha.narwhals.dao.Order
import org.jesperancinha.narwhals.dao.OrderResponse
import org.jesperancinha.narwhals.dao.NarwhalsWebShopDao
import io.kotest.common.runBlocking
import io.kotest.matchers.maps.shouldHaveSize
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.*
import org.springframework.http.HttpMethod.POST
import org.springframework.http.HttpStatus.*
import org.springframework.http.MediaType.*
import java.math.BigDecimal

@SpringBootTest(webEnvironment = RANDOM_PORT)
class NarwhalsShopControllerTest @Autowired constructor(
    val narwhalsWebShopDao: NarwhalsWebShopDao,
    val testRestTemplate: TestRestTemplate,
) {
    val xmlHeaders = HttpHeaders().apply {
        add("Content-Type", APPLICATION_XML_VALUE)
    }
    val jsonHeaders = HttpHeaders().apply {
        add("Content-Type", APPLICATION_JSON_VALUE)
    }

    @Test
    fun `should load narwhals`() {
        narwhalsWebShopDao.mapNarwhals()["narwhals"]
            .shouldBeNull()

        run {
            loadNarwhals1()
            narwhalsWebShopDao.mapNarwhals()
                .shouldNotBeNull()
                .shouldAssertBetties(1, 2, 3)
        }

        run {
            val entity = HttpEntity(
                javaClass.getResource("/narwhals2.xml")
                    .shouldNotBeNull().readText(), xmlHeaders
            )
            testRestTemplate.exchange("/rnarwhals-shop/load", POST, entity, String::class.java)
                .shouldNotBeNull()
                .statusCode shouldBe RESET_CONTENT
            narwhalsWebShopDao.mapNarwhals()
                .shouldNotBeNull()
                .shouldAssertBetties(4, 5, 6)
                .shouldHaveSize(3)
        }
    }

    @Test
    fun `should make full purchase when stocks are available`() {
        loadNarwhals1()
        makeCustomerRequest1()
            .shouldNotBeNull()
            .apply {
                body shouldBe OrderResponse(
                    seaCabbage = BigDecimal("1100.0"),
                    tusks = 3,
                    cabbagesAvailableInDays = 0,
                    tusksAvailableInDays = 0
                )
                statusCode shouldBe OK
            }
    }

    @Test
    fun `should make multiple purchase request but only one succeeds when stocks are available`(): Unit = runBlocking {
        withContext(Dispatchers.Default) {
            loadNarwhals1()
            (0..1000).map {
                async {
                    makeCustomerRequest1()
                }
            }.awaitAll().count { it.statusCode == OK } shouldBe 1
        }

    }

    @Test
    fun `should make partial purchase when stocks are not available`() {
        loadNarwhals1()
        val entity = HttpEntity(
            CustomerOrder(
                customer = "PinkOgre",
                order = Order(
                    seaCabbage = 1200.toBigDecimal(),
                    tusks = 3
                )
            ), jsonHeaders
        )
        testRestTemplate.exchange("/rnarwhals-shop/order/14", POST, entity, OrderResponse::class.java)
            .shouldNotBeNull()
            .apply {
                body shouldBe OrderResponse(
                    seaCabbage = BigDecimal("0.0"),
                    tusks = 3,
                    cabbagesAvailableInDays = 2,
                    tusksAvailableInDays = 0
                )
                statusCode shouldBe PARTIAL_CONTENT
            }
    }

    @Test
    fun `should make no purchase when stocks are not available and show predictions`() {
        loadNarwhals1()
        val entity = HttpEntity(
            CustomerOrder(
                customer = "PinkOgre",
                order = Order(
                    seaCabbage = 20.toBigDecimal(),
                    tusks = 1
                )
            ), jsonHeaders
        )
        testRestTemplate.exchange("/rnarwhals-shop/order/1", POST, entity, OrderResponse::class.java)
            .shouldNotBeNull()
            .apply {
                body shouldBe OrderResponse(
                    seaCabbage = BigDecimal("0.0"),
                    tusks = 0,
                    cabbagesAvailableInDays = 1,
                    tusksAvailableInDays = 1
                )
                statusCode shouldBe NOT_FOUND
            }
    }

    @Test
    fun `should return no expectation failed when nothing is available`() {
        loadNarwhals1()
        val entity = HttpEntity(
            CustomerOrder(
                customer = "PinkOgre",
                order = Order(
                    seaCabbage = 4000.toBigDecimal(),
                    tusks = 50
                )
            ), jsonHeaders
        )
        testRestTemplate.exchange("/rnarwhals-shop/order/1", POST, entity, OrderResponse::class.java)
            .shouldNotBeNull()
            .apply {
                body shouldBe OrderResponse(
                    seaCabbage = BigDecimal("0.0"),
                    tusks = 0,
                    cabbagesAvailableInDays = 48,
                    tusksAvailableInDays = 485
                )
                statusCode shouldBe NOT_FOUND
            }
    }

    fun makeCustomerRequest1(): ResponseEntity<OrderResponse> = HttpEntity(
        CustomerOrder(
            customer = "PinkOgre",
            order = Order(
                seaCabbage = 1100.toBigDecimal(),
                tusks = 3
            )
        ), jsonHeaders
    ).let { testRestTemplate.exchange("/rnarwhals-shop/order/14", POST, it, OrderResponse::class.java) }

    private fun loadNarwhals1() {
        val entity = HttpEntity(
            javaClass.getResource("/narwhals1.xml")
                .shouldNotBeNull().readText(), xmlHeaders
        )
        testRestTemplate.exchange("/rnarwhals-shop/load", POST, entity, String::class.java)
            .shouldNotBeNull()
            .statusCode shouldBe RESET_CONTENT
    }
}

private fun MutableMap<String, Narwhal>.shouldAssertBetties(one: Int, two: Int, three: Int) = apply {
    get("SonicDJ-$one")
        .shouldNotBeNull().apply {
            name shouldBe "SonicDJ-$one"
            age shouldBe BigDecimal.valueOf(4)
            sex shouldBe "f"
        }
    get("SonicDJ-$two")
        .shouldNotBeNull().apply {
            name shouldBe "SonicDJ-$two"
            age shouldBe BigDecimal.valueOf(8)
            sex shouldBe "f"
        }
    get("SonicDJ-$three")
        .shouldNotBeNull().apply {
            name shouldBe "SonicDJ-$three"
            age shouldBe BigDecimal("9.5")
            sex shouldBe "f"
        }
}

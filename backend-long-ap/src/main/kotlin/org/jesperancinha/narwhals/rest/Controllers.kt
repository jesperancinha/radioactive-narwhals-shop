package org.jesperancinha.narwhals.rest

import org.jesperancinha.narwhals.anti.pattern.CurrentNarwhals
import org.jesperancinha.narwhals.anti.pattern.XmlNarwhals
import org.jesperancinha.narwhals.dao.CustomerOrder
import org.jesperancinha.narwhals.dao.NarwhalsWebShopDao
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/")
internal class NarwhalsShopController(
    @Autowired
    val narwhalsWebShopDao: NarwhalsWebShopDao,
) {
    @PostMapping(
        value = ["/load"],
        consumes = [MediaType.APPLICATION_XML_VALUE]
    )
    suspend fun loadNarwhals(@RequestBody narwhals: XmlNarwhals): ResponseEntity<String> =
        narwhalsWebShopDao.loadWebShop(narwhals)
            .run { ResponseEntity.status(HttpStatus.RESET_CONTENT).build() }

    @GetMapping(
        value = ["/stock/{days}"],
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    suspend fun getStocks(@PathVariable days: Long) = narwhalsWebShopDao.findStocks(days)

    @GetMapping(
        value = ["/narwhals/{days}"],
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    suspend fun getNarwhalss(@PathVariable days: Long): CurrentNarwhals = narwhalsWebShopDao.findNarwhals(days)

    @PostMapping(
        value = ["/order/{days}"],
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    suspend fun orders(@RequestBody customerOrder: CustomerOrder, @PathVariable days: Long) =
        narwhalsWebShopDao.order(customerOrder, days - 1)

}
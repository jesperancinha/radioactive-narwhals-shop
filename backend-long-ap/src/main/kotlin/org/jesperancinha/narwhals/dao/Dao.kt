package org.jesperancinha.narwhals.dao

import com.hazelcast.core.HazelcastInstance
import org.jesperancinha.narwhals.NarwhalInterface
import org.jesperancinha.narwhals.NarwhalsInterface
import org.jesperancinha.narwhals.anti.pattern.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus.*
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.status
import org.springframework.stereotype.Service
import java.math.RoundingMode

private const val SALES_MAP = "salesMap"

typealias SeaCabbagesQuantity = Long
typealias TusksQuantity = Long

@Service
class NarwhalsWebShopDao(
    @Autowired
    private val hazelcastNarwhalsInstance: HazelcastInstance,
) {

    fun mapNarwhals(): MutableMap<String, Narwhal> = hazelcastNarwhalsInstance.getMap("narwhals")

    fun mapSales(): MutableMap<String, SoldItems> = hazelcastNarwhalsInstance.getMap("sales")

    fun loadWebShop(narwhals: NarwhalsInterface<NarwhalInterface<Long>>) = mapNarwhals().clear().run {
        mapSales().apply {
            clear()
            put(SALES_MAP, SoldItems())
        }

        narwhals.toDomain().narwhal.forEach { narwhal ->
            mapNarwhals().apply { put(narwhal.name, narwhal) }
        }
    }

    fun findStocks(days: Long) = Narwhals(narwhal = mapNarwhals().map { it.value }).toCurrentStock(days)

    fun findNarwhals(days: Long) = Narwhals(narwhal = mapNarwhals().map { it.value }).toCurrentNarwhals(days)

    fun areNarwhalsActive(days: Long) =
        Narwhals(narwhal = mapNarwhals().map { it.value }).toOutput(days).narwhals.narwhal.any {
            it.age < NARWHAL_YEARS_TO_LIVE
        }

    @Synchronized
    fun order(customerOrder: CustomerOrder, days: Long): ResponseEntity<OrderResponse> =
        mapSales().let { mapSalesPerYear ->
            requireNotNull(mapSalesPerYear[SALES_MAP])
                .let { sales: SoldItems ->
                    findStocks(days)
                        .let { producedStock ->
                            EffectiveStock(
                                seaCabbage = producedStock.seaCabbage - sales.seaCabbage,
                                tusks = producedStock.tusks - sales.tusks
                            )
                        }
                        .let { effectiveStock ->
                            val orderedCabbage = customerOrder.order.seaCabbage
                            val orderedTusks = customerOrder.order.tusks
                            val hasCabbage = effectiveStock.hasCabbages(orderedCabbage)
                            val hasTusks = effectiveStock.hasTusks(orderedTusks)
                            val newSales = SoldItems(
                                seaCabbage = sales.seaCabbage + (if (hasCabbage) orderedCabbage else 0),
                                tusks = sales.tusks + if (hasTusks) orderedTusks else 0,
                            )
                            mapSalesPerYear.apply {
                                put(SALES_MAP, newSales)
                            }
                            OrderResponse(
                                seaCabbage = (if (hasCabbage) orderedCabbage else 0).toBigDecimal().setScale(
                                    1,
                                    RoundingMode.FLOOR
                                ),
                                tusks = if (hasTusks) orderedTusks else 0,
                            ).let {
                                when {
                                    !hasCabbage && !hasTusks -> status(NOT_FOUND).body(
                                        it.fillPredictions(
                                            order = customerOrder.order,
                                            newSales = newSales,
                                            days = days
                                        )
                                    )

                                    !hasCabbage || !hasTusks ->
                                        status(PARTIAL_CONTENT).body(
                                            it.fillPredictions(
                                                order = customerOrder.order,
                                                newSales = newSales,
                                                days = days,
                                                hasCabbage = hasCabbage,
                                                hasTusks = hasTusks
                                            )
                                        )

                                    else -> status(OK).body(it)
                                }
                            }
                        }
                }
        }

    private fun OrderResponse.fillPredictions(
        order: Order,
        newSales: SoldItems,
        days: Long,
        hasCabbage: Boolean = false,
        hasTusks: Boolean = false,
    ) = this.copy(
        cabbagesAvailableInDays = if (!hasCabbage) order.seaCabbage.availableCabbageFromSalesInDays(
            newSales,
            days
        ) else 0,
        tusksAvailableInDays = if (!hasTusks) order.tusks.availableTusksFromSalesInDays(newSales, days) else 0
    )

    private fun SeaCabbagesQuantity.availableCabbageFromSalesInDays(newSales: SoldItems, days: Long): Int =
        generateSequence(days to findStocks(days).seaCabbage) { (d, _) ->
            val seaCabbage = findStocks(d + 1).seaCabbage
            when {
                d == -1L -> -2L to (this + (newSales.seaCabbage))
                !areNarwhalsActive(d) -> -1L to (this + newSales.seaCabbage)
                else -> d + 1 to seaCabbage
            }
        }.takeWhile { (d, seaCabbage) ->
            this > (seaCabbage - (newSales.seaCabbage)) || d == -1L
        }
            .toList()
            .let {
                if (it.firstOrNull { (d, _) -> d == -1L } != null) emptyList() else it
            }
            .count()

    private fun TusksQuantity.availableTusksFromSalesInDays(newSales: SoldItems, days: Long): Int =
        generateSequence(days to findStocks(days).tusks) { (d, _) ->
            val tusks = findStocks(d + 1).tusks
            when {
                d == -1L -> -2L to (this + newSales.tusks)
                !areNarwhalsActive(d) -> -1L to (this + newSales.tusks)
                else -> d + 1L to tusks
            }
        }.takeWhile { (d, tusks) ->
            this > (tusks - newSales.tusks) || d == -1L
        }
            .toList()
            .let {
                if (it.firstOrNull { (d, _) -> d == -1L } != null) emptyList() else it
            }
            .count()
}


private fun EffectiveStock.hasCabbages(orderedCabbage: Long) = orderedCabbage <= this.seaCabbage
private fun EffectiveStock.hasTusks(orderedTusks: Long) = orderedTusks <= this.tusks

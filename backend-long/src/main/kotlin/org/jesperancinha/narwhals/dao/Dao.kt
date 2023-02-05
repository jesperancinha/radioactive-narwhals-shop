package org.jesperancinha.narwhals.dao

import com.hazelcast.core.HazelcastInstance
import org.jesperancinha.narwhals.*
import org.jesperancinha.narwhals.safe.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus.*
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.status
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.math.BigDecimal.ZERO
import java.math.RoundingMode

private const val SALES_MAP = "salesMap"

@Service
class NarwhalsWebShopDao(
    @Autowired
    private val hazelcastNarwhalsInstance: HazelcastInstance,
) {

    fun mapNarwhals(): MutableMap<String, org.jesperancinha.narwhals.safe.Narwhal> = hazelcastNarwhalsInstance.getMap("narwhals")

    fun mapSales(): MutableMap<String, SoldItems> = hazelcastNarwhalsInstance.getMap("sales")

    fun loadWebShop(narwhals: NarwhalsInterface<NarwhalInterface>) = mapNarwhals().clear().run {
        mapSales().apply {
            clear()
            put(SALES_MAP, SoldItems(ZERO, 0))
        }

        narwhals.toDomain().narwhal.forEach { narwhal ->
            mapNarwhals().apply { put(narwhal.name, narwhal) }
        }
    }

    fun findStocks(days: Int) = Narwhals(narwhal = mapNarwhals().map { it.value }).toCurrentStock(days)

    fun findNarwhals(days: Int) = Narwhals(narwhal = mapNarwhals().map { it.value }).toCurrentNarwhals(days)

    fun areNarwhalsActive(days: Int) =
        Narwhals(narwhal = mapNarwhals().map { it.value }).toOutput(days).narwhals.narwhal.any {
            it.age < NARWHAL_YEARS_TO_LIVE
        }

    @Synchronized
    fun order(customerOrder: CustomerOrder, days: Int): ResponseEntity<OrderResponse> =
        mapSales().let { mapSalesPerYear ->
            requireNotNull(mapSalesPerYear[SALES_MAP])
                .let { sales: SoldItems ->
                    findStocks(days)
                        .let { producedStock ->
                            EffectiveStock(
                                seaCabbage = producedStock.seaCabbage.subtract(sales.seaCabbage),
                                tusks = producedStock.tusks - sales.tusks
                            )
                        }
                        .let { effectiveStock ->
                            val orderedCabbage = customerOrder.order.seaCabbage
                            val orderedTusks = customerOrder.order.tusks
                            val hasCabbage = effectiveStock.hasCabbages(orderedCabbage)
                            val hasTusks = effectiveStock.hasTusks(orderedTusks)
                            val newSales = SoldItems(
                                seaCabbage = sales.seaCabbage.add(if (hasCabbage) orderedCabbage else 0.toBigDecimal()),
                                tusks = sales.tusks + if (hasTusks) orderedTusks else 0,
                            )
                            mapSalesPerYear.apply {
                                put(SALES_MAP, newSales)
                            }
                            OrderResponse(
                                seaCabbage = (if (hasCabbage) orderedCabbage else 0.toBigDecimal()).setScale(
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
        days: Int,
        hasCabbage: Boolean = false,
        hasTusks: Boolean = false,
    ) = this.copy(
        cabbagesAvailableInDays = if (!hasCabbage) order.seaCabbage.availableCabbageFromSalesInDays(
            newSales,
            days
        ) else 0,
        tusksAvailableInDays = if (!hasTusks) order.tusks.availableTusksFromSalesInDays(newSales, days) else 0
    )

    private fun BigDecimal.availableCabbageFromSalesInDays(newSales: SoldItems, days: Int): Int =
        generateSequence(days to findStocks(days).seaCabbage) { (d, _) ->
            val seaCabbage = findStocks(d + 1).seaCabbage
            when {
                d == -1 -> -2 to this.add(newSales.seaCabbage)
                !areNarwhalsActive(d) -> -1 to this.add(newSales.seaCabbage)
                else -> d + 1 to seaCabbage
            }
        }.takeWhile { (d, seaCabbage) ->
            this > seaCabbage.subtract(newSales.seaCabbage) || d == -1
        }
            .toList()
            .let {
                if (it.firstOrNull { (d, _) -> d == -1 } != null) emptyList() else it
            }
            .count()

    private fun Int.availableTusksFromSalesInDays(newSales: SoldItems, days: Int): Int =
        generateSequence(days to findStocks(days).tusks) { (d, _) ->
            val tusks = findStocks(d + 1).tusks
            when {
                d == -1 -> -2 to (this + newSales.tusks)
                !areNarwhalsActive(d) -> -1 to (this + newSales.tusks)
                else -> d + 1 to tusks
            }
        }.takeWhile { (d, tusks) ->
            this > (tusks - newSales.tusks) || d == -1
        }
            .toList()
            .let {
                if (it.firstOrNull { (d, _) -> d == -1 } != null) emptyList() else it
            }
            .count()
}


private fun EffectiveStock.hasCabbages(orderedCabbage: BigDecimal) = orderedCabbage <= this.seaCabbage
private fun EffectiveStock.hasTusks(orderedTusks: Int) = orderedTusks <= this.tusks

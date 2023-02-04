package org.jesperancinha.narwhals

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import java.io.File
import java.io.InputStream
import java.math.BigDecimal
import java.math.BigDecimal.*
import java.math.RoundingMode.*
import java.nio.charset.Charset

val NARWHAL_YEAR_DURATION = 100.toBigDecimal()
val NARWHAL_YEARS_TO_LIVE = 10.toBigDecimal()

typealias ElapsedDays = BigDecimal
typealias AgeInYears = BigDecimal
typealias NarwhalsXmlText = String

data class Output(
    val stock: CurrentStock,
    val narwhals: CurrentNarwhals,
)

data class CurrentStock(
    val seaCabbage: BigDecimal,
    val tusks: Int,
)

data class CurrentNarwhals(
    @JsonProperty("narwhals")
    override val narwhal: List<CurrentNarwhal>,
) : NarwhalsInterface<NarwhalInterface>

data class CurrentNarwhal(
    @JsonProperty
    override val age: BigDecimal,
    @JsonProperty
    override val name: String,
    override val sex: String,
    @JsonProperty("age-last-tusk-shed")
    val ageLastTuskShed: BigDecimal,
) : NarwhalInterface


internal val kotlinXmlMapper = XmlMapper(JacksonXmlModule().apply {
    setDefaultUseWrapper(false)
}).registerKotlinModule()
    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)


fun Narwhals.toOutput(elapsedDays: Int): Output {
    return Output(
        stock = toCurrentStock(elapsedDays),
        narwhals = toCurrentNarwhals(elapsedDays)
    )
}

fun Narwhals.toCurrentNarwhals(elapsedDays: Int) = CurrentNarwhals(
    narwhal = this.narwhal.map {
        CurrentNarwhal(
            name = it.name,
            age = it.age + BigDecimal(elapsedDays).divide(NARWHAL_YEAR_DURATION),
            ageLastTuskShed = it.age
                .shavingTable(elapsedDays)
                .maxOfOrNull { (_, age) -> age }
                ?.setScale(1, FLOOR) ?: it.age,
            sex = it.sex
        )
    }
)

fun Narwhals.toCurrentStock(elapsedDays: Int) =
    CurrentStock(
        seaCabbage = narwhal
            .map { it.age }
            .filter { it <= NARWHAL_YEARS_TO_LIVE }
            .sumOf { ageInYears -> ageInYears.seaCabbageForecastInElapsedDays(elapsedDays) },
        tusks = narwhal
            .map { it.age }
            .filter { it <= NARWHAL_YEARS_TO_LIVE }
            .sumOf { ageInYears -> ageInYears.tusksForecastInElapsedDays(elapsedDays) }
    )

private fun AgeInYears.tusksForecastInElapsedDays(elapsedDays: Int) =
    if (elapsedDays == 0) 0 else shavingTable(elapsedDays).count() + 1

private fun AgeInYears.shavingTable(elapsedDays: Int) = elapsedDays.toBigDecimal().shavingSequence(this)

private fun ElapsedDays.seaCabbageForecastInElapsedDays(elapsedDays: Int) = (0 until elapsedDays)
    .filter { days -> plus(days.toBigDecimal().divide(NARWHAL_YEAR_DURATION)) < NARWHAL_YEARS_TO_LIVE }
    .fold(ZERO) { accumulatedCabbages, elapsedDay ->
        accumulatedCabbages.plus((((multiply(NARWHAL_YEAR_DURATION)).plus(elapsedDay.toBigDecimal()))).dailyCabbages())
    }

fun NarwhalsXmlText.parseNarwhals() = kotlinXmlMapper.readValue<Narwhals>(this)

fun File.parseNarwhals() = this.readText(Charset.defaultCharset()).parseNarwhals()

fun InputStream.parseNarwhals() = this.readAllBytes().toString(Charset.defaultCharset()).parseNarwhals()

fun ElapsedDays.shavingSequence(ageYears: BigDecimal) =
    generateSequence(ZERO to ageYears) { (shaveDay, ageYears) ->
        val shaveAfter = ageYears.multiply(NARWHAL_YEAR_DURATION).tusksFall()
        shaveDay.plus(shaveAfter) to ageYears.plus(shaveAfter / NARWHAL_YEAR_DURATION)
    }.takeWhile { (shaveDay, ageYears) ->
        shaveDay < this.subtract(ONE) && ageYears <= NARWHAL_YEARS_TO_LIVE
    }.filter { (shaveDay, _) -> shaveDay != ZERO }.toList()

fun NarwhalsInterface<NarwhalInterface>.toDomain() = Narwhals(
    narwhal = requireNotNull(narwhal?.map {
        Narwhal(
            name = requireNotNull(it.name),
            age = requireNotNull(it.age),
            sex = requireNotNull(it.sex)
        )
    }
    )
)
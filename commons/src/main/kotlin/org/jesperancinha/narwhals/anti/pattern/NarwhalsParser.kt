package org.jesperancinha.narwhals.anti.pattern

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import java.io.File
import java.io.InputStream
import java.math.BigDecimal.*
import java.math.RoundingMode.*
import java.nio.charset.Charset
import kotlin.math.min

val NARWHAL_YEAR_DURATION = 1000
val NARWHAL_YEARS_TO_LIVE = 20
val VANILLA_FACTOR = 1000

typealias ElapsedDays = Long
typealias AgeInYears = Long
typealias NarwhalsXmlText = String

data class Output(
    val stock: CurrentStock,
    val narwhals: CurrentNarwhals,
)

data class CurrentStock(
    val seaCabbage: Long,
    val tusks: Int,
)

data class CurrentNarwhals(
    @JsonProperty("narwhals")
    override val narwhal: List<CurrentNarwhal>,
) : NarwhalsInterface<NarwhalInterface>

data class CurrentNarwhal(
    @JsonProperty
    override val age: Long,
    @JsonProperty
    override val name: String,
    override val sex: String,
    @JsonProperty("age-last-tusk-shed")
    val ageLastTuskShed: Long,
) : NarwhalInterface


internal val kotlinXmlMapper = XmlMapper(JacksonXmlModule().apply {
    setDefaultUseWrapper(false)
}).registerKotlinModule()
    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)


fun Narwhals.toOutput(elapsedDays: Long): Output {
    return Output(
        stock = toCurrentStock(elapsedDays),
        narwhals = toCurrentNarwhals(elapsedDays)
    )
}

fun Narwhals.toCurrentNarwhals(elapsedDays: Long) = CurrentNarwhals(
    narwhal = this.narwhal.map {
        CurrentNarwhal(
            name = it.name,
            age = it.age + (elapsedDays * VANILLA_FACTOR / NARWHAL_YEAR_DURATION),
            ageLastTuskShed = it.age
                .tusksForecastInElapsedDays(elapsedDays)
                .second,
            sex = it.sex
        )
    }
)

fun Narwhals.toCurrentStock(elapsedDays: Long) =
    CurrentStock(
        seaCabbage = narwhal
            .map { it.age }
            .filter { it / VANILLA_FACTOR <= NARWHAL_YEARS_TO_LIVE }
            .sumOf { ageInYears -> ageInYears.seaCabbageForecastInElapsedDays(elapsedDays) },
        tusks = narwhal
            .map { it.age }
            .filter { it / VANILLA_FACTOR <= NARWHAL_YEARS_TO_LIVE }
            .sumOf { ageInYears -> ageInYears.tusksForecastInElapsedDays(elapsedDays).first + 1 }
    )

fun AgeInYears.tusksForecastInElapsedDays(elapsedDays: Long): Pair<Int, AgeInYears> {
    var currentAge = this
    var count = 0
    var tuskShedDay = 0L
    var lastAge = this
    while (tuskShedDay < elapsedDays - 1 && currentAge <= NARWHAL_YEARS_TO_LIVE * VANILLA_FACTOR) {
        val shedAfter = (currentAge * NARWHAL_YEAR_DURATION / VANILLA_FACTOR).tusksFall().toLong() / VANILLA_FACTOR
        tuskShedDay += shedAfter
        lastAge = currentAge
        currentAge += shedAfter * VANILLA_FACTOR / NARWHAL_YEAR_DURATION
        count++
    }
    return (count - 1) to lastAge
}


private fun ElapsedDays.seaCabbageForecastInElapsedDays(elapsedDays: Long) =
    (0 until min(
        (NARWHAL_YEARS_TO_LIVE * VANILLA_FACTOR - this) * NARWHAL_YEAR_DURATION * VANILLA_FACTOR,
        elapsedDays
    ))
        .let {
            var accumulatedCabbages = 0L
            it.forEach { elapsedDay ->
                accumulatedCabbages += ((this * NARWHAL_YEAR_DURATION / VANILLA_FACTOR) + elapsedDay).dailyCabbages()
                    .toLong()
            }
            accumulatedCabbages
        }


fun NarwhalsXmlText.parseNarwhals() = kotlinXmlMapper.readValue<Narwhals>(this)

fun File.parseNarwhals() = this.readText(Charset.defaultCharset()).parseNarwhals()

fun InputStream.parseNarwhals() = this.readAllBytes().toString(Charset.defaultCharset()).parseNarwhals()

fun NarwhalsInterface<NarwhalInterface>.toDomain() =
    Narwhals(
        narwhal = requireNotNull(narwhal?.map {
            Narwhal(
                name = requireNotNull(it.name),
                age = requireNotNull(it.age),
                sex = requireNotNull(it.sex)
            )
        }
        )
    )
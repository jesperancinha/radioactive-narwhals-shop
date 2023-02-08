package org.jesperancinha.narwhals.vanilla

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.jesperancinha.narwhals.NarwhalInterface
import org.jesperancinha.narwhals.NarwhalsInterface
import java.io.File
import java.io.InputStream
import java.math.BigDecimal.*
import java.math.RoundingMode.*
import java.nio.charset.Charset

const val NARWHAL_YEAR_DURATION = 1000
const val NARWHAL_YEARS_TO_LIVE = 20
const val VANILLA_FACTOR = 1000

typealias ElapsedDays = Long
typealias AgeInYears = Long
typealias NarwhalsXmlText = String

data class Output(
    val stock: CurrentStock,
    val narwhals: CurrentNarwhals,
)

data class CurrentStock(
    val seaCabbage: Long,
    val tusks: Long,
)

data class CurrentNarwhals(
    @JsonProperty("narwhals")
    override val narwhal: List<CurrentNarwhal>,
) : NarwhalsInterface<NarwhalInterface<Long>>

data class CurrentNarwhal(
    @JsonProperty
    override val age: Long,
    @JsonProperty
    override val name: String,
    override val sex: String,
    @JsonProperty("age-last-tusk-shed")
    val ageLastTuskShed: Long,
) : NarwhalInterface<Long>


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
                .tuskSheddingTable(elapsedDays)
                .maxOfOrNull { (_, age) -> age } ?: it.age,
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
            .sumOf { ageInYears -> ageInYears.tusksForecastInElapsedDays(elapsedDays) }
    )

private fun AgeInYears.tusksForecastInElapsedDays(elapsedDays: Long) =
    if (elapsedDays == 0L) 0 else tuskSheddingTable(elapsedDays).count() + 1L

private fun AgeInYears.tuskSheddingTable(elapsedDays: Long) = elapsedDays.tuskShedSequence(this)

private fun ElapsedDays.seaCabbageForecastInElapsedDays(elapsedDays: Long) = (0 until elapsedDays)
    .filter { days -> (this + (days / NARWHAL_YEAR_DURATION * VANILLA_FACTOR)) / VANILLA_FACTOR < NARWHAL_YEARS_TO_LIVE }
    .fold(0L) { accumulatedCabbages, elapsedDay ->
        accumulatedCabbages + ((this * NARWHAL_YEAR_DURATION / VANILLA_FACTOR) + elapsedDay).dailyCabbages().toLong()
    }

fun NarwhalsXmlText.parseNarwhals() = kotlinXmlMapper.readValue<Narwhals>(this)

fun File.parseNarwhals() = this.readText(Charset.defaultCharset()).parseNarwhals()

fun InputStream.parseNarwhals() = this.readAllBytes().toString(Charset.defaultCharset()).parseNarwhals()

fun ElapsedDays.tuskShedSequence(ageYears: Long) =
    generateSequence(0L to ageYears) { (tuskShedDay, ageYears) ->
        val shedAfter = (ageYears * NARWHAL_YEAR_DURATION / VANILLA_FACTOR).tusksFall().toLong() / VANILLA_FACTOR
        (tuskShedDay + shedAfter) to (ageYears + (shedAfter * VANILLA_FACTOR / NARWHAL_YEAR_DURATION))
    }.takeWhile { (tuskShedDay, ageYears) ->
        tuskShedDay < this && ageYears <= NARWHAL_YEARS_TO_LIVE * VANILLA_FACTOR
    }.filter { (tuskShedDay, _) ->
        tuskShedDay != 0L
    }.toList()

fun NarwhalsInterface<NarwhalInterface<Long>>.toDomain() =
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
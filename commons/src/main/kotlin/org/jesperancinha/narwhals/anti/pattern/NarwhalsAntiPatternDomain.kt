package org.jesperancinha.narwhals.anti.pattern

import com.fasterxml.jackson.annotation.JsonAlias
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonRootName
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import jakarta.xml.bind.annotation.*
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter
import org.jesperancinha.narwhals.*

@JsonRootName("narwhals")
data class Narwhals(
    @JsonAlias("narwhal")
    override val narwhal: List<Narwhal>,
) : NarwhalsInterface<Narwhal>

@JsonRootName("narwhal")
data class Narwhal(
    @JsonProperty
    override val name: String,
    @JsonProperty
    @JsonDeserialize(using = DecimalToMillisDeserializer::class)
    override val age: Long,
    @JsonProperty
    override val sex: String,
) : NarwhalInterface<Long>

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "XmlNarwhals", propOrder = ["narwhal"])
data class XmlNarwhals(
    override var narwhal: List<XmlNarwhal> = mutableListOf(),
) : NarwhalsInterface<NarwhalInterface<Long>>

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "XmlNarwhal")
data class XmlNarwhal(
    @XmlAttribute(name = "name")
    override var name: String? = null,
    @XmlAttribute(name = "age")
    @field:XmlJavaTypeAdapter(DecimalToMillisAdapter::class)
    override var age: Long? = null,
    @XmlAttribute(name = "sex")
    override var sex: String? = null,
) : NarwhalInterface<Long>

fun ElapsedDays.dailyCabbages() = (1200 - (this * VANILLA_FACTOR * 0.06) / VANILLA_FACTOR) * VANILLA_FACTOR
fun ElapsedDays.tusksFall() = (200 + (this * VANILLA_FACTOR * 0.01) / VANILLA_FACTOR) * VANILLA_FACTOR

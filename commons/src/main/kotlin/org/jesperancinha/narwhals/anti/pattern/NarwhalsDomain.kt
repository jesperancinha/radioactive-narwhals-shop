package org.jesperancinha.narwhals.anti.pattern

import com.fasterxml.jackson.annotation.JsonAlias
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonRootName
import jakarta.xml.bind.annotation.*
import java.math.BigDecimal

interface NarwhalInterface {
    val name: String?
    val age: BigDecimal?
    val sex: String?
}

interface NarwhalsInterface<T> {
    val narwhal: List<T>?
}

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
    override val age: BigDecimal,
    @JsonProperty
    override val sex: String,
) : NarwhalInterface

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "XmlNarwhals", propOrder = ["narwhal"])
data class XmlNarwhals(
    override var narwhal: List<XmlNarwhal>? = null,
) : NarwhalsInterface<NarwhalInterface>

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "XmlNarwhal")
data class XmlNarwhal(
    @XmlAttribute(name = "name")
    override var name: String? = null,
    @XmlAttribute(name = "age")
    override var age: BigDecimal? = null,
    @XmlAttribute(name = "sex")
    override var sex: String? = null,
) : NarwhalInterface

fun ElapsedDays.dailyCabbages(): BigDecimal = BigDecimal(1200).subtract(this.multiply(BigDecimal(0.06)))

fun ElapsedDays.tusksFall(): BigDecimal = BigDecimal(200).add(this.multiply(BigDecimal(0.01)))

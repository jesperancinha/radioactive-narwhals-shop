package org.jesperancinha.narwhals

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import jakarta.xml.bind.annotation.adapters.XmlAdapter
import java.math.BigDecimal

interface NarwhalInterface<T> {
    val name: String?
    val age: T?
    val sex: String?
}

interface NarwhalsInterface<T> {
    val narwhal: List<T>
}

class DecimalToMillisAdapter : XmlAdapter<BigDecimal, Long>() {
    override fun unmarshal(p0: BigDecimal?): Long = p0?.multiply(1000L.toBigDecimal())?.toLong() ?: 0

    override fun marshal(p0: Long?): BigDecimal = p0?.toBigDecimal()?.divide(1000L.toBigDecimal()) ?: BigDecimal.ZERO
}

class DecimalToMillisDeserializer : JsonDeserializer<Long>() {
    override fun deserialize(p0: JsonParser?, p1: DeserializationContext?) =
        ((p0?.valueAsDouble ?: 0.0) * 1000).toLong()
}

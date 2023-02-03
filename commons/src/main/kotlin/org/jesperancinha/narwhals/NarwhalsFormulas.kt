package org.jesperancinha.narwhals

import java.math.BigDecimal

fun ElapsedDays.dailyCabbages(): BigDecimal = BigDecimal(50).subtract(this.multiply(BigDecimal("0.03")))

fun ElapsedDays.tusksFall(): BigDecimal = BigDecimal(8).add(this.multiply(BigDecimal("0.01")))
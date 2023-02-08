package org.jesperancinha.narwhals

import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe

fun <NARWHAL : NarwhalInterface<NUMBER>, NUMBER> MutableMap<String, NARWHAL>.shouldAssertNarwhals(vararg ages: Pair<NUMBER, String>) =
    ages.let {
        this.toSortedMap().let { mapFromFile ->
            it.forEachIndexed { index, (age, sex) ->
                val name = mapFromFile.toList()[index].first
                get(name)
                    .shouldNotBeNull().apply {
                        this.name shouldBe name
                        this.age shouldBe age
                        this.sex shouldBe sex
                    }
            }
        }
    }.let { this }

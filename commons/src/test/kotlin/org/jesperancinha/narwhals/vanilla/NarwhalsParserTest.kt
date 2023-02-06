package org.jesperancinha.narwhals.vanilla

import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class NarwhalsParserTest {

    @Test
    fun `should read a narwhals correctly`() {
        this.javaClass.getResourceAsStream("/narwhals1.xml")
            .shouldNotBeNull()
            .parseNarwhals()
            .apply {
                narwhal.shouldHaveSize(4)
                    .toList()
                    .apply {
                        get(0).apply {
                            name shouldBe "SonicDJ1"
                            age shouldBe BigDecimal.valueOf(8)
                            sex shouldBe "f"
                        }
                        get(1).apply {
                            name shouldBe "SonicDJ2"
                            age shouldBe BigDecimal.valueOf(19)
                            sex shouldBe "f"
                        }
                        get(2).apply {
                            name shouldBe "SonicDJ3"
                            age shouldBe BigDecimal(12)
                            sex shouldBe "f"
                        }
                        get(3).apply {
                            name shouldBe "SonicDJ4"
                            age shouldBe BigDecimal(18.5)
                            sex shouldBe "m"
                        }
                    }
            }
    }

    @Test
    fun `should make elapse 13 day predictions correctly`() {
        this.javaClass.getResourceAsStream("/narwhals1.xml")
            .shouldNotBeNull()
            .parseNarwhals()
            .toOutput(13)
            .apply {
                stock.apply {
                    seaCabbage shouldBe BigDecimal("17531.280")
                    tusks shouldBe 4
                }
                narwhals.apply {
                    narwhal.shouldHaveSize(4)
                        .toList()
                        .apply {
                            get(0).apply {
                                name shouldBe "SonicDJ1"
                                age shouldBe BigDecimal("8.013")
                                ageLastTuskShed shouldBe BigDecimal(8)
                            }
                            get(1).apply {
                                name shouldBe "SonicDJ2"
                                age shouldBe BigDecimal("19.013")
                                ageLastTuskShed shouldBe BigDecimal(19)
                            }
                            get(2).apply {
                                name shouldBe "SonicDJ3"
                                age shouldBe BigDecimal("12.013")
                                ageLastTuskShed shouldBe BigDecimal(12)
                            }
                            get(3).apply {
                                name shouldBe "SonicDJ4"
                                age shouldBe BigDecimal("18.513")
                                ageLastTuskShed shouldBe BigDecimal(18.5)
                            }
                        }
                }
            }
    }
    @Test
    fun `should make elapse 14 day predictions correctly`() {
        this.javaClass.getResourceAsStream("/narwhals1.xml")
            .shouldNotBeNull()
            .parseNarwhals()
            .toOutput(14)
            .apply {
                stock.apply {
                    seaCabbage shouldBe BigDecimal("18878.160")
                    tusks shouldBe 4
                }
                narwhals.apply {
                    narwhal.shouldHaveSize(4)
                        .toList()
                        .apply {
                            get(0).apply {
                                name shouldBe "SonicDJ1"
                                age shouldBe BigDecimal("8.014")
                                ageLastTuskShed shouldBe BigDecimal(8)
                            }
                            get(1).apply {
                                name shouldBe "SonicDJ2"
                                age shouldBe BigDecimal("19.014")
                                ageLastTuskShed shouldBe BigDecimal(19)
                            }
                            get(2).apply {
                                name shouldBe "SonicDJ3"
                                age shouldBe BigDecimal("12.014")
                                ageLastTuskShed shouldBe BigDecimal(12)
                            }
                            get(3).apply {
                                name shouldBe "SonicDJ4"
                                age shouldBe BigDecimal("18.514")
                                ageLastTuskShed shouldBe BigDecimal(18.5)
                            }
                        }
                }
            }
    }

    @Test
    fun `should test tuskShedding of day 13`(){
        13.toLong().tuskShedSequence(4.toLong()).shouldHaveSize(0)
    }

    @Test
    fun `should test tuskShedding of day 12`(){
        12.toLong().tuskShedSequence(4.toLong()).shouldHaveSize(0)
    }

    @Test
    fun `should test tuskShedding of day 14`(){
        14.toLong().tuskShedSequence(4.toLong()).shouldHaveSize(0)
    }
    @Test
    fun `should test tuskShedding sequence of day 1000`(){
        1000.toLong().tuskShedSequence(4.toLong()).shouldHaveSize(4)
    }
    @Test
    fun `should test tuskShedding sequence of day 20000`(){
        20000.toLong().tuskShedSequence(4.toLong()).shouldHaveSize(51)
    }
    @Test
    fun `should test tuskShedding sequence of day 40000`(){
        40000.toLong().tuskShedSequence(4.toLong()).shouldHaveSize(51)
    }
}

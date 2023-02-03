package org.jesperancinha.narwhals

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
                narwhal.shouldHaveSize(3)
                    .toList()
                    .apply {
                        get(0).apply {
                            name shouldBe "SonicDJ-1"
                            age shouldBe BigDecimal.valueOf(4)
                            sex shouldBe "f"
                        }
                        get(1).apply {
                            name shouldBe "SonicDJ-2"
                            age shouldBe BigDecimal.valueOf(8)
                            sex shouldBe "f"
                        }
                        get(2).apply {
                            name shouldBe "SonicDJ-3"
                            age shouldBe BigDecimal("9.5")
                            sex shouldBe "f"
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
                    seaCabbage shouldBe BigDecimal("1104.480")
                    tusks shouldBe 3
                }
                narwhals.apply {
                    narwhal.shouldHaveSize(3)
                        .toList()
                        .apply {
                            get(0).apply {
                                name shouldBe "SonicDJ-1"
                                age shouldBe BigDecimal("4.13")
                                ageLastTuskShed shouldBe BigDecimal(4)
                            }
                            get(1).apply {
                                name shouldBe "SonicDJ-2"
                                age shouldBe BigDecimal("8.13")
                                ageLastTuskShed shouldBe BigDecimal(8)
                            }
                            get(2).apply {
                                name shouldBe "SonicDJ-3"
                                age shouldBe BigDecimal("9.63")
                                ageLastTuskShed shouldBe BigDecimal(9.5)
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
                    seaCabbage shouldBe BigDecimal("1188.810")
                    tusks shouldBe 4
                }
                narwhals.apply {
                    narwhal.shouldHaveSize(3)
                        .toList()
                        .apply {
                            get(0).apply {
                                name shouldBe "SonicDJ-1"
                                age shouldBe BigDecimal("4.14")
                            }
                            get(1).apply {
                                name shouldBe "SonicDJ-2"
                                age shouldBe BigDecimal("8.14")
                            }
                            get(2).apply {
                                name shouldBe "SonicDJ-3"
                                age shouldBe BigDecimal("9.64")
                            }
                        }
                }
            }
    }

    @Test
    fun `should test shaving sequence 1`(){
        13.toBigDecimal().shavingSequence(BigDecimal.valueOf(4)).shouldHaveSize(0)
    }

    @Test
    fun `should test shaving sequence 2`(){
        12.toBigDecimal().shavingSequence(BigDecimal.valueOf(4)).shouldHaveSize(0)
    }

    @Test
    fun `should test shaving sequence 3`(){
        14.toBigDecimal().shavingSequence(BigDecimal.valueOf(4)).shouldHaveSize(1)
    }
}

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
                            age shouldBe 8000L
                            sex shouldBe "f"
                        }
                        get(1).apply {
                            name shouldBe "SonicDJ2"
                            age shouldBe 19000L
                            sex shouldBe "f"
                        }
                        get(2).apply {
                            name shouldBe "SonicDJ3"
                            age shouldBe 12000L
                            sex shouldBe "f"
                        }
                        get(3).apply {
                            name shouldBe "SonicDJ4"
                            age shouldBe 18500
                            sex shouldBe "m"
                        }
                    }
            }
    }

    /**
     * The first problem of using this Long solution comes when making the calculations
     * Most of the time the assumption os that using a Long on a milimetric scale will be enough, however in this case the difference arises
     *
     * Using BigDecimals, the expected result was 17531,28
     * Using Long, however the result is 17531271L
     *
     * Comparing both: 17531280 - 17531271L = 9L difference.
     *
     * 9L means in this case 9 grams and this proves that using Long to "avoid the complexity of Decimals" and "useless computation" is actually a bad idea
     */
    @Test
    fun `should make elapse 13 day predictions correctly`() {
        this.javaClass.getResourceAsStream("/narwhals1.xml")
            .shouldNotBeNull()
            .parseNarwhals()
            .toOutput(13)
            .apply {
                stock.apply {
                    seaCabbage shouldBe 17531271L
                    tusks shouldBe 4
                }
                narwhals.apply {
                    narwhal.shouldHaveSize(4)
                        .toList()
                        .apply {
                            get(0).apply {
                                name shouldBe "SonicDJ1"
                                age shouldBe 8013L
                                ageLastTuskShed shouldBe 8000L
                            }
                            get(1).apply {
                                name shouldBe "SonicDJ2"
                                age shouldBe 19013L
                                ageLastTuskShed shouldBe 19000L
                            }
                            get(2).apply {
                                name shouldBe "SonicDJ3"
                                age shouldBe 12013L
                                ageLastTuskShed shouldBe 12000L
                            }
                            get(3).apply {
                                name shouldBe "SonicDJ4"
                                age shouldBe 18513L
                                ageLastTuskShed shouldBe 18500L
                            }
                        }
                }
            }
    }

    /**
     * The first problem of using this Long solution comes when making the calculations
     * Most of the time the assumption os that using a Long on a milimetric scale will be enough, however in this case the difference arises
     *
     * Using BigDecimals, the expected result was 18878.160
     * Using Long, however the result is 18878151L
     *
     * Comparing both: 18878160L - 18878151L = 9L difference.
     *
     * 9L means in this case 9 grams and this proves that using Long to "avoid the complexity of Decimals" and "useless computation" is actually a bad idea
     */
    @Test
    fun `should make elapse 14 day predictions correctly`() {
        this.javaClass.getResourceAsStream("/narwhals1.xml")
            .shouldNotBeNull()
            .parseNarwhals()
            .toOutput(14)
            .apply {
                stock.apply {
                    seaCabbage shouldBe 18878151L
                    tusks shouldBe 4
                }
                narwhals.apply {
                    narwhal.shouldHaveSize(4)
                        .toList()
                        .apply {
                            get(0).apply {
                                name shouldBe "SonicDJ1"
                                age shouldBe 8014
                                ageLastTuskShed shouldBe 8000
                            }
                            get(1).apply {
                                name shouldBe "SonicDJ2"
                                age shouldBe 19014
                                ageLastTuskShed shouldBe 19000
                            }
                            get(2).apply {
                                name shouldBe "SonicDJ3"
                                age shouldBe 12014
                                ageLastTuskShed shouldBe 12000
                            }
                            get(3).apply {
                                name shouldBe "SonicDJ4"
                                age shouldBe 18514
                                ageLastTuskShed shouldBe 18500
                            }
                        }
                }
            }
    }

    @Test
    fun `should test tuskShedding of day 13`() {
        13L.tuskShedSequence(4000L).shouldHaveSize(0)
    }

    @Test
    fun `should test tuskShedding of day 12`() {
        12L.tuskShedSequence(4000L).shouldHaveSize(0)
    }

    @Test
    fun `should test tuskShedding of day 14`() {
        14L.tuskShedSequence(4000L).shouldHaveSize(0)
    }

    @Test
    fun `should test tuskShedding sequence of day 1000`() {
        1000L.tuskShedSequence(4000L).shouldHaveSize(4)
    }

    @Test
    fun `should test tuskShedding sequence of day 20000`() {
        20000L.tuskShedSequence(4000L).shouldHaveSize(51)
    }

    @Test
    fun `should test tuskShedding sequence of day 40000`() {
        40000L.tuskShedSequence(4000L).shouldHaveSize(51)
    }
}

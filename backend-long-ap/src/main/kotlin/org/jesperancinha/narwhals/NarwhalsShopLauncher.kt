package org.jesperancinha.narwhals

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
class NarwhalsShopLauncher

fun main(args: Array<String>) {
    SpringApplication.run(NarwhalsShopLauncher::class.java, *args).start()
}
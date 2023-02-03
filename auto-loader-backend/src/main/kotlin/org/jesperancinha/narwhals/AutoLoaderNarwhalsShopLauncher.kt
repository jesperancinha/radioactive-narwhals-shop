package org.jesperancinha.narwhals

import org.jesperancinha.narwhals.dao.NarwhalsWebShopDao
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import picocli.CommandLine
import picocli.CommandLine.*
import java.io.File
import java.lang.Exception
import java.util.concurrent.Callable
import kotlin.system.exitProcess

@Command(
    name = "auto-loader-backend", mixinStandardHelpOptions = true, version = ["0.0.0"],
    description = ["Narwhals Auto Loading Service - It reads narwhals data as input in an XML format and loads the server directly"]
)
class NarwhalsParserCommand : Callable<Int> {

    @Option(names = ["-f", "--file"], description = ["narwhals filename"])
    var filename: File? = null

    @Option(names = ["-d", "--days"], description = ["elapsed days"])
    var days: Int = -1
    override fun call(): Int = try {
        0
    } catch (_: Exception) {
        1
    }
}

@SpringBootApplication
class AutoLoaderNarwhalsShopLauncher(
    val narwhalsWebShopDao: NarwhalsWebShopDao,
) : CommandLineRunner {
    override fun run(vararg args: String?) {
        val narwhalsParserCommand = NarwhalsParserCommand()
        CommandLine(narwhalsParserCommand).parseArgs(*args)
        narwhalsParserCommand.apply {
            narwhalsWebShopDao.loadWebShop(requireNotNull(filename?.parseNarwhals()?.toCurrentNarwhals(days)))
        }
    }
}

fun main(args: Array<String>) {
    if (args.isEmpty())
        exitProcess(runCommand(arrayOf("-h")))
    else
        SpringApplication.run(AutoLoaderNarwhalsShopLauncher::class.java, *args).start()
}

private fun runCommand(args: Array<String>) = CommandLine(NarwhalsParserCommand()).execute(*args)

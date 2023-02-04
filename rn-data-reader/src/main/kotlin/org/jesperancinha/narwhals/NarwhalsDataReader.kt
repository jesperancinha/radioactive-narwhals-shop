package org.jesperancinha.narwhals

import picocli.CommandLine
import picocli.CommandLine.Command
import picocli.CommandLine.Option
import java.io.File
import java.lang.Exception
import java.util.concurrent.Callable
import kotlin.system.exitProcess

const val TAB = '\t'

@Command(
    name = "rn-data-reader", mixinStandardHelpOptions = true, version = ["0.0.0"],
    description = ["Narwhals Data Reader - It reads narwhals data as input in an XML format"]
)
class NarwhalsParserCommand : Callable<Int> {

    @Option(names = ["-f", "--file"], description = ["narwhals filename"])
    var filename: File? = null

    @Option(names = ["-d", "--days"], description = ["elapsed days"])
    var days: Int = -1
    override fun call(): Int = try {
        println(filename.makePrintOut(days))
        0
    } catch (_: Exception) {
        1
    }
}
fun File?.makePrintOut(days: Int) = this?.parseNarwhals()?.toOutput(days)
    .let { it?.displayLogText() }

private fun Output.displayLogText(): String = """
In Stock:
$TAB${stock.seaCabbage} of seaCabbage
$TAB${stock.tusks} tusks
Narwhals:
${narwhals.narwhal.displayLogText()}
""".trimIndent()

private fun List<CurrentNarwhal>.displayLogText(): String = joinToString("\n") {
    "$TAB${it.name} ${it.age} years old"
}


fun main(args: Array<String>) {
    if (args.isEmpty())
        exitProcess(runCommand(arrayOf("-h")))
    else
        exitProcess(runCommand(args))
}

fun runCommand(args: Array<String>) = CommandLine(NarwhalsParserCommand()).execute(*args)
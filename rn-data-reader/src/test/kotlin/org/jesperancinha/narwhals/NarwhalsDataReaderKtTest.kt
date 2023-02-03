package org.jesperancinha.narwhals

import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.nio.charset.Charset
import java.nio.file.Files
import java.nio.file.Files.writeString
import java.nio.file.Path
import kotlin.io.path.absolutePathString

class NarwhalsDataReaderKtTest {

    @TempDir
    lateinit var tempDir: Path

    @Test
    fun `should run command`() {
        val tempFile = Files.createFile(tempDir.resolve("narwhalsTest.xml"))
        writeString(
            tempFile,
            javaClass.getResourceAsStream("/narwhals1.xml").shouldNotBeNull().reader(Charset.defaultCharset()).readText()
        )
        runCommand(arrayOf("-f", tempFile.absolutePathString(), "-d", "13")) shouldBe 0

    }

    @Test
    fun `should make good printout`() {
        val tempFile = Files.createFile(tempDir.resolve("narwhalsTest.xml"))
        writeString(
            tempFile,
            javaClass.getResourceAsStream("/narwhals1.xml").shouldNotBeNull().reader(Charset.defaultCharset()).readText()
        )
        tempFile.toFile().makePrintOut(13) shouldBe "Warehouse Goods:\n" +
                "\t17531.280 of seaCabbage\n" +
                "\t4 tusks\n" +
                "Narwhals:\n" +
                "\tSonicDJ1 8.013 years of age\n" +
                "\tSonicDJ2 19.013 years of age\n" +
                "\tSonicDJ3 12.013 years of age\n" +
                "\tSonicDJ4 18.513 years of age"
    }
    @Test
    fun `should make good printout for 14`() {
        val tempFile = Files.createFile(tempDir.resolve("narwhalsTest.xml"))
        writeString(
            tempFile,
            javaClass.getResourceAsStream("/narwhals1.xml").shouldNotBeNull().reader(Charset.defaultCharset()).readText()
        )
        tempFile.toFile().makePrintOut(14) shouldBe "Warehouse Goods:\n" +
                "\t18878.160 of seaCabbage\n" +
                "\t4 tusks\n" +
                "Narwhals:\n" +
                "\tSonicDJ1 8.014 years of age\n" +
                "\tSonicDJ2 19.014 years of age\n" +
                "\tSonicDJ3 12.014 years of age\n" +
                "\tSonicDJ4 18.514 years of age"
    }
}
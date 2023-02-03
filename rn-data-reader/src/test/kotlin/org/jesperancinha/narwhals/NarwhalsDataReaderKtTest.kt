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
        tempFile.toFile().makePrintOut(13) shouldBe "In Stock:\n" +
                "\t1104.480 of seaCabbage\n" +
                "\t3 tusks\n" +
                "Narwhals:\n" +
                "\tSonicDJ-1 4.13 years old\n" +
                "\tSonicDJ-2 8.13 years old\n" +
                "\tSonicDJ-3 9.63 years old"
    }
    @Test
    fun `should make good printout for 14`() {
        val tempFile = Files.createFile(tempDir.resolve("narwhalsTest.xml"))
        writeString(
            tempFile,
            javaClass.getResourceAsStream("/narwhals1.xml").shouldNotBeNull().reader(Charset.defaultCharset()).readText()
        )
        tempFile.toFile().makePrintOut(14) shouldBe "In Stock:\n" +
                "\t1188.810 of seaCabbage\n" +
                "\t4 tusks\n" +
                "Narwhals:\n" +
                "\tSonicDJ-1 4.14 years old\n" +
                "\tSonicDJ-2 8.14 years old\n" +
                "\tSonicDJ-3 9.64 years old"
    }
}
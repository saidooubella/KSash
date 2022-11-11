package sash

import sash.evaluate.Evaluator
import java.io.FileReader

private const val TEST_FILE_PATH = "/Volumes/NO NAME/Files/Projects/Sash/src/sash/main.sash"

fun main() {
    val fileReader = FileReader(TEST_FILE_PATH)
    val (program, errors) = Compiler(fileReader)
    errors.forEach { println(it.formattedMessage) }
    if (errors.isEmpty) Evaluator.evaluate(program)
}

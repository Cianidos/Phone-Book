package phonebook

import java.io.File

inline fun <R> measureTime(block: () -> R): Pair<R, Long> {
    val start = System.currentTimeMillis()
    val res = block()
    val elapsed = System.currentTimeMillis() - start
    return res to elapsed
}

inline fun <R> printMeasured(message: String = "Elapsed", block: () -> R): R {
    val (res, time) = measureTime(block)
    println("$message $time ms")
    return res
}

fun main() {
    val dataFile = "C:\\Users\\Ariel\\IdeaProjects\\Phone Book\\directory.txt"
    val findFile = "C:\\Users\\Ariel\\IdeaProjects\\Phone Book\\find.txt"

    val data = printMeasured("Reading data file...")
    { File(dataFile).readLines() }

    val finds = printMeasured("Reading findings file...")
    { File(findFile).readLines() }

    println("Start searching...")
    val (foundNum, time) = measureTime {
        finds.map { find ->
            data.find { it.contains(find) } != null
        }.count { it }
    }

    val timeFormatted = "%1\$tM min. %1\$tS sec. %1\$tL ms.".format(time)
    println("Found $foundNum / ${finds.size} entries. Time taken: $timeFormatted")
}
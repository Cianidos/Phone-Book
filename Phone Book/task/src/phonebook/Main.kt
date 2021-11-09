package phonebook

import java.io.File
import kotlin.math.floor
import kotlin.math.sqrt

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

infix fun String.splitAt(n: Int) = listOf(take(n), this.drop(n + 1))
infix fun String.splitAt(ch: Char) = splitAt(indexOf(ch))

fun main() {
    val timeFormat = { time: Long ->
        "%1\$tM min. %1\$tS sec. %1\$tL ms.".format(time)
    }
    val dataFile = "C:\\Users\\Ariel\\IdeaProjects\\Phone " +
            "Book\\directory.txt"
    val findFile = "C:\\Users\\Ariel\\IdeaProjects\\Phone Book\\find.txt"

    val data = printMeasured("Reading data file...")
    { File(dataFile).readLines() }

    val finds = printMeasured("Reading findings file...")
    { File(findFile).readLines() }

    println("Start searching (linear search)...")
    val (foundNum, time) = measureTime {
        finds.map { find ->
            data.find { it.contains(find) } != null
        }.count { it }
    }
    val timeFormatted = timeFormat(time)
    println("Found $foundNum / ${finds.size} entries. Time taken: $timeFormatted")

    println()
    println("Start searching (bubble sort + jump search)...")
    val (sorted, sortingTime) = measureTime {
        data.map { it.splitAt(' ')[1] }.bubbleSort()
    }

    val (foundJump, jumpTime) = measureTime {
        finds.map { find ->
            sorted.jumpSearch(find) != null
        }.count { it }
    }
    val total = timeFormat(sortingTime + jumpTime)
    println("Found $foundJump / ${finds.size} entries. Time taken: $total")
    println("Sorting time: ${timeFormat(sortingTime)}")
    println("Searching time: ${timeFormat(jumpTime)}")
}

inline fun <reified T : Comparable<T>> List<T>.bubbleSort(
    cmp: (T, T) -> Boolean = { a, b -> a < b }
): List<T> {
    return toMutableList().apply {
        for (i in indices)
            for (j in i..lastIndex)
                if (cmp(get(j), get(i))) {
                    val tmp = get(j)
                    set(j, get(i))
                    set(i, tmp)
                }
    }
}

inline fun <reified T : Comparable<T>> List<T>.jumpSearch(
    element: T,
    cmp: (T, T) -> Boolean = { a, b -> a < b }
): T? {
    val jumpLen = floor(sqrt(size.toDouble())).toInt()
    var currIdx = 0

    while (cmp(get(currIdx), element)) {
        currIdx += jumpLen
        if (currIdx > lastIndex)
            return null
    }
    while (cmp(element, get(currIdx)))
        currIdx -= 1

    return get(currIdx)
}

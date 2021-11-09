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
        data.map { it.splitAt(' ')[1] }.sorted()
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



    println()
    println("Start searching (quick sort + binary search)...")
    val (sortedQ, sortingTimeQ) = measureTime {
        data.map { it.splitAt(' ')[1] }.quickSort()
    }

    val (foundBS, bSTime) = measureTime {
        finds.map { find ->
            sorted.binSearch(find) >= 0
        }.count { it }
    }
    val totalQBS = timeFormat(sortingTimeQ + bSTime)
    println("Found $foundBS / ${finds.size} entries. Time taken: $totalQBS")
    println("Sorting time: ${timeFormat(sortingTimeQ)}")
    println("Searching time: ${timeFormat(bSTime)}")



    println()
    println("Start searching (hash table)...")
    val (sortedH, sortingTimeH) = measureTime {
        data.map { it.splitAt(' ')[1] }.toHashSet()
    }

    val (foundH, hTime) = measureTime {
        finds.map { find ->
            sortedH.contains(find)
        }.count { it }
    }
    val totalH = timeFormat(sortingTimeH + hTime)
    println("Found $foundH / ${finds.size} entries. Time taken: $totalH")
    println("Creating time: ${timeFormat(sortingTimeH)}")
    println("Searching time: ${timeFormat(hTime)}")
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

fun <T : Comparable<T>> List<T>.quickSort(
    cmp: (T, T) -> Boolean = { a, b -> a < b }
): List<T> {
    if (size <= 1) return this
    val rnd = random()
    val (left, right) = partition { cmp(it, rnd) }
    return left.quickSort(cmp) + right.quickSort(cmp)
}

fun <T : Comparable<T>> List<T>.binSearch(
    element: T,
    cmp: (T, T) -> Boolean = { a, b -> a < b }
): Int {
    var low = 0
    var high = size - 1

    while (low <= high) {
        val mid = (low + high).ushr(1) // safe from overflows
        val midVal = get(mid)

        if (cmp(midVal, element))
            low = mid + 1
        else if (cmp(element, midVal))
            high = mid - 1
        else
            return mid
    }
    return -(low + 1)
}
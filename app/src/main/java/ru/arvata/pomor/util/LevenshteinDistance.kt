package ru.arvata.pomor.util

fun levenshteinDistance(lhs : CharSequence, rhs : CharSequence) : Int {
    val lhsLength = lhs.length
    val rhsLength = rhs.length

    var cost = Array(lhsLength) { it }
    var newCost = Array(lhsLength) { 0 }

    for (i in 1..rhsLength-1) {
        newCost[0] = i

        for (j in 1..lhsLength-1) {
            val match = if(lhs[j - 1] == rhs[i - 1]) 0 else 1

            val costReplace = cost[j - 1] + match
            val costInsert = cost[j] + 1
            val costDelete = newCost[j - 1] + 1

            newCost[j] = Math.min(Math.min(costInsert, costDelete), costReplace)
        }

        val swap = cost
        cost = newCost
        newCost = swap
    }

    return cost[lhsLength - 1]
}
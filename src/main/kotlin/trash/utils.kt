package trash

import java.awt.Graphics
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt

fun distance(a: Node, b: Node): Double {
    return sqrt((b.x - a.x).pow(2) + (b.y - a.y).pow(2))
}

fun findShortestPath(startNode: Node, finishNode: Node): Pair<List<Node>, Double> {
    val distances = mutableMapOf<Node, Double>()
    distances[startNode] = 0.0
    val visited = mutableSetOf<Node>()
    visited.add(startNode)

    val nodesQueue = mutableSetOf<Node>()
    nodesQueue.add(startNode)

    while (nodesQueue.isNotEmpty()) {
        val nearestNode = nodesQueue.minByOrNull { distances.getOrPut(it) { Double.MAX_VALUE } }!!
        visited.add(nearestNode)
        nodesQueue.remove(nearestNode)
        if (nearestNode == finishNode) break

        for (it in nearestNode.neighbours) {
            if (it in visited) continue
            val nearestNodeDist = distances[nearestNode]!!
            val distanceToNeighbour = distances.getOrPut(it) { Double.MAX_VALUE }
            distances[it] = minOf(distanceToNeighbour, nearestNodeDist + distance(nearestNode, it))
            nodesQueue.add(it)
        }
    }

    val path = mutableListOf<Node>()
    var nextNode = finishNode
    while (true) {
        path.add(nextNode)
        if (nextNode == startNode) break
        val distanceToNextNode = distances[nextNode]!!
        val node = nextNode.neighbours.first {
            val distanceToNeighbour = distances.getOrPut(it) { Double.MAX_VALUE }
            abs(distanceToNextNode - distance(nextNode, it) - distanceToNeighbour) < 0.00001
        }
        nextNode = node
    }
    return path.reversed() to distances[finishNode]!!
}

fun Graphics.drawAOfBText(x: Double, y:Double, offsetKM: Double, a: Double, b: Double) {
    drawText(x, y, offsetKM, "%.2f/%.2f".format(a, b))
}

fun  Graphics.drawText(x: Double, y: Double, offsetKM: Double, text: String) {
    val (xPx, yPx) = Drawing.positionToPixels(x + offsetKM, y)
    val textArray = text.toCharArray()
    drawChars(textArray, 0, textArray.size, xPx, yPx)
}
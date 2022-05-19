package trash

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import trash.Drawing.Companion.positionToPixels
import trash.Drawing.Companion.toPixels
import java.awt.Color
import java.awt.Graphics
import kotlin.math.abs
import kotlin.math.ln
import kotlin.math.pow
import kotlin.math.sqrt
import kotlin.random.Random

class TrashModel(private val objectRepository: ObjectRepository, private val calendar: EventCalendar) {
    private val canQueue = mutableSetOf<GarbageCan>()
    private val visited = mutableSetOf<GarbageCan>()
    private var isWorking = false

    fun start() {
        calendar.clear()
        canQueue.clear()
        visited.clear()
        objectRepository.getGarbageCans().forEach {
            it.trashVolume = 0.0
        }
        objectRepository.getCar().apply {
            val parking = objectRepository.getParking()
            x = parking.x
            y = parking.y
            trashVolume = 0.0
            isWorking = false
            lastNode = parking
        }

        fun addTrash(can: GarbageCan) {
            can.trashVolume += 0.02
            if (can.trashVolume >= can.capacity) {
                canQueue.add(can)
            }
            scheduleEvent(can.intensity) { addTrash(can) }
        }
        objectRepository.getGarbageCans().forEach { can ->
            scheduleEvent(can.intensity) {
                addTrash(can)
            }
        }

        fun Car.checkGarbageCanQueueForClever() {
            scheduleEventInTime(1 / 12.0) {
                if (!workOnSchedule && !isWorking && canQueue.isNotEmpty()) {
                    goToWork()
                }
                checkGarbageCanQueueForClever()
            }
        }
        objectRepository.getCar().apply {
            checkGarbageCanQueueForClever()
            scheduleGoToWorkForStupid()
        }
    }

    fun Car.scheduleGoToWorkForStupid() {
        scheduleEventInTime(restTime) {
            if (workOnSchedule && !isWorking) {
                goToWork()
            }
            scheduleGoToWorkForStupid()
        }
    }

    fun Car.goToWork() {
        isWorking = true
        visited.clear()
        if (workOnSchedule) {
            canQueue.addAll(objectRepository.getGarbageCans())
            if (traversalOrder.size < 2) return
            driveAndCollectTrash(traversalOrder, 0)
            return
        }
        val can = canQueue.first()
        val traversalOrder = findShortestPath(lastNode, can).first
        driveAndCollectTrash(traversalOrder, 0)
    }

    fun Car.driveAndCollectTrash(traversalOrder: List<Node>, i: Int) {
        if (i == traversalOrder.lastIndex) {
            goHome()
            return
        }
        drive(traversalOrder[i], traversalOrder[i + 1]) {
            if (it is GarbageCan && it !in visited && it in canQueue) {
                collectTrash(it) {
                    visited.add(it)
                    canQueue.remove(it)
                    driveAndCollectTrash(traversalOrder, i + 1)
                }
                return@drive

            }
            if (it is Dump) {
                dumpTrash() {}
            }
            driveAndCollectTrash(traversalOrder, i + 1)
        }
    }

    fun Car.goHome() {
        if (trashVolume > 0.0) {
            driveToDump { driveHome() }
            return
        }
        driveHome()
    }

    fun Car.dumpTrash(onDump: () -> Unit) {
        trashVolume = 0.0
        onDump()
    }

    fun Car.driveHome() {
        val parking = objectRepository.getParking()
        val path = findShortestPath(lastNode, parking).first
        drive(path) {
            isWorking = false
        }
    }

    fun Car.collectTrash(can: GarbageCan, onCollect: () -> Unit) {
        scheduleEvent(can.collectIntensity) {
            totalWorkTime += it
            if (can.trashVolume + trashVolume > capacity) {
                val collectedTrash = capacity - trashVolume
                can.trashVolume -= collectedTrash
                trashVolume += collectedTrash
                driveToDump {
                    val path = findShortestPath(lastNode, can).first
                    drive(path) { node ->
                        if (node == can) {
                            collectTrash(can, onCollect)
                        }
                    }
                }
                return@scheduleEvent
            }
            trashVolume += can.trashVolume
            can.trashVolume = 0.0
            onCollect()
        }
    }

    fun Car.driveToDump(onDump: () -> Unit) {
        val dump = objectRepository.getDump()
        val path = findShortestPath(lastNode, dump).first
        drive(path) {
            if (it == dump) {
                dumpTrash() {
                    onDump()
                }
            }
        }
    }

    fun Car.move(dt: Double, vx: Double, vy: Double) {
        x += vx * dt
        y += vy * dt
        val dist = sqrt((vx * dt).pow(2) + (vy * dt).pow(2))
        consumedFuel += dist / fuelConsumption
        totalWorkTime += dt
    }

    fun Car.drive(from: Node, to: Node, onNodeListener: (Node) -> Unit) {
        val time = distance(from, to) / speed
        val dt = time / 1000
        val vx = (to.x - from.x) / time
        val vy = (to.y - from.y) / time
        var nextEventTime = dt
        while (nextEventTime < time) {
            scheduleEventInTime(nextEventTime) {
                move(dt, vx, vy)
            }
            nextEventTime += dt
        }
        scheduleEventInTime(time) {
            x = to.x
            y = to.y
            lastNode = to
            onNodeListener(to)
        }
    }

    fun Car.drive(path: List<Node>, onNodeListener: (Node) -> Unit = {}) {
        fun driveToLastNode(path: List<Node>, i: Int) {
            if (i + 1 == path.size) {
                onNodeListener(path[0])
                return
            }
            drive(path[i], path[i + 1]) {
                if (i + 1 == path.lastIndex) {
                    onNodeListener(it)
                } else {
                    driveToLastNode(path, i + 1)
                }
            }
        }
        driveToLastNode(path, 0)
    }

    fun draw(g: Graphics) {
        val nodes = objectRepository.getNodes()
        drawEdges(nodes, g)
        objectRepository.objects.values.forEach { it.draw(g) }
        g.drawText(1.9, 1.9, 0.0, "Time: %.2f hours".format(calendar.currentTime))
    }

    private fun drawEdges(nodes: List<Node>, g: Graphics) {
        nodes.forEach {
            drawEdgesToNeighbours(it, g)
        }

        val traversalOrder = objectRepository.getCar().traversalOrder
        g.color = Color.BLUE
        (0 until traversalOrder.lastIndex).forEach {
            drawEdge(traversalOrder[it], traversalOrder[it + 1], g)
        }
    }

    private fun drawEdgesToNeighbours(node: Node, g: Graphics) {
        node.neighbours.forEach {
            drawEdge(node, it, g)
        }
    }

    private fun drawEdge(node1: Node, node2: Node, g: Graphics) {
        val (x1, y1) = positionToPixels(node1.x, node1.y)
        val (x2, y2) = positionToPixels(node2.x, node2.y)
        g.drawLine(x1, y1, x2, y2)
    }

    private fun scheduleEvent(intensity: Double, block: (Double) -> Unit) {
        val dt = -1 / intensity * ln(Random.nextDouble(0.0, 1.0))
        calendar.scheduleEvent(dt) {
            block(dt)
        }
    }

    private fun scheduleEventInTime(dt: Double, block: () -> Unit) {
        calendar.scheduleEvent(dt, block)
    }
}


class Car : Object() {
    var x = 0.0
    var y = 0.0

    @JsonSerialize(using = LastNodeSerializer::class)
    @JsonProperty("lastNodeId")
    lateinit var lastNode: Node

    @JsonIgnore
    var traversalOrder = listOf<Node>()

    var speed = 100

    var restTime = 4.0

    var capacity = 40.0
    var trashVolume = 0.0

    var workOnSchedule = true

    var consumedFuel = 0.0
    var fuelConsumption = 30
    var totalWorkTime = 0.0

    override fun draw(g: Graphics) {
        val (x, y) = positionToPixels(x, y)
        val size = toPixels(0.7 / 10)
        g.color = Color.RED
        g.fillOval(x - size / 2, y - size / 2, size, size)

        g.drawAOfBText(this.x, this.y + 0.3 / 10, 0.4 / 10, trashVolume, capacity)
    }
}

class LastNodeSerializer : JsonSerializer<Node>() {
    override fun serialize(value: Node, gen: JsonGenerator, serializers: SerializerProvider?) {
        gen.writeNumber(value.id)
    }
}

class ObjectRepository {
    val objects = mutableMapOf<Int, Object>()

    fun getGarbageCans(): List<GarbageCan> {
        return objects.values.filterIsInstance<GarbageCan>()
    }

    fun getCar(): Car {
        return objects.values.filterIsInstance<Car>().first()
    }

    fun getParking(): Parking {
        return objects.values.filterIsInstance<Parking>().first()
    }

    fun getDump(): Dump {
        return objects.values.filterIsInstance<Dump>().first()
    }

    fun getTurns(): List<Turn> {
        return objects.values.filterIsInstance<Turn>()
    }

    fun getNodes(): List<Node> {
        return objects.values.filterIsInstance(Node::class.java)
    }
}

abstract class Object {
    @JsonIgnore
    var id = getId()

    companion object {
        var nextId = 0
        fun getId(): Int {
            return nextId++
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Object

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id
    }

    abstract fun draw(g: Graphics)
}

abstract class Node : Object() {
    open var x = 0.0
    open var y = 0.0

    @JsonSerialize(using = NeighboursSerializer::class)
    val neighbours = mutableSetOf<Node>()

    fun addNeighbour(node: Node) {
        neighbours += node
        node.neighbours += this
    }

    fun removeNeighbour(node: Node) {
        neighbours -= node
        node.neighbours -= this
    }

    protected fun drawNode(g: Graphics, size: Double = 0.5 / 10) {
        val (x, y) = positionToPixels(x, y)
        val pxSize = toPixels(size)
        g.fillOval(x - pxSize / 2, y - pxSize / 2, pxSize, pxSize)
        g.color = Color.BLACK
        g.drawText(this.x, this.y, -1.0 / 10, "$id")
    }
}

class NeighboursSerializer : JsonSerializer<Set<Node>>() {
    override fun serialize(value: Set<Node>, gen: JsonGenerator, serializers: SerializerProvider) {
        gen.writeArray(value.map { it.id }.toIntArray(), 0, value.size)
    }
}

class GarbageCan : Node() {
    var trashVolume = 0.0
    var capacity = Random.nextInt(1, 6).toDouble()
    var intensity = Random.nextDouble(0.4, 1.0)
    var collectIntensity = Random.nextDouble(4.0, 20.0)

    override fun draw(g: Graphics) {
        g.color = Color.GREEN
        drawNode(g)
        g.drawAOfBText(this.x, this.y + 0.4 / 10, 0.3 / 10, trashVolume, capacity)
    }
}

class Parking : Node() {
    override fun draw(g: Graphics) {
        g.color = Color.BLUE
        drawNode(g, 1.5 / 10)
    }
}

class Dump : Node() {
    override fun draw(g: Graphics) {
        g.color = Color.GRAY
        drawNode(g, 2.0 / 10)
    }
}

class Turn : Node() {
    override fun draw(g: Graphics) {
        g.color = Color.BLACK
        drawNode(g)
    }
}

class Factory(private val objectRepository: ObjectRepository) {
    fun createGarbageCan(): GarbageCan {
        return GarbageCan().also {
            objectRepository.objects[it.id] = it
        }
    }

    fun createParking(): Parking {
        return Parking().also {
            objectRepository.objects[it.id] = it
        }
    }

    fun createDump(): Dump {
        return Dump().also {
            objectRepository.objects[it.id] = it
        }
    }

    fun createTurn(): Turn {
        return Turn().also {
            objectRepository.objects[it.id] = it
        }
    }

    fun createCar(): Car {
        return Car().also {
            objectRepository.objects[it.id] = it
        }
    }
}
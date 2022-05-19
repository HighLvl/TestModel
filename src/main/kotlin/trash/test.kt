package trash

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import java.awt.Dimension
import java.awt.Frame.MAXIMIZED_BOTH
import java.awt.Graphics
import java.awt.Toolkit
import java.awt.event.WindowEvent
import java.util.concurrent.atomic.AtomicBoolean
import javax.swing.JFrame
import javax.swing.JPanel
import javax.swing.Timer

private val input = """[[
  [0, -0.4, -1.2, [2]],
  [1, -0.8, -0.8, [5]],
  [2, -0.4, -0.8, [6, 3, 1]],
  [3, -0.2, -0.8, [4]],
  [4, 0.2, -0.8, [7]],
  [5, -0.8, -0.4, [17, 18]],
  [6, -0.4, -0.4, [10, 11]],
  [7, 0.2, -0.4, [12, 8]],
  [8, 0.6, -0.4, [13, 9]],
  [9, 1.0, -0.4, [23]],
  [10, -0.4, -0.2, []],
  [11, -0.2, -0.2, [19]],
  [12, 0.2, -0.2, [20]], 
  [13, 0.6, -0.2, [14, 22]],
  [14, 0.8, -0.2, []],
  [15, -1.2, 0.0, [16]],
  [16, -1.0, 0.0, [25, 17]],
  [17, -0.8, 0.0, [27]],
  [18, -0.4, 0.0, [32, 19]],
  [19, 0.0, 0.0, []],
  [20, 0.2, 0.0, [21]],
  [21, 0.4, 0.0, [22]],
  [22, 0.6, 0.0, [23, 45]],
  [23, 1.0, 0.0, []],
  [24, -1.2, 0.2, [26]],
  [25, -1.0, 0.1, [26]],
  [26, -1.0, 0.2, [27]],
  [27, -0.8, 0.2, [30]],
  [28, -1.2, 0.4, [29]],
  [29, -1.0, 0.4, [35, 30]],
  [30, -0.8, 0.4, [38, 31]],
  [31, -0.6, 0.4, [32]],
  [32, -0.4, 0.4, [39, 33]],
  [33, -0.2, 0.4, [40, 34]],
  [34, 0.0, 0.4, [42, 43]],
  [35, -1.0, 0.5, [37]],
  [36, -1.2, 0.6, [37]],
  [37, -1.0, 0.6, [38]],
  [38, -0.8, 0.6, []],
  [39, -0.4, 0.6, [40, 41]],
  [40, -0.2, 0.6, []],
  [41, -0.4, 0.8, [42]],
  [42, 0.0, 0.8, []],
  [43, 0.2, 0.7, [44]],
  [44, 0.4, 0.6, [45]],
  [45, 0.6, 0.5, [46]],
  [46, 1.5, 1.0, []] 
], 0, 46, [3, 10, 12, 14, 21, 25, 35, 31, 40, 44]]"""

fun setupMap(objectRepository: ObjectRepository, factory: Factory) {
    Object.nextId = 0
    val objectMapper = jacksonObjectMapper()
    val inputData = objectMapper.readValue<List<Any>>(input)
    val parkingId = inputData[1] as Int
    val dumpId = inputData[2] as Int
    val garbageCanIds = inputData[3] as List<*>

    val nodeData = inputData[0] as List<*>
    val idMap = mutableMapOf<Int, Node>()

    var parking: Parking? = null
    val garbageCans = mutableListOf<GarbageCan>()
    nodeData.forEach {
        it as List<*>
        val id = it[0] as Int
        val x = it[1] as Double
        val y = it[2] as Double

        val node = when {
            id == parkingId -> {
                parking = factory.createParking()
                parking
            }
            id == dumpId -> {
                factory.createDump()
            }
            id in garbageCanIds -> {
                val garbageCan = factory.createGarbageCan()
                garbageCans += garbageCan
                garbageCan
            }
            else -> {
                factory.createTurn()
            }
        }
        node!!.x = x
        node.y = y
        idMap[id] = node
    }

    nodeData.forEach {
        it as List<*>
        val id = it[0] as Int
        val neighbourIds = it[3] as List<*>
        neighbourIds.map { idMap[it as Int] }.forEach {
            idMap[id]!!.addNeighbour(it!!)
        }
    }

    val car = factory.createCar()
    car.apply {
        x = parking!!.x
        y = parking!!.y
        lastNode = parking!!

        val visited = mutableSetOf<GarbageCan>()
        var path = listOf<Node>()
        val queue = mutableSetOf<GarbageCan>()
        queue.addAll(garbageCans)
        var (closestCan: GarbageCan, shortestPath) = shortestPath(garbageCans, parking!!, visited)
        queue.remove(closestCan)
        path = path + shortestPath

        while (queue.isNotEmpty()) {
            val (nextCan: GarbageCan, nextCanPath) = shortestPath(garbageCans, closestCan, visited)
            closestCan = nextCan
            queue.remove(nextCan)
            path = path + nextCanPath
        }

        println(path.map { it.id })

        traversalOrder = path
    }
}

private fun shortestPath(
    garbageCans: MutableList<GarbageCan>,
    parking: Node,
    visited: MutableSet<GarbageCan>
): Pair<GarbageCan, List<Node>> {
    var closestCan: GarbageCan? = null
    var shortestPath = listOf<Node>()
    var minDist = Double.MAX_VALUE
    for (can in garbageCans) {
        if (can in visited) continue
        val (path, dist) = findShortestPath(parking, can)
        if (dist < minDist) {
            closestCan = can
            minDist = dist
            shortestPath = path
        }
    }
    visited.add(closestCan!!)
    return Pair(closestCan, shortestPath)
}

class Drawing : JPanel() {
    private val trashModel: TrashModel
    val calendar = EventCalendar()
    val objectRepository = ObjectRepository()
    private lateinit var timer: Timer
    private val isPause = AtomicBoolean(false)
    var hourSec = 10.0

    init {
        val factory = Factory(objectRepository)
        setupMap(objectRepository, factory)
        trashModel = TrashModel(objectRepository, calendar)
    }

    fun start() {
        trashModel.start()
        var startTime = System.currentTimeMillis()
        timer = Timer(1) {
            synchronized(Drawing) {
                val finishTime = System.currentTimeMillis()
                val dt = finishTime - startTime
                if (isPause.get()) return@Timer
                calendar.update(dt / hourSec / 1000)
                startTime = System.currentTimeMillis()
                repaint()
            }
        }.apply { start() }
    }

    fun stop() {
        timer.stop()
    }

    override fun paintComponent(g: Graphics) {
        super.paintComponent(g)
        trashModel.draw(g)
    }

    private fun pause() {
        isPause.set(true)
    }

    private fun resume() {
        isPause.set(false)
    }

    companion object {
        private var screenHeight = 0
        private var screenWidth = 0
        private lateinit var panel: Drawing
        private lateinit var frame: JFrame

        var hourSec: Double
            get() = panel.hourSec
            set(value) {
                panel.hourSec = value
            }

        fun start(): ModelRepository {
            panel = Drawing()
            frame = JFrame()
            frame.add(panel)
            frame.pack()
            frame.isVisible = true
            frame.extendedState = frame.extendedState or MAXIMIZED_BOTH

            val screenSize: Dimension = Toolkit.getDefaultToolkit().screenSize
            screenHeight = screenSize.height
            screenWidth = screenSize.width
            panel.start()
            return ModelRepository(panel.objectRepository, panel.calendar)
        }

        fun stop() {
            panel.stop()
            frame.remove(panel)
            frame.dispatchEvent(WindowEvent(frame, WindowEvent.WINDOW_CLOSING))
        }

        fun pause() {
            panel.pause()
        }

        fun resume() {
            panel.resume()
        }

        fun restart() {
            panel.restart()
        }

        fun xToPixels(x: Double): Int {
            return screenWidth / 2 + toPixels(x)
        }

        fun yToPixels(y: Double): Int {
            return screenHeight / 2 - toPixels(y)
        }

        fun toPixels(x: Double): Int {
            return (x * screenHeight / 4).toInt()
        }

        fun positionToPixels(x: Double, y: Double): Pair<Int, Int> {
            return xToPixels(x) to yToPixels(y)
        }
    }

    private fun restart() {
        trashModel.start()
    }
}

class ModelRepository(val objectRepository: ObjectRepository, private val calendar: EventCalendar) {
    val modelTime: Double
        get() = calendar.currentTime
}
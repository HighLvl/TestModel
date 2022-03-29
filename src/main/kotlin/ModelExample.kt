import dto.*
import project2.Ant
import project2.Board
import project2.Doodlebug
import project2.Simulator

class ModelExample : ModelApi {
    private var state: State = State.STOP
    private var t: Float = 0f

    private var testBoard: Board? = null

    private val responses = mutableListOf<Response>()


    private fun getState(): State {
        return state
    }

    private fun run(inputArgs: List<Any>) {
        val ants = inputArgs[1] as Int
        val bugs = inputArgs[0] as Int

        t = 0f
        state = State.RUN

        testBoard = Board(ants, bugs)
        testBoard?.printBoard()
    }

    private fun pause() {
        state = State.PAUSE
    }

    private fun stop() {
        state = State.STOP
        testBoard = null
    }

    private fun resume() {
        state = State.RUN
    }

    private fun getSnapshot(): Snapshot {
        if (state == State.RUN) {
            t += 1f
            Simulator.turnSequence(testBoard!!)
            testBoard!!.printBoard()
        }
        return createSnapshot(t.toDouble(), testBoard)
    }


    override fun handleRequests(requests: Requests): Responses {
        requests.requests.forEach {
            when (it.agentId) {
                0 -> processControlRequest(it.ack, it.name, it.args)
                else -> processAgentRequest(it.agentId, it.ack, it.name, it.args)
            }
        }

        val responses = responses.toList().also { responses.clear() }
        return Responses(responses)
    }

    private fun processControlRequest(ack: Int, name: String, args: List<Any>) {
        kotlin.runCatching {
            when (name) {
                "Run" -> run(args)
                "Stop" -> stop()
                "Pause" -> pause()
                "Resume" -> resume()
                "GetState" -> getState()
                "GetSnapshot" -> getSnapshot()
                else -> {}
            }
        }.onFailure {
            responses.add(Response(ack, false, Error(0, it.message.orEmpty())))
        }.onSuccess {
            responses.add(Response(ack, true, it))
        }

    }

    private fun processAgentRequest(agentId: Int, ack: Int, name: String, args: List<Any>) {
        when (agentId) {
            1 -> processBoardRequest(ack, name, args)
            else -> {
            }
        }
    }

    private fun processBoardRequest(ack: Int, name: String, args: List<Any>) {
        when (name) {
            "SetA" -> {
                Board.a = args[0] as Int
                responses.add(Response(ack, true, Unit))
            }
        }
    }

    private fun createSnapshot(t: Double, board: Board?): Snapshot {
        board ?: return Snapshot(t, mapOf())
        val ants = mutableListOf<AgentSnapshot>()
        val bugs = mutableListOf<AgentSnapshot>()
        board.state.field.flatten().forEach {
            when (it) {
                is Ant -> ants.add(it.getSnapshot())
                is Doodlebug -> bugs.add(it.getSnapshot())
            }
        }
        return Snapshot(
            t,
            mapOf("Ant" to ants, "Doodlebug" to bugs, "Board" to listOf(board.getSnapshot())),
        )
    }
}

fun Ant.getSnapshot(): AgentSnapshot {
    return AgentSnapshot(
        id,
        mapOf(
            "colPosition" to getColPosition(),
            "rowPosition" to getRowPosition(),
            "breedThreshold" to breedThreshold,
            "timeSinceBreed" to timeSinceBreed
        )
    )
}

fun Doodlebug.getSnapshot(): AgentSnapshot {
    return AgentSnapshot(
        id,
        mapOf(
            "colPosition" to getColPosition(),
            "rowPosition" to getRowPosition(),
            "breedThreshold" to breedThreshold,
            "timeSinceBreed" to timeSinceBreed,
            "timeSinceEat" to timeSinceEat
        )
    )
}

data class Size(val height: Int, val width: Int)

fun Board.getSnapshot(): AgentSnapshot {
    return AgentSnapshot(
        1,
        mapOf(
            "size" to Size(state.field.size, state.field[0].size),
            "a" to Board.a
        )
    )
}


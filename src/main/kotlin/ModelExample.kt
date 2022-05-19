import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.convertValue
import dto.*
import trash.*

class ModelExample : ModelApi {
    private var state: State = State.STOP

    private val responses = mutableListOf<Response>()
    private lateinit var repository: ModelRepository
    private lateinit var objectRepository: ObjectRepository
    private val objectMapper = ObjectMapper()


    private fun getState(): State {
        return state
    }

    private fun run(inputArgs: List<Any>) {
        repository = Drawing.start()
        objectRepository = repository.objectRepository
        state = State.RUN
    }

    private fun pause() {
        Drawing.pause()
        state = State.PAUSE
    }

    private fun stop() {
        Drawing.stop()
        state = State.STOP
    }

    private fun resume() {
        Drawing.resume()
        state = State.RUN
    }

    private fun getSnapshot(): Snapshot {
        return Snapshot(
            repository.modelTime,
            mapOf(
                "ModelSettings" to listOf(getModelSettingsSnapshot()),
                "Car" to listOf(getCarSnapshot()),
                "Parking" to listOf(getParkingSnapshot()),
                "Dump" to listOf(getDumpSnapshot()),
                "Turn" to getTurnSnapshots(),
                "GarbageCan" to getGarbageCanSnapshots()
            )
        )
    }

    private fun getModelSettingsSnapshot(): AgentSnapshot {
        return AgentSnapshot(-1, mapOf("hourSec" to Drawing.hourSec))
    }

    private fun getCarSnapshot(): AgentSnapshot {
        return convertToSnapshot(objectRepository.getCar())
    }

    private fun getParkingSnapshot(): AgentSnapshot {
        return convertToSnapshot(objectRepository.getParking())
    }

    private fun getDumpSnapshot(): AgentSnapshot {
        return convertToSnapshot(objectRepository.getDump())
    }

    private fun getTurnSnapshots(): List<AgentSnapshot> {
        return convertToSnapshots(objectRepository.getTurns())
    }

    private fun getGarbageCanSnapshots(): List<AgentSnapshot> {
        return convertToSnapshots(objectRepository.getGarbageCans())
    }

    private fun convertToSnapshots(list: List<Object>): List<AgentSnapshot> {
        return list.map { convertToSnapshot(it) }
    }

    private fun convertToSnapshot(obj: Object): AgentSnapshot {
        val carProps = objectMapper.convertValue<Map<String, Any>>(obj)
        return AgentSnapshot(obj.id, carProps)
    }


    override fun handleRequests(requests: Requests): Responses = synchronized(Drawing){
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
                else -> {
                }
            }
        }.onFailure {
            responses.add(Response(ack, false, Error(0, it.message.orEmpty())))
        }.onSuccess {
            responses.add(Response(ack, true, it))
        }

    }

    private fun processAgentRequest(agentId: Int, ack: Int, name: String, args: List<Any>) {
        if (agentId == -1) {
            if (name == "SetHourSec") {
                Drawing.hourSec = args[0] as Double
                responses.add(Response(ack, true, Unit))
            }
            if (name == "Restart") {
                Drawing.restart()
                responses.add(Response(ack, true, Unit))
            }
            return
        }
        when (val obj = objectRepository.objects[agentId]!!) {
            is GarbageCan -> obj.processGarbageCanRequest(ack, name, args)
            is Car -> obj.processCarRequest(ack, name, args)
            else -> { }
        }
    }

    private fun Car.processCarRequest(ack: Int, name: String, args: List<Any>) {
        when (name) {
            "SetSpeed" -> speed = args[0] as Int
            "SetRestTime" -> restTime = args[0] as Double
            "SetCapacity" -> capacity = args[0] as Double
            "SetWorkOnSchedule" -> workOnSchedule = args[0] as Boolean
            "SetFuelConsumption" -> fuelConsumption = args[0] as Int
            "SetTraversalOrder" -> traversalOrder = (args[0] as List<*>).map { objectRepository.objects[it] as Node }
        }
        responses.add(Response(ack, true, Unit))
    }

    private fun GarbageCan.processGarbageCanRequest(ack: Int, name: String, args: List<Any>) {
        when (name) {
            "SetCapacity" -> capacity = args[0] as Double
            "SetIntensity" -> intensity = args[0] as Double
            "SetCollectIntensity" -> collectIntensity = args[0] as Double
        }
        responses.add(Response(ack, true, Unit))
    }
}
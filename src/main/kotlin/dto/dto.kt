package dto

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.msgpack.jackson.dataformat.MessagePackFactory


private val objectMapper = ObjectMapper(MessagePackFactory())

data class Requests(val requests: List<Request> = listOf())
data class Request(val agentId: Int = 0, val name: String = "", val ack: Int = 0, val args: List<Any> = listOf())

data class Responses(val responses: List<Response> = listOf())
data class Response(val ack: Int = 0, val success: Boolean = true, val value: Any = Any())
data class Error(val code: Int = 0, val text: String = "")

data class Snapshot(val t: Double, val agentSnapshots: Map<String, List<AgentSnapshot>> = mapOf())
data class AgentSnapshot(val id: Int = 0, val props: Map<String, Any> = mapOf())

enum class State {
    RUN, STOP, PAUSE
}

fun ByteArray.mapToRequests(): Requests {
    return objectMapper.readValue(this)
}


fun Responses.mapToBytes(): ByteArray {
    return objectMapper.writeValueAsBytes(this)
}

import dto.Requests
import dto.Responses

interface ModelApi {
    fun handleRequests(requests: Requests): Responses
}
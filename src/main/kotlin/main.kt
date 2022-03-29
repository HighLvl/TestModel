import com.google.protobuf.kotlin.toByteString
import dto.mapToBytes
import dto.mapToRequests
import io.grpc.ServerBuilder
import kotlinx.coroutines.delay


fun main() {
    ServerBuilder.forPort(4444).addService(ApiServer(ModelExample())).build().start().awaitTermination()
}

class ApiServer(private val modelApi: ModelApi) : APIGrpcKt.APICoroutineImplBase() {

    override suspend fun handleRequests(request: Model.Requests): Model.Responses {
        val requests = request.data.toByteArray().mapToRequests()
        val responses = modelApi.handleRequests(requests)
        return Model.Responses.newBuilder().setData(responses.mapToBytes().toByteString()).build()
    }
}


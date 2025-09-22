package com.glambiase.run.network

import com.glambiase.core.data.networking.buildRoute
import com.glambiase.core.data.networking.delete
import com.glambiase.core.data.networking.get
import com.glambiase.core.data.networking.safeCall
import com.glambiase.core.domain.run.RemoteRunDataSource
import com.glambiase.core.domain.run.Run
import com.glambiase.core.domain.util.DataError
import com.glambiase.core.domain.util.EmptyResult
import com.glambiase.core.domain.util.Result
import com.glambiase.core.domain.util.map
import io.ktor.client.HttpClient
import io.ktor.client.request.forms.formData
import io.ktor.client.request.forms.submitFormWithBinaryData
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class KtorRemoteRunDataSource(
    private val httpClient: HttpClient
) : RemoteRunDataSource {

    override suspend fun getRuns(): Result<List<Run>, DataError.Network> =
        httpClient.get<List<RunDto>>(
            route = "/runs"
        ).map {
            it.map(RunDto::toRun)
        }

    override suspend fun postRun(run: Run, mapPicture: ByteArray): Result<Run, DataError.Network> {
        val createRunRequestJson = Json.encodeToString(run.toCreateRunRequest())
        val result = safeCall<RunDto> {
            httpClient.submitFormWithBinaryData(
                url = buildRoute("/run"),
                formData = formData {
                    append(
                        key = "MAP_PICTURE",
                        value = mapPicture,
                        headers = Headers.build {
                            append(HttpHeaders.ContentType, "image/jpeg")
                            append(HttpHeaders.ContentDisposition, "filename=map_picture.jpg")
                        }
                    )
                    append(
                        key = "RUN_DATA",
                        value = createRunRequestJson,
                        headers = Headers.build {
                            append(HttpHeaders.ContentType, "text/plain")
                            append(HttpHeaders.ContentDisposition, "form-data; name=\"RUN_DATA\"")
                        }
                    )
                }
            ) {
                method = HttpMethod.Post
            }
        }
        return result.map(RunDto::toRun)
    }

    override suspend fun deleteRun(id: String): EmptyResult<DataError.Network> =
        httpClient.delete(
            route = "/run",
            queryParameters = mapOf("id" to id)
        )
}
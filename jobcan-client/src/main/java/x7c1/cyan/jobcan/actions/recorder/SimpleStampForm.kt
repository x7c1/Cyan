package x7c1.cyan.jobcan.actions.recorder

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import x7c1.cyan.jobcan.JobcanLogger
import x7c1.cyan.jobcan.actions.start.Sid
import java.io.BufferedWriter
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

class SimpleStampForm(
    private val logger: JobcanLogger,
    private val sid: Sid) {

    data class Response(
        val statusCode: Int,
        val location: String?
    )

    data class Parameter(
        val token: Token,
        val groupId: GroupId
    )

    suspend fun submit(parameter: Parameter): Response {
        logger.info("[submit] started: $parameter")
        val response = requestBy(mapOf(
            "token" to parameter.token.value,
            "time" to "",
            "adit_item" to "打刻",
            "gps" to 0,
            "group_id" to parameter.groupId.value,
            "reason" to ""
        ))
        logger.info("[submit] done.")
        return response
    }

    private val encodeParams = { params: Map<String, Any> ->
        val encode = { x: String ->
            URLEncoder.encode(x, "UTF-8")
        }
        params.entries.associate {
            encode(it.key) to encode(it.value.toString())
        }.map { (k, v) ->
            "$k=$v"
        }.joinToString(
            separator = "&",
            prefix = "?"
        )
    }

    private suspend fun requestBy(params: Map<String, Any>): Response = withContext(Dispatchers.Default) {
        val url = URL("https://jobcan.jp/m/work/simplestamp")
        logger.info("[SimpleStampForm] request started: $url")

        val content = encodeParams(params)
        val connection = (url.openConnection() as HttpURLConnection).also {
            it.requestMethod = "POST"
            it.instanceFollowRedirects = false
            it.readTimeout = 10 * 1000
            it.connectTimeout = 10 * 1000
            it.useCaches = false
            it.doOutput = true
            it.doInput = true
            propertiesOf(content).forEach { (k, v) -> it.setRequestProperty(k, v) }
            it.connect()
        }
        connection.outputStream.use {
            BufferedWriter(OutputStreamWriter(it, "UTF-8")).use {
                it.write(content)
                it.flush()
            }
        }
        Response(
            statusCode = connection.responseCode,
            location = connection.getHeaderField("Location")
        )
    }

    private fun propertiesOf(content: String): Map<String, String> {
        return mapOf(
            "Accept" to "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,c*/*;q=0.8",
            "Accept-Language" to "en-US,en;q=0.9,ja;q=0.8,ja-JP;q=0.7",
            "Content-Length" to content.toByteArray().size.toString(),
            "Content-Type" to "application/x-www-form-urlencoded",
            "Cookie" to "sid=${sid.value}",
            "Referer" to "https://jobcan.jp/m/work/accessrecord?_m=adit"
        )
    }
}

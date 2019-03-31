package x7c1.cyan.jobcan.actions.recorder

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import x7c1.cyan.jobcan.JobcanLogger
import x7c1.cyan.jobcan.actions.start.Sid
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

data class RecorderPage(
    val token: Token,
    val groups: List<Group>
)

class RecorderPageLoader(
    private val logger: JobcanLogger) {

    /**
     * @throws TokenNotFound
     */
    suspend fun loadFrom(sid: Sid): RecorderPage {
        val content = loadContent(sid)
        val document = Jsoup.parse(content)
        return RecorderPage(
            token = extractToken(document),
            groups = extractGroups(document)
        )
    }

    private suspend fun loadContent(sid: Sid) = withContext(Dispatchers.Default) {
        val url = URL("https://jobcan.jp/m/work/accessrecord?_m=adit")
        logger.info("[RecorderPage] request started: $url")

        val connection = (url.openConnection() as HttpURLConnection).also {
            it.setRequestProperty("Cookie", "sid=${sid.value}")
            it.requestMethod = "GET"
            it.connect()
        }
        val content = connection.inputStream.use {
            InputStreamReader(it).use {
                BufferedReader(it).use {
                    it.readLines().joinToString("\n")
                }
            }
        }
        logger.info("[RecorderPage] received. (size: ${content.length})")
        content
    }

    private fun extractToken(document: Document): Token {
        val value = document
            .select("input[name=token]")
            .attr("value")

        if (value == null || value.isBlank()) {
            throw TokenNotFound(document.html())
        }

//        val regex = """<input type="hidden" class="token" name="token" value="(\w+)">""".toRegex()
//        val value = regex.find(content)
//            ?.groupValues
//            ?.get(1)
//            ?: throw TokenNotFound(content)

        return Token(value)
    }

    private fun extractGroups(document: Document): List<Group> {
        val xs = document.select("select[name=group_id] option")
        return xs.map {
            Group(
                id = GroupId(it.attr("value")),
                label = it.text()
            )
        }
    }
}

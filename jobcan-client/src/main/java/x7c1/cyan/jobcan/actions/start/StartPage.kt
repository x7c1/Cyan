package x7c1.cyan.jobcan.actions.start

import kotlinx.coroutines.experimental.DefaultDispatcher
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.withContext
import x7c1.cyan.jobcan.JobcanLogger
import java.net.HttpCookie
import java.net.HttpURLConnection
import java.net.URL

data class StartPage(val sid: Sid)

class StartPageLoader(
    private val logger: JobcanLogger) {

    /**
     * @throws StartPageNotLoaded
     * @throws SidNotFound
     */
    suspend fun loadFrom(code: String): StartPage {
        val cookies = loadCookies(code)
        val sid = cookies.flatMap(HttpCookie::parse).lastOrNull { it.name == "sid" }
            ?.value
            ?: throw SidNotFound(code)

        return StartPage(Sid(sid))
    }

    private suspend fun loadCookies(code: String) = withContext(DefaultDispatcher) {
        val url = URL("https://ssl.jobcan.jp/m/?code=$code")
        logger.info("[StartPage] request started: $url")

        val connection = (url.openConnection() as HttpURLConnection).also {
            it.requestMethod = "GET"
            it.instanceFollowRedirects = false
            it.connect()
        }
        if (connection.responseCode == 302) {
            throw StartPageNotLoaded(connection.getHeaderField("Location"))
        }
        logger.info("[StartPage] received.")
        connection.headerFields.get("Set-Cookie").orEmpty()
    }
}

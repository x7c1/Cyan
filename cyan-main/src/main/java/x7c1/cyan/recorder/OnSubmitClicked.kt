package x7c1.cyan.recorder

import android.view.View
import x7c1.cyan.jobcan.JobcanClient
import x7c1.cyan.jobcan.JobcanLogger
import x7c1.cyan.repository.GlobalSettings

class OnSubmitClicked(
    private val provider: JobcanCodeProvider,
    private val settings: GlobalSettings,
    private val console: Console) : OnClickSuspender {

    private val logger = LoggerAdapter(console)

    override suspend fun onClickView(v: View?) {
        val code = provider.loadCurrentCode()
        if (code == null || code.isBlank()) {
            console.error("code required.")
            return
        }
        settings.updateCurrentCode(code)
        console.info("code updated to: $code")

        try {
            val client = JobcanClient(code, logger)
            client.punchIn()
            console.info("done by: $client")
        } catch (e: Exception) {
            console.error("punch-in failed: $e")
        }
    }
}

private class LoggerAdapter(private val console: Console) : JobcanLogger {

    override suspend fun info(message: String) {
        console.info(message)
    }

    override suspend fun error(message: String) {
        console.error(message)
    }

    override fun toString(): String {
        return "LoggerAdapter(console=$console)"
    }
}

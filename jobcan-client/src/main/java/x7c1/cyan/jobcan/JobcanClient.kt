package x7c1.cyan.jobcan

import x7c1.cyan.jobcan.actions.recorder.RecorderPageLoader
import x7c1.cyan.jobcan.actions.recorder.SimpleStampForm
import x7c1.cyan.jobcan.actions.recorder.SimpleStampForm.Parameter
import x7c1.cyan.jobcan.actions.start.StartPageLoader

class JobcanClient(
    private val code: String,
    private val logger: JobcanLogger
) {
    suspend fun punchIn() {
        val main = StartPageLoader(logger).loadFrom(code)
        logger.info("sid loaded: $main")

        val page = RecorderPageLoader(logger).loadFrom(main.sid)
        logger.info("token loaded: $page")

        val response = SimpleStampForm(logger, main.sid).submit(Parameter(
            token = page.token,
            groupId = page.groups.first().id
        ))
        logger.info("[punchIn] done: $response")
    }

    override fun toString(): String {
        return "JobcanClient(code='$code', logger=$logger)"
    }

}

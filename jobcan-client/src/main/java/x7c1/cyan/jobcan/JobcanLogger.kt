package x7c1.cyan.jobcan

interface JobcanLogger {

    suspend fun info(message: String)

    suspend fun error(message: String)
}

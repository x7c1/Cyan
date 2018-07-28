package x7c1.cyan.recorder

interface Console {

    suspend fun info(message: String)

    suspend fun error(message: String)
}

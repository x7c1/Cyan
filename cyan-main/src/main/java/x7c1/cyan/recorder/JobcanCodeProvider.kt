package x7c1.cyan.recorder

interface JobcanCodeProvider {
    suspend fun loadCurrentCode(): String?
}

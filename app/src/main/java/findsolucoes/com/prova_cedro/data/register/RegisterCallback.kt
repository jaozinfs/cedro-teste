package findsolucoes.com.prova_cedro.data.register

import findsolucoes.com.prova_cedro.data.login.LoginResponse

interface RegisterCallback{

    fun registerResponse(registerResponse: RegisterResponse)
    fun registerError(error: Throwable, cause: String)
}
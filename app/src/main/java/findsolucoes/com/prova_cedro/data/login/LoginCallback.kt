package findsolucoes.com.prova_cedro.data.login

interface LoginCallback{

    fun loginResponse(loginResponse: LoginResponse)
    fun loginError(error: Throwable)
}
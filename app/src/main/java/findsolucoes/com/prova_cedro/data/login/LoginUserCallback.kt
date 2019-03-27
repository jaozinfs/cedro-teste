package findsolucoes.com.prova_cedro.data.login

import findsolucoes.com.prova_cedro.entites.UserEntity

/**
 * Insert User Object in database callback
 * sucess (Userentity)
 * error (Trowable)
 */
interface LoginUserCallback{
    fun onSucess()
    fun onError(error : Throwable)
}
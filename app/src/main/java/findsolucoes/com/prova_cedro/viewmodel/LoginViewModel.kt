package findsolucoes.com.prova_cedro.viewmodel

import android.app.Application
import android.util.Log
import android.util.MutableBoolean
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.jakewharton.retrofit2.adapter.rxjava2.HttpException
import findsolucoes.com.prova_cedro.R
import findsolucoes.com.prova_cedro.data.login.LoginCallback
import findsolucoes.com.prova_cedro.data.login.LoginCredentials
import findsolucoes.com.prova_cedro.data.login.LoginResponse
import findsolucoes.com.prova_cedro.entites.UserEntity
import findsolucoes.com.prova_cedro.repositories.LoginRepository
import findsolucoes.com.prova_cedro.repositories.LogindataRepository


class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private var loginRopo: LoginRepository? = null
    private var progressState  = MutableLiveData<Boolean>()
    private var toastMessage = MutableLiveData<Boolean>()
    private var logindataRepository: LogindataRepository? = null
    private lateinit var apc: Application
    //
    private var messageToast =  MutableLiveData<String>()


    init {

        loginRopo = LoginRepository(application)
        progressState = MutableLiveData()
        logindataRepository = LogindataRepository(application)
        apc = application
    }



    fun login(loginCredentials: LoginCredentials){
        progressState.value = true

        logindataRepository!!.loginRequest(loginCredentials, object : LoginCallback {
            override fun loginResponse(loginResponse: LoginResponse) {
                //dismiss progress
                progressState.value = false

                if(loginResponse.message == null){
                    messageToast.value = loginResponse.message
                    return
                }
                messageToast.value = loginResponse.token
            }

            override fun loginError(error: Throwable) {
                progressState.value = false
                // messageToast.value = apc.getString(R.string.internal_error)

                messageToast.value = error.message
                return
            }
        })
    }

    //
    fun getProgressState() : LiveData<Boolean> = progressState
    fun getToastError() : LiveData<String> = messageToast

}
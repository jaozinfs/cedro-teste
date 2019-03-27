package findsolucoes.com.prova_cedro.viewmodel

import android.app.Application
import android.text.TextUtils
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import findsolucoes.com.prova_cedro.data.login.LoginCallback
import findsolucoes.com.prova_cedro.data.login.LoginCredentials
import findsolucoes.com.prova_cedro.data.login.LoginResponse
import findsolucoes.com.prova_cedro.repositories.LoginRepository
import findsolucoes.com.prova_cedro.repositories.LogindataRepository
import findsolucoes.com.prova_cedro.utils.Utils


class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private var loginRopo: LoginRepository? = null
    private var progressState  = MutableLiveData<Boolean>()
    private var logindataRepository: LogindataRepository? = null
    private var apc: Application
    private var messageToast =  MutableLiveData<String>()


    init {

        loginRopo = LoginRepository(application)
        progressState = MutableLiveData()
        logindataRepository = LogindataRepository(application)
        apc = application
    }



    fun login(loginCredentials: LoginCredentials){
        progressState.value = true

        verifyCredentials(loginCredentials)

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
    fun getToastMessage() : LiveData<String> = messageToast


    /**
     * Verify credentials
     */
    fun verifyCredentials(credentials: LoginCredentials){
        //strings errors
        val error_invalid_email = apc.getString(findsolucoes.com.prova_cedro.R.string.error_invalid_email)
        val error_invalid_password = apc.getString(findsolucoes.com.prova_cedro.R.string.error_invalid_password)


        if(TextUtils.isEmpty(credentials.email) ||
            !android.util.Patterns.EMAIL_ADDRESS.matcher(credentials.email).matches()){
            messageToast.value = error_invalid_email
            return
        }

//        if(Utils.isLegalPassword(credentials.password)){
//
//
//        }
    }



}
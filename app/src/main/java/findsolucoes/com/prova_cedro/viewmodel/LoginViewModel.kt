package findsolucoes.com.prova_cedro.viewmodel

import android.app.Application
import android.text.TextUtils
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import findsolucoes.com.prova_cedro.data.login.LoginCallback
import findsolucoes.com.prova_cedro.data.login.LoginCredentials
import findsolucoes.com.prova_cedro.data.login.LoginResponse
import findsolucoes.com.prova_cedro.repositories.UserRepository
import findsolucoes.com.prova_cedro.repositories.LogindataRepository


class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private var userRopo: UserRepository? = null
    private var progressState  = MutableLiveData<Boolean>()
    private var logindataRepository: LogindataRepository? = null
    private var apc: Application
    private var messageToast =  MutableLiveData<String>()
    private var messageInputEmail =  MutableLiveData<String>()
    private var messageInputPassword =  MutableLiveData<String>()
    private var clearInputErrors = MutableLiveData<Boolean>()

    init {

        userRopo = UserRepository(application)
        progressState = MutableLiveData()
        logindataRepository = LogindataRepository(application)
        apc = application
    }



    fun login(loginCredentials: LoginCredentials){
        progressState.value = true
        clearInputErrors.value = true

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
    fun getInputEmailMessage() : LiveData<String> = messageInputEmail
    fun getInputPasswordMessage() : LiveData<String> = messageInputPassword
    fun getIsClearInputMessages() : LiveData<Boolean> = clearInputErrors

    /**
     * Verify credentials
     */
    fun verifyCredentials(credentials: LoginCredentials) : Boolean{
        //strings errors
        val error_invalid_email = apc.getString(findsolucoes.com.prova_cedro.R.string.error_invalid_email)
        val error_empty_password = apc.getString(findsolucoes.com.prova_cedro.R.string.error_empty_password)


        if(TextUtils.isEmpty(credentials.email) ||
            !android.util.Patterns.EMAIL_ADDRESS.matcher(credentials.email).matches()){
            messageInputEmail.value = error_invalid_email
            return false
        }

        if(TextUtils.isEmpty(credentials.password)){
            messageInputPassword.value = error_empty_password
            return false
        }

        return true
    }



}
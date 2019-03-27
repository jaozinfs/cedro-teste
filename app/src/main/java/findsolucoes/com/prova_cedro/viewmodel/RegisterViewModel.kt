package findsolucoes.com.prova_cedro.viewmodel

import android.app.Application
import android.text.TextUtils
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import findsolucoes.com.prova_cedro.data.login.LoginCallback
import findsolucoes.com.prova_cedro.data.login.LoginCredentials
import findsolucoes.com.prova_cedro.data.login.LoginResponse
import findsolucoes.com.prova_cedro.data.register.RegisterCallback
import findsolucoes.com.prova_cedro.data.register.RegisterCredentials
import findsolucoes.com.prova_cedro.data.register.RegisterResponse
import findsolucoes.com.prova_cedro.repositories.LoginRepository
import findsolucoes.com.prova_cedro.repositories.LogindataRepository
import findsolucoes.com.prova_cedro.repositories.RegisterdataRepository
import findsolucoes.com.prova_cedro.utils.Utils
import javax.xml.transform.Templates

class RegisterViewModel(application: Application) : AndroidViewModel(application) {

    private lateinit var registerdataRepository: RegisterdataRepository
    private var progressState  = MutableLiveData<Boolean>()
    private var apc: Application
    private var messageToast =  MutableLiveData<String>()
    private var messageInputName =  MutableLiveData<String>()
    private var messageInputEmail =  MutableLiveData<String>()
    private var messageInputPassword =  MutableLiveData<String>()
    private var clearInputErrors = MutableLiveData<Boolean>()


    init {
        registerdataRepository = RegisterdataRepository(application)
        progressState = MutableLiveData()
        apc = application
    }

    //uiview register click
    fun register(registerCredentials: RegisterCredentials){
        //;show progress wait
        //.make verifications register credentials
        //.request
        //.stopprogress

        //progress show
        progressState.value = true
        clearInputErrors.value = true

        //verifications
        if(!verifyCredentials(registerCredentials)) {
            progressState.value = false
            return
        }

        //call request
        registerdataRepository.requestRegister(registerCredentials, object : RegisterCallback {
            override fun registerResponse(registerResponse: RegisterResponse) {
                progressState.value = false
                Log.d("TAG", "Message: ${registerResponse.message}")
                Log.d("TAG", "Token: ${registerResponse.token}")
                Log.d("TAG", "Type: ${registerResponse.type}")
            }
            override fun registerError(error: Throwable, cause: String) {
                progressState.value = false

                //set error
                messageToast.value = error.message
                when {
                    error.message.equals("Invalid name") -> messageInputName.value = cause
                    error.message.equals("Invalid email") -> messageInputEmail.value = cause
                    error.message.equals("Invalid password") -> messageInputPassword.value = cause
                }
                return
            }})
    }

    //viewmodel state of uiview
    //progress wait
    fun getProgressState() : LiveData<Boolean> = progressState
    //message in toast
    fun getToastMessage() : LiveData<String> = messageToast
    fun getInputNameMessage() : LiveData<String> = messageInputName
    fun getInputEmailMessage() : LiveData<String> = messageInputEmail
    fun getInputPasswordMessage() : LiveData<String> = messageInputPassword
    fun getIsClearInputMessages() : LiveData<Boolean> = clearInputErrors

    /**
     * Verify credentials
     */
    fun verifyCredentials(registerCredentials: RegisterCredentials): Boolean{
        //strings errors
        val error_invalid_name = apc.getString(findsolucoes.com.prova_cedro.R.string.error_invalid_name)
        val error_invalid_email = apc.getString(findsolucoes.com.prova_cedro.R.string.error_invalid_email)
        val error_invalid_password = apc.getString(findsolucoes.com.prova_cedro.R.string.error_invalid_password)


        if(TextUtils.isEmpty(registerCredentials.name)){
            messageToast.value = error_invalid_name
            messageInputName.value = error_invalid_name

            return false
        }
        if(TextUtils.isEmpty(registerCredentials.email) ||
            !android.util.Patterns.EMAIL_ADDRESS.matcher(registerCredentials.email).matches()){
            messageToast.value = error_invalid_email
            messageInputEmail.value = error_invalid_email
            return false
        }
        if(TextUtils.isEmpty(registerCredentials.password)){
            messageToast.value = error_invalid_password
            messageInputName.value = error_invalid_password

            return false
        }
        return true
    }
}
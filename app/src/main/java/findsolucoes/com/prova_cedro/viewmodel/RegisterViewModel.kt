package findsolucoes.com.prova_cedro.viewmodel

import android.app.Application
import android.text.TextUtils
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import findsolucoes.com.prova_cedro.R
import findsolucoes.com.prova_cedro.data.login.LoginUserCallback
import findsolucoes.com.prova_cedro.data.register.RegisterCallback
import findsolucoes.com.prova_cedro.data.register.RegisterCredentials
import findsolucoes.com.prova_cedro.data.register.RegisterResponse
import findsolucoes.com.prova_cedro.entites.UserEntity
import findsolucoes.com.prova_cedro.repositories.UserRepository
import findsolucoes.com.prova_cedro.repositories.RegisterdataRepository

class RegisterViewModel(application: Application) : AndroidViewModel(application) {

    private var registerdataRepository: RegisterdataRepository = RegisterdataRepository(application)
    private var progressState  = MutableLiveData<Boolean>()
    private var apc: Application
    private var messageToast =  MutableLiveData<String>()
    private var messageInputName =  MutableLiveData<String>()
    private var messageInputEmail =  MutableLiveData<String>()
    private var messageInputPassword =  MutableLiveData<String>()
    private var clearInputErrors = MutableLiveData<Boolean>()
    private val loginRepository: UserRepository = UserRepository(application)
    private val registerSucessFull = MutableLiveData<Boolean>()

    init {
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

                //delete all user from table
                deleteUser(object : LoginUserCallback{
                    override fun onSucess() {
                        //create user
                        createUser(registerCredentials, registerResponse.token)
                    }
                    override fun onError(error: Throwable) { messageToast.value = apc.getString(R.string.internal_error)}
                })

            }
            override fun registerError(error: Throwable, cause: String) {
                progressState.value = false

                //set error
                messageToast.value = error.message
                when {
                    error.message.equals("Invalid name") -> messageInputName.value = cause
                    error.message.equals("Invalid email") -> messageInputEmail.value = cause
                    error.message.equals("Invalid password") -> messageInputPassword.value = cause
                    error.message.equals("Duplicate key for property email: ${registerCredentials.email}") -> messageInputEmail.value = error.message
                }
                return
            }})
    }

    //verify credentials
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
            messageInputPassword.value = error_invalid_password
            return false
        }

        return true
    }

    //add user in database
    fun createUser(registerCredentials: RegisterCredentials, token: String){
        //database repository
        loginRepository.insertUser(UserEntity(0,
            registerCredentials.name,
            registerCredentials.email, token), object : LoginUserCallback{
            override fun onSucess() {
                registerSucessFull.value = true
            }

            override fun onError(error: Throwable) { messageToast.value = apc.getString(R.string.internal_error) }})

    }

    //delete user from table to create another
    fun deleteUser(loginUserCallback: LoginUserCallback){
        loginRepository.deleteUser(loginUserCallback)
    }


    //////////////////////////////
    //viewmodel state of uiview///
    //////////////////////////////

    //progress wait
    fun getProgressState() : LiveData<Boolean> = progressState
    //message in toast
    fun getToastMessage() : LiveData<String> = messageToast
    fun getInputNameMessage() : LiveData<String> = messageInputName
    fun getInputEmailMessage() : LiveData<String> = messageInputEmail
    fun getInputPasswordMessage() : LiveData<String> = messageInputPassword
    fun getIsClearInputMessages() : LiveData<Boolean> = clearInputErrors
    fun registerSucessFull() : LiveData<Boolean> = registerSucessFull
}
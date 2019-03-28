package findsolucoes.com.prova_cedro.viewmodel

import android.app.Application
import android.text.TextUtils
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import findsolucoes.com.prova_cedro.R
import findsolucoes.com.prova_cedro.data.login.LoginCallback
import findsolucoes.com.prova_cedro.data.login.LoginCredentials
import findsolucoes.com.prova_cedro.data.login.LoginResponse
import findsolucoes.com.prova_cedro.data.login.LoginUserCallback
import findsolucoes.com.prova_cedro.data.register.RegisterCredentials
import findsolucoes.com.prova_cedro.entites.UserEntity
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
    private val loginRepository: UserRepository = UserRepository(application)
    private val loginSucessFull = MutableLiveData<Boolean>()

    init {

        userRopo = UserRepository(application)
        progressState = MutableLiveData()
        logindataRepository = LogindataRepository(application)
        apc = application
    }


    //login button click from view
    fun login(loginCredentials: LoginCredentials){
        progressState.value = true
        clearInputErrors.value = true

        if(!verifyCredentials(loginCredentials)){
            progressState.value = false
            return
        }

        logindataRepository!!.loginRequest(loginCredentials, object : LoginCallback {
            override fun loginResponse(loginResponse: LoginResponse) {
                //dismiss progress
                progressState.value = false

                if(loginResponse.type.equals("sucess")){
                    //set errors message
                    messageToast.value = loginResponse.message
                    //sucess case
                    //init user database delete all users and create new
                    //
                    //delete all user from table
                    deleteUser(loginCredentials, loginResponse.token)
                }
            }

            override fun loginError(error: Throwable) {
                progressState.value = false
                // messageToast.value = apc.getString(R.string.internal_error)

                messageToast.value = error.message
                if(error.message.equals("Invalid e-mail or password")){
                    messageInputEmail.value = " "
                    messageInputPassword.value = " "
                }
                return
            }
        })
    }

    //ui view observers
    fun getProgressState() : LiveData<Boolean> = progressState
    fun getToastMessage() : LiveData<String> = messageToast
    fun getInputEmailMessage() : LiveData<String> = messageInputEmail
    fun getInputPasswordMessage() : LiveData<String> = messageInputPassword
    fun getIsClearInputMessages() : LiveData<Boolean> = clearInputErrors
    fun loginSucessfull() : LiveData<Boolean> = loginSucessFull


    //verify credentials to continue with login response
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

    //delete user from table to create another
    fun deleteUser(loginCredentials: LoginCredentials, token: String){
        loginRepository.deleteUser( object : LoginUserCallback {
            override fun onSucess() {
                //create user
                createUser(loginCredentials, token)
            }
            override fun onError(error: Throwable) { messageToast.value = apc.getString(R.string.internal_error)}
        })
    }

    //add user in database
    fun createUser(loginCredentials: LoginCredentials, token: String){
        //database repository
        loginRepository.insertUser(
            UserEntity(0, "", loginCredentials.email, token), object : LoginUserCallback{
                override fun onSucess() { loginSucessFull.value = true }
                override fun onError(error: Throwable) { messageToast.value = apc.getString(R.string.internal_error) }})
    }
}
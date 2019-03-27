package findsolucoes.com.prova_cedro.repositories

import android.app.Application
import com.google.gson.JsonParser
import com.jakewharton.retrofit2.adapter.rxjava2.HttpException
import findsolucoes.com.prova_cedro.data.RetrofitAPI
import findsolucoes.com.prova_cedro.data.login.LoginCallback
import findsolucoes.com.prova_cedro.data.login.LoginCredentials
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

class LogindataRepository(application : Application){

    private var retrofitAPI: RetrofitAPI
    private var LoginRepository: UserRepository

    init {
        retrofitAPI = RetrofitAPI(application.resources)
        LoginRepository = UserRepository(application)
    }

    /**
     * Request login with callback
     */
    fun loginRequest(loginCredentials: LoginCredentials, loginCallback: LoginCallback){
        retrofitAPI.loginRequest(LoginCredentials(loginCredentials.email, loginCredentials.password))
            .timeout(50000, TimeUnit.SECONDS)
            .subscribeOn(Schedulers.io())
            .unsubscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(

                {

                    loginCallback.loginResponse(it)
                },

                {
                    val error = it as HttpException
                    val errorJsonString = error.response()
                        .errorBody()?.string()
                    val message = JsonParser().parse(errorJsonString)
                        .asJsonObject["message"]
                        .asString

                    loginCallback.loginError(Throwable(message))
                })

    }


}
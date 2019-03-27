package findsolucoes.com.prova_cedro.repositories

import android.app.Application
import com.google.gson.JsonParser
import com.jakewharton.retrofit2.adapter.rxjava2.HttpException
import findsolucoes.com.prova_cedro.R
import findsolucoes.com.prova_cedro.data.RetrofitAPI
import findsolucoes.com.prova_cedro.data.register.RegisterCallback
import findsolucoes.com.prova_cedro.data.register.RegisterCredentials
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.json.JSONArray
import java.util.concurrent.TimeUnit


class RegisterdataRepository(application : Application){
    private var retrofitAPI: RetrofitAPI
    private var apc: Application = application
    init {
        retrofitAPI = RetrofitAPI(application.resources)
    }


    //rest request post register with params register credentials @name,@email,@password body
    fun requestRegister(registerCredentials: RegisterCredentials, registerCallback: RegisterCallback){

        retrofitAPI.registerRequest(registerCredentials)
            .timeout(50000, TimeUnit.SECONDS)
            .subscribeOn(Schedulers.io())
            .unsubscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(

                {

                    registerCallback.registerResponse(it)
                },

                {
                    val error = it as HttpException
                    val errorJsonString = error.response()
                        .errorBody()?.string()
                    val message = JsonParser().parse(errorJsonString)
                        .asJsonObject["message"]
                        .asString

                    var cause = apc.getString(R.string.internal_error)

                    if(JsonParser().parse(errorJsonString).asJsonObject.has("errors")){
                        cause = JsonParser().parse(errorJsonString).asJsonObject["errors"].asJsonArray[0].asString
                    }

                    registerCallback.registerError(Throwable(message), cause)
                })
    }

}
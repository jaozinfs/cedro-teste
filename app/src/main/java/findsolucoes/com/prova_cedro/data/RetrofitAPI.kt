package findsolucoes.com.prova_cedro.data

import android.content.res.Resources
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import findsolucoes.com.prova_cedro.R
import findsolucoes.com.prova_cedro.data.login.LoginCredentials
import findsolucoes.com.prova_cedro.data.login.LoginResponse
import io.reactivex.Observable
import io.reactivex.internal.operators.observable.ObservableAll
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class RetrofitAPI(resources: Resources) : LogoDatasource{
    override fun loginRequest(loginCredentials: LoginCredentials): Observable<LoginResponse> {
        return logoDatasource.loginRequest(loginCredentials)
    }


    private var logoDatasource: LogoDatasource
    init {
        val url = resources.getString(R.string.base_url)

        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(interceptor) // same for .addInterceptor(...)
            .connectTimeout(30, TimeUnit.SECONDS) //Backend is really slow
            .writeTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()

        val retrofit : Retrofit = Retrofit.Builder()
            .baseUrl(url)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()

        logoDatasource = retrofit.create(LogoDatasource::class.java)

    }



}
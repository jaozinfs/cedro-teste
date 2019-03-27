package findsolucoes.com.prova_cedro.data.login

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Response.success

fun <T> callback(response: (response: Response<T>?) -> Unit): Callback<T> {
    return object : Callback<T> {
        override fun onResponse(call: Call<T>?, response: Response<T>?) {
            success(response)
        }

        override fun onFailure(call: Call<T>?, t: Throwable?) {

        }
    }
}
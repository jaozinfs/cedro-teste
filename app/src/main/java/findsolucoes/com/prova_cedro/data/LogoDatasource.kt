package findsolucoes.com.prova_cedro.data
import findsolucoes.com.prova_cedro.data.login.LoginCredentials
import findsolucoes.com.prova_cedro.data.login.LoginResponse
import io.reactivex.Observable
import io.reactivex.internal.operators.observable.ObservableAll
import okhttp3.Credentials
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface LogoDatasource {

    @POST("login")
    fun loginRequest(@Body loginCredentials: LoginCredentials) : Observable<LoginResponse>

}
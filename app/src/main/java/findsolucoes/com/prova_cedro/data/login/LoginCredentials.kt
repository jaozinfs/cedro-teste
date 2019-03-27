package findsolucoes.com.prova_cedro.data.login

import com.google.gson.annotations.SerializedName

data class LoginCredentials(
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String
   )


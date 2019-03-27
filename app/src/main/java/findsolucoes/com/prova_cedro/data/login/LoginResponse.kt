package findsolucoes.com.prova_cedro.data.login

import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @SerializedName("type") val type: String,
    @SerializedName("message") val message: String,
    @SerializedName("token") val token: String
)
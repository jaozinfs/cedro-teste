package findsolucoes.com.prova_cedro.data.register

import com.google.gson.annotations.SerializedName

data class RegisterResponse(
    @SerializedName("type") val type: String,
    @SerializedName("message") val message: String,
    @SerializedName("token") val token: String
)
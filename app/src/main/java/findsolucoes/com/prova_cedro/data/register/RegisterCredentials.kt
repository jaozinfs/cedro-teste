package findsolucoes.com.prova_cedro.data.register

import com.google.gson.annotations.SerializedName

data class RegisterCredentials(
    @SerializedName("name")val name: String,
    @SerializedName("email")val email: String,
    @SerializedName("password")val password: String
)
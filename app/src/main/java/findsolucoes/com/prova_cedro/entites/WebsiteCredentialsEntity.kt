package findsolucoes.com.prova_cedro.entites

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
class WebsiteCredentialsEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val url: String,
    val email: String,
    val password: String
)
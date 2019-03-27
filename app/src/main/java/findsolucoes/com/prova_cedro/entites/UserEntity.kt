package findsolucoes.com.prova_cedro.entites

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
class UserEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val email: String,
    val token: String)
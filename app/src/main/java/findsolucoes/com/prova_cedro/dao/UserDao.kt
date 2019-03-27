package findsolucoes.com.prova_cedro.dao

import androidx.room.*
import findsolucoes.com.prova_cedro.entites.UserEntity

@Dao
interface UserDao {

    @Query("SELECT * FROM userentity")
    fun getUser(): UserEntity

    @Insert
    fun add(vararg user: UserEntity)

    @Delete
    fun delete(vararg  user: UserEntity)

    @Update
    fun updateUser(vararg user: UserEntity)

}
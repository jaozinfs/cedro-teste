package findsolucoes.com.prova_cedro.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import findsolucoes.com.prova_cedro.entites.UserEntity
import findsolucoes.com.prova_cedro.entites.WebsiteCredentialsEntity

@Dao
interface WebsiteCredentialsDao {

    @Query("SELECT * FROM websitecredentialsentity")
    fun getAllWebsiteCredentials(): List<WebsiteCredentialsEntity>

    @Insert
    fun add(websiteCredentialsEntity: WebsiteCredentialsEntity)

    @Update
    fun updateWebsiteCredentials(vararg websiteCredentialsEntity: WebsiteCredentialsEntity)

    @Delete
    fun deleteWebsiteCredentials(websiteCredentialsEntity: WebsiteCredentialsEntity)

}
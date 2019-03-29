package findsolucoes.com.prova_cedro.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import findsolucoes.com.prova_cedro.dao.UserDao
import findsolucoes.com.prova_cedro.dao.WebsiteCredentialsDao
import findsolucoes.com.prova_cedro.entites.UserEntity
import findsolucoes.com.prova_cedro.entites.WebsiteCredentialsEntity

@Database(entities = [UserEntity::class, WebsiteCredentialsEntity::class], version = 1, exportSchema = false)
abstract class LogoDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun websiteCredentialsDao() : WebsiteCredentialsDao

    companion object {
        var INSTANCE: LogoDatabase? = null

        fun  getAppDataBase(context: Context): LogoDatabase? {
            synchronized(this){
                if (INSTANCE == null) {
                    synchronized(LogoDatabase::class) {
                        INSTANCE =
                            Room.databaseBuilder(context.applicationContext, LogoDatabase::class.java, "database").build()
                    }
                }
                return INSTANCE
            }
        }

        fun destroyDataBase(){
            INSTANCE = null
        }
    }
}
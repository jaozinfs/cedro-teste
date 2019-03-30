package findsolucoes.com.prova_cedro.repositories

import android.app.Application
import android.os.AsyncTask
import android.util.Log
import androidx.room.RoomDatabase
import findsolucoes.com.prova_cedro.database.LogoDatabase
import findsolucoes.com.prova_cedro.dao.UserDao
import findsolucoes.com.prova_cedro.data.RetrofitAPI
import findsolucoes.com.prova_cedro.data.login.LoginUserCallback
import findsolucoes.com.prova_cedro.entites.UserEntity

class UserRepository(application: Application){
    private var userDao: UserDao? = null
    init {
        val db = LogoDatabase.getAppDataBase(application)
        userDao = db!!.userDao()


    }

    //add user with callback
    fun insertUser(userEntity: UserEntity, loginUserCallback : LoginUserCallback){
        try{
            InsertMovieAsynkTask(userDao!!, loginUserCallback).execute(userEntity)
        }catch (error : Throwable){
            loginUserCallback.onError(error)
        }
    }

    //delete asynk user from database
    fun deleteUser(deleteUserCallBack : LoginUserCallback){
        try{
            DeleteUserAsynkTask(userDao!!, deleteUserCallBack).execute()
        }catch (error: Throwable){
            deleteUserCallBack.onError(error)
        }
    }

    //get user asynk in database
    fun getUser() : UserEntity = GetUserAsynkTask(userDao!!).execute().get()

    /**
     * Add user asynk
     */
    private class InsertMovieAsynkTask (val userDao: UserDao, val userCallback: LoginUserCallback) : AsyncTask<UserEntity, Void, Void>() {

        override fun doInBackground(vararg userEntity: UserEntity): Void? {
            userDao!!.add(userEntity[0])
            return null
        }

        override fun onPostExecute(result: Void?) {
            userCallback.onSucess()
        }
    }
    private class GetUserAsynkTask (val userDao: UserDao) : AsyncTask<UserEntity, Void, UserEntity>() {

        override fun doInBackground(vararg userEntity: UserEntity): UserEntity {
           return userDao!!.getUser()
        }
    }

    private class DeleteUserAsynkTask(val userDao: UserDao, val loginUserCallback: LoginUserCallback) : AsyncTask<Void?, Void, Void>() {
        override fun doInBackground(vararg params: Void?): Void? {
            userDao!!.deleteUser()
            return null
        }

        override fun onPostExecute(result: Void?) {
            loginUserCallback.onSucess()
        }
    }
}
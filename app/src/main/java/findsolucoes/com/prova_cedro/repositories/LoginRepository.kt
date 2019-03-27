package findsolucoes.com.prova_cedro.repositories

import android.app.Application
import android.os.AsyncTask
import android.util.Log
import findsolucoes.com.prova_cedro.database.LogoDatabase
import findsolucoes.com.prova_cedro.dao.UserDao
import findsolucoes.com.prova_cedro.data.RetrofitAPI
import findsolucoes.com.prova_cedro.entites.UserEntity

class LoginRepository(application: Application){
    private var userDao: UserDao? = null
    private lateinit var retrofitAPI: RetrofitAPI

    init {
        val db = LogoDatabase.getAppDataBase(application)
        userDao = db!!.userDao()
        retrofitAPI = RetrofitAPI(application.resources)
    }


    //add user
    fun insertUser(userEntity: UserEntity){
        InsertMovieAsynkTask(userDao!!).execute(userEntity)
        getUser()
    }

    fun getUser(){
        val user : UserEntity = GetUserAsynkTask(userDao!!).execute().get()
        Log.d("TAG", "user ${user.name}")
    }

    /**
     * Add user asynk
     */
    private class InsertMovieAsynkTask (val userDao: UserDao) :
        AsyncTask<UserEntity, Void, Void>() {

        override fun doInBackground(vararg userEntity: UserEntity): Void? {
            userDao!!.add(userEntity[0])
            return null
        }
    }

    private class GetUserAsynkTask (val userDao: UserDao) :
        AsyncTask<UserEntity, Void, UserEntity>() {

        override fun doInBackground(vararg userEntity: UserEntity): UserEntity {
           return userDao!!.getUser()
        }
    }
}
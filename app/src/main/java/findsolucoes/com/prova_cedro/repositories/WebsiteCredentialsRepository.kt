package findsolucoes.com.prova_cedro.repositories

import android.app.Application
import android.os.AsyncTask
import android.util.Log
import androidx.lifecycle.LiveData
import findsolucoes.com.prova_cedro.database.LogoDatabase
import findsolucoes.com.prova_cedro.dao.UserDao
import findsolucoes.com.prova_cedro.dao.WebsiteCredentialsDao
import findsolucoes.com.prova_cedro.data.RetrofitAPI
import findsolucoes.com.prova_cedro.data.login.LoginUserCallback
import findsolucoes.com.prova_cedro.entites.UserEntity
import findsolucoes.com.prova_cedro.entites.WebsiteCredentialsEntity

class WebsiteCredentialsRepository(application: Application){
    private var websiteCredentialsDao: WebsiteCredentialsDao? = null

    init {
        val db = LogoDatabase.getAppDataBase(application)
        websiteCredentialsDao = db!!.websiteCredentialsDao()
    }

    //add websiteCredentials with callback
    fun insertWebsiteCredentials(websiteCredentialsEntity: WebsiteCredentialsEntity){
        InsertWebsiteCredentials(websiteCredentialsDao!!).execute(websiteCredentialsEntity)
    }

    //delete asynk user from database
    fun deleteWebsiteCredentials(websiteCredentialsEntity: WebsiteCredentialsEntity){
        DeleteWebsiteCredentials(websiteCredentialsDao!!).execute(websiteCredentialsEntity)
    }

    //get user asynk in database
    fun getAllWebsiteCredentials() : ArrayList<WebsiteCredentialsEntity> {
        val list : ArrayList<WebsiteCredentialsEntity> = ArrayList()
        list.addAll( GetWebsiteCredentials(websiteCredentialsDao!!).execute().get() )
        return list
    }

    //update websitecredentials
    fun updateWebsiteCredentials(websiteCredentialsEntity: WebsiteCredentialsEntity){
        UpdateWebsiteCredentials(websiteCredentialsDao!!).execute(websiteCredentialsEntity)
    }


    /**
     * Add  websiteCredentials asynk
     */
    private class InsertWebsiteCredentials (val  websiteCredentialsDao: WebsiteCredentialsDao) : AsyncTask<WebsiteCredentialsEntity, Void, Void>() {
        override fun doInBackground(vararg  websiteCredentialsEntity: WebsiteCredentialsEntity): Void? {
            websiteCredentialsDao!!.add(websiteCredentialsEntity[0])
            return null
        }
    }

    private class GetWebsiteCredentials(val websiteCredentialsDao: WebsiteCredentialsDao) : AsyncTask<Void?, Void?, List<WebsiteCredentialsEntity>>() {
        override fun doInBackground(vararg params: Void?): List<WebsiteCredentialsEntity> {
            return websiteCredentialsDao!!.getAllWebsiteCredentials()
        }
    }

    private class DeleteWebsiteCredentials(val websiteCredentialsDao: WebsiteCredentialsDao) : AsyncTask<WebsiteCredentialsEntity, Void, Void>() {
        override fun doInBackground(vararg websiteCredentials: WebsiteCredentialsEntity): Void? {
            websiteCredentialsDao!!.deleteWebsiteCredentials(websiteCredentials[0])
            return null
        }
    }

    private class UpdateWebsiteCredentials(val websiteCredentialsDao: WebsiteCredentialsDao) : AsyncTask<WebsiteCredentialsEntity, Void?, Void?>(){
        override fun doInBackground(vararg websiteCredentialsEntity: WebsiteCredentialsEntity): Void? {
            websiteCredentialsDao!!.updateWebsiteCredentials(websiteCredentialsEntity[0])
            return null
        }

    }
}
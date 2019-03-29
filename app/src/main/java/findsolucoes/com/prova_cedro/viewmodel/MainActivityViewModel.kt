package findsolucoes.com.prova_cedro.viewmodel

import android.app.Application
import android.graphics.Bitmap
import android.text.TextUtils
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import findsolucoes.com.prova_cedro.R
import findsolucoes.com.prova_cedro.entites.WebsiteCredentialsEntity
import findsolucoes.com.prova_cedro.repositories.WebsiteCredentialsRepository
import findsolucoes.com.prova_cedro.repositories.logo.BitmapDownloadCallback
import findsolucoes.com.prova_cedro.repositories.logo.DownloadImage

class MainActivityViewModel(application: Application) : AndroidViewModel(application){

    private val websiteCredentialsRepository = WebsiteCredentialsRepository(application)
    private val favListWebsiteCredentials = MutableLiveData<ArrayList<WebsiteCredentialsEntity>>()
    private val listWebsiteCredentials = MutableLiveData<ArrayList<WebsiteCredentialsEntity>>()
    private val downloadLogo: DownloadImage = DownloadImage(application)
    private val imageNewWebsiteCredentials = MutableLiveData<Bitmap>()
    private val sucessSaveWebsiteCredentials = MutableLiveData<Boolean>()

    //bottomsheet prompts
    private var messageInputEmail =  MutableLiveData<String>()
    private var messageInputPassword =  MutableLiveData<String>()
    private var messageInputUrl =  MutableLiveData<String>()
    private var clearPromptsMessage = MutableLiveData<Boolean>()
    private val apcContext = application

    init {
        listWebsiteCredentials.value = getAllWebsiteCredentials()
    }

    //livedata websitecredentials list
    fun getlistFavWebsiteCredentials() : LiveData<ArrayList<WebsiteCredentialsEntity>>{
        return favListWebsiteCredentials
    }
    fun getlistWebsiteCredentials() : LiveData<ArrayList<WebsiteCredentialsEntity>>{
        return listWebsiteCredentials
    }
    fun updateImageNewWebsiteCredentials() : LiveData<Bitmap> = imageNewWebsiteCredentials

    //bottomsheet sucess
    fun sucessSaveWebsiteCredentials() : LiveData<Boolean> = sucessSaveWebsiteCredentials

    //prompt errors bottom sheet
    fun setErrorMessagePromptEmail() : LiveData<String> = messageInputEmail
    fun setErrorMessagePromptPassword() : LiveData<String> = messageInputPassword
    fun setErrorMessagePromptUrl() : LiveData<String> = messageInputUrl
    //clear prompts
    fun clearPromptsBottomSheetMessage() : LiveData<Boolean> = clearPromptsMessage

    //list website credentials
    fun addWebsiteCredential(websiteCredentialsEntity: WebsiteCredentialsEntity){
        websiteCredentialsRepository.insertWebsiteCredentials(websiteCredentialsEntity)
        favListWebsiteCredentials.value?.add(websiteCredentialsEntity)
        favListWebsiteCredentials.value = listWebsiteCredentials.value
    }
    fun deleteWebsiteCredentials(websiteCredentialsEntity: WebsiteCredentialsEntity){
        websiteCredentialsRepository.deleteWebsiteCredentials(websiteCredentialsEntity)
    }
    fun updateWebsiteCredentials(websiteCredentialsEntity: WebsiteCredentialsEntity){
        websiteCredentialsRepository.updateWebsiteCredentials(websiteCredentialsEntity)
    }
    fun getAllWebsiteCredentials() : ArrayList<WebsiteCredentialsEntity>{
        return websiteCredentialsRepository.getAllWebsiteCredentials()
    }

    // watcher url from bottomsheet
    fun watchUrl(s: CharSequence?) {
        if(android.util.Patterns.WEB_URL.matcher(s).matches()){
            downloadLogo.searchFavIconImageFromUrl(s.toString(), object : BitmapDownloadCallback{
                override fun onSucess(bitmap: Bitmap) {
                    imageNewWebsiteCredentials.value = bitmap
                }

                override fun onError(error: Throwable) {
                    error.printStackTrace()
                }

            })
        }
    }

    //save new WebsiteCredentials
    fun saveWebsiteCredentials(email: String, password: String, url: String){

        if(!android.util.Patterns.WEB_URL.matcher(url).matches() || TextUtils.isEmpty(url)){
            messageInputUrl.value = apcContext.getString(R.string.bottom_sheet_prompt_url_error)
            return
        }
        if(TextUtils.isEmpty(email) || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            messageInputEmail.value = apcContext.getString(R.string.bottom_sheet_prompt_email_error)
            return
        }
        if(TextUtils.isEmpty(password)){
            messageInputPassword.value = apcContext.getString(R.string.bottom_sheet_prompt_password_error)
            return
        }

        sucessSaveWebsiteCredentials.value = true
    }
}
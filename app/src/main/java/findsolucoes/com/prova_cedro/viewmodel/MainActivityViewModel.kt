package findsolucoes.com.prova_cedro.viewmodel

import android.app.Application
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.TextView
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.selection.SelectionTracker
import findsolucoes.com.prova_cedro.R
import findsolucoes.com.prova_cedro.database.LogoDatabase
import findsolucoes.com.prova_cedro.entites.WebsiteCredentialsEntity
import findsolucoes.com.prova_cedro.models.WebsiteCredentialsListAdapter
import findsolucoes.com.prova_cedro.repositories.UserRepository
import findsolucoes.com.prova_cedro.repositories.WebsiteCredentialsRepository
import findsolucoes.com.prova_cedro.repositories.logo.BitmapDownloadCallback
import findsolucoes.com.prova_cedro.repositories.logo.DownloadImage
import findsolucoes.com.prova_cedro.views.LoginActivity

class MainActivityViewModel(application: Application) : AndroidViewModel(application){

    private val websiteCredentialsRepository = WebsiteCredentialsRepository(application)
    private val favListWebsiteCredentials = MutableLiveData<ArrayList<WebsiteCredentialsEntity>>()
    private val listWebsiteCredentials = MutableLiveData<ArrayList<WebsiteCredentialsEntity>>()
    private val downloadLogo: DownloadImage = DownloadImage(application)
    private val imageNewWebsiteCredentials = MutableLiveData<Bitmap>()
    private val sucessSaveWebsiteCredentials = MutableLiveData<Boolean>()
    private val deleteItemFloatActionButton = MutableLiveData<Boolean>()
    private val editItemToolbar = MutableLiveData<Boolean>()
    private val selectionCleared = MutableLiveData<Boolean>()
    private val openBottomSheetEdit = MutableLiveData<Boolean>()
    private val hiddenBottomSheet = MutableLiveData<Boolean>()

    //bottomsheet prompts
    private var messageInputEmail =  MutableLiveData<String>()
    private var messageInputPassword =  MutableLiveData<String>()
    private var messageInputUrl =  MutableLiveData<String>()
    private var clearPromptsMessage = MutableLiveData<Boolean>()
    private val apcContext = application


    //livedata websitecredentials list
    fun getlistFavWebsiteCredentials() : LiveData<ArrayList<WebsiteCredentialsEntity>> = favListWebsiteCredentials

    fun getlistWebsiteCredentials() : LiveData<ArrayList<WebsiteCredentialsEntity>> = listWebsiteCredentials

    fun updateImageNewWebsiteCredentials() : LiveData<Bitmap> = imageNewWebsiteCredentials

    //bottomsheet sucess
    fun sucessSaveWebsiteCredentials() : LiveData<Boolean> = sucessSaveWebsiteCredentials
    fun openBottomSheetToEditItem() : LiveData<Boolean> = openBottomSheetEdit
    fun hiddenBottomSheet() : LiveData<Boolean> = hiddenBottomSheet

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

    fun deleteWebsiteCredentials(
        selectionTracker: SelectionTracker<Long>,
        list: ArrayList<WebsiteCredentialsEntity>,
        adapterWebisiteCredentials: WebsiteCredentialsListAdapter
    ){
        val deleteItems = mutableListOf<WebsiteCredentialsEntity>()

        for( key in selectionTracker.selection ){
            val websiteCredentialsEntity = adapterWebisiteCredentials.getList().filter{ m -> m.id.toLong() == key }.single()
            deleteItems.add( websiteCredentialsEntity )
        }
        deleteItems.forEach{
            websiteCredentialsRepository.deleteWebsiteCredentials(it)
        }

       sucessSaveWebsiteCredentials.value = true

        selectionTracker.clearSelection()
    }

    fun updateWebsiteCredentials(websiteCredentialsEntity: WebsiteCredentialsEntity){
        websiteCredentialsRepository.updateWebsiteCredentials(websiteCredentialsEntity)
    }
    fun populateFromDatabase(){
        listWebsiteCredentials.value = websiteCredentialsRepository.getAllWebsiteCredentials()
    }

    //recyclerview options when selectiontracker observer
    fun setEditButtonToolbar() : LiveData<Boolean> = editItemToolbar
    fun setDeleteItemFloatingActionButton() : LiveData<Boolean> = deleteItemFloatActionButton
    fun selectionTrackerCleardSelections() : LiveData<Boolean> =  selectionCleared

    //get all items from database
    fun getListFromDatabase():ArrayList<WebsiteCredentialsEntity> = websiteCredentialsRepository.getAllWebsiteCredentials()

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
        websiteCredentialsRepository.insertWebsiteCredentials(WebsiteCredentialsEntity(0, url, email, password))


        sucessSaveWebsiteCredentials.value = true

    }

    //selectiontracker observer patterns
    fun selectionTrackerObserver(selectionTracker: SelectionTracker<Long>) {
        selectionTracker?.addObserver(
            object : SelectionTracker.SelectionObserver<Long>() {
                override fun onSelectionChanged() {
                    super.onSelectionChanged()
                    val items = selectionTracker?.selection!!.size()
                    //nothing to do
                    if (items > 2) {
                        return
                    }

                    //no items selected
                    if (items == 0) selectionCleared.value = true

                    //one item select
                    if(items == 1) {
                        editItemToolbar.value = true
                        deleteItemFloatActionButton.value = true
                    }
                    //more that one item selected
                    if(items == 2) {
                        hiddenBottomSheet.value = true
                        editItemToolbar.value = false
                    }
                }

            })
    }

    //set text in header view from user repository
    fun configEmailNavHeader(headerView: TextView) {
        try{
            headerView.text = UserRepository(apcContext).getUser().email
        }catch (error : Throwable){
            error.printStackTrace()
        }

    }

    fun gitHub() {
        val uris = Uri.parse("http://github.com/jaozinfs/cedro-teste")
        val intents = Intent(Intent.ACTION_VIEW, uris)
        val b = Bundle()
        b.putBoolean("new_window", true)
        intents.putExtras(b)
        apcContext.startActivity(intents)
    }

    fun logout() {

    }

}
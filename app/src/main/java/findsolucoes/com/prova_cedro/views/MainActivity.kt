package findsolucoes.com.prova_cedro.views

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ContextThemeWrapper
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.core.widget.NestedScrollView
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.selection.StorageStrategy
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.navigation.NavigationView
import findsolucoes.com.prova_cedro.R
import findsolucoes.com.prova_cedro.database.LogoDatabase
import findsolucoes.com.prova_cedro.entites.WebsiteCredentialsEntity
import findsolucoes.com.prova_cedro.models.WebsiteCredentialsListAdapter
import findsolucoes.com.prova_cedro.models.tracker.WebsiteCredentialsKeyprovider
import findsolucoes.com.prova_cedro.models.tracker.WebsiteCredentialsLoockup
import findsolucoes.com.prova_cedro.models.tracker.WebsiteCredentialsPredicate
import findsolucoes.com.prova_cedro.repositories.logo.DownloadImage
import findsolucoes.com.prova_cedro.viewmodel.MainActivityViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main_bottom_sheet.*
import kotlinx.android.synthetic.main.activity_main_nav_header.view.*


class MainActivity : AppCompatActivity(),  View.OnClickListener, DrawerLayout.DrawerListener, NavigationView.OnNavigationItemSelectedListener {


    override fun onDrawerStateChanged(newState: Int) {}
    override fun onDrawerSlide(drawerView: View, slideOffset: Float) { }
    override fun onDrawerOpened(drawerView: View) { }
    @SuppressLint("WrongConstant")
    override fun onDrawerClosed(drawerView: View) {
        if (drawer_layout.isDrawerOpen(Gravity.LEFT or Gravity.START)) {
            drawer_layout.closeDrawers()
        }
    }
    override fun onConfigurationChanged(newConfig: Configuration?) {
        super.onConfigurationChanged(newConfig)
        actionBarDrawerToggle.onConfigurationChanged(newConfig)
    }
    override fun onPostCreate(savedInstanceState: Bundle?) {
        // TODO Auto-generated method stub
        super.onPostCreate(savedInstanceState)
        actionBarDrawerToggle.syncState()
    }


    private val toolbar: Toolbar? = null
    private val navigationView: NavigationView? = null
    private lateinit var actionBarDrawerToggle: ActionBarDrawerToggle

    //selection tracker
    private lateinit var selectionTracker : SelectionTracker<Long>
    private lateinit var websiteCredentialskeyProvider : WebsiteCredentialsKeyprovider

    //bottom sheet
    private lateinit var sheetBehavior: BottomSheetBehavior<NestedScrollView>

    //livedataList
    private lateinit var viewModel: MainActivityViewModel

    //adapter
    private lateinit var adapterWebisiteCredentials: WebsiteCredentialsListAdapter

    //list websitecredentials
    private var list = ArrayList<WebsiteCredentialsEntity>()

    //button edit main menu
    private   var editMenuItem: MenuItem? = null

    //action to floating action button view
    private enum class FloatingActionButtonAct{
        ActionAdd,
        ActionDelete
    }
    private enum class BottomSheetAction{
        ActionAdd,
        ActionEdit
    }
    private enum class BottomSheetActionSave{
        ActionAdd,
        ActionEdit
    }
    private var bottomSheetSaveAction = BottomSheetActionSave.ActionAdd
    private var bottomSheetAction = BottomSheetAction.ActionAdd
    private var floatingActionButtonAct = FloatingActionButtonAct.ActionAdd
    private val TIME_INTERVAL = 2000 // # milliseconds, desired time passed between two back presses.
    private var mBackPressed: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //config toolbar
        configToolbar()

        //view model
        viewModel = ViewModelProviders.of(this).get(MainActivityViewModel::class.java)

        //config recyclerview
        configMainRecyclerView()
        createBottomSheet()
        configSelectionTracker(savedInstanceState)
        configNavigationView()


        //observe bottomsheet url image bitmap when seearch
        viewModel.updateImageNewWebsiteCredentials().observe(this, Observer { b->
            activity_main_bottomsheet_image.setImageBitmap(b)
        })

        //observe all items viewmodel
        viewModel.setErrorMessagePromptEmail().observe(this, Observer { cause-> setInputErrorEmail(cause)})
        viewModel.setErrorMessagePromptPassword().observe(this, Observer { cause-> setInputErrorPassword(cause)})
        viewModel.setErrorMessagePromptUrl().observe(this, Observer { cause-> setInputErrorUrl(cause)})
        viewModel.clearPromptsBottomSheetMessage().observe(this, Observer { clearInputs() })
        viewModel.sucessSaveWebsiteCredentials().observe(this, Observer { clearAndCloseBottomSheet()
        })
        viewModel.selectionTrackerCleardSelections().observe(this, Observer { state -> selectionTrackerCleared()})
    }


    //state bundle
    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        selectionTracker.onSaveInstanceState(outState!!)
    }

    //start activity
    override fun onStart() {
        super.onStart()
        activity_main_action_add.setOnClickListener(this)
        activity_main_bottomsheet_save.setOnClickListener(this)
        nav_view.setNavigationItemSelectedListener(this)
        viewModel.populateFromDatabase()

        //obser livedatalist
        viewModel.getlistWebsiteCredentials().observe(this, Observer { list ->
            this.list = list
            websiteCredentialskeyProvider.list = list
            adapterWebisiteCredentials.updateList(list)

        })
        viewModel.setEditButtonToolbar().observe(this, Observer {
                state -> setEditButtonState(state)
        })
        viewModel.setDeleteItemFloatingActionButton().observe(this, Observer {
                state-> setFloatingActionButtonState(state)
        })

        viewModel.hiddenBottomSheet().observe(this, Observer {
            hiddenBottomSheet()
        })

    }

    //onclick buttons of view
    override fun onClick(v: View?) {
        if (v!!.id == activity_main_action_add.id) {
            when{
                floatingActionButtonAct == FloatingActionButtonAct.ActionAdd -> expandCloseSheet()
                floatingActionButtonAct == FloatingActionButtonAct.ActionDelete -> deleteItemClick()
            }
        }else if(v!!.id == activity_main_bottomsheet_save.id){
           when{
                bottomSheetSaveAction == BottomSheetActionSave.ActionEdit ->{
                    viewModel.updateWebsiteCredentials(activity_main_bottomsheet_email.text.toString(),
                        activity_main_bottomsheet_password.text.toString(),
                        activity_main_bottomsheet_url.text.toString(), selectionTracker)
                }
                bottomSheetSaveAction == BottomSheetActionSave.ActionAdd -> {
                    viewModel.saveWebsiteCredentials(activity_main_bottomsheet_email.text.toString(),
                        activity_main_bottomsheet_password.text.toString(),
                        activity_main_bottomsheet_url.text.toString())
                }
            }
       }
    }

    //menu item
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu to use in the action bar
        val inflater = menuInflater
        inflater.inflate(R.menu.main, menu)
        editMenuItem = menu.findItem(R.id.menu_action_edit)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when{
            item!!.itemId == editMenuItem?.itemId -> editItemClick()
        }

        return if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            true
        }else{
            super.onOptionsItemSelected(item)
        }
    }


    //toolbar
    private fun configToolbar() {
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_drawer)
        actionBarDrawerToggle = ActionBarDrawerToggle(this, drawer_layout, toolbar, R.string.open_drawer, R.string.close_drawer)
        drawer_layout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()
        supportActionBar!!.title = getString( R.string.hometitle )
    }

    //main recycler view
    private fun configMainRecyclerView() {
        adapterWebisiteCredentials = WebsiteCredentialsListAdapter(application)
        main_activity_rv_saves_websitecredentials.layoutManager = LinearLayoutManager(applicationContext)
        main_activity_rv_saves_websitecredentials.adapter = adapterWebisiteCredentials
    }

    @SuppressLint("RestrictedApi")
    //initalize bottomsheet view
    private fun createBottomSheet() {
        sheetBehavior = BottomSheetBehavior.from<NestedScrollView>(bottom_sheet)

        sheetBehavior.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            @SuppressLint("RestrictedApi")
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when{



                }
            }
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                Log.d("as", "offset: $slideOffset")
                if(slideOffset.toInt() == 1){
                    activity_main_action_add.visibility = View.GONE
                }else{
                    activity_main_action_add.visibility = View.VISIBLE
                }
                if(slideOffset > 0)
                    activity_main_action_add.animate().scaleX(1 - slideOffset).scaleY(1 - slideOffset).setDuration(0).start()

            }
        })
        //add watcher to get url when user search website and download favimage
        activity_main_bottomsheet_url.addTextChangedListener(
            BottomSheetUrlTextWatcher(
                viewModel
            )
        )
    }



    //bt click action with bottom sheet
    @SuppressLint("RestrictedApi")
    private fun expandCloseSheet() {
        if (sheetBehavior!!.state != BottomSheetBehavior.STATE_EXPANDED) {
            sheetBehavior!!.state = BottomSheetBehavior.STATE_EXPANDED
        } else {
            sheetBehavior!!.state = BottomSheetBehavior.STATE_COLLAPSED
        }
    }


    //Watcher URL BottomSheet to download fav image logo
    private class BottomSheetUrlTextWatcher(val viewModel: MainActivityViewModel) : TextWatcher{
        override fun afterTextChanged(s: Editable?) {}

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            viewModel.watchUrl(s)
        }
    }

    ///////////////////////////////////////////
    //////////bottomsheet//////////////
    ///////////////////////////////////////////
    //set input error in prompts
    private fun setInputErrorEmail(message: String){
        activity_main_bottomsheet_email.error = message
    }
    private fun setInputErrorPassword(message: String){
        activity_main_bottomsheet_password.error = message
    }
    private fun setInputErrorUrl(message: String){
        activity_main_bottomsheet_url.error = message
    }
    //clear inputs
    private fun clearInputs(){
        activity_main_bottomsheet_email.error = null
        activity_main_bottomsheet_password.error = null
        activity_main_bottomsheet_url.error = null
    }
    //clear prompts and close bottomsheet
    private fun clearAndCloseBottomSheet(){
        activity_main_bottomsheet_url.setText("")
        activity_main_bottomsheet_password.setText("")
        activity_main_bottomsheet_email.setText("")
        activity_main_bottomsheet_image.setImageBitmap(null)
        sheetBehavior!!.state = BottomSheetBehavior.STATE_HIDDEN
        viewModel.populateFromDatabase()
    }

    //recyclerview selection tracker items interact config
    private fun configSelectionTracker(savedInstanceState: Bundle?){
        list = viewModel.getListFromDatabase()
        websiteCredentialskeyProvider = WebsiteCredentialsKeyprovider( list )

        selectionTracker = SelectionTracker.Builder<Long>(
            "selectionTrackerKey",
            main_activity_rv_saves_websitecredentials,
            websiteCredentialskeyProvider,
            WebsiteCredentialsLoockup( main_activity_rv_saves_websitecredentials ),
            StorageStrategy.createLongStorage())
            .withSelectionPredicate( WebsiteCredentialsPredicate() )
            .build()
        (main_activity_rv_saves_websitecredentials.adapter as WebsiteCredentialsListAdapter).selectionTracker = selectionTracker

        //observe when user interact
        selectionTrackerObserver()


        if(savedInstanceState != null){
            selectionTracker.onRestoreInstanceState( savedInstanceState )
        }

    }

    override fun onBackPressed() {
        if(drawer_layout.isDrawerOpen(GravityCompat.START)){
            drawer_layout.closeDrawer(Gravity.LEFT)
            return
        }


        if(sheetBehavior!!.state == BottomSheetBehavior.STATE_EXPANDED){
            sheetBehavior!!.state = BottomSheetBehavior.STATE_COLLAPSED
            return
        }


        if (mBackPressed + TIME_INTERVAL > System.currentTimeMillis())
        {
            super.onBackPressed()
            return
        }
        else { Toast.makeText(this, getString(R.string.action_back_app), Toast.LENGTH_SHORT).show(); }

        mBackPressed = System.currentTimeMillis()
    }

    //interact observer
    private fun selectionTrackerObserver(){
        viewModel.selectionTrackerObserver(selectionTracker)
    }
    private fun setEditButtonState(state: Boolean){

        when{
            state  -> {
                editMenuItem?.isVisible = true
                activity_main_bottomsheet_title.setText(R.string.bottom_sheet_header_title_edit)
                bottomSheetSaveAction = BottomSheetActionSave.ActionEdit
                setEditModal()
            }
            !state -> {
                editMenuItem?.isVisible = false
                activity_main_bottomsheet_title.setText(R.string.bottom_sheet_header_title)
                bottomSheetSaveAction = BottomSheetActionSave.ActionAdd
            }
        }
    }
    private fun setFloatingActionButtonState(state: Boolean){
        when{
            state  -> {
                floatingActionButtonAct = FloatingActionButtonAct.ActionDelete
                activity_main_action_add.setImageDrawable(ContextCompat.getDrawable(applicationContext, R.drawable.ic_delete_24dp))
            }
            !state -> {
                floatingActionButtonAct = FloatingActionButtonAct.ActionAdd
                activity_main_action_add.setImageDrawable(ContextCompat.getDrawable(applicationContext, R.drawable.ic_add_24dp))
            }
        }

    }

    private fun selectionTrackerCleared() {
        editMenuItem?.isVisible = false
        floatingActionButtonAct = FloatingActionButtonAct.ActionAdd
        bottomSheetAction = BottomSheetAction.ActionAdd
        clearEditBottomSheet()
        setFloatingActionButtonState(false)
    }

    //delete item click
    private fun deleteItemClick(){
        val alertDialog = AlertDialog.Builder(ContextThemeWrapper(this, R.style.AlertDialogTheme))

        alertDialog.setTitle( R.string.alert_dialog_title )
        alertDialog.setMessage( R.string.alert_dialog_message )

        alertDialog.setPositiveButton("Delete"){dialog, _ ->
            // Do something when user press the positive button
            viewModel.deleteWebsiteCredentials(selectionTracker, adapterWebisiteCredentials)
        }
        //cancel delete
        alertDialog.setNegativeButton("Cancel"){ dialog, _ -> dialog.dismiss() }
        //show alert
        alertDialog.show()

    }

    //edit item click
    private fun editItemClick() {

        sheetBehavior!!.state = BottomSheetBehavior.STATE_EXPANDED
    }

    //open edit modal
    private fun setEditModal() {
        lateinit var websiteCredentialsEntity: WebsiteCredentialsEntity

        for (key in selectionTracker.selection){
            websiteCredentialsEntity = list.filter{ m -> m.id.toLong() == key }.single()

            break
        }

        activity_main_bottomsheet_url.setText(websiteCredentialsEntity.url)
        activity_main_bottomsheet_email.setText(websiteCredentialsEntity.email)
        activity_main_bottomsheet_password.setText(websiteCredentialsEntity.password)
        DownloadImage(application).requestDownloadImage(websiteCredentialsEntity.url, activity_main_bottomsheet_image)
    }

    //clear modal bototmsheet
    private fun clearEditBottomSheet() {

        activity_main_bottomsheet_url.setText("")
        activity_main_bottomsheet_email.setText("")
        activity_main_bottomsheet_password.setText("")
        activity_main_bottomsheet_image.setImageBitmap(null)
        activity_main_bottomsheet_title.setText(R.string.bottom_sheet_header_title)

    }

    //hidden bottom sheet when needed
    private fun hiddenBottomSheet(){
        sheetBehavior!!.state = BottomSheetBehavior.STATE_HIDDEN
    }

    //set email in navigation header
    private fun configNavigationView(){
        val headerView: View = nav_view.getHeaderView(0)
        viewModel.configEmailNavHeader(headerView.navigation_header_container_email)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        Log.d("as", "as")
        when{
            item.itemId == R.id.action_logout -> logOut()
            item.itemId == R.id.action_github -> gitHub()
        }
        return true
    }

    //open git
    private fun gitHub() {
       viewModel.gitHub()
    }


    //logout user
    private fun logOut() {
        val alertDialog = AlertDialog.Builder(ContextThemeWrapper(this, R.style.AlertDialogTheme))

        alertDialog.setTitle( R.string.aler_logout )
        alertDialog.setMessage( R.string.aler_logout_message )

        alertDialog.setPositiveButton("Logout"){dialog, _ ->
            // Do something when user press the positive button
            LogoDatabase.Companion.ClearAlltable(application).execute()
            finish()
            startActivity(Intent(this, LoginActivity::class.java))
        }
        //cancel delete
        alertDialog.setNegativeButton("Cancel"){ dialog, _ -> dialog.dismiss() }
        //show alert
        alertDialog.show()
    }
}

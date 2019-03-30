package findsolucoes.com.prova_cedro.views

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.selection.StorageStrategy
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import findsolucoes.com.prova_cedro.R
import findsolucoes.com.prova_cedro.entites.WebsiteCredentialsEntity
import findsolucoes.com.prova_cedro.models.WebsiteCredentialsListAdapter
import findsolucoes.com.prova_cedro.models.tracker.WebsiteCredentialsKeyprovider
import findsolucoes.com.prova_cedro.models.tracker.WebsiteCredentialsLoockup
import findsolucoes.com.prova_cedro.models.tracker.WebsiteCredentialsPredicate
import findsolucoes.com.prova_cedro.viewmodel.MainActivityViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main_bottom_sheet.*


class MainActivity : AppCompatActivity(),  View.OnClickListener {




    //selection tracker
    private lateinit var selectionTracker : SelectionTracker<Long>

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
    private var floatingActionButtonAct = FloatingActionButtonAct.ActionAdd

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

        //obser livedatalist
        viewModel.getlistWebsiteCredentials().observe(this, Observer { list ->
            adapterWebisiteCredentials.updateList(list)
            this.list = adapterWebisiteCredentials.getList()
        })

        //observe bottomsheet url image bitmap when seearch
        viewModel.updateImageNewWebsiteCredentials().observe(this, Observer { b->
            activity_main_bottomsheet_image.setImageBitmap(b)
        })

        //observe all items viewmodel
        viewModel.setErrorMessagePromptEmail().observe(this, Observer { cause-> setInputErrorEmail(cause)})
        viewModel.setErrorMessagePromptPassword().observe(this, Observer { cause-> setInputErrorPassword(cause)})
        viewModel.setErrorMessagePromptUrl().observe(this, Observer { cause-> setInputErrorUrl(cause)})
        viewModel.clearPromptsBottomSheetMessage().observe(this, Observer { clearInputs() })
        viewModel.sucessSaveWebsiteCredentials().observe(this, Observer { clearAndCloseBottomSheet() })
        viewModel.setEditButtonToolbar().observe(this, Observer { state -> setEditButtonState(state) })
        viewModel.selectionTrackerCleardSelections().observe(this, Observer { state -> selectionTrackerCleared()})
        viewModel.setDeleteItemFloatingActionButton().observe(this, Observer { state-> setFloatingActionButtonState(state) })
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
        viewModel.populateFromDatabase()
    }

    //onclick buttons of view
    override fun onClick(v: View?) {
        if (v!!.id == activity_main_action_add.id) {
            when{
                floatingActionButtonAct == FloatingActionButtonAct.ActionAdd -> expandCloseSheet()
                floatingActionButtonAct == FloatingActionButtonAct.ActionDelete -> Toast.makeText(this, "Delete", Toast.LENGTH_LONG).show()
            }
        }else if(v!!.id == activity_main_bottomsheet_save.id){
            viewModel.saveWebsiteCredentials(activity_main_bottomsheet_email.text.toString(),
                activity_main_bottomsheet_password.text.toString(),
                activity_main_bottomsheet_url.text.toString())
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

    //toolbar
    private fun configToolbar() {
        setSupportActionBar(findViewById(R.id.toolbar))
    }

    //main recycler view
    private fun configMainRecyclerView() {
        adapterWebisiteCredentials = WebsiteCredentialsListAdapter(application)
        main_activity_rv_saves_websitecredentials.layoutManager = LinearLayoutManager(applicationContext)
        main_activity_rv_saves_websitecredentials.adapter = adapterWebisiteCredentials
    }
    //initalize bottomsheet view
    private fun createBottomSheet() {
        sheetBehavior = BottomSheetBehavior.from<NestedScrollView>(bottom_sheet)

        sheetBehavior.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            @SuppressLint("RestrictedApi")
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when{
                    sheetBehavior!!.state == BottomSheetBehavior.STATE_HIDDEN ->  activity_main_action_add.visibility = View.VISIBLE
//                    sheetBehavior!!.state == BottomSheetBehavior.STATE_COLLAPSED -> sheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                }
            }
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                Log.d("TAS", "offset: $slideOffset")
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

        selectionTracker = SelectionTracker.Builder<Long>(
            "selectionTrackerKey",
            main_activity_rv_saves_websitecredentials,
            WebsiteCredentialsKeyprovider( list ),
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

    //interact observer
    fun selectionTrackerObserver(){
        viewModel.selectionTrackerObserver(selectionTracker)
    }
    fun setEditButtonState(state: Boolean){

        when{
            state  -> editMenuItem?.isVisible = true
            !state -> editMenuItem?.isVisible = false
        }
    }
    fun setFloatingActionButtonState(state: Boolean){
        when{
            state  -> {
                floatingActionButtonAct = FloatingActionButtonAct.ActionDelete
                activity_main_action_add.setImageResource(R.drawable.ic_delete_24dp)
            }
            !state -> {
                floatingActionButtonAct = FloatingActionButtonAct.ActionAdd
                activity_main_action_add.setImageResource(R.drawable.ic_add_24dp)
            }
        }

    }
    private fun selectionTrackerCleared() {
        editMenuItem?.isVisible = false
        floatingActionButtonAct = FloatingActionButtonAct.ActionAdd
        activity_main_action_add.setImageResource(R.drawable.ic_add_24dp)
    }

    //delete item click
    fun deleteItemClick(){
        val alertDialog = AlertDialog.Builder(applicationContext)
        alertDialog.setTitle()
        alertDialog.setMessage()

        //click of alert
        alertDialog.


    }
}

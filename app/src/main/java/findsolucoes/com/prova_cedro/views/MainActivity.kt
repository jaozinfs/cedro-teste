package findsolucoes.com.prova_cedro.views

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.widget.NestedScrollView
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import findsolucoes.com.prova_cedro.R
import findsolucoes.com.prova_cedro.models.FavWebsiteCredentialsListAdapter
import findsolucoes.com.prova_cedro.models.WebsiteCredentialsListAdapter
import findsolucoes.com.prova_cedro.viewmodel.MainActivityViewModel
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main_bottom_sheet.*


class MainActivity : AppCompatActivity(), DrawerLayout.DrawerListener, View.OnClickListener {


    //drawer options
    override fun onDrawerStateChanged(newState: Int) {}

    override fun onDrawerSlide(drawerView: View, slideOffset: Float) {}
    override fun onDrawerOpened(drawerView: View) {}
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


    //bottom sheet
    private lateinit var sheetBehavior: BottomSheetBehavior<NestedScrollView>

    //toolbar views
    private val toolbar: Toolbar? = null
    private lateinit var actionBarDrawerToggle: ActionBarDrawerToggle

    //livedataList
    private lateinit var viewModel: MainActivityViewModel

    private lateinit var adapterFavWebisiteCredentials: FavWebsiteCredentialsListAdapter
    private lateinit var adapterWebisiteCredentials: WebsiteCredentialsListAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //config toolbar
        configToolbar()

        //view model
        viewModel = ViewModelProviders.of(this).get(MainActivityViewModel::class.java)

        //config recyclerview
        configFavRecyclerView()
        configMainRecyclerView()
        createBottomSheet()

        //obser livedatalist
        viewModel.getlistFavWebsiteCredentials().observe(this, Observer { list ->
            adapterFavWebisiteCredentials.updateList(list)
        })
        viewModel.getlistWebsiteCredentials().observe(this, Observer { list ->
            adapterWebisiteCredentials.updateList(list)
        })

        //observe bottomsheet url image bitmap when seearch
        viewModel.updateImageNewWebsiteCredentials().observe(this, Observer { b->
            activity_main_bottomsheet_image.setImageBitmap(b)
        })

        //observe prompt errors
        viewModel.setErrorMessagePromptEmail().observe(this, Observer { cause-> setInputErrorEmail(cause)})
        viewModel.setErrorMessagePromptPassword().observe(this, Observer { cause-> setInputErrorPassword(cause)})
        viewModel.setErrorMessagePromptUrl().observe(this, Observer { cause-> setInputErrorUrl(cause)})
        viewModel.clearPromptsBottomSheetMessage().observe(this, Observer { clearInputs() })
        viewModel.sucessSaveWebsiteCredentials().observe(this, Observer { clearAndCloseBottomSheet() })
    }

    //start activity
    override fun onStart() {
        super.onStart()
        activity_main_action_add.setOnClickListener(this)
        activity_main_bottomsheet_save.setOnClickListener(this)
    }

    //onclick buttons of view
    override fun onClick(v: View?) {
        if (v!!.id == activity_main_action_add.id) {
            expandCloseSheet()
        }else if(v!!.id == activity_main_bottomsheet_save.id){
            viewModel.saveWebsiteCredentials(activity_main_bottomsheet_email.text.toString(),
                activity_main_bottomsheet_password.text.toString(),
                activity_main_bottomsheet_url.text.toString())
        }
    }


    //toolbar
    private fun configToolbar() {
        setSupportActionBar(findViewById(R.id.toolbar))

        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_drawer)
        actionBarDrawerToggle =
            ActionBarDrawerToggle(this, drawer_layout, toolbar,
                R.string.open_drawer,
                R.string.close_drawer
            )
        drawer_layout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()
    }
    //favorites recycler view
    private fun configFavRecyclerView() {
        adapterFavWebisiteCredentials = FavWebsiteCredentialsListAdapter(application)
        main_activity_rv.layoutManager = LinearLayoutManager(applicationContext)
        main_activity_rv.adapter = adapterFavWebisiteCredentials
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
    //menu options click


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            true
        } else super.onOptionsItemSelected(item)
    }
    //android back button press
    @SuppressLint("WrongConstant")
    override fun onBackPressed() {
        // TODO Auto-generated method stub
        if (drawer_layout.isDrawerOpen(Gravity.LEFT or Gravity.START)) {
            drawer_layout.closeDrawers()
            return
        }
        super.onBackPressed()
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
        sheetBehavior!!.state = BottomSheetBehavior.STATE_COLLAPSED
    }
}

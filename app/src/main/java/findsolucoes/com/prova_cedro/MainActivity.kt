package findsolucoes.com.prova_cedro

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import findsolucoes.com.prova_cedro.models.LogoListAdapter
import kotlinx.android.synthetic.main.activity_main.*
import com.google.android.material.navigation.NavigationView
import android.view.Gravity
import com.google.gson.JsonParser
import com.jakewharton.retrofit2.adapter.rxjava2.HttpException
import findsolucoes.com.prova_cedro.data.RetrofitAPI
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity(), DrawerLayout.DrawerListener {
    //drawer options
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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //config toolbar
        configToolbar()


        var listString = listOf("ASD", "ASD", "ASD")
        val adapter = LogoListAdapter(listString, application)
        main_activity_rv.layoutManager = LinearLayoutManager(applicationContext)
        main_activity_rv.adapter = adapter

        val retro = RetrofitAPI(application)
        retro.searchLogoFromUrl("www.google.com", null).timeout(60, TimeUnit.SECONDS)
            .subscribeOn(Schedulers.io())
            .unsubscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(

                {

                    imgv.setImageBitmap(BitmapFactory.decodeStream(it.byteStream()))
                },

                {
                    Log.d("ASSA", "Error: ${it.message}")
                })


    }


    fun configToolbar(){
        setSupportActionBar(findViewById(R.id.toolbar))

        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_drawer)
        actionBarDrawerToggle = ActionBarDrawerToggle(this, drawer_layout, toolbar, R.string.open_drawer, R.string.close_drawer)
        drawer_layout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            true
        } else super.onOptionsItemSelected(item)
    }

    @SuppressLint("WrongConstant")
    override fun onBackPressed() {
        // TODO Auto-generated method stub
        if (drawer_layout.isDrawerOpen(Gravity.LEFT or Gravity.START)) {
            drawer_layout.closeDrawers()
            return
        }
        super.onBackPressed()
    }

}

package findsolucoes.com.prova_cedro.views


import android.os.Build.VERSION_CODES.O
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProviders
import findsolucoes.com.prova_cedro.database.LogoDatabase
import findsolucoes.com.prova_cedro.dao.UserDao
import findsolucoes.com.prova_cedro.viewmodel.LoginViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import findsolucoes.com.prova_cedro.R
import findsolucoes.com.prova_cedro.data.login.LoginCredentials
import kotlinx.android.synthetic.main.activity_login.*


class LoginActivity : AppCompatActivity(), LifecycleOwner {

    private lateinit var viewModel : LoginViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        // setup viewmodel contract
        viewModel = ViewModelProviders.of(this).get(LoginViewModel::class.java)


        //user login is clicked
        viewModel.getProgressState().observe(this , Observer { state ->
            if(state) showProgress()
            else stopProgress()
        })

        viewModel.getToastError().observe(this, Observer { toastMessage ->
            showToast(toastMessage)
        })


    }

    private fun showToast(toastMessage: String?) {
        Toast.makeText(this, toastMessage, Toast.LENGTH_LONG).show()
    }

    fun onLoginClick(view: View){
        //
        val emailCred = email.text.toString()
        val passwordCred = password.text.toString()

        //
        viewModel.login(LoginCredentials(emailCred, passwordCred))
    }


    private fun showProgress(){
        login_progress.visibility = View.VISIBLE
    }

    private fun stopProgress(){
        login_progress.visibility = View.GONE
    }
}

package findsolucoes.com.prova_cedro.views


import android.content.Intent
import android.os.Build.VERSION_CODES.O
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProviders
import findsolucoes.com.prova_cedro.database.LogoDatabase
import findsolucoes.com.prova_cedro.dao.UserDao
import findsolucoes.com.prova_cedro.viewmodel.LoginViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.google.android.material.button.MaterialButton
import findsolucoes.com.prova_cedro.R
import findsolucoes.com.prova_cedro.data.login.LoginCredentials
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_register.*


class LoginActivity : AppCompatActivity(), View.OnClickListener, LifecycleOwner {


    private lateinit var viewModel : LoginViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        // setup viewmodel contract
        viewModel = ViewModelProviders.of(this).get(LoginViewModel::class.java)

        //current state of progress view
        viewModel.getProgressState().observe(this , Observer { state ->
            if(state) showProgress()
            else stopProgress()
        })

        //observe message from viewmodel to show in toast
        viewModel.getToastMessage().observe(this, Observer { toastMessage ->
            showToast(toastMessage)
        })
        //input errors in edittext
        viewModel.getInputEmailMessage().observe(this, Observer { cause-> setInputErrorEmail(cause)})
        viewModel.getInputPasswordMessage().observe(this, Observer { cause-> setInputErrorPassword(cause)})
        viewModel.getIsClearInputMessages().observe(this, Observer { clearInputs()})

    }

    override fun onStart() {
        super.onStart()

        //set click action
        email_sign_in_button.setOnClickListener(this)
        action_register.setOnClickListener(this)

    }

    //onclick actions
    override fun onClick(v: View?) {
        if(v!!.id == email_sign_in_button.id){
            onLoginClick()
        }else if(v!!.id == action_register.id){
            onRegisterClick()
        }
    }

    //show message in toast from viewmodel
    private fun showToast(toastMessage: String?) {Toast.makeText(this, toastMessage, Toast.LENGTH_LONG).show() }

    //login bt click
    fun onLoginClick(){
        //
        val emailCred = email.text.toString()
        val passwordCred = password.text.toString()

        //
        viewModel.login(LoginCredentials(emailCred, passwordCred))
    }

    //register bt click
    fun onRegisterClick(){
        //start activity register
        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
    }

    //load wait progress show
    private fun showProgress(){
        login_progress.visibility = View.VISIBLE
    }
    //load wait progress stop
    private fun stopProgress(){
        login_progress.visibility = View.GONE
    }

    //set input error
    private fun setInputErrorEmail(message: String){
        register_input_email.error = message
    }
    private fun setInputErrorPassword(message: String){
        register_input_password.error = message
    }

    //clear inputs
    private fun clearInputs(){
        login_input_email.error = null
        login_input_password.error = null
    }
}

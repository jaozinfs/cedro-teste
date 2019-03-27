package findsolucoes.com.prova_cedro.views

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import findsolucoes.com.prova_cedro.R
import findsolucoes.com.prova_cedro.data.register.RegisterCredentials
import findsolucoes.com.prova_cedro.viewmodel.LoginViewModel
import findsolucoes.com.prova_cedro.viewmodel.RegisterViewModel
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity(), View.OnClickListener {

    //View model
    private lateinit var viewModel: RegisterViewModel


    //oncreate
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        viewModel = ViewModelProviders.of(this).get(RegisterViewModel::class.java)
    }

    override fun onStart() {
        super.onStart()

        //click listener
        register_button.setOnClickListener(this)

        //viewmodel bind state
        //progress wait state
        viewModel.getProgressState().observe(this, Observer {
            if(it)showProgress()
            else stopProgress()
        })

        //show message toast from viewmodel
        viewModel.getToastMessage().observe(this, Observer { showToast(it) })

        //input errors in edittext
        viewModel.getInputNameMessage().observe(this, Observer { cause-> setInputErrorName(cause)})
        viewModel.getInputEmailMessage().observe(this, Observer { cause-> setInputErrorEmail(cause)})
        viewModel.getInputPasswordMessage().observe(this, Observer { cause-> setInputErrorPassword(cause)})
        viewModel.getIsClearInputMessages().observe(this, Observer { clearInputs()})
    }

    //onclick actions
    override fun onClick(v: View?) {
        //call register action
        onRegisterClick()
    }

    //register action
    fun onRegisterClick(){
        val registerCredentials = RegisterCredentials(register_name.text.toString(),
            register_email.text.toString(),
            register_password.text.toString())

        viewModel.register(registerCredentials)
    }

    //load wait progress show
    private fun showProgress(){
        register_progress.visibility = View.VISIBLE
    }
    //load wait progress stop
    private fun stopProgress(){
        register_progress.visibility = View.GONE
    }
    //show message in toast from viewmodel
    private fun showToast(toastMessage: String?) {
        Toast.makeText(this, toastMessage, Toast.LENGTH_LONG).show()
    }

    //set input error
    private fun setInputErrorName(message: String){
        register_input_name.error = message
    }
    private fun setInputErrorEmail(message: String){
        register_input_email.error = message
    }
    private fun setInputErrorPassword(message: String){
        register_input_password.error = message
    }

    //clear inputs
    private fun clearInputs(){
        register_input_name.error = null
        register_input_email.error = null
        register_input_password.error = null
    }


}

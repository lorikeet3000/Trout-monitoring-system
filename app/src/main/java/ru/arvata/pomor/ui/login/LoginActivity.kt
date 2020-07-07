package ru.arvata.pomor.ui.login

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_login.*
import ru.arvata.pomor.R
import ru.arvata.pomor.data.LoginState
import ru.arvata.pomor.ui.main.MainActivity
import ru.arvata.pomor.util.startActivity

class LoginActivity : AppCompatActivity() {
    private lateinit var loginViewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        loginViewModel = ViewModelProviders.of(this).get(LoginViewModel::class.java)

        action_login.setOnClickListener {
            login()
        }

        loginViewModel.loginStateLiveData.observe(this, Observer {
            when (it) {
                LoginState.InProgress -> showLoginProgress()
                LoginState.Logged -> {
                    startActivity<MainActivity>()
                    finish()
                }
                LoginState.NotLogged -> {
                    hideLoginProgress()
                }
            }
        })

        loginViewModel.loginErrorLiveData.observe(this, Observer {
            if(it != null) {
                showLoginErrorMessage()
            }
        })

        input_login.setText("gtrout") //todo remove FOR RELEASE
        input_password.setText("testFishRole0220")
    }

    private fun showLoginProgress() {
        container_progress.visibility = View.VISIBLE
        input_login.isEnabled = false
        input_password.isEnabled = false
        action_login.isEnabled = false
    }

    private fun hideLoginProgress() {
        container_progress.visibility = View.GONE
        input_login.isEnabled = true
        input_password.isEnabled = true
        action_login.isEnabled = true
    }

    private fun showLoginErrorMessage() {
        Toast.makeText(this, getString(R.string.login_error), Toast.LENGTH_LONG).show()
    }

    private fun validateFields(login: EditText, password: EditText): Boolean {
        var result = true

        var s = login.text.toString().trim()
        login.setText(s)
        if(s.isEmpty()) {
            login.error = getString(R.string.enter_login)
            result = false
        } else {
            login.error = null
        }

        s = password.text.toString().trim()
        password.setText(s)

        if(s.isEmpty()) {
            password.error = getString(R.string.enter_password)
            result = false
        } else {
            password.error = null
        }

        return result
    }

    private fun login() {
        if(validateFields(input_login, input_password)) {
            loginViewModel.login(input_login.text.toString(), input_password.text.toString())
        }
    }
}


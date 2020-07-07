package ru.arvata.pomor

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import ru.arvata.pomor.data.UserRepository
import ru.arvata.pomor.ui.login.LoginActivity
import ru.arvata.pomor.ui.main.MainActivity
import ru.arvata.pomor.util.startActivity

class StartActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if(UserRepository.isLoggedIn()) {
            startActivity<MainActivity>()
        } else {
            startActivity<LoginActivity>()
        }
        finish()
    }
}

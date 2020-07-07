package ru.arvata.pomor.ui.login

import android.view.View
import kotlinx.android.synthetic.main.nav_header_main.view.*

class ProfileView(private val container: View) {

    fun setUser(name: String?, role: String?) {
        container.text_name.text = name
        container.text_role.text = role
    }
}
package ru.arvata.pomor.ui.main

import android.Manifest
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.os.PersistableBundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.layout_site_selector.*
import ru.arvata.pomor.R
import ru.arvata.pomor.data.LoginState
import ru.arvata.pomor.data.RemoteRepository
import ru.arvata.pomor.ui.login.LoginActivity
import ru.arvata.pomor.ui.login.ProfileView
import ru.arvata.pomor.ui.navigation.MainNavigator
import ru.arvata.pomor.ui.newrecord.TroutVoiceRecognizer
import ru.arvata.pomor.ui.newrecord.VoiceRecognizer
import ru.arvata.pomor.util.*

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var viewModel: MainViewModel
    private lateinit var profileView: ProfileView
    private var siteSelectorView: SiteSelectorView? = null
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var keyboardListener: KeyboardBroadcaster

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        toggle = ActionBarDrawerToggle(
            this, drawer_layout, toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)

        navigation_view.setNavigationItemSelectedListener(this)

        keyboardListener = KeyboardBroadcaster(drawer_layout)

        profileView = ProfileView(navigation_view.getHeaderView(0))

        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)

        viewModel.sitesLiveData.observe(this, Observer {
            siteSelectorView = SiteSelectorView(container_site_selector, it ?: arrayOf()) { position: Int, text: String? ->
                viewModel.setSite(position)
            }
        })

        viewModel.selectedSiteLiveData.observe(this, Observer {
            siteSelectorView?.selectSite(it ?: 0)
        })

        viewModel.loginStateLiveData.observe(this, Observer {
            when (it) {
                LoginState.NotLogged, LoginState.InProgress -> onNotLogged()
                LoginState.Logged -> onLogged()
            }
        })

        viewModel.loggedUserLiveData.observe(this, Observer {
            if(it != null) {
                profileView.setUser(it.name, it.role)
            } else {
                profileView.setUser(null, null)
            }
        })

        viewModel.networkEvents.observe(this, Observer {
            if(it != null) {
                val networkMessage = when(it) {
                    RemoteRepository.NetworkEvent.IndicatorsSuccess -> "Показатели отправлены на сервер"
                    RemoteRepository.NetworkEvent.IndicatorsError -> "Ошибка при отправке показателей"
                    RemoteRepository.NetworkEvent.SummarySuccess -> "Данные обновлены"
                    RemoteRepository.NetworkEvent.SummaryError -> "Ошибка при обновлении данных"
                    RemoteRepository.NetworkEvent.PlanSuccess -> "Задача добавлена"
                    RemoteRepository.NetworkEvent.PlanError -> "Ошибка при добавлении задачи"
                }
                Toast.makeText(this, networkMessage, Toast.LENGTH_LONG).show()
            }
        })

        askPermissions(Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE, onAllGrantedBlock = {
            initVoiceRecognizer()
        }) {
            Toast.makeText(this, getString(R.string.info_permissions_required), Toast.LENGTH_LONG).show()
        }

        if(savedInstanceState == null) {
            viewModel.refreshData()
        }
    }

    override fun onPostCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onPostCreate(savedInstanceState, persistentState)
        toggle.syncState()
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        toggle.syncState()
    }

    private fun initVoiceRecognizer() {
        val result = VoiceRecognizer.init(this, TroutVoiceRecognizer)
        if(!result) {
            Toast.makeText(this, getString(R.string.error_init_voice_input), Toast.LENGTH_LONG).show()
        }
        TroutVoiceRecognizer.keywordTriggerLiveData.observe(this, Observer {
            navigateToScreen(Screen.NewRecord)
        })
    }

    private fun onNotLogged() {
        startActivity<LoginActivity>()
        finish()
    }

    private fun onLogged() {
        navigateToScreen(Screen.Main)
    }

    private fun navigateToScreen(screens: Screen) {
        when (screens) {
            Screen.Events, Screen.Plans, Screen.NewRecord -> siteSelectorView?.hideSiteSelector()
            else -> siteSelectorView?.showSiteSelector()
        }

        when (screens) {
            Screen.Main -> MainNavigator.navigateToMainPage(this)
            Screen.Events -> MainNavigator.navigateToEvent(this)
            Screen.NewRecord -> MainNavigator.navigateToNewRecord(this)
            Screen.Overview -> MainNavigator.navigateToOverview(this)
            Screen.Plans -> MainNavigator.navigateToPlans(this)
        }
    }

    private fun logout() {
        viewModel.logout()
    }

    private fun refreshData() {
        viewModel.refreshData()
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_main -> navigateToScreen(Screen.Main)
            R.id.nav_event -> navigateToScreen(Screen.Events)
            R.id.nav_new_record -> navigateToScreen(Screen.NewRecord)
            R.id.nav_overview -> navigateToScreen(Screen.Overview)
            R.id.nav_plans -> navigateToScreen(Screen.Plans)
            R.id.action_refresh -> refreshData()
            R.id.action_logout -> logout()
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onDestroy() {
        VoiceRecognizer.destroy(this)
        super.onDestroy()
    }
}

private enum class Screen {
    Main, Events, NewRecord, Overview, Plans
}

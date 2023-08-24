package kz.divtech.odyssey.rotation.app

import android.app.Application
import android.content.Context
import com.jakewharton.threetenabp.AndroidThreeTen
import kz.divtech.odyssey.rotation.domain.repository.FindEmployeeRepository
import kz.divtech.odyssey.rotation.domain.repository.LoginRepository
import kz.divtech.odyssey.rotation.utils.SharedPrefs.fetchDeviceId
import kz.divtech.odyssey.rotation.utils.SharedPrefs.saveDeviceId
import timber.log.Timber
import java.util.*

class App : Application() {
    val findEmployeeRepository by lazy { FindEmployeeRepository() }
    val loginRepository by lazy { LoginRepository() }
    companion object { lateinit var appContext : Context }

    override fun onCreate() {
        super.onCreate()

        Timber.plant(Timber.DebugTree())
        AndroidThreeTen.init(this)
        appContext = this

        if(fetchDeviceId().isEmpty()) {
            val userId = UUID.randomUUID().toString()
            saveDeviceId(userId)
        }
    }

}
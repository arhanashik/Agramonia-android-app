package com.workfort.apps

import android.content.Context
import androidx.multidex.MultiDex
import androidx.multidex.MultiDexApplication
import com.workfort.apps.agramonia.BuildConfig
import com.workfort.apps.agramonia.data.local.prefs.PrefsGlobal
import com.workfort.apps.agramonia.data.local.prefs.PrefsUser
import timber.log.Timber

class AgromoniaApp : MultiDexApplication() {
    init {
        sInstance = this
    }

    companion object {
        private lateinit var sInstance: AgromoniaApp

        fun getBaseApplicationContext(): Context {
            return sInstance.applicationContext
        }
    }

    override fun onCreate() {
        super.onCreate()

        if (applicationContext != null) {
            if (BuildConfig.DEBUG) {
                initiateOnlyInDebugMode()
            }
            initiate(applicationContext)
        }
    }

    private fun initiateOnlyInDebugMode() {
        Timber.plant(object : Timber.DebugTree() {
            override fun createStackElementTag(element: StackTraceElement): String? {
                return super.createStackElementTag(element) +
                        " - Method:${element.methodName} - Line:${element.lineNumber}"
            }
        })
    }

    private fun initiate(context: Context) {
        PrefsGlobal.init(context)
        PrefsUser.init(context)
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }
}
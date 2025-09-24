package com.glambiase.run_tracker

import android.app.Application
import android.content.Context
import com.glambiase.auth.data.di.authDataModule
import com.glambiase.auth.presentation.di.authPresentationModule
import com.glambiase.core.data.di.coreDataModule
import com.glambiase.core.database.di.coreDatabaseModule
import com.glambiase.run.data.di.runDataModule
import com.glambiase.run.location.di.runLocationModule
import com.glambiase.run.network.di.runNetworkModule
import com.glambiase.run.presentation.di.runPresentationModule
import com.glambiase.run_tracker.di.appModule
import com.google.android.play.core.splitcompat.SplitCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.workmanager.koin.workManagerFactory
import org.koin.core.context.startKoin
import timber.log.Timber

class RunTrackerApp : Application() {

    val applicationScope = CoroutineScope(SupervisorJob())

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) Timber.plant(Timber.DebugTree())

        startKoin {
            androidLogger()
            androidContext(this@RunTrackerApp)
            workManagerFactory()
            modules(
                appModule,
                coreDataModule,
                coreDatabaseModule,
                authDataModule,
                authPresentationModule,
                runPresentationModule,
                runLocationModule,
                runNetworkModule,
                runDataModule
            )
        }
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)

        SplitCompat.install(this)
    }
}
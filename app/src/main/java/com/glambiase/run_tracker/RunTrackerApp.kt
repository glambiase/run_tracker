package com.glambiase.run_tracker

import android.app.Application
import com.glambiase.auth.data.di.authDataModule
import com.glambiase.auth.presentation.di.authPresentationModule
import com.glambiase.core.data.di.coreDataModule
import com.glambiase.core.database.di.databaseModule
import com.glambiase.run.location.di.locationModule
import com.glambiase.run.presentation.di.runPresentationModule
import com.glambiase.run_tracker.di.appModule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
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

            modules(
                appModule,
                coreDataModule,
                authDataModule,
                authPresentationModule,
                runPresentationModule,
                locationModule,
                databaseModule
            )
        }
    }
}
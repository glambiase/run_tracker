package com.glambiase.analytics.dynamicfeature

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.glambiase.analytics.presentation.AnalyticsDashboardScreenRoot
import com.glambiase.analytics.presentation.di.analyticsPresentationModule
import com.glambiase.core.presentation.designsystem.RunTrackerTheme
import com.glambise.analytics.data.di.analyticsDataModule
import com.google.android.play.core.splitcompat.SplitCompat
import org.koin.core.context.loadKoinModules
import kotlinx.serialization.Serializable

class AnalyticsActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        loadKoinModules(
            modules = listOf(
                analyticsDataModule,
                analyticsPresentationModule
            )
        )

        SplitCompat.installActivity(this)

        setContent {
            RunTrackerTheme {
                val navHostController = rememberNavController()
                NavHost(
                    navController = navHostController,
                    startDestination = AnalyticsDashboardRoute
                ) {
                    composable<AnalyticsDashboardRoute> {
                        AnalyticsDashboardScreenRoot(
                            onBackClick = { finish() }
                        )
                    }
                }
            }
        }
    }

    @Serializable
    data object AnalyticsDashboardRoute
}
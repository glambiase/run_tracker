package com.glambiase.run_tracker

import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.glambiase.auth.presentation.intro.IntroScreenRoot
import com.glambiase.auth.presentation.login.LoginScreenRoot
import com.glambiase.auth.presentation.registration.RegistrationScreenRoot
import com.glambiase.run.presentation.active_run.ActiveRunScreenRoot
import com.glambiase.run.presentation.run_overview.RunOverviewScreenRoot

@Composable
fun NavigationRoot(
    navHostController: NavHostController,
    isLoggedIn: Boolean
) {
    NavHost(
        navController = navHostController,
        startDestination = if (isLoggedIn) Routes.Run else Routes.Auth
    ) {
        authGraph(navHostController = navHostController)
        runGraph(navHostController = navHostController)
    }
}

private fun NavGraphBuilder.authGraph(navHostController: NavHostController) {
    navigation<Routes.Auth>(
        startDestination = Routes.Intro
    ) {
        composable<Routes.Intro> {
            IntroScreenRoot(
                onSignInClick = {
                    navHostController.navigate(Routes.Login)
                },
                onSignUpClick = {
                    navHostController.navigate(Routes.Registration)
                }
            )
        }
        composable<Routes.Registration> {
            RegistrationScreenRoot(
                onSignInClick = {
                    navHostController.navigate(Routes.Login) {
                        popUpTo(Routes.Registration) {
                            inclusive = true
                            saveState = true
                        }
                        restoreState = true
                    }
                },
                onSuccessfulRegistration = {
                    navHostController.navigate(Routes.Login)
                }
            )
        }
        composable<Routes.Login> {
            LoginScreenRoot(
                onSignUpClick = {
                    navHostController.navigate(Routes.Registration) {
                        popUpTo(Routes.Login) {
                            inclusive = true
                            saveState = true
                        }
                        restoreState = true
                    }
                },
                onSuccessfulLogin = {
                    navHostController.navigate(Routes.Run) {
                        popUpTo(Routes.Auth) {
                            inclusive = true
                        }
                    }
                }
            )
        }
    }
}

private fun NavGraphBuilder.runGraph(navHostController: NavHostController) {
    navigation<Routes.Run>(
        startDestination = Routes.RunOverview
    ) {
        composable<Routes.RunOverview> {
            RunOverviewScreenRoot(
                onStartRunClick = {
                    navHostController.navigate(Routes.ActiveRun)
                }
            )
        }
        composable<Routes.ActiveRun> {
            ActiveRunScreenRoot(
            )
        }
    }
}
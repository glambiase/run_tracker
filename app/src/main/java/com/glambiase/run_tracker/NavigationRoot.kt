package com.glambiase.run_tracker

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.glambiase.auth.presentation.intro.IntroScreenRoot
import com.glambiase.auth.presentation.registration.RegistrationScreenRoot

@Composable
fun NavigationRoot(
    navHostController: NavHostController
) {
    NavHost(
        navController = navHostController,
        startDestination = Routes.Auth
    ) {
        authGraph(navHostController = navHostController)
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
            Text(text = "Login")
        }
    }
}
package com.glambiase.auth.presentation.login

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.glambiase.auth.presentation.R
import com.glambiase.auth.presentation.util.Constants.CLICKABLE_TEXT_TAG
import com.glambiase.auth.presentation.util.Constants.SIGN_UP_ANNOTATION
import com.glambiase.core.presentation.designsystem.EmailIcon
import com.glambiase.core.presentation.designsystem.Poppins
import com.glambiase.core.presentation.designsystem.RunTrackerTheme
import com.glambiase.core.presentation.designsystem.components.ClickableTextWrapper
import com.glambiase.core.presentation.designsystem.components.GradientBackground
import com.glambiase.core.presentation.designsystem.components.buttons.RunTrackerActionButton
import com.glambiase.core.presentation.designsystem.components.textfields.RunTrackerPasswordTextField
import com.glambiase.core.presentation.designsystem.components.textfields.RunTrackerTextField
import com.glambiase.core.presentation.ui.ObserveAsEvents
import org.koin.androidx.compose.koinViewModel

@Composable
fun LoginScreenRoot(
    onSignUpClick: () -> Unit,
    onSuccessfulLogin: () -> Unit,
    viewModel: LoginViewModel = koinViewModel()
) {
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current

    ObserveAsEvents(flow = viewModel.events) { event ->
        when (event) {
            is LoginEvent.Error -> {
                keyboardController?.hide()
                Toast.makeText(context, event.error.asString(context), Toast.LENGTH_SHORT).show()
            }
            LoginEvent.Success -> {
                keyboardController?.hide()
                Toast.makeText(context, R.string.login_successful, Toast.LENGTH_SHORT).show()
                onSuccessfulLogin()
            }
        }
    }

    LoginScreen(
        state = viewModel.state,
        onAction = { action ->
            when (action) {
                LoginAction.OnRegisterClick -> onSignUpClick()
                else -> Unit
            }
            viewModel.onAction(action)
        }
    )
}

@Composable
fun LoginScreen(
    state: LoginState,
    onAction: (LoginAction) -> Unit
) {
    GradientBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(vertical = 32.dp)
            ) {
                Text(
                    text = stringResource(R.string.login_main_text),
                    style = MaterialTheme.typography.headlineMedium
                )
                Text(
                    text = stringResource(R.string.login_secondary_text),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(48.dp))
                RunTrackerTextField(
                    state = state.email,
                    startIcon = EmailIcon,
                    endIcon = null,
                    hint = stringResource(R.string.email_hint),
                    title = stringResource(R.string.email_title),
                    modifier = Modifier
                        .fillMaxWidth(),
                    keyboardType = KeyboardType.Email
                )
                Spacer(modifier = Modifier.height(24.dp))
                RunTrackerPasswordTextField(
                    state = state.password,
                    hint = stringResource(R.string.password_hint),
                    title = stringResource(R.string.password_title),
                    isPasswordVisible = state.isPasswordVisible,
                    onPasswordVisibilityClick = { onAction(LoginAction.OnPasswordVisibilityClick) },
                    modifier = Modifier
                        .fillMaxWidth()
                )
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
            ) {
                RunTrackerActionButton(
                    text = stringResource(R.string.registration_btn),
                    isLoading = state.isLoggingIn,
                    enabled = state.canLogin && !state.isLoggingIn,
                    onClick = { onAction(LoginAction.OnLoginClick) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(top = 24.dp)
                )
                Spacer(modifier = Modifier.height(24.dp))
                val annotatedString = buildAnnotatedString {
                    withStyle(
                        style = SpanStyle(
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontFamily = Poppins
                        )
                    ) {
                        append(stringResource(R.string.dont_have_account) + " ")
                        pushStringAnnotation(
                            tag = CLICKABLE_TEXT_TAG,
                            annotation = SIGN_UP_ANNOTATION
                        )
                        withStyle(
                            style = SpanStyle(
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.SemiBold,
                                fontFamily = Poppins
                            )
                        ) {
                            append(stringResource(R.string.sign_up))
                        }
                        pop()
                    }
                }
                ClickableTextWrapper(
                    text = annotatedString,
                    annotationsClickHandler = mapOf(
                        SIGN_UP_ANNOTATION to { onAction(LoginAction.OnRegisterClick) }
                    )
                )
            }
        }
    }
}

@Preview
@Composable
private fun LoginScreenPreview() {
    RunTrackerTheme {
        LoginScreen(
            state = LoginState(),
            onAction = {}
        )
    }
}
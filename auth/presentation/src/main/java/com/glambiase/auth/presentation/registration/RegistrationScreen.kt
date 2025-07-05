package com.glambiase.auth.presentation.registration

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
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
import com.glambiase.auth.domain.PasswordValidationState
import com.glambiase.auth.domain.UserDataValidator
import com.glambiase.auth.presentation.R
import com.glambiase.auth.presentation.registration.util.RegistrationConstants.CLICKABLE_TEXT_TAG
import com.glambiase.auth.presentation.registration.util.RegistrationConstants.LOGIN_ANNOTATION
import com.glambiase.core.presentation.designsystem.CheckIcon
import com.glambiase.core.presentation.designsystem.CrossIcon
import com.glambiase.core.presentation.designsystem.EmailIcon
import com.glambiase.core.presentation.designsystem.Poppins
import com.glambiase.core.presentation.designsystem.RunTrackerDarkRed
import com.glambiase.core.presentation.designsystem.RunTrackerGreen
import com.glambiase.core.presentation.designsystem.RunTrackerTheme
import com.glambiase.core.presentation.designsystem.components.ClickableTextWrapper
import com.glambiase.core.presentation.designsystem.components.GradientBackground
import com.glambiase.core.presentation.designsystem.components.buttons.RunTrackerActionButton
import com.glambiase.core.presentation.designsystem.components.textfields.RunTrackerPasswordTextField
import com.glambiase.core.presentation.designsystem.components.textfields.RunTrackerTextField
import com.glambiase.core.presentation.ui.ObserveAsEvents
import org.koin.androidx.compose.koinViewModel

@Composable
fun RegistrationScreenRoot(
    onSignInClick: () -> Unit,
    onSuccessfulRegistration: () -> Unit,
    viewModel: RegistrationViewModel = koinViewModel()
) {
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current

    ObserveAsEvents(flow = viewModel.events) { event ->
        when (event) {
            is RegistrationEvent.Error -> {
                keyboardController?.hide()
                Toast.makeText(context, event.error.asString(context), Toast.LENGTH_SHORT).show()
            }
            RegistrationEvent.Success -> {
                keyboardController?.hide()
                Toast.makeText(context, R.string.registration_successful, Toast.LENGTH_SHORT).show()
                onSuccessfulRegistration()
            }
        }
    }

    RegistrationScreen(
        state = viewModel.state,
        onAction = viewModel::onAction
    )
}

@Composable
fun RegistrationScreen(
    state: RegistrationState,
    onAction: (RegistrationAction) -> Unit
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
                    text = stringResource(R.string.create_account),
                    style = MaterialTheme.typography.headlineMedium
                )
                val annotatedString = buildAnnotatedString {
                    withStyle(
                        style = SpanStyle(
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontFamily = Poppins
                        )
                    ) {
                        append(stringResource(R.string.already_have_account) + " ")
                        pushStringAnnotation(
                            tag = CLICKABLE_TEXT_TAG,
                            annotation = LOGIN_ANNOTATION
                        )
                        withStyle(
                            style = SpanStyle(
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.SemiBold,
                                fontFamily = Poppins
                            )
                        ) {
                            append(stringResource(R.string.login))
                        }
                        pop()
                    }
                }
                ClickableTextWrapper(
                    text = annotatedString,
                    annotationsClickHandler = mapOf(
                        LOGIN_ANNOTATION to { onAction(RegistrationAction.OnLoginClick) }
                    )
                )
                Spacer(modifier = Modifier.height(48.dp))
                RunTrackerTextField(
                    state = state.email,
                    startIcon = EmailIcon,
                    endIcon = if (state.isEmailValid) CheckIcon else null,
                    hint = stringResource(R.string.email_hint),
                    title = stringResource(R.string.email_title),
                    modifier = Modifier
                        .fillMaxWidth(),
                    additionalInfo = stringResource(R.string.email_additional_info),
                    keyboardType = KeyboardType.Email
                )
                Spacer(modifier = Modifier.height(24.dp))
                RunTrackerPasswordTextField(
                    state = state.password,
                    hint = stringResource(R.string.password_hint),
                    title = stringResource(R.string.password_title),
                    isPasswordVisible = state.isPasswordVisible,
                    onPasswordVisibilityClick = { onAction(RegistrationAction.OnPasswordVisibilityClick) },
                    modifier = Modifier
                        .fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(24.dp))
                PasswordRequirementsSection(
                    passwordValidationState = state.passwordValidationState
                )
            }
            RunTrackerActionButton(
                text = stringResource(R.string.registration_btn),
                isLoading = state.isRegistering,
                enabled = state.canRegister,
                onClick = { onAction(RegistrationAction.OnRegisterClick) },
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(top = 24.dp)
            )
        }
    }
}

@Composable
private fun PasswordRequirementsSection(
    passwordValidationState: PasswordValidationState
) {
    PasswordRequirement(
        text = stringResource(R.string.pwd_requirement_0, UserDataValidator.MIN_PWD_LENGTH),
        isValid = passwordValidationState.hasMinLength
    )
    Spacer(modifier = Modifier.height(4.dp))
    PasswordRequirement(
        text = stringResource(R.string.pwd_requirement_1),
        isValid = passwordValidationState.hasNumber
    )
    Spacer(modifier = Modifier.height(4.dp))
    PasswordRequirement(
        text = stringResource(R.string.pwd_requirement_2),
        isValid = passwordValidationState.hasLowerCaseChar
    )
    Spacer(modifier = Modifier.height(4.dp))
    PasswordRequirement(
        text = stringResource(R.string.pwd_requirement_3),
        isValid = passwordValidationState.hasUpperCaseChar
    )
}

@Composable
private fun PasswordRequirement(
    text: String,
    isValid: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = if (isValid) CheckIcon else CrossIcon,
            contentDescription = null,
            tint = if (isValid) RunTrackerGreen else RunTrackerDarkRed
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = text,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 16.sp
        )
    }
}

@Preview
@Composable
fun RegistrationScreenPreview(
) {
    RunTrackerTheme {
        RegistrationScreen(
            state = RegistrationState(
                passwordValidationState = PasswordValidationState(
                    hasNumber = true,
                    hasLowerCaseChar = true
                )
            ),
            onAction = {}
        )
    }
}
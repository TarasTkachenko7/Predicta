package com.predicta.app.feature_auth.presentation

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.predicta.app.R
import com.predicta.app.ui.theme.PredictaShapes
import com.predicta.app.ui.theme.SuccessGreen
import org.koin.androidx.compose.koinViewModel

@Composable
fun ForgotPasswordScreen(
    onNavigateBackToLogin: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AuthViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.onEvent(AuthEvent.ResetPasswordRecoveryStep)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        // Logo Image
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "Predicta Logo",
            modifier = Modifier.size(120.dp),
            contentScale = ContentScale.Fit
        )

        Spacer(modifier = Modifier.height(32.dp))

        Crossfade(
            targetState = state.resetStep,
            label = "ResetStepTransition",
            modifier = Modifier.fillMaxWidth()
        ) { step ->
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                when (step) {
                    ResetStep.EMAIL_INPUT -> {
                        Text(
                            text = "Восстановление пароля",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center,
                        )

                        Spacer(modifier = Modifier.height(32.dp))

                        OutlinedTextField(
                            value = state.email,
                            onValueChange = { viewModel.onEvent(AuthEvent.EmailChanged(it)) },
                            label = { Text("Почта") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = PredictaShapes.medium,
                            singleLine = true,
                            isError = state.emailError != null,
                            supportingText = state.emailError?.let { { Text(it) } },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        )

                        if (state.globalError != null) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = state.globalError!!,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center
                            )
                        }

                        Spacer(modifier = Modifier.height(32.dp))

                        Button(
                            onClick = { viewModel.onEvent(AuthEvent.SubmitEmailForReset) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            shape = PredictaShapes.medium,
                            enabled = !state.isLoading,
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            if (state.isLoading) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                            } else {
                                Text(
                                    text = "Отправить код",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        TextButton(onClick = {
                            viewModel.onEvent(AuthEvent.ResetPasswordRecoveryStep)
                            onNavigateBackToLogin()
                        }) {
                            Text(
                                text = "Вернуться ко входу",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                    ResetStep.CODE_VERIFICATION -> {
                        Text(
                            text = "Введите код",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center,
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Код отправлен на вашу почту",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center,
                        )

                        Spacer(modifier = Modifier.height(32.dp))

                        OutlinedTextField(
                            value = state.recoveryCode,
                            onValueChange = { viewModel.onEvent(AuthEvent.RecoveryCodeChanged(it)) },
                            label = { Text("Код восстановления") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = PredictaShapes.medium,
                            singleLine = true,
                            isError = state.recoveryCodeError != null,
                            supportingText = state.recoveryCodeError?.let { { Text(it) } },
                        )

                        if (state.globalError != null) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = state.globalError!!,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center
                            )
                        }

                        Spacer(modifier = Modifier.height(32.dp))

                        Button(
                            onClick = { viewModel.onEvent(AuthEvent.SubmitRecoveryCode) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            shape = PredictaShapes.medium,
                            enabled = !state.isLoading,
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            if (state.isLoading) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                            } else {
                                Text(
                                    text = "Подтвердить",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        TextButton(onClick = {
                            viewModel.onEvent(AuthEvent.ResetPasswordRecoveryStep)
                        }) {
                            Text(
                                text = "Отмена",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                    ResetStep.NEW_PASSWORD -> {
                        Text(
                            text = "Новый пароль",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center,
                        )

                        Spacer(modifier = Modifier.height(32.dp))

                        OutlinedTextField(
                            value = state.newPassword,
                            onValueChange = { viewModel.onEvent(AuthEvent.NewPasswordChanged(it)) },
                            label = { Text("Новый пароль") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = PredictaShapes.medium,
                            singleLine = true,
                            visualTransformation = PasswordVisualTransformation(),
                            isError = state.newPasswordError != null,
                            supportingText = { state.newPasswordError?.let { Text(it) } },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = state.confirmPassword,
                            onValueChange = { viewModel.onEvent(AuthEvent.ConfirmPasswordChanged(it)) },
                            label = { Text("Подтвердите пароль") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = PredictaShapes.medium,
                            singleLine = true,
                            visualTransformation = PasswordVisualTransformation(),
                            isError = state.confirmPasswordError != null,
                            supportingText = { state.confirmPasswordError?.let { Text(it) } },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        )

                        if (state.globalError != null) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = state.globalError!!,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center
                            )
                        }

                        Spacer(modifier = Modifier.height(32.dp))

                        Button(
                            onClick = { viewModel.onEvent(AuthEvent.SubmitNewPasswords) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            shape = PredictaShapes.medium,
                            enabled = !state.isLoading,
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            if (state.isLoading) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                            } else {
                                Text(
                                    text = "Сохранить пароль",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        TextButton(onClick = {
                            viewModel.onEvent(AuthEvent.ResetPasswordRecoveryStep)
                        }) {
                            Text(
                                text = "Отмена",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                    ResetStep.SUCCESS -> {
                        Text(
                            text = "Пароль изменен!",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center,
                        )

                        Spacer(modifier = Modifier.height(32.dp))

                        Spacer(modifier = Modifier.height(32.dp))

                        Button(
                            onClick = {
                                viewModel.onEvent(AuthEvent.ResetPasswordRecoveryStep)
                                onNavigateBackToLogin()
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            shape = PredictaShapes.medium,
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Text(
                                text = "Вернуться ко входу",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }
    }
}


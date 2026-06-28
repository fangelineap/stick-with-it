package com.stickwithit.ui.screen.auth

import android.util.Patterns
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.clickable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.stickwithit.data.state.AuthState
import com.stickwithit.data.viewModel.AuthViewModel
import com.stickwithit.ui.components.FieldError
import com.stickwithit.ui.theme.AzureBlue
import com.stickwithit.ui.theme.NavyBlue
import com.stickwithit.ui.theme.SkyBlue
import com.stickwithit.ui.theme.StickWithItTheme
import com.stickwithit.ui.theme.White
import com.stickwithit.ui.theme.kanaTextFieldColors

@Composable
fun LoginScreen(
    viewModel: AuthViewModel,
    onNavigateToRegister: () -> Unit,
    onLoginSuccess: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    val loginState by viewModel.loginState.collectAsStateWithLifecycle()

    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(loginState) {
        if (loginState is AuthState.Success) {
            onLoginSuccess()
            viewModel.resetLoginState()
        }
    }

    fun validate(): Boolean {
        emailError = when {
            email.isBlank() -> "Email is required."
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> "Please enter a valid email address."
            else -> null
        }
        passwordError = when {
            password.isBlank() -> "Password is required."
            password.length < 6 -> "Password must be at least 6 characters."
            else -> null
        }
        return emailError == null && passwordError == null
    }

    LoginScreenContent(
        email = email,
        password = password,
        passwordVisible = passwordVisible,
        isLoading = loginState is AuthState.Loading,
        errorMessage = (loginState as? AuthState.Error)?.message,
        emailError = emailError,
        passwordError = passwordError,
        onEmailChange = { email = it; emailError = null },
        onPasswordChange = { password = it; passwordError = null },
        onPasswordVisibilityToggle = { passwordVisible = !passwordVisible },
        onSignIn = { if (validate()) viewModel.login(email, password) },
        onNavigateToRegister = onNavigateToRegister
    )
}

@Composable
private fun LoginScreenContent(
    email: String,
    password: String,
    passwordVisible: Boolean,
    isLoading: Boolean,
    errorMessage: String?,
    emailError: String?,
    passwordError: String?,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onPasswordVisibilityToggle: () -> Unit,
    onSignIn: () -> Unit,
    onNavigateToRegister: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 28.dp, vertical = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        KanaLogoIcon()

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Stick With It",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = NavyBlue,
            letterSpacing = (-0.5).sp
        )

        Text(
            text = "TASK BOARD",
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = AzureBlue,
            letterSpacing = 3.2.sp
        )

        Spacer(modifier = Modifier.height(28.dp))

        Text(
            text = "Welcome back",
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = NavyBlue,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Sign in to your workspace",
            fontSize = 14.sp,
            color = AzureBlue,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "EMAIL",
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = AzureBlue,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
                value = email,
                onValueChange = onEmailChange,
                placeholder = { Text("you@example.com", color = AzureBlue.copy(alpha = 0.5f)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                isError = emailError != null,
                supportingText = emailError?.let { { FieldError(it) } },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(10.dp),
                colors = kanaTextFieldColors()
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "PASSWORD",
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = AzureBlue,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
                value = password,
                onValueChange = onPasswordChange,
                placeholder = { Text("••••••••", color = AzureBlue.copy(alpha = 0.5f)) },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                isError = passwordError != null,
                supportingText = passwordError?.let { { FieldError(it) } },
                trailingIcon = {
                    IconButton(onClick = onPasswordVisibilityToggle) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                            contentDescription = if (passwordVisible) "Hide password" else "Show password",
                            tint = AzureBlue
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(10.dp),
                colors = kanaTextFieldColors()
            )
        }

        TextButton(
            onClick = {},
            modifier = Modifier.align(Alignment.End)
        ) {
            Text(
                text = "Forgot password?",
                fontSize = 13.sp,
                color = AzureBlue
            )
        }

        if (errorMessage != null) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = onSignIn,
            enabled = !isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = NavyBlue,
                contentColor = White,
                disabledContainerColor = NavyBlue.copy(alpha = 0.5f)
            )
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(22.dp),
                    strokeWidth = 2.dp,
                    color = White
                )
            } else {
                Text(
                    text = "Sign in",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Don't have an account? ",
                fontSize = 14.sp,
                color = AzureBlue
            )
            Text(
                text = "Register",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = NavyBlue,
                modifier = Modifier.clickable { onNavigateToRegister() }
            )
        }
    }
}

@Composable
fun KanaLogoIcon(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(80.dp)
            .background(NavyBlue, RoundedCornerShape(20.dp)),
        contentAlignment = Alignment.Center
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                LogoCell(White)
                LogoCell(White)
                LogoCell(White)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                LogoCell(White.copy(alpha = 0.62f))
                LogoCell(SkyBlue)
                LogoCell(White.copy(alpha = 0.48f))
            }
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                LogoCell(SkyBlue)
                LogoCell(AzureBlue)
                Spacer(modifier = Modifier.width(18.dp))
            }
        }
    }
}

@Composable
private fun LogoCell(color: Color) {
    Box(
        modifier = Modifier
            .size(width = 18.dp, height = 8.dp)
            .background(color, RoundedCornerShape(4.dp))
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun LoginScreenPreview() {
    StickWithItTheme {
        LoginScreenContent(
            email = "",
            password = "",
            passwordVisible = false,
            isLoading = false,
            errorMessage = "Error Message",
            emailError = "Email is required.",
            passwordError = "Password is required.",
            onEmailChange = {},
            onPasswordChange = {},
            onPasswordVisibilityToggle = {},
            onSignIn = {},
            onNavigateToRegister = {}
        )
    }
}

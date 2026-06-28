package com.stickwithit.ui.screen.auth

import android.util.Patterns
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.stickwithit.ui.theme.StickWithItTheme
import com.stickwithit.ui.theme.White
import com.stickwithit.ui.theme.kanaTextFieldColors

@Composable
fun RegisterScreen(
    viewModel: AuthViewModel,
    onNavigateToLogin: () -> Unit,
    onRegisterSuccess: () -> Unit
) {
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    val registerState by viewModel.registerState.collectAsStateWithLifecycle()

    var fullNameError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var confirmPasswordError by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(registerState) {
        if (registerState is AuthState.Success) {
            onRegisterSuccess()
            viewModel.resetRegisterState()
        }
    }

    fun validate(): Boolean {
        fullNameError = when {
            fullName.isBlank() -> "Full name is required."
            fullName.trim().length < 2 -> "Must be at least 2 characters."
            else -> null
        }
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
        confirmPasswordError = when {
            confirmPassword.isBlank() -> "Please confirm your password."
            confirmPassword != password -> "Passwords do not match."
            else -> null
        }
        return fullNameError == null && emailError == null &&
                passwordError == null && confirmPasswordError == null
    }

    RegisterScreenContent(
        email = email,
        password = password,
        confirmPassword = confirmPassword,
        fullName = fullName,
        passwordVisible = passwordVisible,
        confirmPasswordVisible = confirmPasswordVisible,
        isLoading = registerState is AuthState.Loading,
        apiErrorMessage = (registerState as? AuthState.Error)?.message,
        fullNameError = fullNameError,
        emailError = emailError,
        passwordError = passwordError,
        confirmPasswordError = confirmPasswordError,
        onEmailChange = { email = it; emailError = null },
        onPasswordChange = { password = it; passwordError = null },
        onFullNameChange = { fullName = it; fullNameError = null },
        onConfirmPasswordChange = { confirmPassword = it; confirmPasswordError = null },
        onPasswordVisibilityToggle = { passwordVisible = !passwordVisible },
        onConfirmPasswordVisibilityToggle = { confirmPasswordVisible = !confirmPasswordVisible },
        onRegister = { if (validate()) viewModel.register(email, password, fullName) },
        onNavigateToLogin = onNavigateToLogin
    )
}

@Composable
private fun RegisterScreenContent(
    email: String,
    password: String,
    confirmPassword: String,
    fullName: String,
    passwordVisible: Boolean,
    confirmPasswordVisible: Boolean,
    isLoading: Boolean,
    apiErrorMessage: String?,
    fullNameError: String?,
    emailError: String?,
    passwordError: String?,
    confirmPasswordError: String?,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onFullNameChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    onPasswordVisibilityToggle: () -> Unit,
    onConfirmPasswordVisibilityToggle: () -> Unit,
    onRegister: () -> Unit,
    onNavigateToLogin: () -> Unit
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
            text = "Create Account",
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = NavyBlue,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Join your workspace today",
            fontSize = 14.sp,
            color = AzureBlue,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(20.dp))

        FieldWithLabel(label = "EMAIL") {
            OutlinedTextField(
                value = email,
                onValueChange = onEmailChange,
                placeholder = { Text("you@example.com", color = AzureBlue.copy(alpha = 0.5f)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(10.dp),
                isError = emailError != null,
                supportingText = emailError?.let { { FieldError(it) } },
                colors = kanaTextFieldColors()
            )
        }

        FieldWithLabel(label = "FULL NAME") {
            OutlinedTextField(
                value = fullName,
                onValueChange = onFullNameChange,
                placeholder = { Text("Jane Doe", color = AzureBlue.copy(alpha = 0.5f)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(10.dp),
                isError = fullNameError != null,
                supportingText = fullNameError?.let { { FieldError(it) } },
                colors = kanaTextFieldColors()
            )
        }

        FieldWithLabel(label = "PASSWORD") {
            OutlinedTextField(
                value = password,
                onValueChange = onPasswordChange,
                placeholder = { Text("••••••••", color = AzureBlue.copy(alpha = 0.5f)) },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
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
                isError = passwordError != null,
                supportingText = passwordError?.let { { FieldError(it) } },
                colors = kanaTextFieldColors()
            )
        }

        FieldWithLabel(label = "CONFIRM PASSWORD") {
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = onConfirmPasswordChange,
                placeholder = { Text("••••••••", color = AzureBlue.copy(alpha = 0.5f)) },
                visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    IconButton(onClick = onConfirmPasswordVisibilityToggle) {
                        Icon(
                            imageVector = if (confirmPasswordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                            contentDescription = if (confirmPasswordVisible) "Hide password" else "Show password",
                            tint = AzureBlue
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(10.dp),
                isError = confirmPasswordError != null,
                supportingText = confirmPasswordError?.let { { FieldError(it) } },
                colors = kanaTextFieldColors()
            )
        }

        if (apiErrorMessage != null) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = apiErrorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onRegister,
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
                    text = "Register",
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
                text = "Already have an account? ",
                fontSize = 14.sp,
                color = AzureBlue
            )
            Text(
                text = "Log In",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = NavyBlue,
                modifier = Modifier.clickable { onNavigateToLogin() }
            )
        }
    }
}

@Composable
private fun FieldWithLabel(label: String, content: @Composable () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = AzureBlue,
            letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.height(4.dp))
        content()
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun RegisterScreenPreview() {
    StickWithItTheme {
        RegisterScreenContent(
            email = "",
            password = "",
            confirmPassword = "",
            fullName = "",
            passwordVisible = false,
            confirmPasswordVisible = false,
            isLoading = false,
            apiErrorMessage = null,
            fullNameError = "Full name is required.",
            emailError = "Please enter a valid email address.",
            passwordError = "Password must be at least 6 characters.",
            confirmPasswordError = "Passwords do not match.",
            onEmailChange = {},
            onPasswordChange = {},
            onFullNameChange = {},
            onConfirmPasswordChange = {},
            onPasswordVisibilityToggle = {},
            onConfirmPasswordVisibilityToggle = {},
            onRegister = {},
            onNavigateToLogin = {}
        )
    }
}

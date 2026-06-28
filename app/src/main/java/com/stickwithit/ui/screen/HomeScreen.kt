package com.stickwithit.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.stickwithit.data.state.AuthState
import com.stickwithit.data.viewModel.AuthViewModel

@Composable
fun HomeScreen (
    viewModel: AuthViewModel,
    onNavigateToLogin: () -> Unit
) {
    val logoutState by viewModel.logoutState.collectAsStateWithLifecycle()
    val userRole by viewModel.userRole.collectAsStateWithLifecycle()

    LaunchedEffect(logoutState) {
        if (logoutState is AuthState.Success) {
            onNavigateToLogin()
            viewModel.resetLogoutState()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Welcome!")
        if (userRole != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text("Role: $userRole", style = MaterialTheme.typography.bodyMedium)
        }

        Spacer(modifier = Modifier.height(18.dp))

        Button(
            onClick = { viewModel.logout() },
            enabled = logoutState !is AuthState.Loading,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (logoutState is AuthState.Loading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text("Logout")
            }
        }
    }
}
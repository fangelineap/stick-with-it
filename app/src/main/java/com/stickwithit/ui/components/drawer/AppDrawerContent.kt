package com.stickwithit.ui.components.drawer

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.stickwithit.data.model.WorkspaceResult
import com.stickwithit.ui.theme.AzureBlue
import com.stickwithit.ui.theme.Cream
import com.stickwithit.ui.theme.NavyBlue
import com.stickwithit.ui.theme.White

/**
 * Contents of the navigation drawer: a pinned "New workspace" action at the top,
 * the scrollable list of the user's workspaces, and a logout action at the bottom.
 * Stateless and reusable — hoist selection/actions to the caller.
 */
@Composable
fun AppDrawerContent(
    workspaces: List<WorkspaceResult>,
    selectedWorkspaceId: String?,
    onAddWorkspace: () -> Unit,
    onSelectWorkspace: (WorkspaceResult) -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    ModalDrawerSheet(
        modifier = modifier,
        drawerContainerColor = Cream
    ) {
        Column(modifier = Modifier.padding(horizontal = 12.dp)) {
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "WORKSPACES",
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 1.5.sp,
                color = AzureBlue,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))

            // Pinned "New workspace" button at the very top.
            Button(
                onClick = onAddWorkspace,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = NavyBlue,
                    contentColor = White
                )
            ) {
                Icon(imageVector = Icons.Filled.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "New workspace",
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 12.dp)
        ) {
            if (workspaces.isEmpty()) {
                Text(
                    text = "No workspaces yet.\nCreate one to get started.",
                    color = AzureBlue,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(16.dp)
                )
            } else {
                workspaces.forEach { workspace ->
                    NavigationDrawerItem(
                        label = { Text(workspace.name) },
                        selected = workspace.id == selectedWorkspaceId,
                        onClick = { onSelectWorkspace(workspace) },
                        icon = {
                            Icon(
                                imageVector = Icons.Filled.Folder,
                                contentDescription = null
                            )
                        },
                        colors = NavigationDrawerItemDefaults.colors(
                            selectedContainerColor = White,
                            selectedTextColor = NavyBlue,
                            selectedIconColor = NavyBlue,
                            unselectedTextColor = NavyBlue,
                            unselectedIconColor = AzureBlue
                        ),
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                    )
                }
            }
        }

        HorizontalDivider(color = White)

        Column(modifier = Modifier.padding(12.dp)) {
            NavigationDrawerItem(
                label = { Text("Logout") },
                selected = false,
                onClick = onLogout,
                icon = {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Logout,
                        contentDescription = null
                    )
                },
                colors = NavigationDrawerItemDefaults.colors(
                    unselectedTextColor = NavyBlue,
                    unselectedIconColor = AzureBlue
                )
            )
        }
    }
}

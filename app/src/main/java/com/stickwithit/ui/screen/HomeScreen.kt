package com.stickwithit.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.stickwithit.data.state.AuthState
import com.stickwithit.data.state.WorkspaceState
import com.stickwithit.data.viewModel.AuthViewModel
import com.stickwithit.data.viewModel.WorkspaceViewModel
import com.stickwithit.ui.components.AddWorkspaceDialog
import com.stickwithit.ui.components.UserAvatar
import com.stickwithit.ui.components.board.BoardColumnUi
import com.stickwithit.ui.components.board.BoardTaskUi
import com.stickwithit.ui.components.board.BoardView
import com.stickwithit.ui.components.drawer.AppDrawerContent
import com.stickwithit.ui.theme.AzureBlue
import com.stickwithit.ui.theme.Cream
import com.stickwithit.ui.theme.NavyBlue
import com.stickwithit.ui.theme.SkyBlue
import com.stickwithit.ui.theme.WarmSand
import com.stickwithit.ui.theme.White
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.launch

/**
 * Sample board data — presentation-only placeholder until the columns/rows
 * data layer lands. Replace with mapped domain models then.
 */
private val sampleBoard = listOf(
    BoardColumnUi(
        name = "To Do",
        accent = AzureBlue,
        tasks = listOf(
            BoardTaskUi("Design", WarmSand, "Design login screen"),
            BoardTaskUi("Dev", SkyBlue, "Set up project repository"),
            BoardTaskUi("Design", WarmSand, "Define color system")
        )
    ),
    BoardColumnUi(
        name = "In Progress",
        accent = Color(0xFFF5A623),
        tasks = listOf(
            BoardTaskUi("Dev", SkyBlue, "Build navigation flow"),
            BoardTaskUi("Docs", Color(0xFFCDE9D3), "Write API specification")
        )
    ),
    BoardColumnUi(name = "Done", accent = Color(0xFF4CAF50), tasks = emptyList()),
    BoardColumnUi(name = "Backlog", accent = NavyBlue, tasks = emptyList())
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: AuthViewModel,
    onNavigateToLogin: () -> Unit,
    workspaceViewModel: WorkspaceViewModel = viewModel()
) {
    val logoutState by viewModel.logoutState.collectAsStateWithLifecycle()
    val workspaces by workspaceViewModel.workspaces.collectAsStateWithLifecycle()
    val addState by workspaceViewModel.loadingSatte.collectAsStateWithLifecycle()

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    var selectedWorkspaceId by remember { mutableStateOf<String?>(null) }
    var selectedTab by remember { mutableIntStateOf(0) }
    var showAddDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) { workspaceViewModel.fetchWorkspaces() }

    // Default the selection to the first workspace once loaded.
    LaunchedEffect(workspaces) {
        if (selectedWorkspaceId == null) {
            selectedWorkspaceId = workspaces.firstOrNull()?.id
        }
    }

    // Refresh the list after a workspace is created, then close the dialog.
    LaunchedEffect(addState) {
        if (addState is WorkspaceState.Success) {
            workspaceViewModel.fetchWorkspaces()
            showAddDialog = false
            workspaceViewModel.resetLoadingState()
        }
    }

    LaunchedEffect(logoutState) {
        if (logoutState is AuthState.Success) {
            onNavigateToLogin()
            viewModel.resetLogoutState()
        }
    }

    val selectedWorkspace = workspaces.firstOrNull { it.id == selectedWorkspaceId }
    val workspaceName = selectedWorkspace?.name ?: "My Workspace"
    val taskCount = sampleBoard.sumOf { it.tasks.size }
    val colCount = sampleBoard.size

    if (showAddDialog) {
        AddWorkspaceDialog(
            onDismiss = {
                showAddDialog = false
                workspaceViewModel.resetLoadingState()
            },
            onConfirm = { name -> workspaceViewModel.addWorkspace(name) },
            isLoading = addState is WorkspaceState.Loading,
            errorMessage = (addState as? WorkspaceState.Error)?.message
        )
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            AppDrawerContent(
                workspaces = workspaces,
                selectedWorkspaceId = selectedWorkspaceId,
                onAddWorkspace = {
                    scope.launch { drawerState.close() }
                    showAddDialog = true
                },
                onSelectWorkspace = { workspace ->
                    selectedWorkspaceId = workspace.id
                    scope.launch { drawerState.close() }
                },
                onLogout = {
                    scope.launch { drawerState.close() }
                    viewModel.logout()
                }
            )
        }
    ) {
        Scaffold(
            containerColor = Cream,
            topBar = {
                TopAppBar(
                    title = {
                        Column {
                            Text(
                                text = workspaceName,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = NavyBlue
                            )
                            Text(
                                text = "$taskCount tasks · $colCount cols",
                                fontSize = 12.sp,
                                color = AzureBlue
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(
                                imageVector = Icons.Filled.Menu,
                                contentDescription = "Open menu",
                                tint = NavyBlue
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = { /* TODO: search */ }) {
                            Icon(
                                imageVector = Icons.Filled.Search,
                                contentDescription = "Search",
                                tint = NavyBlue
                            )
                        }
                        UserAvatar(
                            initials = workspaceName.take(2),
                            modifier = Modifier.padding(end = 12.dp)
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Cream)
                )
            },
            bottomBar = {
                NavigationBar(containerColor = White) {
                    NavigationBarItem(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        icon = { Icon(Icons.Filled.GridView, contentDescription = null) },
                        label = { Text("Board") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = NavyBlue,
                            selectedTextColor = NavyBlue,
                            indicatorColor = SkyBlue,
                            unselectedIconColor = AzureBlue,
                            unselectedTextColor = AzureBlue
                        )
                    )
                    NavigationBarItem(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        icon = { Icon(Icons.Filled.CheckCircle, contentDescription = null) },
                        label = { Text("My Tasks") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = NavyBlue,
                            selectedTextColor = NavyBlue,
                            indicatorColor = SkyBlue,
                            unselectedIconColor = AzureBlue,
                            unselectedTextColor = AzureBlue
                        )
                    )
                }
            },
            floatingActionButton = {
                if (selectedTab == 0) {
                    FloatingActionButton(
                        onClick = { showAddDialog = true },
                        containerColor = NavyBlue,
                        contentColor = White
                    ) {
                        Icon(Icons.Filled.Add, contentDescription = "Create")
                    }
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                when (selectedTab) {
                    0 -> BoardView(
                        columns = sampleBoard,
                        modifier = Modifier.fillMaxSize()
                    )
                    else -> MyTasksPlaceholder()
                }
            }
        }
    }
}

@Composable
private fun MyTasksPlaceholder() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Tasks assigned to you will appear here.",
            color = AzureBlue,
            fontSize = 14.sp
        )
    }
}

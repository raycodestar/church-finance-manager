package com.example.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.ui.MainViewModel
import com.example.ui.components.QuickActionSheet
import com.example.ui.screens.auth.AuthScreen
import com.example.ui.screens.dashboard.DashboardScreen
import com.example.ui.screens.gatherings.AddEditGatheringDialog
import com.example.ui.screens.gatherings.GatheringDetailScreen
import com.example.ui.screens.gatherings.GatheringsScreen
import com.example.ui.screens.onboarding.ChurchOnboardingScreen
import com.example.ui.screens.reports.ReportsScreen
import com.example.ui.screens.settings.ActivityHistoryScreen
import com.example.ui.screens.settings.CategoryManagementScreen
import com.example.ui.screens.settings.ChurchProfileScreen
import com.example.ui.screens.settings.RecentlyDeletedScreen
import com.example.ui.screens.settings.SettingsScreen
import com.example.ui.screens.settings.SupabaseSqlScreen
import com.example.ui.screens.transactions.RecordExpenseScreen
import com.example.ui.screens.transactions.RecordIncomeScreen
import com.example.ui.screens.transactions.TransactionsScreen
import com.example.ui.theme.BlueAccentLight
import com.example.ui.theme.DarkSlatePrimary
import com.example.ui.theme.SlateBorder
import com.example.ui.theme.SlateTextMuted
import com.example.ui.theme.SlateTextPrimary
import com.example.ui.theme.SlateTextSecondary
import kotlinx.coroutines.launch

data class BottomNavItem(
    val label: String,
    val route: String,
    val icon: ImageVector
)

val bottomNavItems = listOf(
    BottomNavItem("Dashboard", NavRoutes.DASHBOARD, Icons.Default.Dashboard),
    BottomNavItem("Gatherings", NavRoutes.GATHERINGS, Icons.Default.Event),
    BottomNavItem("Transactions", NavRoutes.TRANSACTIONS, Icons.AutoMirrored.Filled.ReceiptLong),
    BottomNavItem("Reports", NavRoutes.REPORTS, Icons.Default.Assessment)
)

private val appDrawerRoutes = bottomNavItems.map { it.route } + NavRoutes.SETTINGS

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation(viewModel: MainViewModel) {
    val navController = rememberNavController()
    val churchProfile by viewModel.churchProfile.collectAsState()
    val adminProfile by viewModel.adminProfile.collectAsState()
    val gatheringTypes by viewModel.gatheringTypes.collectAsState()

    val sheetState = rememberModalBottomSheetState()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()
    var showQuickActionSheet by remember { mutableStateOf(false) }
    var showCreateGatheringDialog by remember { mutableStateOf(false) }

    val startDestination = if (churchProfile == null) NavRoutes.ONBOARDING else NavRoutes.DASHBOARD

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val showTopBar = currentRoute in appDrawerRoutes
    val showBottomBar = currentRoute in bottomNavItems.map { it.route }
    fun navigateToTopLevel(route: String) {
        if (currentRoute != route) {
            navController.navigate(route) {
                popUpTo(navController.graph.findStartDestination().id) {
                    saveState = true
                }
                launchSingleTop = true
                restoreState = true
            }
        }
    }

    fun navigateFromDrawer(route: String) {
        coroutineScope.launch {
            drawerState.close()
            if (route == NavRoutes.CHURCH_PROFILE) {
                if (currentRoute != route) {
                    navController.navigate(route) { launchSingleTop = true }
                }
            } else {
                navigateToTopLevel(route)
            }
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = showTopBar,
        drawerContent = {
            AppDrawerContent(
                currentRoute = currentRoute,
                churchName = churchProfile?.name ?: "Church Finance Manager",
                adminName = adminProfile?.fullName ?: churchProfile?.adminFullName.orEmpty().ifBlank { "Church Administrator" },
                adminEmail = adminProfile?.email.orEmpty(),
                onNavigate = ::navigateFromDrawer
            )
        }
    ) {
        Scaffold(
            topBar = {
                if (showTopBar) {
                    TopAppBar(
                        title = {
                            Text(
                                text = "Finance Manager",
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                fontWeight = FontWeight.Bold
                            )
                        },
                        navigationIcon = {
                            IconButton(
                                onClick = {
                                    coroutineScope.launch { drawerState.open() }
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Menu,
                                    contentDescription = "Open navigation menu"
                                )
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = DarkSlatePrimary,
                            navigationIconContentColor = Color.White,
                            titleContentColor = Color.White
                        )
                    )
                }
            },
            bottomBar = {
                if (showBottomBar) {
                    NavigationBar(
                        containerColor = Color.White,
                        tonalElevation = 8.dp,
                        windowInsets = WindowInsets.navigationBars
                    ) {
                        bottomNavItems.forEach { item ->
                            val selected = currentRoute == item.route
                            NavigationBarItem(
                                selected = selected,
                                alwaysShowLabel = true,
                                onClick = { navigateToTopLevel(item.route) },
                                icon = {
                                    Icon(
                                        imageVector = item.icon,
                                        contentDescription = item.label,
                                        tint = if (selected) DarkSlatePrimary else SlateTextMuted
                                    )
                                },
                                label = {
                                    Text(
                                        text = item.label,
                                        maxLines = 1,
                                        softWrap = false,
                                        overflow = TextOverflow.Clip,
                                        style = androidx.compose.material3.MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                                            fontSize = 10.sp
                                        ),
                                        color = if (selected) DarkSlatePrimary else SlateTextMuted
                                    )
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    indicatorColor = BlueAccentLight
                                )
                            )
                        }
                    }
                }
            }
        ) { padding ->
            NavHost(
                navController = navController,
                startDestination = startDestination,
                modifier = Modifier.padding(padding)
            ) {
                composable(NavRoutes.AUTH) {
                    AuthScreen(
                        onLoginSuccess = {
                            navController.navigate(NavRoutes.DASHBOARD) {
                                popUpTo(NavRoutes.AUTH) { inclusive = true }
                            }
                        },
                        onNeedOnboarding = {
                            navController.navigate(NavRoutes.ONBOARDING)
                        }
                    )
                }

                composable(NavRoutes.ONBOARDING) {
                    ChurchOnboardingScreen(
                        onCompleteOnboarding = { adminEmail, adminName, password, churchName, location, phone, email, currency ->
                            viewModel.initializeChurch(
                                adminEmail = adminEmail,
                                adminName = adminName,
                                passwordHash = password,
                                churchName = churchName,
                                location = location,
                                phone = phone,
                                email = email,
                                currency = currency
                            )
                            navController.navigate(NavRoutes.DASHBOARD) {
                                popUpTo(NavRoutes.ONBOARDING) { inclusive = true }
                            }
                        }
                    )
                }

                composable(NavRoutes.DASHBOARD) {
                    DashboardScreen(
                        viewModel = viewModel,
                        onNavigateToGatherings = { navController.navigate(NavRoutes.GATHERINGS) },
                        onNavigateToTransactions = { navController.navigate(NavRoutes.TRANSACTIONS) },
                        onOpenQuickActions = { showQuickActionSheet = true },
                        onOpenGatheringDetail = { id ->
                            navController.navigate(NavRoutes.gatheringDetailRoute(id))
                        }
                    )
                }

                composable(NavRoutes.GATHERINGS) {
                    GatheringsScreen(
                        viewModel = viewModel,
                        onOpenGatheringDetail = { id ->
                            navController.navigate(NavRoutes.gatheringDetailRoute(id))
                        },
                        onCreateGathering = { showCreateGatheringDialog = true }
                    )
                }

                composable(
                    route = NavRoutes.GATHERING_DETAIL,
                    arguments = listOf(navArgument("gatheringId") { type = NavType.StringType })
                ) { backStack ->
                    val gatheringId = backStack.arguments?.getString("gatheringId") ?: ""
                    GatheringDetailScreen(
                        gatheringId = gatheringId,
                        viewModel = viewModel,
                        onBack = { navController.popBackStack() },
                        onRecordIncomeForGathering = { id, name ->
                            navController.navigate(NavRoutes.recordIncomeRoute(id, name))
                        },
                        onRecordExpenseForGathering = { id, name ->
                            navController.navigate(NavRoutes.recordExpenseRoute(id, name))
                        }
                    )
                }

                composable(NavRoutes.TRANSACTIONS) {
                    TransactionsScreen(
                        viewModel = viewModel,
                        onOpenQuickActions = { showQuickActionSheet = true }
                    )
                }

                composable(
                    route = NavRoutes.RECORD_INCOME,
                    arguments = listOf(
                        navArgument("gatheringId") { type = NavType.StringType; nullable = true; defaultValue = null },
                        navArgument("gatheringName") { type = NavType.StringType; nullable = true; defaultValue = null }
                    )
                ) { backStack ->
                    val gId = backStack.arguments?.getString("gatheringId")?.ifBlank { null }
                    val gName = backStack.arguments?.getString("gatheringName")?.ifBlank { null }

                    RecordIncomeScreen(
                        viewModel = viewModel,
                        presetGatheringId = gId,
                        presetGatheringName = gName,
                        onBack = { navController.popBackStack() }
                    )
                }

                composable(
                    route = NavRoutes.RECORD_EXPENSE,
                    arguments = listOf(
                        navArgument("gatheringId") { type = NavType.StringType; nullable = true; defaultValue = null },
                        navArgument("gatheringName") { type = NavType.StringType; nullable = true; defaultValue = null }
                    )
                ) { backStack ->
                    val gId = backStack.arguments?.getString("gatheringId")?.ifBlank { null }
                    val gName = backStack.arguments?.getString("gatheringName")?.ifBlank { null }

                    RecordExpenseScreen(
                        viewModel = viewModel,
                        presetGatheringId = gId,
                        presetGatheringName = gName,
                        onBack = { navController.popBackStack() }
                    )
                }

                composable(NavRoutes.REPORTS) {
                    ReportsScreen(viewModel = viewModel)
                }

                composable(NavRoutes.SETTINGS) {
                    SettingsScreen(
                        viewModel = viewModel,
                        onNavigateToChurchProfile = { navController.navigate(NavRoutes.CHURCH_PROFILE) },
                        onNavigateToCategories = { navController.navigate(NavRoutes.CATEGORIES) },
                        onNavigateToActivityHistory = { navController.navigate(NavRoutes.ACTIVITY_HISTORY) },
                        onNavigateToRecentlyDeleted = { navController.navigate(NavRoutes.RECENTLY_DELETED) },
                        onNavigateToSupabaseSql = { navController.navigate(NavRoutes.SUPABASE_SQL) },
                        onSignOut = {
                            navController.navigate(NavRoutes.AUTH) {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                    )
                }

                composable(NavRoutes.CHURCH_PROFILE) {
                    ChurchProfileScreen(
                        viewModel = viewModel,
                        onBack = { navController.popBackStack() }
                    )
                }

                composable(NavRoutes.CATEGORIES) {
                    CategoryManagementScreen(
                        viewModel = viewModel,
                        onBack = { navController.popBackStack() }
                    )
                }

                composable(NavRoutes.ACTIVITY_HISTORY) {
                    ActivityHistoryScreen(
                        viewModel = viewModel,
                        onBack = { navController.popBackStack() }
                    )
                }

                composable(NavRoutes.RECENTLY_DELETED) {
                    RecentlyDeletedScreen(
                        viewModel = viewModel,
                        onBack = { navController.popBackStack() }
                    )
                }

                composable(NavRoutes.SUPABASE_SQL) {
                    SupabaseSqlScreen(onBack = { navController.popBackStack() })
                }
            }

            if (showQuickActionSheet) {
                QuickActionSheet(
                    sheetState = sheetState,
                    onDismiss = { showQuickActionSheet = false },
                    onCreateGathering = {
                        showQuickActionSheet = false
                        showCreateGatheringDialog = true
                    },
                    onRecordIncome = {
                        showQuickActionSheet = false
                        navController.navigate(NavRoutes.recordIncomeRoute())
                    },
                    onRecordExpense = {
                        showQuickActionSheet = false
                        navController.navigate(NavRoutes.recordExpenseRoute())
                    }
                )
            }

            if (showCreateGatheringDialog) {
                AddEditGatheringDialog(
                    gatheringTypes = gatheringTypes,
                    onDismiss = { showCreateGatheringDialog = false },
                    onSave = { name, typeId, typeName, dateMillis, description ->
                        viewModel.createGathering(name, typeId, typeName, dateMillis, null, description)
                        showCreateGatheringDialog = false
                    }
                )
            }
        }
    }
}

@Composable
private fun AppDrawerContent(
    currentRoute: String?,
    churchName: String,
    adminName: String,
    adminEmail: String,
    onNavigate: (String) -> Unit
) {
    val initials = adminName
        .split(" ")
        .mapNotNull { it.firstOrNull()?.toString() }
        .take(2)
        .joinToString("")
        .uppercase()
        .ifEmpty { "AD" }

    ModalDrawerSheet(
        modifier = Modifier.fillMaxWidth(0.86f),
        drawerContainerColor = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(DarkSlatePrimary)
                .padding(horizontal = 20.dp, vertical = 24.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(BlueAccentLight, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = initials,
                    color = DarkSlatePrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = adminName,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (adminEmail.isNotBlank()) {
                Text(
                    text = adminEmail,
                    color = Color.White.copy(alpha = 0.72f),
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = churchName,
                color = Color.White.copy(alpha = 0.86f),
                fontSize = 13.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }

        Spacer(modifier = Modifier.height(12.dp))
        bottomNavItems.forEach { item ->
            NavigationDrawerItem(
                label = {
                    Text(
                        text = item.label,
                        maxLines = 1,
                        softWrap = false
                    )
                },
                selected = currentRoute == item.route,
                onClick = { onNavigate(item.route) },
                icon = {
                    Icon(imageVector = item.icon, contentDescription = null)
                },
                modifier = Modifier.padding(horizontal = 12.dp),
                colors = NavigationDrawerItemDefaults.colors(
                    selectedContainerColor = BlueAccentLight,
                    selectedIconColor = DarkSlatePrimary,
                    selectedTextColor = SlateTextPrimary,
                    unselectedIconColor = SlateTextSecondary,
                    unselectedTextColor = SlateTextPrimary
                )
            )
        }

        HorizontalDivider(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
            color = SlateBorder
        )

        NavigationDrawerItem(
            label = { Text("Church Profile", maxLines = 1) },
            selected = currentRoute == NavRoutes.CHURCH_PROFILE,
            onClick = { onNavigate(NavRoutes.CHURCH_PROFILE) },
            icon = {
                Icon(imageVector = Icons.Default.Person, contentDescription = null)
            },
            modifier = Modifier.padding(horizontal = 12.dp),
            colors = NavigationDrawerItemDefaults.colors(
                selectedContainerColor = BlueAccentLight,
                selectedIconColor = DarkSlatePrimary,
                selectedTextColor = SlateTextPrimary,
                unselectedIconColor = SlateTextSecondary,
                unselectedTextColor = SlateTextPrimary
            )
        )

        NavigationDrawerItem(
            label = { Text("Settings", maxLines = 1) },
            selected = currentRoute == NavRoutes.SETTINGS,
            onClick = { onNavigate(NavRoutes.SETTINGS) },
            icon = {
                Icon(imageVector = Icons.Default.Settings, contentDescription = null)
            },
            modifier = Modifier.padding(horizontal = 12.dp),
            colors = NavigationDrawerItemDefaults.colors(
                selectedContainerColor = BlueAccentLight,
                selectedIconColor = DarkSlatePrimary,
                selectedTextColor = SlateTextPrimary,
                unselectedIconColor = SlateTextSecondary,
                unselectedTextColor = SlateTextPrimary
            )
        )
    }
}

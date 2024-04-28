package fe.linksheet.experiment.ui.overhaul.composable.page.settings.app

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.outlined.ArrowDropDown
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import dev.zwander.shared.ShizukuUtil
import dev.zwander.shared.ShizukuUtil.rememberHasShizukuPermissionAsState
import fe.linksheet.R
import fe.linksheet.composable.util.listState
import fe.linksheet.experiment.ui.overhaul.composable.component.appbar.SearchTopAppBar
import fe.linksheet.experiment.ui.overhaul.composable.component.icon.AppIconImage
import fe.linksheet.experiment.ui.overhaul.composable.component.list.item.ClickableShapeListItem
import fe.linksheet.experiment.ui.overhaul.composable.component.list.item.ContentPosition
import fe.linksheet.experiment.ui.overhaul.composable.component.list.item.ListItemFilledIconButton
import fe.linksheet.experiment.ui.overhaul.composable.component.page.SaneSettingsScaffold
import fe.linksheet.experiment.ui.overhaul.composable.component.page.layout.SaneLazyColumnPageDefaults
import fe.linksheet.experiment.ui.overhaul.composable.component.page.layout.SaneLazyColumnPageLayout
import fe.linksheet.experiment.ui.overhaul.composable.component.util.ComposableTextContent.Companion.content
import fe.linksheet.experiment.ui.overhaul.composable.component.util.Resource.Companion.textContent
import fe.linksheet.extension.android.startActivityWithConfirmation
import fe.linksheet.extension.compose.ObserveStateChange
import fe.linksheet.extension.compose.listHelper
import fe.linksheet.extension.kotlin.collectOnIO
import fe.linksheet.module.viewmodel.AppsWhichCanOpenLinksViewModel
import fe.linksheet.module.viewmodel.FilterMode
import fe.linksheet.module.viewmodel.PretendToBeAppSettingsViewModel
import fe.linksheet.resolver.DisplayActivityInfo
import fe.linksheet.ui.LocalActivity
import org.koin.androidx.compose.koinViewModel


private const val allPackages = "all"

@OptIn(
    ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class
)
@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun VerifiedLinkHandlersRoute(
    onBackPressed: () -> Unit,
    viewModel: AppsWhichCanOpenLinksViewModel = koinViewModel(),
) {
    val activity = LocalActivity.current


    val linkHandlingAllowed by viewModel.filterDisabledOnly.collectOnIO(true)


    val userApps by viewModel.userApps.collectOnIO()
    val lastEmitted by viewModel.lastEmitted.collectOnIO()


    val shizukuInstalled by remember { mutableStateOf(ShizukuUtil.isShizukuInstalled(activity)) }
    val shizukuRunning by remember { mutableStateOf(ShizukuUtil.isShizukuRunning()) }

    val shizukuPermission by rememberHasShizukuPermissionAsState()

    val shizukuMode = shizukuInstalled && shizukuRunning && shizukuPermission
    val state = rememberPullToRefreshState()

    LocalLifecycleOwner.current.lifecycle.ObserveStateChange(invokeOnCall = true) {
        viewModel.emitLatest()
    }

    LaunchedEffect(lastEmitted) { state.endRefresh() }

    fun postCommand(packageName: String) {
        state.startRefresh()
        viewModel.postShizukuCommand(if (linkHandlingAllowed) 0 else 500) {
            val newState = !linkHandlingAllowed
            val result = setDomainState(packageName, "all", newState)
            if (packageName == allPackages) {
                // TODO: Revert previous state instead of always setting to !newState
                setDomainState(PretendToBeAppSettingsViewModel.linksheetCompatPackage, "all", !newState)
            }

            result
        }
    }

    fun openDefaultSettings(info: DisplayActivityInfo) {
        activity.startActivityWithConfirmation(viewModel.makeOpenByDefaultSettingsIntent(info))
    }

    fun openDefaultSettings(info: Any) {
//        activity.startActivityWithConfirmation(viewModel.makeOpenByDefaultSettingsIntent(info))
    }

    val context = LocalContext.current

    val items by viewModel.appsFiltered.collectOnIO()
    val filter by viewModel.searchFilter.collectOnIO()
    val filterMode by viewModel.filterMode.collectOnIO()

    val listState = remember(items?.size, filter, linkHandlingAllowed) {
        listState(items, filter)
    }

    SaneSettingsScaffold(
        topBar = {
            SearchTopAppBar(
                titleContent = textContent(R.string.apps_which_can_open_links),
                placeholderContent = textContent(R.string.settings__title_filter_apps),
                query = filter,
                onQueryChange = viewModel::search,
                onBackPressed = onBackPressed
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            FlowRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = SaneLazyColumnPageDefaults.HorizontalSpacing),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                StateFilter(selection = filterMode, onSelected = {
                    viewModel.filterMode.value = it
                })

                FilterChip(
                    selected = userApps,
                    onClick = {
                        viewModel.userApps.value = !userApps
                    },
                    label = {
                        Text(text = stringResource(id = R.string.settings_verified_link_handlers__text_user_apps))
                    },
                    leadingIcon = if (userApps) {
                        {
                            Icon(
                                modifier = Modifier.size(FilterChipDefaults.IconSize),
                                imageVector = Icons.Filled.Done,
                                contentDescription = null,
                            )
                        }
                    } else null
                )
            }

            SaneLazyColumnPageLayout(padding = PaddingValues()) {
                listHelper(
                    noItems = R.string.no_apps_found,
                    notFound = R.string.no_such_app_found,
                    listState = listState,
                    list = items,
                    listKey = { it.packageName }
                ) { item, padding, shape ->
                    var expanded by remember { mutableStateOf(false) }
                    val rotation by animateFloatAsState(
                        targetValue = if (expanded) 180f else 0f,
                        label = "Arrow rotation"
                    )

                    ClickableShapeListItem(
                        padding = padding,
                        shape = shape,
                        position = ContentPosition.Leading,
                        onClick = {
                            if (shizukuMode) postCommand(item.packageName)
                            else openDefaultSettings(item)
                        },
                        headlineContent = content {
                            Text(text = item.label, overflow = TextOverflow.Ellipsis, maxLines = 1)
                        },
                        supportingContent = content {
//                            Column(modifier = Modifier.animateContentSize()) {
                                Text(text = item.packageName, overflow = TextOverflow.Ellipsis, maxLines = 1)
//                                if (expanded) {
//                                    Row(horizontalArrangement = Arrangement.End) {
//                                        Button(onClick = { /*TODO*/ }) {
//                                            Text(text = "Edit")
//                                        }
//                                    }
//                                }
//                            }
                        },
                        primaryContent = {
                            AppIconImage(
                                bitmap = item.loadIcon(context),
                                label = item.label
                            )
                        },
                        otherContent = {
//                            CompositionLocalProvider(LocalMinimumInteractiveComponentSize provides 24.dp) {
//                                Box(
//                                    modifier = ShapeListItemDefaults.BaseContentModifier,
//                                    contentAlignment = Alignment.Center
//                                ) {
//                                    FilledTonalIconButton(
//                                        modifier = Modifier.size(28.dp),
//                                        colors = IconButtonDefaults.filledTonalIconButtonColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHighest),
//                                        onClick = { expanded = !expanded }
//                                    ) {
//                                        Icon(
//                                            modifier = Modifier.rotate(rotation),
//                                            imageVector = Icons.Outlined.KeyboardArrowDown,
//                                            contentDescription = null
//                                        )
//                                    }
//                                }
//                            }


                            ListItemFilledIconButton(
                                imageVector = Icons.Outlined.Settings,
                                contentDescription = stringResource(id = R.string.settings),
                                onClick = {
                                    activity.startActivityWithConfirmation(viewModel.makeOpenByDefaultSettingsIntent(item.packageName))
                                }
                            )
                        }
                    )
                }
            }
        }
    }
}


@Composable
private fun StateFilter(
    selection: FilterMode,
    onSelected: (FilterMode) -> Unit,
) {
    val context = LocalContext.current

    var expanded by remember { mutableStateOf(false) }
    val rotation by animateFloatAsState(targetValue = if (expanded) 180f else 0f, label = "Arrow rotation")

    Box(modifier = Modifier) {
        FilterChip(
            selected = true,
            onClick = { expanded = !expanded },
            label = {
                Text(
                    text = stringResource(
                        id = selection.shortStringRes
                    )
                )
            },
//            leadingIcon = {
//                Icon(
//                    modifier = Modifier.size(FilterChipDefaults.IconSize),
//                    imageVector = selection.loadIcon(context),
//                    contentDescription = null,
//                )
//            },
            trailingIcon = {
                Icon(
                    modifier = Modifier.rotate(rotation),
                    imageVector = Icons.Outlined.ArrowDropDown,
                    contentDescription = null
                )
            }
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            FilterMode.Modes.forEach { mode ->
                DropdownMenuItem(
                    onClick = {
                        expanded = false
                        onSelected(mode)
                    },
                    text = {
                        Text(text = stringResource(id = mode.stringRes))
                    }
//                    , leadingIcon = {
//                        Icon(
//                            imageVector = mode.loadIcon(context),
//                            contentDescription = null
//                        )
//                    }
                )
            }
        }
    }
}
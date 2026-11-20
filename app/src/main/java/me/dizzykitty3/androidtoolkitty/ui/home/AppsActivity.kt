package me.dizzykitty3.androidtoolkitty.ui.home

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.text.format.Formatter
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.outlined.FilterList
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Sort
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.getSystemService
import androidx.core.graphics.drawable.toBitmap
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.dizzykitty3.androidtoolkitty.R
import me.dizzykitty3.androidtoolkitty.datastore.SettingsViewModel
import me.dizzykitty3.androidtoolkitty.uicomponents.*
import me.dizzykitty3.androidtoolkitty.utils.IntentUtils.checkOnMarket
import java.io.File
import java.text.Collator
import java.text.DateFormat
import java.util.Date

private data class InstalledApp(
    val name: String,
    val info: ApplicationInfo,
    val version: String,
    val versionCode: Long,
    val installed: Long,
    val updated: Long,
    val apkBytes: Long?,
) {
    val packageName: String get() = info.packageName
    val system: Boolean get() =
        info.flags and (ApplicationInfo.FLAG_SYSTEM or ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0
}

private enum class AppSort(val label: Int) {
    NAME(R.string.apps_sort_name),
    NEWEST(R.string.apps_sort_newest),
    OLDEST(R.string.apps_sort_oldest),
    SIZE(R.string.apps_sort_size),
}

private fun ApplicationInfo.apkSize(): Long? = runCatching {
    val files = (listOfNotNull(sourceDir) + splitSourceDirs.orEmpty()).distinct().map(::File)
    if (files.isEmpty() || files.any { !it.isFile || !it.canRead() }) null
    else files.sumOf { it.length() }
}.getOrNull()

@Suppress("DEPRECATION")
private fun PackageManager.readInstalledApps(): List<InstalledApp> {
    val packages = if (Build.VERSION.SDK_INT >= 33) {
        getInstalledPackages(PackageManager.PackageInfoFlags.of(0))
    } else getInstalledPackages(0)
    val collator = Collator.getInstance()
    return packages.mapNotNull { pkg ->
        val info = pkg.applicationInfo ?: return@mapNotNull null
        InstalledApp(
            name = runCatching { info.loadLabel(this).toString() }.getOrDefault(pkg.packageName),
            info = info,
            version = pkg.versionName.orEmpty(),
            versionCode = if (Build.VERSION.SDK_INT >= 28) pkg.longVersionCode else pkg.versionCode.toLong(),
            installed = pkg.firstInstallTime,
            updated = pkg.lastUpdateTime,
            apkBytes = info.apkSize(),
        )
    }.sortedWith { a, b ->
        val comparison = collator.compare(a.name, b.name)
        if (comparison == 0) a.packageName.compareTo(b.packageName) else comparison
    }
}

@AndroidEntryPoint
class AppsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val vm: SettingsViewModel = hiltViewModel()
            val settings by vm.settingsState.collectAsStateWithLifecycle()
            BaseScaffold(dynamicColor = settings.dynamicColor) { InstalledAppsScreen(vm) }
        }
    }
}

@Composable
private fun InstalledAppsScreen(vm: SettingsViewModel) {
    val savedSettings by vm.persistedSettings.collectAsStateWithLifecycle(initialValue = null)
    val preferences = savedSettings
    if (preferences == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        return
    }
    val context = LocalContext.current
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    var apps by remember { mutableStateOf<List<InstalledApp>?>(null) }
    var failed by remember { mutableStateOf(false) }
    var reload by remember { mutableIntStateOf(0) }
    var query by rememberSaveable { mutableStateOf("") }
    val filter = preferences.appsFilter
    val sort = AppSort.entries.firstOrNull { it.name == preferences.appsSort } ?: AppSort.NAME
    val filterLabels = listOf(R.string.apps_all, R.string.apps_user, R.string.apps_system)
    var filterMenuOpen by remember { mutableStateOf(false) }
    var sortMenuOpen by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()
    LaunchedEffect(sort, filter, query) { listState.scrollToItem(0) }
    var selectedPackage by rememberSaveable { mutableStateOf<String?>(null) }

    LaunchedEffect(lifecycle, reload) {
        lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            val result = withContext(Dispatchers.IO) {
                runCatching { context.packageManager.readInstalledApps() }
            }
            failed = result.isFailure
            result.onSuccess { apps = it }
        }
    }
    val visible = remember(apps, query, filter, sort) {
        val filtered = apps.orEmpty().filter {
            (filter == 0 || it.system == (filter == 2)) &&
                (it.name.contains(query, true) || it.packageName.contains(query, true))
        }
        when (sort) {
            AppSort.NAME -> filtered
            AppSort.NEWEST -> filtered.sortedByDescending { it.installed }
            AppSort.OLDEST -> filtered.sortedBy { if (it.installed > 0) it.installed else Long.MAX_VALUE }
            AppSort.SIZE -> filtered.sortedByDescending { it.apkBytes ?: -1L }
        }
    }
    Column(Modifier.fillMaxSize().padding(horizontal = dimensionResource(R.dimen.padding_screen))) {
        TopPadding()
        TopBar(stringResource(R.string.apps_title)) {
            Box {
                IconButton(onClick = { filterMenuOpen = true }) {
                    Icon(
                        Icons.Outlined.FilterList,
                        contentDescription = stringResource(R.string.apps_filter_by, stringResource(filterLabels[filter])),
                        tint = if (filter == 0) LocalContentColor.current else MaterialTheme.colorScheme.primary,
                    )
                }
                DropdownMenu(expanded = filterMenuOpen, onDismissRequest = { filterMenuOpen = false }) {
                    filterLabels.forEachIndexed { index, label ->
                        DropdownMenuItem(
                            text = { Text(stringResource(label)) },
                            trailingIcon = {
                                if (filter == index) Icon(Icons.Outlined.Check, contentDescription = null)
                            },
                            onClick = {
                                vm.setAppsFilter(index)
                                filterMenuOpen = false
                            },
                        )
                    }
                }
            }
            Box {
                IconButton(onClick = { sortMenuOpen = true }) {
                    Icon(
                        Icons.AutoMirrored.Outlined.Sort,
                        contentDescription = stringResource(R.string.apps_sort_by, stringResource(sort.label)),
                    )
                }
                DropdownMenu(expanded = sortMenuOpen, onDismissRequest = { sortMenuOpen = false }) {
                    AppSort.entries.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(stringResource(option.label)) },
                            onClick = {
                                vm.setAppsSort(option.name)
                                sortMenuOpen = false
                            },
                        )
                    }
                }
            }
        }
        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text(stringResource(R.string.apps_search)) },
            singleLine = true,
            trailingIcon = { ClearInput(query) { query = "" } },
        )
        SpacerPadding()
        LazyColumn(Modifier.weight(1f), state = listState, verticalArrangement = Arrangement.spacedBy(8.dp)) {
            if (failed) item {
                Text(stringResource(R.string.apps_load_failed))
                FilledTonalButton(onClick = { reload++ }) { Text(stringResource(R.string.apps_retry)) }
            }
            if (apps == null && !failed) item { CircularProgressIndicator() }
            if (apps != null && visible.isEmpty()) item { Text(stringResource(R.string.apps_empty)) }
            items(visible, key = { it.packageName }) { app ->
                Surface(shape = MaterialTheme.shapes.large, color = MaterialTheme.colorScheme.surfaceBright) {
                    Row(
                        Modifier.fillMaxWidth().clickable { selectedPackage = app.packageName }.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        AppIcon(app)
                        Column(Modifier.weight(1f)) {
                            Text(app.name, style = MaterialTheme.typography.titleMedium)
                            Text(app.packageName, style = MaterialTheme.typography.bodySmall)
                            Text(app.version.ifBlank { app.versionCode.toString() },
                                style = MaterialTheme.typography.bodySmall)
                            if (sort == AppSort.SIZE) {
                                Text(
                                    app.apkBytes?.let { Formatter.formatFileSize(context, it) }
                                        ?: stringResource(R.string.apps_unavailable),
                                    style = MaterialTheme.typography.bodySmall,
                                )
                            }
                        }
                    }
                }
            }
            item { BottomPadding() }
        }
    }
    apps?.firstOrNull { it.packageName == selectedPackage }?.let { app ->
        AppDetails(app) { selectedPackage = null }
    }
}

@Composable
private fun AppIcon(app: InstalledApp) {
    val context = LocalContext.current
    val bitmap by produceState<ImageBitmap?>(null, app.packageName, app.updated) {
        value = withContext(Dispatchers.IO) {
            runCatching { app.info.loadIcon(context.packageManager).toBitmap(96, 96).asImageBitmap() }.getOrNull()
        }
    }
    Box(Modifier.size(48.dp), contentAlignment = Alignment.Center) {
        bitmap?.let { Image(it, contentDescription = null, modifier = Modifier.fillMaxSize()) }
    }
}

@Composable
private fun AppDetails(app: InstalledApp, onDismiss: () -> Unit) {
    val context = LocalContext.current
    val unknown = stringResource(R.string.apps_unavailable)
    val size = app.apkBytes?.let { Formatter.formatFileSize(context, it) } ?: unknown
    fun date(time: Long): String =
        if (time > 0) DateFormat.getDateTimeInstance().format(Date(time)) else unknown

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(app.name) },
        text = {
            Column(Modifier.verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                AppIcon(app)
                Text(app.packageName)
                Text(stringResource(R.string.apps_version, app.version.ifBlank { unknown }, app.versionCode))
                Text(stringResource(R.string.apps_installed, date(app.installed)))
                Text(stringResource(R.string.apps_updated, date(app.updated)))
                Text(stringResource(R.string.apps_apk_size, size))
                Text(stringResource(R.string.apps_size_note), style = MaterialTheme.typography.bodySmall)
                FilledTonalButton(onClick = {
                    context.getSystemService<ClipboardManager>()?.setPrimaryClip(
                        ClipData.newPlainText("Package name", app.packageName)
                    )
                }) { Text(stringResource(R.string.apps_copy_package)) }
                FilledTonalButton(onClick = {
                    try {
                        context.startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                            Uri.fromParts("package", app.packageName, null)))
                    } catch (_: android.content.ActivityNotFoundException) {
                        Toast.makeText(context, R.string.apps_details_unavailable, Toast.LENGTH_SHORT).show()
                    }
                }) { Text(stringResource(R.string.apps_system_details)) }
                FilledTonalButton(onClick = {
                    context.checkOnMarket(app.packageName, isGooglePlay = true)
                }) { Text(stringResource(R.string.open_on_google_play)) }
            }
        },
        confirmButton = { TextButton(onClick = onDismiss) { Text(stringResource(android.R.string.ok)) } },
    )
}

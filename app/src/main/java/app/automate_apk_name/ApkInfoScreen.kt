package app.automate_apk_name

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/** Environment identity: color + icon, keyed by flavor name (lowercase) */
private data class EnvTheme(
    val gradient: List<Color>,
    val icon: ImageVector,
    val label: String
)

private fun envThemeFor(flavor: String): EnvTheme = when (flavor.lowercase()) {
    "production" -> EnvTheme(
        gradient = listOf(Color(0xFFE53935), Color(0xFFB71C1C)),
        icon = Icons.Default.Warning,
        label = "Live environment"
    )
    "staging" -> EnvTheme(
        gradient = listOf(Color(0xFFFB8C00), Color(0xFFE65100)),
        icon = Icons.Default.Science,
        label = "Pre-release testing"
    )
    else -> EnvTheme(
        gradient = listOf(Color(0xFF1E88E5), Color(0xFF3949AB)),
        icon = Icons.Default.Construction,
        label = "Active development"
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApkInfoScreen() {

    val apkName =
        "AutomateApkName-" +
                "${BuildConfig.FLAVOR}-" +
                "${BuildConfig.BUILD_TYPE}-" +
                "v${BuildConfig.VERSION_NAME}" +
                "(${BuildConfig.VERSION_CODE})-" +
                "${BuildConfig.GIT_BRANCH}-" +
                "${BuildConfig.BUILD_DATE}.apk"

    val currentVariant =
        BuildConfig.FLAVOR +
                BuildConfig.BUILD_TYPE.replaceFirstChar { it.uppercase() }

    val variants = listOf(
        "developmentDebug", "developmentRelease",
        "stagingDebug", "stagingRelease",
        "productionDebug", "productionRelease"
    )

    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("APK Name Automation", fontWeight = FontWeight.SemiBold) }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {

            Spacer(Modifier.height(2.dp))

            AnimatedEntry(visible, 0) { HeroCard(BuildConfig.FLAVOR, currentVariant) }

            AnimatedEntry(visible, 1) {
                Text(
                    "Build Variants",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }

            AnimatedEntry(visible, 2) { VariantChipsRow(variants, currentVariant) }

            AnimatedEntry(visible, 3) { ExpandableBuildDetails() }

            AnimatedEntry(visible, 4, fillRemaining = true) { ApkNameCard(apkName) }

            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
fun AnimatedEntry(
    parentVisible: Boolean,
    index: Int,
    fillRemaining: Boolean = false,
    content: @Composable () -> Unit
) {
    AnimatedVisibility(
        visible = parentVisible,
        enter = fadeIn(tween(350, delayMillis = index * 70)) +
                slideInVertically(
                    tween(350, delayMillis = index * 70),
                    initialOffsetY = { it / 4 }
                )
    ) {
        if (fillRemaining) {
            Box(modifier = Modifier.fillMaxWidth()) { content() }
        } else {
            content()
        }
    }
}

@Composable
fun HeroCard(flavor: String, currentVariant: String) {

    val theme = envThemeFor(flavor)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(26.dp))
            .background(Brush.linearGradient(theme.gradient))
            .padding(20.dp)
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = CircleShape,
                    color = Color.White.copy(alpha = 0.18f)
                ) {
                    Icon(
                        theme.icon,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.padding(12.dp).size(22.dp)
                    )
                }

                Spacer(Modifier.width(14.dp))

                Column {
                    Text(
                        flavor.replaceFirstChar { it.uppercase() },
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        theme.label,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.85f)
                    )
                }
            }

            Spacer(Modifier.height(18.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                StatPill("Version", "v${BuildConfig.VERSION_NAME}", Modifier.weight(1f))
                StatPill("Variant", currentVariant, Modifier.weight(1f))
                StatPill("Branch", BuildConfig.GIT_BRANCH, Modifier.weight(1f))
            }
        }
    }
}

@Composable
fun StatPill(label: String, value: String, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        color = Color.White.copy(alpha = 0.14f)
    ) {
        Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp)) {
            Text(label, style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.75f))
            Text(value, style = MaterialTheme.typography.bodyMedium, color = Color.White, fontWeight = FontWeight.SemiBold, maxLines = 1)
        }
    }
}

@Composable
fun VariantChipsRow(variants: List<String>, currentVariant: String) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(vertical = 2.dp)
    ) {
        items(variants.size) { i ->
            val name = variants[i]
            val isCurrent = name.equals(currentVariant, ignoreCase = true)
            val flavorPart = name.replace("Debug", "").replace("Release", "")
            val theme = envThemeFor(flavorPart)

            val scale by animateFloatAsState(
                targetValue = if (isCurrent) 1f else 0.96f,
                animationSpec = tween(250),
                label = "chipScale"
            )

            if (isCurrent) {
                Box(
                    modifier = Modifier
                        .scale(scale)
                        .clip(RoundedCornerShape(50))
                        .background(Brush.horizontalGradient(theme.gradient))
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            } else {
                FilterChip(
                    selected = false,
                    onClick = {},
                    label = { Text(name, fontSize = 13.sp) },
                    shape = RoundedCornerShape(50),
                    modifier = Modifier.scale(scale)
                )
            }
        }
    }
}

@Composable
fun ExpandableBuildDetails() {
    var expanded by remember { mutableStateOf(false) }

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            Row(
                modifier = Modifier.fillMaxWidth().clickable { expanded = !expanded },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Build Details", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)

                val rotation by animateFloatAsState(
                    targetValue = if (expanded) 180f else 0f,
                    label = "chevronRotation"
                )
                Icon(Icons.Default.ExpandMore, contentDescription = null, modifier = Modifier.rotate(rotation))
            }

            AnimatedVisibility(visible = expanded) {
                Column(modifier = Modifier.padding(top = 12.dp)) {
                    InfoRow("Application", "AutomateApkName", Icons.Default.Android)
                    InfoRow("Flavor", BuildConfig.FLAVOR, Icons.Default.Tag)
                    InfoRow("Build Type", BuildConfig.BUILD_TYPE, Icons.Default.Code)
                    InfoRow("Version", "${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})", Icons.Default.Info)
                    InfoRow("Git Branch", BuildConfig.GIT_BRANCH, Icons.Default.CallSplit)
                    InfoRow("Build Date", BuildConfig.BUILD_DATE, Icons.Default.CalendarToday)
                }
            }
        }
    }
}

@Composable
fun InfoRow(title: String, value: String, icon: ImageVector) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 7.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(shape = RoundedCornerShape(10.dp), color = MaterialTheme.colorScheme.primaryContainer) {
            Icon(
                icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.padding(8.dp).size(14.dp)
            )
        }
        Spacer(Modifier.width(12.dp))
        Column {
            Text(title, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
fun ApkNameCard(apkName: String) {

    val clipboard = LocalClipboardManager.current
    var copied by remember { mutableStateOf(false) }

    val buttonScale by animateFloatAsState(
        targetValue = if (copied) 0.97f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "buttonScale"
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Generated APK Name", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)

                AnimatedVisibility(
                    visible = copied,
                    enter = fadeIn() + scaleIn(),
                    exit = fadeOut() + scaleOut()
                ) {
                    Surface(shape = RoundedCornerShape(50), color = MaterialTheme.colorScheme.primary) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(Modifier.width(4.dp))
                            Text("Copied", fontSize = 11.sp, color = MaterialTheme.colorScheme.onPrimary)
                        }
                    }
                }
            }

            Spacer(Modifier.height(10.dp))

            Surface(
                shape = RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 2.dp
            ) {
                Text(
                    text = apkName,
                    style = MaterialTheme.typography.bodyMedium.copy(fontFamily = FontFamily.Monospace),
                    modifier = Modifier.padding(14.dp)
                )
            }

            Spacer(Modifier.height(14.dp))

            Button(
                onClick = {
                    clipboard.setText(AnnotatedString(apkName))
                    copied = true
                },
                modifier = Modifier.fillMaxWidth().scale(buttonScale),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text(if (copied) "Copied to clipboard" else "Copy APK Name")
            }
        }
    }
}
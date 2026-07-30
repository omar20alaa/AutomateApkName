package app.automate_apk_name

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Numbers
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material.icons.filled.Update
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {

        Text(
            text = "📦 APK Name Automation",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = "Every build variant generates a unique APK name automatically.",
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(Modifier.height(24.dp))

        InfoCard(
            "Application",
            "AutomateApkName",
            Icons.Default.Android
        )

        InfoCard(
            "Flavor",
            BuildConfig.FLAVOR,
            Icons.Default.Tag
        )

        InfoCard(
            "Build Type",
            BuildConfig.BUILD_TYPE,
            Icons.Default.Code
        )

        InfoCard(
            "Version Name",
            BuildConfig.VERSION_NAME,
            Icons.Default.Update
        )

        InfoCard(
            "Version Code",
            BuildConfig.VERSION_CODE.toString(),
            Icons.Default.Numbers
        )

        InfoCard(
            "Git Branch",
            BuildConfig.GIT_BRANCH,
            Icons.Default.Apps
        )

        InfoCard(
            "Build Date",
            BuildConfig.BUILD_DATE,
            Icons.Default.CalendarToday
        )

        Spacer(Modifier.height(20.dp))

        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {

            Column(
                modifier = Modifier.padding(16.dp)
            ) {

                Text(
                    "Generated APK Name",
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(Modifier.height(12.dp))

                Text(
                    apkName,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }

        Spacer(Modifier.height(32.dp))
    }
}

@Composable
fun InfoCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
    ) {

        Row(
            modifier = Modifier.padding(16.dp)
        ) {

            Icon(
                imageVector = icon,
                contentDescription = null
            )

            Spacer(Modifier.width(16.dp))

            Column {

                Text(
                    title,
                    style = MaterialTheme.typography.titleMedium
                )

                Text(
                    value,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}
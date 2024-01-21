package fe.linksheet.composable.settings.dev

import android.content.Intent
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeveloperMode
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import fe.linksheet.R
import fe.linksheet.activity.onboarding.OnboardingActivity
import fe.linksheet.composable.settings.SettingsScaffold
import fe.linksheet.composable.util.ColoredIcon
import fe.linksheet.composable.util.SettingsItemRow
import fe.linksheet.composable.util.SwitchRow
import fe.linksheet.module.viewmodel.DevSettingsViewModel
import org.koin.androidx.compose.koinViewModel


@Composable
fun DevSettingsRoute(
//    navController: NavHostController,
    onBackPressed: () -> Unit,
    viewModel: DevSettingsViewModel = koinViewModel()
) {
    val context = LocalContext.current

    SettingsScaffold(R.string.dev, onBackPressed = onBackPressed) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxHeight(),
            contentPadding = PaddingValues(horizontal = 5.dp)
        ) {
            item(key = "launch_onboarding") {
                SettingsItemRow(
                    headlineId = R.string.launch_onboaring,
                    subtitleId = R.string.launch_onboaring_explainer,
                    image = {
                        ColoredIcon(
                            icon = Icons.Default.DeveloperMode,
                            descriptionId = R.string.launch_onboaring
                        )
                    },
                    onClick = {
                        context.startActivity(Intent(context, OnboardingActivity::class.java))
                    }
                )
            }

            item(key = "dev_bottomsheet") {
                SwitchRow(
                    state = viewModel.useDevBottomSheet,
                    viewModel = viewModel,
                    headlineId = R.string.enable_dev_bottom_sheet,
                    subtitleId = R.string.enable_dev_bottom_sheet_explainer
                )
            }
        }
    }
}

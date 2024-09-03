package fe.linksheet.composable.page.settings.about

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import fe.android.compose.feedback.FeedbackType
import fe.android.compose.feedback.LocalHapticFeedbackInteraction
import fe.android.compose.icon.DrawableIconPainter.Companion.drawable
import fe.android.compose.text.StringResourceContent.Companion.textContent
import fe.composekit.component.CommonDefaults
import fe.composekit.component.list.column.group.ListItemData
import fe.composekit.component.list.column.shape.ClickableShapeListItem
import fe.composekit.layout.column.group
import fe.linksheet.*
import fe.linksheet.composable.component.page.SaneScaffoldSettingsPage


internal object NewCreditsSettingsRouteData {
    val apps = arrayOf(
        ListItemData(
            drawable(R.drawable.app_openlinkwith),
            textContent(R.string.open_link_with),
            textContent(R.string.open_link_with_subtitle_2),
            additional = openLinkWithGithub
        ),
        ListItemData(
            drawable(R.drawable.app_mastodonredirect),
            textContent(R.string.mastodon_redirect),
            textContent(R.string.mastodon_redirect_subtitle_2),
            additional = mastodonRedirectGithub
        ),
        ListItemData(
            drawable(R.drawable.app_seal),
            textContent(R.string.settings_credits__app_name_seal),
            textContent(R.string.settings_credits__app_credit_reason_seal),
            additional = sealGithub
        ),
        ListItemData(
            drawable(R.drawable.app_gmsflags),
            textContent(R.string.settings_credits__app_name_gmsflags),
            textContent(R.string.settings_credits__app_credit_reason_gmsflags),
            additional = gmsFlagsGithub
        )
    )
}

@Composable
fun NewCreditsSettingsRoute(onBackPressed: () -> Unit) {
    val interaction = LocalHapticFeedbackInteraction.current

    SaneScaffoldSettingsPage(headline = stringResource(id = R.string.credits), onBackPressed = onBackPressed) {
        group(array = NewCreditsSettingsRouteData.apps) { app, padding, shape ->
            ClickableShapeListItem(
                shape = shape,
                padding = padding,
                headlineContent = app.headlineContent,
                supportingContent = app.subtitleContent,
                leadingContent = {
                    Box(modifier = CommonDefaults.BaseContentModifier, contentAlignment = Alignment.Center) {
                        Image(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape),
                            painter = app.icon!!.rememberPainter(),
                            contentDescription = null
                        )
                    }
                },
                onClick = {
                    interaction.openUri(app.additional!!, FeedbackType.LongPress)
                }
            )
        }
    }
}

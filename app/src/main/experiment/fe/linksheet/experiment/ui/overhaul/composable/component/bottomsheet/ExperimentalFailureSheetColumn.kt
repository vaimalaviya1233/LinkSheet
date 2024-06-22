package fe.linksheet.experiment.ui.overhaul.composable.component.bottomsheet

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import androidx.compose.ui.util.fastForEachIndexed
import fe.linksheet.R
import fe.linksheet.experiment.ui.overhaul.composable.component.layout.*
import fe.linksheet.experiment.ui.overhaul.composable.component.layout.DialogMaxWidth
import fe.linksheet.experiment.ui.overhaul.composable.util.ProvideContentColorTextStyle
import kotlin.math.max

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ExperimentalFailureSheetColumn(
    onShareClick: () -> Unit,
    onCopyClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 24.dp, end = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(modifier = Modifier.widthIn(min = DialogMinWidth, max = DialogMaxWidth)) {
            CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.secondary) {
                Box(
                    modifier = Modifier
                        .padding(bottom = 16.dp)
                        .align(Alignment.CenterHorizontally)
                ) {
                    Icon(
                        imageVector = Icons.Default.Error,
                        contentDescription = null,
                    )
                }
            }
            ProvideContentColorTextStyle(
                contentColor = MaterialTheme.colorScheme.onSurface,
                textStyle = MaterialTheme.typography.headlineSmall
            ) {
                Box(
                    modifier = Modifier
                        .padding(bottom = 16.dp)
                        .align(Alignment.CenterHorizontally)
                ) {
                    Text(
                        text = stringResource(id = R.string.link_handle_failed),
                    )
                }
            }

            ProvideContentColorTextStyle(
                contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                textStyle = MaterialTheme.typography.bodyMedium
            ) {
                Column(
                    modifier = Modifier
                        .weight(weight = 1f, fill = false)
                        .padding(bottom = 24.dp)
                        .align(Alignment.Start),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    Text(text = stringResource(id = R.string.link_handle_failed_text))

                    TextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 48.dp),
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Public,
                                contentDescription = null,
                            )
                        },
                        label = {
                            Text(text = stringResource(id = R.string.link_handle_failed_text_field_label))
                        },
                        singleLine = true,
                        value = "exampleurl", onValueChange = {}
                    )
                }
            }


        }

        Box(modifier = Modifier.align(Alignment.End)) {
            AlertDialogFlowRow(
                mainAxisSpacing = ButtonsMainAxisSpacing,
                crossAxisSpacing = ButtonsCrossAxisSpacing
            ) {
                FilledTonalButton(
                    contentPadding = ButtonDefaults.ButtonWithIconContentPadding,
                    onClick = { /*TODO*/ }
                ) {
                    Icon(
                        modifier = Modifier.size(ButtonDefaults.IconSize),
                        imageVector = Icons.Default.RestartAlt,
                        contentDescription = stringResource(id = R.string.retry)
                    )

                    Spacer(modifier = Modifier.width(ButtonDefaults.IconSpacing))

                    Text(text = stringResource(id = R.string.retry))
                }

                Button(
                    contentPadding = ButtonDefaults.ButtonWithIconContentPadding,
                    onClick = onShareClick,

                    ) {
                    Icon(
                        modifier = Modifier.size(ButtonDefaults.IconSize),
                        imageVector = Icons.Default.Search,
                        contentDescription = stringResource(id = R.string.search)
                    )

                    Spacer(modifier = Modifier.width(ButtonDefaults.IconSpacing))

                    Text(text = stringResource(id = R.string.search))
                }
            }
        }
    }
}



@Preview(showBackground = true, widthDp = 400)
@Composable
fun ExperimentalFailureSheetColumnPreview() {
    Surface(
//        modifier = modifier,
//        shape = shape,
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
        tonalElevation = 6.0.dp,
    ) {
        ExperimentalFailureSheetColumn(
            onShareClick = {},
            onCopyClick = {}
        )
    }


}



package org.wordpress.android.designsystem

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.wordpress.android.R

@Composable
fun DesignSystemColorsScreen(
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(
            dimensionResource(id = R.dimen.reader_follow_sheet_button_margin_top)
        )
    ) {
        item {
            val list: List<ColorOption> = listOf(
                ColorOption("Primary Foreground", MaterialTheme.colorScheme.primary),
                ColorOption("Primary Background", MaterialTheme.colorScheme.primaryContainer),
                ColorOption("Secondary Foreground", MaterialTheme.colorScheme.secondary),
                ColorOption("Secondary Background", MaterialTheme.colorScheme.secondaryContainer),
                ColorOption("Tertiary Foreground", MaterialTheme.colorScheme.tertiary),
                ColorOption("Tertiary Background", MaterialTheme.colorScheme.tertiaryContainer),
                ColorOption("Quartenary", MaterialTheme.colorScheme.quartenary),
                ColorOption("Brand", MaterialTheme.colorScheme.brand),
                ColorOption("Brand Background", MaterialTheme.colorScheme.brandContainer),
                ColorOption("Error", MaterialTheme.colorScheme.error),
                ColorOption("Warning", MaterialTheme.colorScheme.warning),
                ColorOption("WP Foreground", MaterialTheme.colorScheme.wp),
                ColorOption("WP Background", MaterialTheme.colorScheme.wpContainer),
                )
            ColorCardList(list)
        }
    }
}
@OptIn(ExperimentalStdlibApi::class)
@Composable
fun ColorCard (colorName: String, color: Color) {
    Row (modifier = Modifier.padding(all = 3.dp)) {
        Box (Modifier.background(MaterialTheme.colorScheme.tertiaryContainer)) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(15.dp)
                    .then(
                        Modifier
                            .fillMaxWidth()
                            .wrapContentSize(Alignment.TopCenter),
                    )
            ) {
                Text(
                    modifier = Modifier.padding(start = 25.dp, end = 40.dp),
                    text = colorName,
                    color = MaterialTheme.colorScheme.primary,
                )
                Text(
                    modifier = Modifier.padding(start = 25.dp, end = 40.dp),
                    text = "#" + color.value.toHexString().uppercase().substring(0,8),
                    color = MaterialTheme.colorScheme.secondary
                )
            }
            Column(
                modifier = Modifier
                    .padding(15.dp)
                    .then(
                        Modifier
                            .fillMaxWidth()
                            .wrapContentSize(Alignment.TopStart)
                    )
            ) {
                Box(
                    modifier = Modifier
                        .size(45.dp)
                        .clip(shape = RoundedCornerShape(5.dp, 5.dp, 5.dp, 5.dp))
                        .background(color)
                        .border(width = 2.dp,
                            color = MaterialTheme.colorScheme.primary,
                            shape = RoundedCornerShape(5.dp))
                )
            }
        }
    }
}
@Composable
fun ColorCardList(colorOptions: List<ColorOption>) {
    colorOptions.forEach { colorOption ->
        ColorCard(colorOption.title, colorOption.color)
    }
}
    class ColorOption(var title: String, var color: Color) {
        fun getInfo(): String {
            return "$title ${color.value}"
        }
    }

@Preview(name = "Light Mode")
@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showBackground = true,
    name = "Dark Mode"
)
@Composable
fun DesignSystemColorsScreenPreview() {
    DesignSystemTheme {
        val list: List<ColorOption> = listOf(
            ColorOption("Primary Foreground", MaterialTheme.colorScheme.primary)
        )
        ColorCardList(list)
    }
}


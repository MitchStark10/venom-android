package com.venom.venomtasks.components

import android.provider.Settings.Global
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.ChipBorder
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import com.venom.venomtasks.classes.GlobalState
import com.venom.venomtasks.classes.LogTag
import com.venom.venomtasks.classes.RefreshCounter
import com.venom.venomtasks.classes.Tag
import com.venom.venomtasks.classes.Views
import com.venom.venomtasks.login.ui.theme.DarkBlue
import com.venom.venomtasks.login.ui.theme.DarkGreen
import com.venom.venomtasks.login.ui.theme.DarkOrange
import com.venom.venomtasks.login.ui.theme.DarkRed
import com.venom.venomtasks.services.ListService
import com.venom.venomtasks.services.RetrofitBuilder
import com.venom.venomtasks.services.TagService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

val tagColorMap = mapOf(
    "green" to DarkGreen,
    "orange" to DarkOrange,
    "blue" to DarkBlue,
    "red" to DarkRed
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TagPill(
    tag: Tag,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = { /* noop */  },
) {

    val chipInteractionSource = remember {
        MutableInteractionSource()
    }
    val containerColor = tagColorMap[tag.tagColor] ?: Color.Blue

    Column ( modifier = modifier ){
        Box {
            SuggestionChip(
                onClick = onClick,
                label = { Text(tag.tagName) },
                colors = SuggestionChipDefaults.suggestionChipColors(containerColor = containerColor, labelColor = Color.White ),
                border = SuggestionChipDefaults.suggestionChipBorder(borderColor = containerColor),
                interactionSource = chipInteractionSource
            )
        }
    }
}
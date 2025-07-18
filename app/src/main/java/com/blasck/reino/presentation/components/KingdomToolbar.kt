package com.blasck.reino.presentation.components

import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.blasck.reino.R
import com.blasck.reino.presentation.state.ToolbarState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KingdomToolbar(
    state: ToolbarState,
    scrollBehavior: TopAppBarScrollBehavior,
    onBackAction: () -> Unit,
    onExtraAction: () -> Unit
) {
    val defaultNavIcon = R.drawable.ic_btn_back
    var navIcon: Int = 0;
    var title: String = ""
    var actionIcon: Int = 0;

    state.let {
        when(it){
            is ToolbarState.CanEdit -> {
                title = it.title
                actionIcon = it.extraIcon
            }
            is ToolbarState.Editing -> {
                title = it.title
                navIcon = it.backIcon
                actionIcon = it.extraIcon
            }
            is ToolbarState.Home -> {
                title = it.title
                navIcon = it.backIcon
            }
            is ToolbarState.OnlyTitle -> {
                title = it.title
            }
        }
    }
    CenterAlignedTopAppBar(
        title = {
            Text(title)
        },
        navigationIcon = {
            IconButton(onClick = { onBackAction() }) {
                Icon(painter = painterResource(id = if(navIcon == 0) defaultNavIcon else navIcon), contentDescription = null)

            }
        },
        actions = {
            if(actionIcon != 0) {
                IconButton(onClick = { onExtraAction() }) {
                    Icon(painter = painterResource(id = actionIcon), contentDescription = null)
                }
            }
        },
        scrollBehavior = scrollBehavior
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun KingdomToolbarPreviewHome() {
    KingdomToolbar(
        state = ToolbarState.Home(),
        scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(),
        onBackAction = {},
        onExtraAction = {}
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun KingdomToolbarPreviewOnlyTitle() {
    KingdomToolbar(
        state = ToolbarState.OnlyTitle("OnlyTitle"),
        scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(),
        onBackAction = {},
        onExtraAction = {}
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun KingdomToolbarPreviewEditing() {
    KingdomToolbar(
        state = ToolbarState.Editing("Editing"),
        scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(),
        onBackAction = {},
        onExtraAction = {}
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun KingdomToolbarPreviewCanEdit() {
    KingdomToolbar(
        state = ToolbarState.CanEdit("CanEdit"),
        scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(),
        onBackAction = {},
        onExtraAction = {}
    )
}
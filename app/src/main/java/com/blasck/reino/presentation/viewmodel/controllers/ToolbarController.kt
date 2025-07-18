package com.blasck.reino.presentation.viewmodel.controllers

import androidx.lifecycle.ViewModel
import com.blasck.reino.presentation.state.ToolbarState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class ToolbarController: ViewModel() {

    private val _toolbarState: MutableStateFlow<ToolbarState> = MutableStateFlow(ToolbarState.Home("home"))
    val toolbarState = _toolbarState.asStateFlow()

    fun updateToolbarState(state: ToolbarState){
        _toolbarState.value = state
    }

}
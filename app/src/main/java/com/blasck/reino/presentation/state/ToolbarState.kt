package com.blasck.reino.presentation.state

import com.blasck.reino.R

sealed class ToolbarState{
    data class Home(val title: String = "O Reino", val backIcon: Int = R.drawable.ic_btn_close): ToolbarState()
    data class OnlyTitle(val title: String): ToolbarState()
    data class Editing(val title: String = "Editando", val backIcon: Int = R.drawable.ic_btn_close, val extraIcon: Int = R.drawable.ic_btn_save): ToolbarState()
    data class CanEdit(val title: String, val extraIcon: Int = R.drawable.ic_btn_edit): ToolbarState()
    data class Error(val title: String, val backIcon: Int = R.drawable.ic_btn_close): ToolbarState()
}
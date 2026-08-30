package com.app.iot.nav
import kotlinx.serialization.Serializable

@Serializable
data object Launcher

@Serializable
data object Home

@Serializable
data object Login

@Serializable
data class Search(val showBackButton: Boolean = true)
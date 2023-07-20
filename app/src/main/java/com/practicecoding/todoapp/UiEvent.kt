package com.practicecoding.todoapp

sealed class UiEvent {
    object PopBackStack: UiEvent()
    data class Navigate(val route: String):UiEvent()
}
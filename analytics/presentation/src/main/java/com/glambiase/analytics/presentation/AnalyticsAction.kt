package com.glambiase.analytics.presentation

sealed interface AnalyticsAction {
    data object OnBackClick : AnalyticsAction
}
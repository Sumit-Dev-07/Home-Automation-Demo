package com.app.iot.nav

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally

private const val DEFAULT_DURATION = 500

object NavAnimations {
    // Forward navigation — new screen slides in from the right
    fun enter(duration: Int = DEFAULT_DURATION): AnimatedContentTransitionScope<*>.() -> EnterTransition = {
        slideInHorizontally(
            animationSpec = tween(duration),
            initialOffsetX = { fullWidth -> fullWidth }
        ) + fadeIn(animationSpec = tween(duration))
    }

    // Forward navigation — current screen slides out to the left
    fun exit(duration: Int = DEFAULT_DURATION): AnimatedContentTransitionScope<*>.() -> ExitTransition = {
        slideOutHorizontally(
            animationSpec = tween(duration),
            targetOffsetX = { fullWidth -> -fullWidth }
        ) + fadeOut(animationSpec = tween(duration))
    }

    // Back navigation — previous screen slides back in from the left
    fun popEnter(duration: Int = DEFAULT_DURATION): AnimatedContentTransitionScope<*>.() -> EnterTransition = {
        slideInHorizontally(
            animationSpec = tween(duration),
            initialOffsetX = { fullWidth -> -fullWidth }
        ) + fadeIn(animationSpec = tween(duration))
    }

    // Back navigation — current screen slides out to the right
    fun popExit(duration: Int = DEFAULT_DURATION): AnimatedContentTransitionScope<*>.() -> ExitTransition = {
        slideOutHorizontally(
            animationSpec = tween(duration),
            targetOffsetX = { fullWidth -> fullWidth }
        ) + fadeOut(animationSpec = tween(duration))
    }

    // Simple fade — good for splash/launcher screens
    fun fadeInOnly(duration: Int = DEFAULT_DURATION): AnimatedContentTransitionScope<*>.() -> EnterTransition = {
        fadeIn(animationSpec = tween(duration))
    }

    fun fadeOutOnly(duration: Int = DEFAULT_DURATION): AnimatedContentTransitionScope<*>.() -> ExitTransition = {
        fadeOut(animationSpec = tween(duration))
    }
}
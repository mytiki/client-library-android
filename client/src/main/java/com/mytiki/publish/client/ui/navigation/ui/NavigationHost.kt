/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in the root directory.
 */

package com.mytiki.apps_receipt_rewards.navigation.ui

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mytiki.apps_receipt_rewards.Rewards
import com.mytiki.apps_receipt_rewards.email.ui.EmailView
import com.mytiki.apps_receipt_rewards.home.ui.HomeView
import com.mytiki.apps_receipt_rewards.license.ui.LicenseTerms
import com.mytiki.apps_receipt_rewards.license.ui.LicenseView
import com.mytiki.apps_receipt_rewards.more.ui.MoreView
import com.mytiki.apps_receipt_rewards.navigation.NavigationRoute
import com.mytiki.apps_receipt_rewards.retailer.ui.RetailerView

private val accountProvider = mutableStateOf<AccountProvider?>(null)

@Composable
fun NavigationHost(activity: AppCompatActivity, navController: NavHostController = rememberNavController()) {
    var finish by mutableStateOf(false)
    val context = LocalContext.current
    navController.addOnDestinationChangedListener { _, _, _ ->
        if (finish) (context as Activity).finish()
    }

    val startRoute: NavigationRoute = if (Rewards.license.isLicensed()) {
        NavigationRoute.HOME
    } else {
        NavigationRoute.LICENSE
    }

    NavHost(navController, startRoute.name) {
        composable(NavigationRoute.LICENSE.name,
            enterTransition = {
                slideInVertically(
                    animationSpec = tween(700),
                    initialOffsetY = { it }
                )
            },
            exitTransition = {
                slideOutVertically(
                    animationSpec = tween(700),
                    targetOffsetY = { it }
                )
            },
            popEnterTransition = {
                slideInVertically(
                    animationSpec = tween(700),
                    initialOffsetY = { it }
                )
            },
            popExitTransition = {
                slideOutVertically(
                    animationSpec = tween(700),
                    targetOffsetY = { it }
                )
            }) {
            LicenseView(
                onGetEstimate = { navController.navigate(NavigationRoute.TERMS.name) },
                onDismiss = {

                    (activity).finish()
                }
            )
        }
        composable(NavigationRoute.TERMS.name,
            enterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(700)
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(700)
                )
            },
            popEnterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(700)
                )
            },
            popExitTransition = {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(700)
                )
            })
        {
            LicenseTerms(
                onBackButton = { navController.popBackStack() },
                onAccept = {
                    navController.navigate(NavigationRoute.HOME.name)
                }
            )
        }
        composable(NavigationRoute.HOME.name,
            enterTransition = {
                slideInVertically(
                    animationSpec = tween(700),
                    initialOffsetY = { it }
                )
            },
            exitTransition = {
                slideOutVertically(
                    animationSpec = tween(700),
                    targetOffsetY = { it }
                )
            },
            popEnterTransition = {
                slideInVertically(
                    animationSpec = tween(700),
                    initialOffsetY = { it }
                )
            },
            popExitTransition = {
                slideOutVertically(
                    animationSpec = tween(700),
                    targetOffsetY = { it }
                )
            }) {
            HomeView(
                onProvider = { prov -> onProvider(prov, navController) },
                onMore = { navController.navigate(NavigationRoute.MORE.name) },
                onDismiss = {
                    finish = true
                    navController.popBackStack(NavigationRoute.HOME.name, true)
                }
            )
        }
        composable(NavigationRoute.MORE.name,
            enterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(700)
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(700)
                )
            },
            popEnterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(700)
                )
            },
            popExitTransition = {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(700)
                )
            }) {
            MoreView(
                onProvider = { prov -> onProvider(prov, navController) },
                onTerms = { navController.navigate(NavigationRoute.TERMS.name) },
                onDecline = {
                    navController.popBackStack(NavigationRoute.HOME.name, true)
                    navController.navigate(NavigationRoute.LICENSE.name)
                },
                onBackButton = { navController.popBackStack() }
            )
        }
        composable(NavigationRoute.RETAILER.name,
            enterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(700)
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(700)
                )
            },
            popEnterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(700)
                )
            },
            popExitTransition = {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(700)
                )
            }) {
            RetailerView(
                activity,
                provider = accountProvider.value!!,
                onBackButton = { navController.popBackStack() }
            )
        }
        composable(NavigationRoute.EMAIL.name,
            enterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(700)
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(700)
                )
            },
            popEnterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(700)
                )
            },
            popExitTransition = {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(700)
                )
            }) {
            EmailView(
                activity,
                provider = accountProvider.value!!,
                onBackButton = { navController.popBackStack() }
            )
        }
    }
}

fun onProvider(prov: AccountProvider, navController: NavController) {
    accountProvider.value = prov
    when (prov.type()) {
        AccountType.RETAILER -> navController.navigate(NavigationRoute.RETAILER.name)
        AccountType.EMAIL -> navController.navigate(NavigationRoute.EMAIL.name)
    }
}
/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in the root directory.
 */

package com.mytiki.publish.client.ui.navigation.ui

import android.app.Activity
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.mytiki.publish.client.ui.email.ui.EmailView
import com.mytiki.publish.client.ui.home.ui.HomeView
import com.mytiki.apps_receipt_rewards.license.ui.LicenseTerms
import com.mytiki.publish.client.ui.license.ui.LicenseView
import com.mytiki.apps_receipt_rewards.more.ui.MoreView
import com.mytiki.apps_receipt_rewards.navigation.NavigationRoute
import com.mytiki.publish.client.ProvidersInterface
import com.mytiki.publish.client.TikiClient
import com.mytiki.publish.client.clo.merchant.MerchantEnum
import com.mytiki.publish.client.email.EmailProviderEnum
import com.mytiki.publish.client.ui.email.EmailViewModel
import com.mytiki.publish.client.ui.home.HomeViewModel
import com.mytiki.publish.client.ui.license.LicenseViewModel
import com.mytiki.publish.client.ui.merchant.ui.MerchantView

@RequiresApi(Build.VERSION_CODES.N)
@Composable
fun NavigationHost(activity: AppCompatActivity, navController: NavHostController = rememberNavController()) {
    var finish by remember {
        mutableStateOf(false)
    }
    val context = LocalContext.current
    navController.addOnDestinationChangedListener { _, _, _ ->
        if (finish) (context as Activity).finish()
    }

    val startRoute: NavigationRoute = if (TikiClient.license.isLicensed()) {
        NavigationRoute.HOME
    } else {
        NavigationRoute.LICENSE_NAVIGATION
    }

    NavHost(navController, startRoute.name) {
        navigation(
            startDestination = NavigationRoute.LICENSE.name,
            route = NavigationRoute.LICENSE_NAVIGATION.name
        ) {
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
                }) {entry ->
                val licenseViewModel = entry.sharedViewModel<LicenseViewModel>(navController)
                LicenseView(
                    licenseViewModel = licenseViewModel,
                    onGetEstimate = { navController.navigate(NavigationRoute.TERMS.name) }
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
            {entry ->
                val licenseViewModel = entry.sharedViewModel<LicenseViewModel>(navController)
                LicenseTerms(
                    licenseViewModel = licenseViewModel,
                    onBackButton = { navController.popBackStack() },
                    onAccept = {
                        navController.navigate(NavigationRoute.HOME.name)
                    }
                )
            }
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
            }) {entry ->
            val homeViewModel = viewModel<HomeViewModel>()
            HomeView(
                homeViewModel = homeViewModel,
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
        composable("${NavigationRoute.MERCHANT.name}/{merchant}",
            arguments = listOf(navArgument("merchant") { type = NavType.StringType }),
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
            }) {backStackEntry ->
            val merchant = backStackEntry.arguments?.getString("merchant")
                ?.let { MerchantEnum.fromString(it) } ?: throw Exception("pass a merchant through the route")
            MerchantView(
                activity,
                provider = merchant,
                onBackButton = { navController.popBackStack() }
            )
        }
        composable("${NavigationRoute.EMAIL.name}/{emailProvider}",
            arguments = listOf(navArgument("emailProvider") { type = NavType.StringType }),
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
            }) {backStackEntry ->
            val emailProvider = backStackEntry.arguments?.getString("userId")
                ?.let { EmailProviderEnum.fromString(it) } ?: throw Exception("pass a emailProvider through the route")
            val emailViewModel = viewModel<EmailViewModel>()
            emailViewModel.updateAccounts(context, emailProvider)
            EmailView(
                activity,
                emailViewModel = emailViewModel,
                emailProvider = emailProvider,
                onBackButton = { navController.popBackStack() }
            )
        }
    }
}

@Composable
inline fun <reified T : ViewModel> NavBackStackEntry.sharedViewModel(
    navController: NavHostController,
): T {
    val navGraphRoute = destination.parent?.route ?: return viewModel()
    val parentEntry = remember(this) {
        navController.getBackStackEntry(navGraphRoute)
    }
    return viewModel(parentEntry)
}

fun onProvider(provider: ProvidersInterface, navController: NavController) {
    if (provider is EmailProviderEnum){
        navController.navigate("${NavigationRoute.EMAIL.name}/$provider")
    } else {
        navController.navigate("${NavigationRoute.MERCHANT.name}/$provider")
    }
}
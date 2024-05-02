package com.domagojleskovic.bleadvert

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.EaseIn
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.WindowCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.domagojleskovic.bleadvert.ui.ForgotPasswordScreen
import com.domagojleskovic.bleadvert.ui.LoginScreen
import com.domagojleskovic.bleadvert.ui.MenuScreen
import com.domagojleskovic.bleadvert.ui.RegisterScreen
import com.domagojleskovic.bleadvert.ui.theme.BLEAdvertTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            enableEdgeToEdge()
            WindowCompat.setDecorFitsSystemWindows(window, false)
            BLEAdvertTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    val emailPasswordAuthenticator = EmailPasswordAuthenticator()

                    NavHost(
                        navController = navController, startDestination = "login",
                        enterTransition = { EnterTransition.None },
                        exitTransition = { ExitTransition.None }
                    ){
                        composable("login"){
                            LoginScreen(
                                onNavigateRegisterScreen = {
                                    navController.navigate("register")
                                },
                                onNavigateForgotPasswordScreen = {
                                    navController.navigate("forgot_password")
                                },
                                onLoginSuccess = {
                                    navController.navigate("menu_screen")
                                },
                                emailPasswordAuthenticator = emailPasswordAuthenticator
                            )
                        }
                        composable("register"){
                            RegisterScreen(
                                onRegisterSuccess = {
                                    navController.navigate("menu_screen")
                                },
                                emailPasswordAuthenticator = emailPasswordAuthenticator
                            )
                        }
                        composable("forgot_password"){
                            ForgotPasswordScreen(
                                onSubmitEmail = {
                                    navController.popBackStack()
                                }
                            )
                        }
                        composable(
                            "menu_screen",
                            enterTransition = {
                                fadeIn(
                                    animationSpec = tween(
                                        300, easing = LinearEasing
                                    )
                                )
                            },

                        ){
                            MenuScreen()
                        }
                    }
                }
            }
        }
    }
}


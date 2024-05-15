package com.domagojleskovic.bleadvert

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.domagojleskovic.bleadvert.ui.ForgotPasswordScreen
import com.domagojleskovic.bleadvert.ui.HomeScreen
import com.domagojleskovic.bleadvert.ui.LoginScreen
import com.domagojleskovic.bleadvert.ui.RegisterScreen
import com.domagojleskovic.bleadvert.ui.theme.BLEAdvertTheme


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
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
                        composable("login",
                            enterTransition = {
                                fadeIn(
                                    animationSpec = tween(
                                        300, easing = LinearEasing
                                    )
                                )
                            },
                        ){
                            LoginScreen(
                                onNavigateRegisterScreen = {
                                    navController.navigate("register")
                                },
                                onNavigateForgotPasswordScreen = {
                                    navController.navigate("forgot_password")
                                },
                                onLoginSuccess = {
                                    navController.navigate("home_screen")
                                },
                                emailPasswordAuthenticator = emailPasswordAuthenticator,
                            )
                        }
                        composable("register",
                            enterTransition = {
                                fadeIn(
                                    animationSpec = tween(
                                        300, easing = LinearEasing
                                    )
                                )
                            },
                        ){
                            RegisterScreen(
                                onRegisterSuccess = {
                                    navController.navigate("home_screen")
                                },
                                emailPasswordAuthenticator = emailPasswordAuthenticator
                            )
                        }
                        composable("forgot_password",
                            enterTransition = {
                                fadeIn(
                                    animationSpec = tween(
                                        300, easing = LinearEasing
                                    )
                                )
                            },){
                            ForgotPasswordScreen(
                                onSubmitEmail = {
                                    navController.popBackStack()
                                }
                            )
                        }
                        composable(
                            "home_screen",
                            enterTransition = {
                                fadeIn(
                                    animationSpec = tween(
                                        300, easing = LinearEasing
                                    )
                                )
                            },

                        ){
                            HomeScreen(){
                                navController.navigate("login")
                            }
                        }
                    }
                }
            }
        }
    }
}


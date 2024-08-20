package com.domagojleskovic.bleadvert.ui

import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import com.domagojleskovic.bleadvert.EmailPasswordAuthenticator
import com.domagojleskovic.bleadvert.R
import com.domagojleskovic.bleadvert.UserInfoStorage
import com.domagojleskovic.bleadvert.ui.theme.MainBlue
import com.domagojleskovic.bleadvert.ui.theme.Typography
import com.domagojleskovic.bleadvert.viewmodels.LoginRegisterViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    LoginScreen(
        onNavigateRegisterScreen = {},
        onLoginSuccess = {},
        emailPasswordAuthenticator = EmailPasswordAuthenticator.getInstance(),
    )
}
@Composable
fun LoginScreen(
    onNavigateRegisterScreen: () -> Unit,
    onLoginSuccess : () -> Unit,
    emailPasswordAuthenticator: EmailPasswordAuthenticator,
    loginRegisterViewModel: LoginRegisterViewModel = LoginRegisterViewModel(),
) {

    val appName = stringResource(id = R.string.app_name)
    val uiColor = if(isSystemInDarkTheme()) Color.Black else Color.White
    val context = LocalContext.current
    val userInfoStorage = UserInfoStorage(context)
    var passwordVisible by remember { mutableStateOf(false)}
    var email by remember { mutableStateOf("d@g.com")} // TODO REMOVE ON RELEASE
    var password by remember { mutableStateOf("123456")} // TODO REMOVE ON RELEASE
    var isLoading by remember { mutableStateOf(false)}
    val scope = rememberCoroutineScope()

    var showForgotPasswordDialog by remember { mutableStateOf(false) }


    LaunchedEffect(Unit) {
        val savedEmail = userInfoStorage.getEmail.first()
        val savedPassword = userInfoStorage.getPassword.first()
        if (savedEmail.isNotEmpty() && savedPassword.isNotEmpty()) {
            isLoading = true
            emailPasswordAuthenticator.signIn(
                savedEmail,
                savedPassword,
                onSuccess = {
                    isLoading = false
                    onLoginSuccess()
                },
                onFailure = {
                    isLoading = false
                    Toast.makeText(context, "Credentials expired", Toast.LENGTH_SHORT).show()
                }
            )
        }
    }
    if(isLoading) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center

        ) {
            CircularProgressIndicator(
                modifier = Modifier.width(96.dp),
                color = MaterialTheme.colorScheme.secondary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
            )
        }
    }else {
        Box(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.40f)
                    .background(MainBlue)
                    .padding(16.dp)
            ) {
                Column(
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxHeight()
                ) {
                    Text(
                        text = "Welcome to\n${appName}",
                        color = Color.White,
                        style = Typography.headlineLarge.copy(fontSize = 42.sp)
                    )
                }
            }
            Surface(
                shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
                color = Color.White,
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .fillMaxHeight(0.65f)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(48.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Login",
                        color = Color.Black,
                        style = Typography.headlineMedium.copy(fontSize = 32.sp)
                    )
                }
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(48.dp))
                    TextInputField(
                        value = email,
                        placeholderText = "Email",
                        onValueChange = { email = it },
                        leadingIcon = { Icon(imageVector = Icons.Filled.Email, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth(),

                        )
                    Spacer(modifier = Modifier.height(16.dp))
                    TextInputField(
                        value = password,
                        placeholderText = "Password",
                        onValueChange = { password = it },
                        leadingIcon = { Icon(imageVector = Icons.Filled.Lock, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth(),
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        trailingIcon = {
                            val icon = if (passwordVisible)
                                Icons.Filled.Visibility
                            else
                                Icons.Filled.VisibilityOff
                            val description =
                                if (passwordVisible) "Hide password" else "Show password"
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = icon,
                                    description,
                                    tint = Color.Black
                                )
                            }
                        }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = { showForgotPasswordDialog = true }) {
                            Text("Forgot Password?", color = MainBlue)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Sign In Button
                    Button(
                        onClick = {
                            isLoading = true
                            emailPasswordAuthenticator.signIn(
                                email,
                                password,
                                onSuccess = {
                                    isLoading = false
                                    scope.launch {
                                        onLoginSuccess()
                                        Log.i("ButtonOnClick", "Success")
                                        userInfoStorage.setEmailAndPassword(email, password)
                                    }
                                },
                                onFailure = {
                                    isLoading = false
                                    Toast.makeText(context, "Invalid credentials", Toast.LENGTH_SHORT).show()
                                }
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .padding(horizontal = 16.dp),
                        shape = RoundedCornerShape(24.dp),
                        colors = ButtonDefaults.buttonColors(MainBlue)
                    ) {
                        Text(
                            text = "SIGN IN",
                            color = Color.White,
                            style = Typography.labelMedium.copy(fontSize = 16.sp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Sign Up
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Don't have an account? ")
                        TextButton(onClick = { onNavigateRegisterScreen() }) {
                            Text("Sign Up", color = MainBlue)
                        }
                    }
                }
            }
        }
    }
    if(showForgotPasswordDialog){
        ForgotPasswordDialog(
            onDismissRequest = { showForgotPasswordDialog = false },
            onConfirm = { enteredEmail ->
                emailPasswordAuthenticator.forgotPassword(context,enteredEmail){
                    showForgotPasswordDialog = false
                }
            }
        )
    }
}

@Composable
fun ForgotPasswordDialog(
    onDismissRequest: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var email by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = {
            Text(
                text = "Forgot Password",
                style = MaterialTheme.typography.headlineMedium.copy(fontSize = 20.sp),
            )
        },
        text = {
            TextInputField(
                value = email,
                onValueChange = { email = it },
                placeholderText = "Enter your email",
                leadingIcon = {
                    Icon(imageVector = Icons.Filled.Email, contentDescription = null)
                },
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm(email)
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MainBlue
                )
            ) {
                Text(
                    text = "Confirm",
                    color = Color.White,
                    style = MaterialTheme.typography.labelMedium.copy(fontSize = 16.sp)
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest)
            {
                Text(
                    text = "Cancel",
                    style = MaterialTheme.typography.labelMedium.copy(fontSize = 16.sp)
                )
            }
        }
    )
}

fun hasSinglePermission(permission: String, context: Context): Boolean {
    return ActivityCompat.checkSelfPermission(
        context,
        permission
    ) == PackageManager.PERMISSION_GRANTED
}
fun askSinglePermission(
    singlePermissionLauncher: ActivityResultLauncher<String>,
    permission: String,
    context: Context,
    actionIfAlreadyGranted: () -> Unit
) {
    if (!hasSinglePermission(permission, context)) {
        //Launching contract permission launcher for the required permissions
        singlePermissionLauncher.launch(permission)
    } else {
        //Permission is already granted so we execute the actionIfAlreadyGranted
        actionIfAlreadyGranted()
    }
}




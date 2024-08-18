package com.domagojleskovic.bleadvert.ui

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Build
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import com.domagojleskovic.bleadvert.EmailPasswordAuthenticator
import com.domagojleskovic.bleadvert.viewmodels.LoginRegisterViewModel
import com.domagojleskovic.bleadvert.R
import com.domagojleskovic.bleadvert.User
import com.domagojleskovic.bleadvert.UserInfoStorage
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    LoginScreen(
        onNavigateRegisterScreen = {},
        onNavigateForgotPasswordScreen = {},
        onLoginSuccess = {},
        emailPasswordAuthenticator = EmailPasswordAuthenticator.getInstance(),
    )
}

@Composable
fun LoginScreen(
    onNavigateRegisterScreen: () -> Unit,
    onNavigateForgotPasswordScreen: () -> Unit,
    onLoginSuccess : () -> Unit,
    emailPasswordAuthenticator: EmailPasswordAuthenticator,
    loginRegisterViewModel: LoginRegisterViewModel = LoginRegisterViewModel()
) {

    val context = LocalContext.current
    val userInfoStorage = UserInfoStorage(context)
    val buttonCurvature = 32.dp
    var passwordVisible by remember { mutableStateOf(false)}
    var email by remember { mutableStateOf("d@g.com")} // TODO REMOVE ON RELEASE
    var password by remember { mutableStateOf("123456")} // TODO REMOVE ON RELEASE
    var isLoading by remember { mutableStateOf(false)}
    val scope = rememberCoroutineScope()


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
    if(isLoading){
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
    }else{
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 100.dp)
                .safeContentPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row {
                Image(
                    painter = painterResource(id = R.drawable.logo_ble),
                    contentDescription = null,
                    modifier = Modifier.size(128.dp)
                        .clip(RoundedCornerShape(16.dp)),
                    contentScale = ContentScale.Crop,
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row {
                Text(
                    text = stringResource(id = R.string.app_name),
                    fontSize = 24.sp
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row {
                OutlinedTextField(
                    value = email,
                    onValueChange = {
                        email = it
                    },
                    shape = RoundedCornerShape(buttonCurvature),
                    label = {
                        Text(text = "email:")
                    },
                    leadingIcon = {
                        Icon(imageVector = Icons.Filled.Person, contentDescription = null)
                    }
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row {
                OutlinedTextField(
                    value = password,
                    onValueChange = {
                        password = it
                    },
                    shape = RoundedCornerShape(buttonCurvature),
                    label = {
                        Text(text = "Password:")
                    },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    trailingIcon = {
                        val icon = if(passwordVisible)
                            Icons.Filled.Visibility
                        else
                            Icons.Filled.VisibilityOff
                        val description = if (passwordVisible) "Hide password" else "Show password"
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = icon,
                                description,
                                tint = Color.Black
                            )
                        }
                    },
                    leadingIcon = {
                        Icon(imageVector = Icons.Filled.Lock, contentDescription = null)
                    }
                )
            }
            Spacer(modifier = Modifier.padding(top = 8.dp))
            Row(
                horizontalArrangement = Arrangement.End
            ){
                ClickableText(
                    text = AnnotatedString("Forgot password?"),
                    onClick = {
                        onNavigateForgotPasswordScreen()
                    },
                    modifier = Modifier.padding(start = 128.dp)
                )
            }
            Spacer(modifier = Modifier.padding(top = 12.dp))
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
                modifier = Modifier.width(128.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Black
                ),

                ) {
                Text(text = "Log in")
            }
            Spacer(modifier = Modifier.weight(1f))
            Row(modifier = Modifier.padding(bottom = 32.dp)){
                Text(text = "Not a member?  ")
                ClickableText(text = AnnotatedString("Sign up"), onClick = {onNavigateRegisterScreen()})
            }
        }
    }
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




package com.domagojleskovic.bleadvert.ui

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.RemoveRedEye
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.domagojleskovic.bleadvert.R
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.domagojleskovic.bleadvert.EmailPasswordAuthenticator
import com.domagojleskovic.bleadvert.UserInfoStorage
import com.domagojleskovic.bleadvert.ui.theme.Typography
import com.domagojleskovic.bleadvert.viewmodels.LoginRegisterViewModel
import kotlinx.coroutines.launch

@Composable
fun TextInputField(
    modifier: Modifier = Modifier,
    value: String,
    placeholderText : String,
    onValueChange: (String) -> Unit,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    leadingIcon: @Composable () -> Unit = {},
    trailingIcon: @Composable () -> Unit = {}
) {
    TextField(
        value = value,
        onValueChange = { onValueChange(it) },
        placeholder = { Text(placeholderText) },
        leadingIcon = leadingIcon,
        modifier = modifier,
        singleLine = true,
        trailingIcon = trailingIcon,
        shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp),
        keyboardOptions = keyboardOptions,
        visualTransformation = visualTransformation
    )
}


@Composable
fun RegisterScreen(
    onRegisterSuccess : () -> Unit,
    onNavigateLoginScreen: () -> Unit,
    emailPasswordAuthenticator: EmailPasswordAuthenticator,
    loginRegisterViewModel: LoginRegisterViewModel = LoginRegisterViewModel()
) {
    val darkGray = Color(0xFF344C64) // Dark Gray Background
    val lightBlue = Color(0xFF378CE7) // Light Blue for Buttons and Highlights
    val darkSurface = Color.White // Darker Surface for the Bottom Section
    val textColor = Color.Black

    val context = LocalContext.current
    val userInfoStorage = UserInfoStorage(context)
    val scope = rememberCoroutineScope()

    var email by remember { mutableStateOf("")}
    var password by remember { mutableStateOf("")}
    var confirmPassword by remember { mutableStateOf("")}
    var isLoading by remember { mutableStateOf(false)}

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
    }
    else{
        Box(
            modifier = Modifier.fillMaxSize(),
        ) {
            // Top half - Dark Gray background with text
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.35f)
                    .background(darkGray)
                    .padding(16.dp)
            ) {
                Column(
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxHeight()
                ) {
                    Text(
                        text = "Create an\nAccount",
                        color = Color.White,
                        style = Typography.headlineLarge.copy(fontSize = 42.sp)
                    )
                }
            }
            Surface(
                shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
                color = darkSurface,
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .fillMaxHeight(0.70f)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Register",
                        color = textColor,
                        style = Typography.headlineMedium.copy(fontSize = 32.sp),
                        modifier = Modifier.padding(vertical = 16.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    TextInputField(
                        modifier = Modifier.fillMaxWidth(),
                        value = email,
                        placeholderText = "Email",
                        onValueChange = { email = it },
                        leadingIcon = {
                            Icon(imageVector = Icons.Default.Email, contentDescription = null)
                        }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    TextInputField(
                        modifier = Modifier.fillMaxWidth(),
                        value = password,
                        placeholderText = "Password",
                        onValueChange = { password = it },
                        leadingIcon = {
                            Icon(imageVector = Icons.Default.Lock, contentDescription = null)
                        },
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    TextInputField(
                        modifier = Modifier.fillMaxWidth(),
                        value = confirmPassword,
                        placeholderText = "Confirm Password",
                        onValueChange = { confirmPassword = it },
                        leadingIcon = {
                            Icon(imageVector = Icons.Default.Lock, contentDescription = null)
                        },
                    )
                    Spacer(modifier = Modifier.height(48.dp))

                    // Sign Up Button
                    Button(
                        onClick = {
                            if(confirmPassword != password){
                                Toast.makeText(context, "Passwords do not match", Toast.LENGTH_SHORT).show()
                            }else{
                                isLoading = true
                                emailPasswordAuthenticator.createAccount(
                                    email,
                                    password,
                                    onSuccess = {
                                        isLoading = false
                                        scope.launch {
                                            onRegisterSuccess()
                                            Log.i("ButtonOnClick", "Success")
                                            userInfoStorage.setEmailAndPassword(email, password)
                                        }
                                    },
                                    onFailure = {
                                        isLoading = false
                                        Toast.makeText(context, "Email already in use", Toast.LENGTH_SHORT).show()
                                    }
                                )
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .padding(horizontal = 16.dp),
                        shape = RoundedCornerShape(24.dp),
                        colors = ButtonDefaults.buttonColors(lightBlue)
                    ) {
                        Text(text = "SIGN UP", color = Color.White)
                    }
                    TextButton(
                        onClick = {
                            isLoading = true
                            emailPasswordAuthenticator.signInAsGuest(
                                onSuccess = {
                                    isLoading = false
                                    onRegisterSuccess()
                                },
                                onFailure = {
                                    isLoading = false
                                    Toast.makeText(context, "Failed creating guest user. Try again", Toast.LENGTH_SHORT).show()
                                }
                            )
                        },
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        Text(
                            text = "Continue as Guest",
                            color = lightBlue,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(WindowInsets.navigationBars.asPaddingValues())
                            .padding(8.dp),
                        verticalArrangement = Arrangement.Bottom
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "Already have an account? ", color = textColor)
                            TextButton(onClick = { onNavigateLoginScreen() }) {
                                Text("Sign In", color = lightBlue)
                            }
                        }
                    }
                }
            }
        }
    }
}


/*
@Composable
fun RegisterScreen(
    onRegisterSuccess : () -> Unit,
    emailPasswordAuthenticator: EmailPasswordAuthenticator,
    loginRegisterViewModel: LoginRegisterViewModel = LoginRegisterViewModel()
) {
    val context = LocalContext.current
    val userInfoStorage = UserInfoStorage(context)
    val scope = rememberCoroutineScope()
    val buttonCurvature = 32.dp
    var passwordVisible by remember { mutableStateOf(false)}

    var email by remember { mutableStateOf("")}
    var password by remember { mutableStateOf("")}
    var confirmPassword by remember { mutableStateOf("")}
    var isLoading by remember { mutableStateOf(false)}


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
                .padding(top = 100.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row {
                Image(
                    painter = painterResource(id = R.drawable.logo_ble),
                    contentDescription = null,
                    modifier = Modifier
                        .size(128.dp)
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
            Spacer(modifier = Modifier.height(16.dp))
            Row {
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = {
                        confirmPassword = it
                    },
                    shape = RoundedCornerShape(buttonCurvature),
                    label = {
                        Text(text = "Confirm password:")
                    },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    leadingIcon = {
                        Icon(imageVector = Icons.Filled.Lock, contentDescription = null)
                    }
                )
            }
            Spacer(modifier = Modifier.height(buttonCurvature))
            Button(
                onClick = {
                    if(confirmPassword != password){
                        Toast.makeText(context, "Passwords do not match", Toast.LENGTH_SHORT).show()
                    }else{
                        isLoading = true
                        emailPasswordAuthenticator.createAccount(
                            email,
                            password,
                            onSuccess = {
                                isLoading = false
                                scope.launch {
                                    onRegisterSuccess()
                                    Log.i("ButtonOnClick", "Success")
                                    userInfoStorage.setEmailAndPassword(email, password)
                                }
                            },
                            onFailure = {
                                isLoading = false
                                Toast.makeText(context, "Email already in use", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                },
                modifier = Modifier.width(128.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Black
                ),

                ) {
                Text(text = "Register")
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row{
                Text(
                    text = "OR",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.W500,
                    fontStyle = FontStyle.Italic
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row{
                Button(
                    modifier = Modifier.width(180.dp),
                    onClick = {
                        isLoading = true
                        emailPasswordAuthenticator.signInAsGuest(
                            onSuccess = {
                                isLoading = false
                                onRegisterSuccess()
                            },
                            onFailure = {
                                isLoading = false
                                Toast.makeText(context, "Failed creating guest user. Try again", Toast.LENGTH_SHORT).show()
                            }
                        )
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                        contentColor = Color.Black
                    ),
                ) {
                    Text(text = "Continue as guest")
                }
            }
        }
    }

}
*/


package com.domagojleskovic.bleadvert.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
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
import androidx.compose.ui.tooling.preview.Preview
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
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import com.domagojleskovic.bleadvert.EmailPasswordAuthenticator
import com.domagojleskovic.bleadvert.LoginRegisterViewModel

@Composable
fun RegisterScreen(
    onRegisterSuccess : () -> Unit,
    emailPasswordAuthenticator: EmailPasswordAuthenticator,
    loginRegisterViewModel: LoginRegisterViewModel = LoginRegisterViewModel()
) {
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
                Image(painter = painterResource(id = R.drawable.feritlogo), contentDescription = null)
            }
            Row {
                Text(
                    text = "Placeholder name",
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
                        // TODO: Show error message
                    }else{
                        isLoading = true
                        emailPasswordAuthenticator.createAccount(email,password){
                            isLoading = false
                            onRegisterSuccess()
                        }
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
                    onClick = { /*TODO*/ },
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

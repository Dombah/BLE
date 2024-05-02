package com.domagojleskovic.bleadvert.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
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
import androidx.compose.ui.text.AnnotatedString

@Preview(showBackground = true)
@Composable
fun ForgotPasswordScreenPreview() {
    ForgotPasswordScreen(
        onSubmitEmail = {}
    )
}


@Composable
fun ForgotPasswordScreen(
    onSubmitEmail : () -> Unit
) {

    val buttonCurvature = 32.dp
    var email by remember { mutableStateOf("")}

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
                    Text(text = "Enter email:")
                },
                leadingIcon = {
                    Icon(imageVector = Icons.Filled.Email, contentDescription = null)
                }
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row {
            Button(
                onClick = {
                    onSubmitEmail()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Black
                )
            ) { 
                Text(text = "Submit")
            }
        }
    }
}

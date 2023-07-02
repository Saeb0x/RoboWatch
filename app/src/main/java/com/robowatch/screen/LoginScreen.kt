package com.robowatch.screen
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.AlertDialog
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.robowatch.R
import com.robowatch.navigation.Screen
import com.robowatch.ui.theme.BackgroundColor
import com.robowatch.ui.theme.Orange

@Composable
fun LoginScreen(navController: NavHostController) {
    // State for the entered username and password
    val usernameState = remember { mutableStateOf("") }
    val passwordState = remember { mutableStateOf("") }
    val errorState = remember { mutableStateOf(false) }
    val showDialog = remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.robo),
            contentDescription = "",
            modifier = Modifier
                .size(150.dp)
                .clip(CircleShape),
        )
        Text(
            text = stringResource(R.string.login_title),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        OutlinedTextField(
            value = usernameState.value,
            onValueChange = { usernameState.value = it },
            label = { Text(text = stringResource(R.string.username_label)) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = passwordState.value,
            onValueChange = { passwordState.value = it },
            label = { Text(text = stringResource(R.string.password_label)) },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                // Perform login logic here
                val username = usernameState.value
                val password = passwordState.value

                if(username.isBlank() || password.isBlank())
                {
                    // Display an error message indicating that all fields are required
                    showDialog.value = true
                    return@Button
                }
                // Authenticate the user using your authentication mechanism
                val isAuthenticated = authenticateUser(username, password) { isAuthenticated ->
                    if (isAuthenticated) {
                        // Navigate to the home screen upon successful login
                        navController.navigate(Screen.Home.route)
                    } else {
                        // Show an error message
                        errorState.value = true
                    }}
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        )
        {
            Text(
                text = stringResource(R.string.login_button_label),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(8.dp))

        if (errorState.value) {
            AlertDialog(
                onDismissRequest = { errorState.value = false },
                title = { Text(text = stringResource(R.string.error_dialog_title)) },
                text = { Text(text = stringResource(R.string.login_error_message)) },
                confirmButton = {
                    Button(
                        onClick = { errorState.value = false }
                    ) {
                        Text(text = stringResource(R.string.dialog_button_ok))
                    }
                })
        }

        val signupText = buildAnnotatedString {
            append(stringResource(R.string.signup_text_part1))
            append(" ") // Add space between the parts
            pushStyle(SpanStyle(textDecoration = TextDecoration.Underline, color = Orange))
            pushStyle(SpanStyle(fontWeight = FontWeight.Bold))
            append(stringResource(R.string.signup_text_part2))
        }

        Spacer(modifier = Modifier.height(8.dp))

        ClickableText(
            text = signupText,
            onClick = {
                // Navigate to the signup screen
                navController.navigate(Screen.AuthS.route)
            }
        )

        if (showDialog.value) {
            AlertDialog(
                onDismissRequest = { showDialog.value = false },
                title = { Text(text = "Error") },
                text = { Text(text = "All fields are required") },
                confirmButton = {
                    Button(onClick = { showDialog.value = false }) {
                        Text(text = "OK")
                    }
                }
            )
        }
    }
}

private fun authenticateUser( username: String, password: String, onAuthenticationResult: (Boolean) -> Unit) {
    // API call, Firebase Authentication, etc.
    // Perform the necessary checks and return the authentication result

    val firebaseAuth = FirebaseAuth.getInstance()

    firebaseAuth.signInWithEmailAndPassword(username, password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // User is successfully authenticated
                onAuthenticationResult(true)
            } else {
                // Handle sign-in error
                val exception = task.exception

                if (exception is FirebaseAuthInvalidUserException) {
                    // User does not exist or has been disabled
                } else if (exception is FirebaseAuthInvalidCredentialsException) {
                    // Invalid password
                } else {
                    // Other sign-in errors
                }

                onAuthenticationResult(false)
            }
        }
        .addOnFailureListener { exception ->
            // Handle exceptions
            exception.printStackTrace()
            onAuthenticationResult(false)
        }
}
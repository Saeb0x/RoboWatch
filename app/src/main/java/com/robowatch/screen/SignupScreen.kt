package com.robowatch.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
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
import com.google.firebase.database.FirebaseDatabase
import com.robowatch.R
import com.robowatch.navigation.Screen
import com.robowatch.ui.theme.BackgroundColor
import com.robowatch.ui.theme.Orange

@Composable
fun SignupScreen(navController: NavHostController) {
    // State for the entered email and password
    val nameState = remember { mutableStateOf("") }
    val emailState = remember { mutableStateOf("") }
    val passwordState = remember { mutableStateOf("") }
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
            text = stringResource(R.string.signup_title),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 32.dp)
        )

        OutlinedTextField(
            value = nameState.value,
            onValueChange = { nameState.value = it },
            label = { Text(text = stringResource(R.string.name_label)) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = emailState.value,
            onValueChange = { emailState.value = it },
            label = { Text(text = stringResource(R.string.email_label)) },
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
                val name = nameState.value
                val email = emailState.value
                val password = passwordState.value

                if (name.isBlank() || email.isBlank() || password.isBlank()) {
                    // Display an error message indicating that all fields are required
                    showDialog.value = true
                    return@Button
                }

                val firebaseAuth = FirebaseAuth.getInstance()
                firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            // User is successfully signed up
                            val user = firebaseAuth.currentUser
                            navController.navigate(Screen.Home.route)
                            user?.let {
                                val databaseReference = FirebaseDatabase.getInstance().reference
                                val userReference = databaseReference.child("Users").child(user.uid)
                                userReference.child("name").setValue(name)
                                userReference.child("email").setValue(email)
                            }
                        } else {
                            // Handle sign-up error
                            val exception = task.exception

                            // You can display an error message or handle the error accordingly
                        }
                    }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Text(
                text = stringResource(R.string.signup_button_label),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }

        val loginText = buildAnnotatedString {
            append(stringResource(R.string.login_text_part1))
            append(" ") // Add space between the parts
            pushStyle(SpanStyle(textDecoration = TextDecoration.Underline, color = Orange))
            pushStyle(SpanStyle(fontWeight = FontWeight.Bold))
            append(stringResource(R.string.login_text_part2))
        }

        Spacer(modifier = Modifier.height(8.dp))

        ClickableText(
            text = loginText,
            onClick = {
                // Navigate to the signup screen
                navController.navigate(Screen.Auth.route)
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
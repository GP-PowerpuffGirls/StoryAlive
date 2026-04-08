package com.example.storyalive

import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.storyalive.ui.theme.StoryAliveTheme
import androidx.compose.material3.Divider
import androidx.compose.material3.OutlinedButton
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import com.example.storyalive.model.UserLoginRequest
import com.example.storyalive.network.RetrofitClient
import com.example.storyalive.ui.theme.themeColors
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            StoryAliveTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        LoginScreen()
                    }
                }
            }
        }
    }
}

@Composable
fun LoginScreen(
    isLightTheme: Boolean = true
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }


    val colors = themeColors(isLightTheme)
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {

        Card(
            colors = CardDefaults.cardColors(containerColor = colors.card),
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier.fillMaxWidth()
        ) {

            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState())
            ) {

                Text(
                    text = "Welcome Back",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.heading,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Sign in to continue to storyAlive",
                    color = colors.text,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                CustomTextField(
                    label = "Email Address",
                    value = email,
                    onValueChange = { email = it },
                    placeholder = "you@example.com",
                    icon = Icons.Default.Email,
                    textColor = colors.text,
                    borderColor = colors.muted,
                    keyboardType = KeyboardType.Email
                )

                Spacer(modifier = Modifier.height(16.dp))

                PasswordField(
                    label = "Password",
                    value = password,
                    onValueChange = { password = it },
                    showPassword = showPassword,
                    onToggle = { showPassword = !showPassword },
                    textColor = colors.text,
                    borderColor = colors.muted
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {

                        if (email.isBlank() || password.isBlank()) {
                            Toast.makeText(
                                context,
                                "Please enter email and password",
                                Toast.LENGTH_SHORT
                            ).show()
                            return@Button
                        }

                        val request = UserLoginRequest(
                            email = email,
                            password = password
                        )

                        CoroutineScope(Dispatchers.IO).launch {
                            try {
                                val response = RetrofitClient.createApi(context).login(request)

                                if (response.isSuccessful) {
                                    val tokens = response.body()
                                    println("Access Token: ${tokens?.accessToken}")
                                    println("Refresh Token: ${tokens?.refreshToken}")

                                    context.getSharedPreferences("app_prefs", MODE_PRIVATE).edit()
                                        .putString("access_token", tokens?.accessToken?.trim()?.replace("\\s".toRegex(), "") ?: "")
                                        .putString("refresh_token", tokens?.refreshToken?.trim()?.replace("\\s".toRegex(), "") ?: "")
                                        .apply()

                                    context.startActivity(Intent(context, UploadActivity::class.java))

                                } else {
                                    println("Login failed")
                                    CoroutineScope(Dispatchers.Main).launch {
                                        Toast.makeText(context, "Login failed: ${response.code()}", Toast.LENGTH_SHORT).show()
                                    }
                                }

                            } catch (e: Exception) {
                                println("Error: ${e.message}")
                                CoroutineScope(Dispatchers.Main).launch {
                                    Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = colors.accent),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Sign In",
                        color = colors.buttonText,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Divider(color = colors.muted)

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Don't have an account?",
                    color = colors.text,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                Spacer(modifier = Modifier.height(12.dp))

                val context = LocalContext.current

                OutlinedButton(
                    onClick = {
                        context.startActivity(
                            Intent(context, SignUpActivity::class.java)
                        )
                    },
                    border = BorderStroke(2.dp, colors.accent),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Create Account",
                        color = colors.accent,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun LoginPreview() {
    StoryAliveTheme {
        LoginScreen()
    }
}

package com.example.storyalive

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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.storyalive.ui.theme.StoryAliveTheme
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Divider
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import com.example.storyalive.model.UserSignupRequest
import com.example.storyalive.network.RetrofitClient
import com.example.storyalive.ui.theme.themeColors
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SignUpActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            StoryAliveTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        SignUpScreen()
                    }
                }
            }
        }
    }
}
val availableTags = listOf(
    "HORROR", "LOVE", "SADNESS", "DRAMA", "DARK", "EMOTIONAL",
    "MYSTERY", "ACTION", "SUPERNATURAL", "RELATIONSHIPS",
    "KIDS", "INTENSE", "EDUCATIONAL", "SELF_HELP", "GRIEF",
    "HAS_SFX", "HAS_BGMUSIC"
)

@Composable
fun SignUpScreen(
    isLightTheme: Boolean = true
) {
    val colors = themeColors(isLightTheme)
    val context = LocalContext.current

    var showPassword by remember { mutableStateOf(false) }
    var showConfirmPassword by remember { mutableStateOf(false) }

    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    val selectedTags = remember { mutableStateListOf<String>() }

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
                    text = "Join storyAlive",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.heading,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Create an account to start sharing your stories",
                    color = colors.text,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                CustomTextField(
                    label = "First Name",
                    value = firstName,
                    onValueChange = { firstName = it },
                    placeholder = "John",
                    icon = Icons.Default.Person,
                    textColor = colors.text,
                    borderColor = colors.muted
                )

                Spacer(modifier = Modifier.height(16.dp))

                CustomTextField(
                    label = "Last Name",
                    value = lastName,
                    onValueChange = { lastName = it },
                    placeholder = "Doe",
                    icon = Icons.Default.Person,
                    textColor = colors.text,
                    borderColor = colors.muted
                )

                Spacer(modifier = Modifier.height(16.dp))

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

                Spacer(modifier = Modifier.height(16.dp))

                PasswordField(
                    label = "Confirm Password",
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    showPassword = showConfirmPassword,
                    onToggle = { showConfirmPassword = !showConfirmPassword },
                    textColor = colors.text,
                    borderColor = colors.muted
                )

                Spacer(modifier = Modifier.height(16.dp))

                CustomTextField(
                    label = "Age",
                    value = age,
                    onValueChange = { age = it },
                    placeholder = "25",
                    icon = Icons.Default.Person,
                    textColor = colors.text,
                    borderColor = colors.muted,
                    keyboardType = KeyboardType.Number
                )

                Spacer(modifier = Modifier.height(24.dp))


                Text(
                    text = "Select your preferences",
                    color = colors.text,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(8.dp))

                Column {
                    availableTags.forEach { tag ->
                        var checked by remember { mutableStateOf(false) }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 2.dp)
                        ) {
                            Checkbox(
                                checked = checked,
                                onCheckedChange = { isChecked ->
                                    checked = isChecked
                                    if (isChecked) selectedTags.add(tag)
                                    else selectedTags.remove(tag)
                                },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = colors.accent,
                                    uncheckedColor = colors.muted
                                )
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = tag, color = colors.text)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {

                        if (password != confirmPassword) {
                            Toast.makeText(context, "Passwords do not match", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        if (age.toIntOrNull() == null || age.toInt() < 5) {
                            Toast.makeText(context, "Age must be ≥ 5", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        if (selectedTags.isEmpty()) {
                            Toast.makeText(context, "Please select at least one tag", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        val passwordRegex = Regex("^(?=.*[A-Za-z])(?=.*\\d).{8,}$")
                        if (!passwordRegex.matches(password)) {
                            Toast.makeText(context, "Password must be at least 8 chars, include letters and numbers", Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        val request = UserSignupRequest(
                            firstName = firstName,
                            lastName = lastName,
                            email = email,
                            password = password,
                            age = age.toIntOrNull() ?: 5,
                            preferencesTags = selectedTags.toList()
                        )

                        CoroutineScope(Dispatchers.IO).launch {

                            try {

                                val response = RetrofitClient.createApi(context).signup(request)

                                if (response.isSuccessful) {

                                    val tokens = response.body()

                                    println("Access Token: ${tokens?.accessToken}")
                                    println("Refresh Token: ${tokens?.refreshToken}")
                                    CoroutineScope(Dispatchers.Main).launch {
                                        Toast.makeText(context, "Signup successful!", Toast.LENGTH_SHORT).show()
                                        context.startActivity(Intent(context, UploadActivity::class.java))
                                    }

                                } else {

                                    println("Signup failed")

                                }

                            } catch (e: Exception) {

                                println("Error: ${e.message}")

                            }
                        }
                    },

                    colors = ButtonDefaults.buttonColors(
                        containerColor = colors.accent
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Create Account",
                        color = if (isLightTheme) Color.White else Color(0xFF1F2937),
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Divider(color = colors.muted)

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Already have an account?",
                    color = colors.text,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                Spacer(modifier = Modifier.height(12.dp))


                OutlinedButton(
                    onClick = {
                        context.startActivity(
                            Intent(context, LoginActivity::class.java)
                        )
                    },
                    border = BorderStroke(2.dp, colors.accent),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Sign In",
                        color = colors.accent,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
            }
        }
    }
}
@Composable
fun CustomTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    icon: ImageVector,
    textColor: Color,
    borderColor: Color,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    Column {
        Text(text = label, color = textColor)

        Spacer(modifier = Modifier.height(6.dp))

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder) },
            leadingIcon = {
                Icon(
                    imageVector = icon,
                    contentDescription = null
                )
            },
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = borderColor,
                unfocusedBorderColor = borderColor
            ),
            shape = RoundedCornerShape(12.dp)
        )
    }
}
@Composable
fun PasswordField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    showPassword: Boolean,
    onToggle: () -> Unit,
    textColor: Color,
    borderColor: Color
) {
    Column {
        Text(text = label, color = textColor)
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text("••••••••") },
            leadingIcon = { Icon(Icons.Default.Lock, null) },
            trailingIcon = {
                IconButton(onClick = onToggle) {
                    Icon(
                        imageVector = if (showPassword)
                            Icons.Default.VisibilityOff
                        else
                            Icons.Default.Visibility,
                        contentDescription = null
                    )
                }
            },
            visualTransformation = if (showPassword)
                VisualTransformation.None
            else
                PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = borderColor,
                unfocusedBorderColor = borderColor
            ),
            shape = RoundedCornerShape(12.dp)
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SignUpPreview() {
    StoryAliveTheme {
        SignUpScreen()
    }
}
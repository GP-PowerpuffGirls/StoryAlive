package com.example.storyalive

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.storyalive.model.UserResponse
import com.example.storyalive.ui.theme.themeColors

@Composable
fun EditProfileScreen(
    user: UserResponse,
    onSave: (UserResponse, String, String) -> Unit, // callback to save changes
    onCancel: () -> Unit,
    isLightTheme: Boolean = true
) {
    val colors = themeColors(isLightTheme)

    var firstName by remember { mutableStateOf(user.firstName) }
    var lastName by remember { mutableStateOf(user.lastName) }
    var email by remember { mutableStateOf(user.email) }
    var age by remember { mutableStateOf(user.age.toString()) }
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        Text("Edit Profile", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = colors.heading)

        OutlinedTextField(
            value = firstName,
            onValueChange = { firstName = it },
            label = { Text("First Name") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = lastName,
            onValueChange = { lastName = it },
            label = { Text("Last Name") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = age,
            onValueChange = { age = it.filter { char -> char.isDigit() } },
            label = { Text("Age") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = currentPassword,
            onValueChange = { currentPassword = it },
            label = { Text("Current Password") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = newPassword,
            onValueChange = { newPassword = it },
            label = { Text("New Password") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row (horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button (
                onClick = {
                    val updatedUser = user.copy(
                        firstName = firstName,
                        lastName = lastName,
                        email = email,
                        age = age.toIntOrNull() ?: user.age
                    )
                    onSave(updatedUser, currentPassword, newPassword)
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Save")
            }

            Button(
                onClick = onCancel,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Gray),
                modifier = Modifier.weight(1f)
            ) {
                Text("Cancel")
            }
        }
    }
}
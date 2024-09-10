package com.example.login_ui_api


import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.login_ui_api.ui.theme.Login_UI_APITheme
import okhttp3.*
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.login_ui_api.ui.theme.Login_UI_APITheme
import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import android.os.Bundle
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.Icon
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import android.util.Log
import androidx.compose.ui.draw.clip
import com.example.login_ui_api.ui.theme.Login_UI_APITheme
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Login_UI_APITheme {
                LoginScreen()
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen() {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var resultMessage by remember { mutableStateOf<String?>(null) }
    var showDialog by remember { mutableStateOf(false) } // Dialog visibility state

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.mobile_password_forgot),
            contentDescription = "Login Icon",
            modifier = Modifier.size(200.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Username") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            leadingIcon = { Icon(Icons.Default.AccountBox, contentDescription = null) },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = Color.Blue,
                unfocusedBorderColor = Color.Gray
            ),
            shape = RoundedCornerShape(8.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = Color.Blue,
                unfocusedBorderColor = Color.Gray
            ),
            shape = RoundedCornerShape(8.dp)
        )

        Spacer(modifier = Modifier.height(3.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(onClick = { /* Handle forgot password */ }) {
                Text("Forgot Password?")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                isLoading = true
                errorMessage = null
                resultMessage = null

                // Execute the first request
                login(username, password, onSuccess = { receivedToken ->
                    Log.d("LoginScreen", "Received token: $receivedToken")

                    // Execute the second request after successfully receiving the token
                    makeSecondRequest(OkHttpClient(), receivedToken, onSuccess = { responseData ->
                        resultMessage = responseData  // Store the result of the second request
                        showDialog = true  // Show dialog with resultMessage
                        isLoading = false
                    }, onError = { error ->
                        errorMessage = error
                        isLoading = false
                    })

                }, onError = { error ->
                    errorMessage = error
                    isLoading = false
                })
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text("Login")
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (isLoading) {
            CircularProgressIndicator()
        }

        errorMessage?.let {
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = it, color = Color.Red)
        }

        // Show AlertDialog when showDialog is true
        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text(text = "Product Information") },
                text = { Text(text = resultMessage ?: "No data") },  // Display resultMessage here
                confirmButton = {
                    TextButton(onClick = { showDialog = false }) {
                        Text("OK")
                    }
                }
            )
        }
    }
}
fun login(username: String, password: String, onSuccess: (String) -> Unit, onError: (String) -> Unit) {
    val client = OkHttpClient()
    val json = JSONObject()
        .put("username", username)
        .put("password", password)
        .toString()

    val requestBody = json
        .toRequestBody("application/json; charset=utf-8".toMediaType())

    val request = Request.Builder()
        .url("https://dummyjson.com/auth/login")
        .post(requestBody)
        .build()

    client.newCall(request).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            onError(e.message ?: "Unknown error occurred")
        }

        override fun onResponse(call: Call, response: Response) {
            if (response.isSuccessful) {
                val responseData = response.body?.string() ?: ""
                val jsonResponse = JSONObject(responseData)
                val token = jsonResponse.getString("token")
                onSuccess(token)
            } else {
                onError("Login failed: ${response.message}")
            }
        }
    })
}

fun makeSecondRequest(client: OkHttpClient, token: String, onSuccess: (String) -> Unit, onError: (String) -> Unit) {
    val secondRequest = Request.Builder()
        .url("https://dummyjson.com/auth/products")
        .header("Authorization", "Bearer $token")
        .get()
        .build()

    client.newCall(secondRequest).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            onError(e.message ?: "Unknown error occurred during second request")
        }

        override fun onResponse(call: Call, response: Response) {
            if (response.isSuccessful) {
                val responseData = response.body?.string() ?: ""
                onSuccess(responseData)  // Pass the response data
            } else {
                onError("Second request failed: ${response.message}")
            }
        }
    })
}


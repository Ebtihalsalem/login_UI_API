package com.example.login_ui_api


import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import android.content.Context
import androidx.compose.foundation.lazy.items
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import androidx.compose.runtime.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.NavController
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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.KeyboardType
import androidx.navigation.compose.rememberNavController
import com.example.login_ui_api.ui.theme.Login_UI_APITheme
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            Login_UI_APITheme {
                NavHost(navController = navController, startDestination = "login_screen") {
                    composable("login_screen") {
                        LoginScreen(context = this@MainActivity, navController = navController)
                    }
                    composable("product_screen/{token}") { backStackEntry ->
                        val token = backStackEntry.arguments?.getString("token")
                        token?.let {
                            ProductScreen(token = it, navController = navController)
                        }
                    }
                    composable("update_product_screen/{productId}/{token}") { backStackEntry ->
                        val productId = backStackEntry.arguments?.getString("productId")?.toIntOrNull()
                        val token = backStackEntry.arguments?.getString("token")
                        productId?.let { id ->
                            token?.let { tkn ->
                                // Retrieve the product based on productId
                                val product = getProductById(id, tkn) // Replace with your data retrieval method
                                UpdateProductScreen(product = product, token = tkn, navController = navController)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun getProductById(productId: Int, token: String): Product {
        // Replace with actual retrieval logic
        return Product(
            id = productId,
            name = "Sample Product",
            description = "Sample Description",
            price = 0.0
        )
    }





@OptIn(ExperimentalMaterial3Api::class)

@Composable
fun LoginScreen(context: Context, navController: NavController) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var resultMessage by remember { mutableStateOf<String?>(null) }
    var showDialog by remember { mutableStateOf(false) }
    val sharedPreferences = context.getSharedPreferences("loginPrefs", Context.MODE_PRIVATE)

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

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                isLoading = true
                errorMessage = null
                resultMessage = null

                login(username, password, context, onSuccess = { receivedToken ->
                    isLoading = false
                    resultMessage = "Login Successful! Token stored."
                    showDialog = true
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

        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                val token = sharedPreferences.getString("token", null)
                if (token != null) {
                    navController.navigate("product_screen/$token")
                } else {
                    resultMessage = "No token found"
                    showDialog = true
                }
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text("Check Token")
        }



        Spacer(modifier = Modifier.height(8.dp))

        if (isLoading) {
            CircularProgressIndicator()
        }

        errorMessage?.let {
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = it, color = Color.Red)
        }

        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text(text = "Info") },
                text = { Text(text = resultMessage ?: "No data") },
                confirmButton = {
                    TextButton(onClick = { showDialog = false }) {
                        Text("OK")
                    }
                }
            )
        }
    }
}

fun login(username: String, password: String, context: Context, onSuccess: (String) -> Unit, onError: (String) -> Unit) {
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

                // Store the token in SharedPreferences
                val sharedPreferences = context.getSharedPreferences("loginPrefs", Context.MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                editor.putString("token", token)
                editor.apply()

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
@Composable
fun ProductScreen(token: String, navController: NavController) {
    var productList by remember { mutableStateOf<List<Product>>(emptyList()) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    LaunchedEffect(token) {
        isLoading = true
        val client = OkHttpClient()

        makeSecondRequest(
            client = client,
            token = token,
            onSuccess = { responseData ->
                // تحليل البيانات وتحويلها إلى قائمة منتجات
                val jsonResponse = JSONObject(responseData)
                val products = jsonResponse.getJSONArray("products")
                val productItems = mutableListOf<Product>()
                for (i in 0 until products.length()) {
                    val product = products.getJSONObject(i)
                    productItems.add(
                        Product(
                            id = product.getInt("id"), // إضافة معرف المنتج
                            name = product.getString("title"),
                            description = product.getString("description"),
                            price = product.getDouble("price")
                        )
                    )
                }
                productList = productItems
                isLoading = false
            },
            onError = {
                errorMessage = it
                isLoading = false
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (isLoading) {
            CircularProgressIndicator()
        }

        errorMessage?.let {
            Text(text = it, color = Color.Red)
        }

        LazyColumn {
            items(productList) { product ->
                ProductCard(product, token, onDelete = { productId ->
                    productList = productList.filter { it.id != productId } // تحديث القائمة بعد الحذف
                }, navController = navController) // Pass the navController here
            }
        }
    }
}

data class Product(
    val id: Int,
    val name: String,
    val description: String,
    val price: Double
)
fun deleteProduct(productId: Int, token: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
    val client = OkHttpClient()
    val request = Request.Builder()
        .url("https://dummyjson.com/auth/products/$productId") // تعديل URL الحذف بناءً على API الخاص بك
        .header("Authorization", "Bearer $token")
        .delete()
        .build()

    client.newCall(request).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            onError(e.message ?: "Unknown error occurred")
        }

        override fun onResponse(call: Call, response: Response) {
            if (response.isSuccessful) {
                onSuccess()
            } else {
                onError("Delete failed: ${response.message}")
            }
        }
    })
}

@Composable
fun ProductCard(product: Product, token: String, onDelete: (Int) -> Unit, navController: NavController) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable {
                navController.navigate("update_product_screen/${product.id}/$token")
            },
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(text = product.name, style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = product.description)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Price: ${product.price}", style = MaterialTheme.typography.bodyMedium)

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    deleteProduct(product.id, token, onSuccess = {
                        onDelete(product.id) // Remove product from list after successful deletion
                    }, onError = { error ->
                        // Handle error
                        Log.e("ProductCard", error)
                    })
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Red,
                    contentColor = Color.White
                )
            ) {
                Text("Delete")
            }
        }
    }
}

@Composable
fun UpdateProductScreen(product: Product, token: String, navController: NavController) {
    var name by remember { mutableStateOf(product.name) }
    var description by remember { mutableStateOf(product.description) }
    var price by remember { mutableStateOf(product.price.toString()) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var resultMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Product Name") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Product Description") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = price,
            onValueChange = { price = it },
            label = { Text("Product Price") },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                isLoading = true
                errorMessage = null
                resultMessage = null

                val priceValue = price.toDoubleOrNull() ?: 0.0

                updateProduct(product.id, name, description, priceValue, token, onSuccess = {
                    isLoading = false
                    resultMessage = "Product updated successfully!"
                }, onError = { error ->
                    errorMessage = error
                    isLoading = false
                })
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Update Product")
        }

        Spacer(modifier = Modifier.height(16.dp))
        if (isLoading) {
            CircularProgressIndicator()
        }

        errorMessage?.let {
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = it, color = Color.Red)
        }

        resultMessage?.let {
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = it, color = Color.Green)
        }
    }
}

    fun updateProduct(
        id: Int,
        name: String,
        description: String,
        price: Double,
        token: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val client = OkHttpClient()
        val json = JSONObject()
            .put("name", name)
            .put("description", description)
            .put("price", price)
            .toString()

        val requestBody = json.toRequestBody("application/json; charset=utf-8".toMediaType())

        val request = Request.Builder()
            .url("https://dummyjson.com/auth/products/$id")
            .header("Authorization", "Bearer $token")
            .put(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                onError(e.message ?: "Unknown error occurred")
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    onSuccess()
                } else {
                    onError("Update failed: ${response.message}")
                }
            }
        })
    }}

/*"username": "emilys",
    "password": "emilyspass"*/

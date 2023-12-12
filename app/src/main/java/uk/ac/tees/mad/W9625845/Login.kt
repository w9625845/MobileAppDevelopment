package uk.ac.tees.mad.W9625845

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.compose.animation.animateColor
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Brush
import androidx.compose.material3.Button
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class Login : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { RegistrationScreen {

        } }
    }



    @Composable
    fun RegistrationScreen(onRegistrationComplete: () -> Unit) {
        var email by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }

        var passwordVisibility by remember { mutableStateOf(false) }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(animatedBackgroundGradient()).padding(PaddingValues(all = Dp(10f)))
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logomedical),
                    contentDescription = "Logo",
                    modifier = Modifier.size(200.dp)
                )
                Spacer(modifier = Modifier.height(20.dp))
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    visualTransformation = if (passwordVisibility) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { passwordVisibility = !passwordVisibility }) {
                            Icon(
                                imageVector = if (passwordVisibility) Icons.Filled.AccountCircle else Icons.Filled.Add,
                                contentDescription = "Toggle Password Visibility"
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(20.dp))
                Button(
                    onClick = { loginUser(email,password,{

                    },{}) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Text("Login")
                }
                Text(
                    buildAnnotatedString {
                        append("Need an account? ")
                        withStyle(style = SpanStyle(color = Color.Blue, textDecoration = TextDecoration.Underline)) {
                            append("Register")
                        }
                    },
                    fontSize = 16.sp,
                    modifier = Modifier.clickable(onClick = {startActivity(Intent(applicationContext,Registation::class.java))})
                )
            }
        }
    }

    @Composable
    fun animatedBackgroundGradient(): Brush {
        val infiniteTransition = rememberInfiniteTransition()
        val colorAnim = infiniteTransition.animateColor(
            initialValue = Color.Blue,
            targetValue = Color.Red,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 3000, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            ), label = ""
        )
        return Brush.verticalGradient(
            colors = listOf(colorAnim.value, Color.Yellow)
        )
    }

    fun loginUser(email: String, password: String, onSuccess: (String) -> Unit, onError: (String) -> Unit) {
        val auth = FirebaseAuth.getInstance()


        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { authTask ->
            if (authTask.isSuccessful) {
                val user = auth.currentUser
                val userId = user?.uid ?: ""

                val database = FirebaseDatabase.getInstance().getReference("users")
                database.child(userId).child("name").get().addOnSuccessListener { dataSnapshot ->
                    val name = dataSnapshot.value as? String ?: ""

                    val sharedPreferences: SharedPreferences = applicationContext.getSharedPreferences("MySharedPref", Context.MODE_PRIVATE)
                    val editor: SharedPreferences.Editor = sharedPreferences.edit()

                    editor.putString("name", name)
                    editor.apply()

                    Toast.makeText(applicationContext,"Login Success $name",Toast.LENGTH_LONG).show()
                    startActivity(Intent(applicationContext,Services::class.java))
                    finish()
                }.addOnFailureListener {
                    onError("Failed to retrieve user data: ${it.message}")
                }
            } else {
                Toast.makeText(applicationContext,"Wrong Details",Toast.LENGTH_LONG).show()
            }
        }
    }

}
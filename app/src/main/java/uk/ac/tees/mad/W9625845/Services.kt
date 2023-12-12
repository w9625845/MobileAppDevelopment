package uk.ac.tees.mad.W9625845

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.biometric.BiometricPrompt
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth

class Services : AppCompatActivity() {

    var onClick = {}
    var userName: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sharedPreferences: SharedPreferences = applicationContext.getSharedPreferences("MySharedPref", Context.MODE_PRIVATE)
        userName  = sharedPreferences.getString("name", null)
        setContent {
            ServicesActivity()
        }
        authenticateUser()
    }

    fun clickAction(text:String){
                 if(text == "Book Doctor Appointment")     {
                     startActivity(Intent(applicationContext,Appointment::class.java))
                 }
        if(text == "Nearby Hospitals")     {
            startActivity(Intent(applicationContext,Near::class.java))
        }

        if(text == "Latest Medical Info")     {
            startActivity(Intent(applicationContext,News::class.java))
        }
        if(text == "View Disease Remedy")     {
            startActivity(Intent(applicationContext,Remedy::class.java))
        }
        if(text == "Ask a Doctor")     {
            startActivity(Intent(applicationContext,AskADoctor::class.java))
        }
    }


    @Composable
    fun ServicesActivity() {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Hello, $userName", style = MaterialTheme.typography.headlineSmall)
                Button(onClick = {FirebaseAuth.getInstance().signOut()
                startActivity(Intent(applicationContext,Login::class.java))}) {
                    Text("Sign Out")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))


            ServiceOption("Book Doctor Appointment")
            ServiceOption("Nearby Hospitals")
            ServiceOption("View Disease Remedy")
            ServiceOption("Ask a Doctor")
            ServiceOption("Latest Medical Info")

            Spacer(modifier = Modifier.height(16.dp))

            MarqueeText(text = "Stay healthy and safe! Remember to check regular health updates.")
        }
    }

    @Composable
    fun ServiceOption(text: String) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
        ) {
            Button(
                onClick = {clickAction(text)},
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = text, modifier = Modifier.padding(8.dp))
            }
        }
    }

    @Composable
    fun MarqueeText(text: String) {
        val textWidth = remember { mutableStateOf(0f) }
        val marquee = rememberInfiniteTransition()
        val xPosition = marquee.animateFloat(
            initialValue = 0f,
            targetValue = -textWidth.value,
            animationSpec = infiniteRepeatable(
                animation = tween(10000, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ), label = ""
        )

        Box(
            modifier = Modifier
                .height(24.dp)
                .background(Color.LightGray)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Text(

                text = text,
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .offset(x = xPosition.value.dp)
                    .onGloballyPositioned { coordinates ->
                        textWidth.value = coordinates.size.width.toFloat()
                    }
            )
        }
    }


    @Preview
    @Composable
    fun PreviewServicesActivity() {
        ServicesActivity()
    }

    private fun authenticateUser() {
        val executor = ContextCompat.getMainExecutor(this)
        val biometricPrompt = BiometricPrompt(this, executor, object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)

            }

            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)

            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()

            }
        })

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Authenticate")
            .setSubtitle("Log in ")
            .setNegativeButtonText("Cancel")
            .build()

        biometricPrompt.authenticate(promptInfo)
    }


}
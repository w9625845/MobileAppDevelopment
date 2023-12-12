package uk.ac.tees.mad.W9625845

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MedAppointSplashScreen {
                VlaidateUserAndLogin()
            }
        }
    }
    fun VlaidateUserAndLogin()
    {
        if(FirebaseAuth.getInstance().currentUser == null)
        {
            startActivity(Intent(this,Registation::class.java))
        }else
        {
            startActivity(Intent(this,Services::class.java))
        }
        finish()
    }
    @Composable
    fun MedAppointSplashScreen(onSplashEnd: () -> Unit) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {

            Image(painter = painterResource(id = R.drawable.logomedical), contentDescription = "App Logo")

            LaunchedEffect(key1 = true) {
                delay(3000)
                onSplashEnd()
            }
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun DefaultPreview() {
        MedAppointSplashScreen {}
    }
}


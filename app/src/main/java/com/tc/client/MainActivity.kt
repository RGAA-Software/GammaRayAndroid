package com.tc.client

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.tc.client.theme.ThunderCloudTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ThunderCloudTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Button(

                        onClick = {
                            startFrameActivity()
                        }
                    ) {

                    }
                }
            }
        }
    }

    fun startFrameActivity() {
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, FrameRenderActivity::class.java))
        }, 300);
    }

}
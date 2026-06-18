package com.forvill.hisaabkhataa.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.forvill.hisaabkhataa.core.ui.theme.HisaabKhataaTheme
import com.forvill.hisaabkhataa.navigation.HisaabKhataaAppRoot

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HisaabKhataaTheme {
                HisaabKhataaAppRoot()
            }
        }
    }
}

package com.fieldops.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.fieldops.app.ui.navigation.FieldOpsNavigation
import com.fieldops.app.ui.theme.FieldOpsTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FieldOpsTheme {
                FieldOpsNavigation()
            }
        }
    }
}

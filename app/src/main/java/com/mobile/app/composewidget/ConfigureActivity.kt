package com.mobile.app.composewidget

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.glance.appwidget.GlanceAppWidgetManager
import com.mobile.app.composewidget.ui.theme.ComposeWidgetTheme
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class ConfigureActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposeWidgetTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.background) {
                    ConfigureScreen()
                }
            }
        }
    }
}

@Composable
fun ConfigureScreen() {

    val context = androidx.compose.ui.platform.LocalContext.current
    val scope = rememberCoroutineScope()
    val alpha = runBlocking { context.dataStore.readValue(intPreferencesKey("alpha"),255).first() }
    var sliderPos by remember { mutableStateOf(alpha.toFloat()) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {

        Button(onClick = {
            scope.launch {
                GlanceAppWidgetManager(context.applicationContext).getGlanceIds(FirstAppWidget::class.java).firstOrNull()?.let {
                    context.dataStore.storeValue(intPreferencesKey("alpha"),sliderPos.toInt())
                    FirstAppWidget().update(context = context.applicationContext,it)
                }
                (context as ComponentActivity).apply {
                    setResult(Activity.RESULT_OK, Intent())
                    finish()
                }
            }
        }) { Text(text = "Save") }

        Text(text = "alpha = ${sliderPos.toInt()}")

        Slider(
            modifier = Modifier.fillMaxWidth(),
            value = sliderPos,
            onValueChange = {
                Log.i("hsik","onValueChange = $it")
                sliderPos = it
            },
            valueRange = 0f..255f
        )
    }
}
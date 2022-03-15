package com.mobile.app.composewidget


import android.content.Context
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.glance.*
import androidx.glance.action.ActionParameters
import androidx.glance.action.actionParametersOf
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.appWidgetBackground
import androidx.glance.layout.*
import androidx.glance.state.PreferencesGlanceStateDefinition
import androidx.glance.text.Text
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class FirstAppWidget : GlanceAppWidget() {

    override val stateDefinition = PreferencesGlanceStateDefinition

    @Composable
    override fun Content() {

        val context = LocalContext.current
        val count = runBlocking { context.dataStore.readValue(intPreferencesKey("count"), 0).first() }
        val drawable = context.resources.getDrawable(R.drawable.app_widget_background,null)
        drawable.alpha = runBlocking { context.dataStore.readValue(intPreferencesKey("alpha"), 0).first() }

        Column(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(imageProvider = ImageProvider(drawable.toBitmap()))
                .clickable(actionStartActivity<MainActivity>())
                .appWidgetBackground()
                .padding(16.dp),
            horizontalAlignment = Alignment.Horizontal.CenterHorizontally
        ) {

            Row(modifier = GlanceModifier.fillMaxWidth(), horizontalAlignment = Alignment.Horizontal.End) {
                Image(
                    modifier = GlanceModifier.clickable(actionStartActivity<ConfigureActivity>()),
                    provider = ImageProvider(android.R.drawable.ic_dialog_info),
                    contentDescription = null
                )
            }

            Text(text = count.toString())

            Row(modifier = GlanceModifier.fillMaxWidth(), horizontalAlignment = Alignment.Horizontal.CenterHorizontally) {

                Button(
                    modifier = GlanceModifier.padding(end = 5.dp),
                    text = "click",
                    onClick = actionRunCallback<CountAction>(
                        actionParametersOf(
                            ActionParameters.Key<Int>("countActionKey") to count
                        )
                    )
                )

                Button(
                    text = "reset",
                    onClick = actionRunCallback<ResetAction>()
                )
            }
        }
    }
}

class FirstAppWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = FirstAppWidget()
}

class CountAction : ActionCallback {
    override suspend fun onRun(context: Context, glanceId: GlanceId, parameters: ActionParameters) {
        val count = parameters[ActionParameters.Key("countActionKey")]?:0
        Log.i("hsik", "onRun = $count")
        context.dataStore.storeValue(intPreferencesKey("count"), count + 1)
        FirstAppWidget().update(context = context, glanceId = glanceId)
    }
}

class ResetAction : ActionCallback {
    override suspend fun onRun(context: Context, glanceId: GlanceId, parameters: ActionParameters) {
        context.dataStore.storeValue(intPreferencesKey("count"), 0)
        FirstAppWidget().update(context = context, glanceId = glanceId)
    }
}

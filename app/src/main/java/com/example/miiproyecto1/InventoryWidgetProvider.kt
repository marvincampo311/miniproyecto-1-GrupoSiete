package com.example.miiproyecto1

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import android.util.Log
import com.example.miiproyecto1.R

class InventoryWidgetProvider : AppWidgetProvider() {

    companion object {
        private const val TOGGLE_VISIBILITY_ACTION = "com.example.miiproyecto1.TOGGLE_VISIBILITY"
        private const val MANAGE_INVENTORY_ACTION = "com.example.miiproyecto1.MainActivity"
        private var isInventoryVisible = false
        private const val HIDDEN_VALUE = "$ ****"
    }

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        appWidgetIds.forEach { appWidgetId ->
            Thread {
                val db = AppDatabase.getDatabase(context.applicationContext)
                val productList = db.productDao().getAllProducts()

                var totalSum = 0.0
                for (product in productList) {
                    totalSum += product.price * product.cantidad
                }

                val locale = java.util.Locale("es", "CO")
                val currencyFormatter = java.text.NumberFormat.getCurrencyInstance(locale)
                val formattedTotal = currencyFormatter.format(totalSum)

                val valueToShow = if (isInventoryVisible) formattedTotal else HIDDEN_VALUE

                val views = RemoteViews(context.packageName, R.layout.inventory_widget_layout)
                views.setTextViewText(R.id.inventory_value, valueToShow)
                views.setImageViewResource(
                    R.id.toggle_visibility_icon,
                    if (isInventoryVisible) R.drawable.ic_visibility_off else R.drawable.ic_visibility_on
                )

                val toggleVisibilityIntent = Intent(context, InventoryWidgetProvider::class.java).apply {
                    action = TOGGLE_VISIBILITY_ACTION
                    putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
                }
                val togglePendingIntent: PendingIntent = PendingIntent.getBroadcast(
                    context,
                    appWidgetId,
                    toggleVisibilityIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
                views.setOnClickPendingIntent(R.id.toggle_visibility_icon, togglePendingIntent)

                val manageIntent = Intent(context, MainActivity::class.java)
                manageIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                val managePendingIntent = PendingIntent.getActivity(
                    context,
                    0,
                    manageIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
                views.setOnClickPendingIntent(R.id.manage_inventory_button, managePendingIntent)

                views.setOnClickPendingIntent(R.id.settings_icon, managePendingIntent)

                appWidgetManager.updateAppWidget(appWidgetId, views)
            }.start()
        }
    }

    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        Log.d("Widget", "Widget eliminado")
    }

    override fun onEnabled(context: Context) {
        Log.d("Widget", "Widget habilitado")
    }

    override fun onDisabled(context: Context) {
        Log.d("Widget", "Widget deshabilitado")
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)

        val appWidgetId = intent.extras?.getInt(
            AppWidgetManager.EXTRA_APPWIDGET_ID,
            AppWidgetManager.INVALID_APPWIDGET_ID
        ) ?: AppWidgetManager.INVALID_APPWIDGET_ID

        when (intent.action) {
            TOGGLE_VISIBILITY_ACTION -> {
                isInventoryVisible = !isInventoryVisible
                if (appWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
                    val appWidgetManager = AppWidgetManager.getInstance(context)
                    updateAppWidget(context, appWidgetManager, appWidgetId)
                }
            }
            MANAGE_INVENTORY_ACTION -> {
                val loginIntent = Intent(context, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
                context.startActivity(loginIntent)
            }
        }
    }

    @SuppressLint("RemoteViewLayout")
    private fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {
        // Esta función puede contener solo actualizaciones para eventos específicos si lo deseas,
        // o bien puedes llamar directamente a onUpdate para refrescar
        onUpdate(context, appWidgetManager, intArrayOf(appWidgetId))
    }
}

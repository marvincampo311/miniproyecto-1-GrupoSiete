package com.example.miiproyecto1

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import android.util.Log
import java.text.NumberFormat
import java.util.Locale
import com.example.miiproyecto1.data.local.AppDatabase

class InventoryWidgetProvider : AppWidgetProvider() {

    companion object {
        private const val TOGGLE_VISIBILITY_ACTION = "com.example.miiproyecto1.TOGGLE_VISIBILITY"
        private const val MANAGE_INVENTORY_ACTION = "com.example.miiproyecto1.MANAGE_INVENTORY"
        private const val WIDGET_PREFS = "widget_prefs"
        private const val IS_INVENTORY_VISIBLE = "isInventoryVisible"
        private const val HIDDEN_VALUE = "$ ****"
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        appWidgetIds.forEach { appWidgetId ->
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }


    private fun updateAppWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int
    ) {
        Thread {
            try {
                // ✅ Obtener productos
                val db = AppDatabase.getDatabase(context.applicationContext)
                val productList = db.productDao().getAllProductsSync()  // ✅ Retorna List

                // ✅ Calcular suma
                var totalSum = 0.0
                for (product in productList) {
                    totalSum += product.price * product.cantidad
                }

                // ✅ Formatear moneda
                val locale = Locale("es", "CO")
                val currencyFormatter = NumberFormat.getCurrencyInstance(locale)
                val formattedTotal = currencyFormatter.format(totalSum)

                // ✅ Obtener visibilidad de SharedPreferences
                val sharedPref = context.getSharedPreferences(WIDGET_PREFS, Context.MODE_PRIVATE)
                val isInventoryVisible = sharedPref.getBoolean(IS_INVENTORY_VISIBLE, false)
                val valueToShow = if (isInventoryVisible) formattedTotal else HIDDEN_VALUE

                // ✅ Crear vistas
                val views = RemoteViews(context.packageName, R.layout.inventory_widget_layout)
                views.setTextViewText(R.id.inventory_value, valueToShow)
                views.setImageViewResource(
                    R.id.toggle_visibility_icon,
                    if (isInventoryVisible) R.drawable.ic_visibility_off else R.drawable.ic_visibility_on
                )

                // ✅ Click en ojo (toggle)
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

                // ✅ Click en botón gestionar
                val manageIntent = Intent(context, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
                val managePendingIntent = PendingIntent.getActivity(
                    context,
                    0,
                    manageIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
                views.setOnClickPendingIntent(R.id.manage_inventory_button, managePendingIntent)
                views.setOnClickPendingIntent(R.id.settings_icon, managePendingIntent)

                // ✅ Actualizar widget
                appWidgetManager.updateAppWidget(appWidgetId, views)

            } catch (e: Exception) {
                Log.e("InventoryWidget", "Error actualizando widget", e)
                e.printStackTrace()
            }
        }.start()
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)

        val appWidgetId = intent.extras?.getInt(
            AppWidgetManager.EXTRA_APPWIDGET_ID,
            AppWidgetManager.INVALID_APPWIDGET_ID
        ) ?: AppWidgetManager.INVALID_APPWIDGET_ID

        when (intent.action) {
            TOGGLE_VISIBILITY_ACTION -> {
                val sharedPref = context.getSharedPreferences(WIDGET_PREFS, Context.MODE_PRIVATE)
                val currentVisibility = sharedPref.getBoolean(IS_INVENTORY_VISIBLE, false)
                val newVisibility = !currentVisibility

                sharedPref.edit().putBoolean(IS_INVENTORY_VISIBLE, newVisibility).apply()

                Log.d("InventoryWidget", "Visibilidad toggled: $newVisibility")

                if (appWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
                    val appWidgetManager = AppWidgetManager.getInstance(context)
                    updateAppWidget(context, appWidgetManager, appWidgetId)
                }
            }
        }
    }

    override fun onEnabled(context: Context) {
        Log.d("InventoryWidget", "Widget habilitado")
        super.onEnabled(context)
    }

    override fun onDisabled(context: Context) {
        Log.d("InventoryWidget", "Widget deshabilitado")
        super.onDisabled(context)
    }

    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        Log.d("InventoryWidget", "Widget eliminado")
        super.onDeleted(context, appWidgetIds)
    }
}

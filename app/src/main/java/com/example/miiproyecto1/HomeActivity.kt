package com.example.miiproyecto1

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.miiproyecto1.databinding.ActivityHomeBinding

class HomeActivity : AppCompatActivity() {

    // 1. Criterio 3.0: Declaración de View Binding
    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Criterio 3.0: Inicialización de View Binding
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 2. Criterio 3.1: Configuración del Toolbar
        setupToolbar()

        // 3. Criterio 3.3: Configuración del FAB (Botón de Agregar)
        setupFab()

        // 4. Criterios 3.2 y 3.0 (parte de la implementación): Configuración del RecyclerView
        setupRecyclerView()
    }

    override fun onResume() {
        super.onResume()
        setupRecyclerView()
    }

    /**
     * Criterio 3.1: Configura la Toolbar con el título y el botón de perfil.
     */
    private fun setupToolbar() {
        // Establece el Toolbar
        setSupportActionBar(binding.toolbar)

        // Asigna el título (CRITERIO 3.1)
        supportActionBar?.title = "Inventario"

        // Configura el botón para regresar al Login (MainActivity)
        binding.imageProfile.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }
    }

    /**
     * Criterio 3.3: Configura el Floating Action Button (FAB) para agregar productos.
     */
    private fun setupFab() {
        binding.fabAdd.setOnClickListener {
            Toast.makeText(this, "Agregar Producto presionado. (Criterio 3.3)", Toast.LENGTH_SHORT).show()


            val intent = Intent(this, AddProductActivity::class.java)
            startActivity(intent)
        }
    }

    /**
     * Criterios 3.0 y 3.2: Configura el RecyclerView con la lista de productos.
     */
    private fun setupRecyclerView() {
        binding.loadingIndicator.visibility = View.VISIBLE
        binding.recyclerView.visibility = View.GONE

        Thread {
            val db = AppDatabase.getDatabase(applicationContext)
            val productList = db.productDao().getAllProducts()
            runOnUiThread {
                binding.loadingIndicator.visibility = View.GONE
                binding.recyclerView.visibility = View.VISIBLE

                val adapter = ProductAdapter(productList)
                adapter.onItemClick = { product ->
                    val intent = Intent(this, ProductDetailActivity::class.java)
                    intent.putExtra(ProductDetailActivity.EXTRA_PRODUCT_ID, product.id)
                    startActivity(intent)
                }
                binding.recyclerView.layoutManager = LinearLayoutManager(this)
                binding.recyclerView.adapter = adapter
            }
        }.start()
    }
}
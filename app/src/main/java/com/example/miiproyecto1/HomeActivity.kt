package com.example.miiproyecto1

import android.content.Intent
import android.os.Bundle
import android.util.Log
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

    /**
     * Criterio 3.1: Configura la Toolbar con el título y el botón de perfil.
     */
    private fun setupToolbar() {
        // Establece el Toolbar
        setSupportActionBar(binding.toolbar)

        // Asigna el título (CRITERIO 3.1)
        supportActionBar?.title = "Inventario"

        // Configura el botón de perfil (Criterio 3.1)
        binding.imageProfile.setOnClickListener {
            Toast.makeText(this, "Perfil presionado. (Criterio 3.1)", Toast.LENGTH_SHORT).show()
            // Aquí iría el código para abrir la pantalla de Perfil
        }
    }

    /**
     * Criterio 3.3: Configura el Floating Action Button (FAB) para agregar productos.
     */
    private fun setupFab() {
        binding.fabAdd.setOnClickListener {
            Toast.makeText(this, "Agregar Producto presionado. (Criterio 3.3)", Toast.LENGTH_SHORT).show()

            // Aquí puedes lanzar la actividad para agregar un producto (si la tuvieras)
            // Por ejemplo:
            // val intent = Intent(this, AddProductActivity::class.java)
            // startActivity(intent)
        }
    }

    /**
     * Criterios 3.0 y 3.2: Configura el RecyclerView con la lista de productos.
     */
    private fun setupRecyclerView() {
        // Simulación de datos de productos (CRITERIO 3.2)
        val productList = listOf(
            Product(id = 101, name = "Laptop Gamer 15'", price = 4_500_000.0),
            Product(id = 102, name = "Monitor 4K", price = 1_200_000.0),
            Product(id = 103, name = "Mouse Inalámbrico", price = 85_000.0),
            Product(id = 104, name = "Teclado Mecánico", price = 320_000.0),
            Product(id = 105, name = "Webcam HD", price = 150_000.0),
            Product(id = 106, name = "Disco Duro Externo 2TB", price = 480_000.0),
            Product(id = 107, name = "Silla Ergonómica", price = 850_000.0)
            // ¡Estos son los 7 ítems de la lista que te pedí en el CRITERIO 3.2!
        )

        // Inicializa el adaptador
        val adapter = ProductAdapter(productList)

        // Configura el listener de clic (parte del Criterio 3.2 y 8)
        adapter.onItemClick = { product ->
            Toast.makeText(this, "Click en: ${product.name}", Toast.LENGTH_SHORT).show()
            // Aquí iría el código para abrir la pantalla de Edición o Detalle
        }

        // Aplica el Layout Manager (CRITERIO 3.0 - lista vertical)
        binding.recyclerView.layoutManager = LinearLayoutManager(this)

        // Asigna el adaptador al RecyclerView
        binding.recyclerView.adapter = adapter
    }
}
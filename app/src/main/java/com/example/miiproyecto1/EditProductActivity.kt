package com.example.miiproyecto1

import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.miiproyecto1.databinding.ActivityEditProductBinding

class EditProductActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditProductBinding
    private var productId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProductBinding.inflate(layoutInflater)
        setContentView(binding.root)

        productId = intent.getIntExtra(ProductDetailActivity.EXTRA_PRODUCT_ID, -1)
        if (productId == -1) {
            Toast.makeText(this, "Producto no encontrado", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        setupToolbar()
        setupFilters()
        setupWatcher()
        setupSaveButton()
    }

    override fun onResume() {
        super.onResume()
        loadProduct()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    private fun setupFilters() {
        binding.editTextCodigo.filters = arrayOf(InputFilter.LengthFilter(4))
        binding.editTextNombre.filters = arrayOf(InputFilter.LengthFilter(40))
        binding.editTextPrecio.filters = arrayOf(InputFilter.LengthFilter(20))
        binding.editTextCantidad.filters = arrayOf(InputFilter.LengthFilter(4))
    }

    private fun setupWatcher() {
        val watcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val codigoOk = binding.editTextCodigo.text?.length == 4
                val nombreOk = !binding.editTextNombre.text.isNullOrBlank()
                val precioOk = !binding.editTextPrecio.text.isNullOrBlank()
                val cantidadOk = !binding.editTextCantidad.text.isNullOrBlank()

                val habilitar = codigoOk && nombreOk && precioOk && cantidadOk
                binding.btnGuardar.isEnabled = habilitar
                binding.btnGuardar.alpha = if (habilitar) 1f else 0.5f
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }

        binding.editTextCodigo.addTextChangedListener(watcher)
        binding.editTextNombre.addTextChangedListener(watcher)
        binding.editTextPrecio.addTextChangedListener(watcher)
        binding.editTextCantidad.addTextChangedListener(watcher)
    }

    private fun setupSaveButton() {
        binding.btnGuardar.setOnClickListener {
            val codigo = binding.editTextCodigo.text.toString()
            val nombre = binding.editTextNombre.text.toString()
            val precio = binding.editTextPrecio.text.toString().toDoubleOrNull() ?: 0.0
            val cantidad = binding.editTextCantidad.text.toString().toIntOrNull() ?: 0

            val updatedProduct = Product(
                id = productId,
                codigo = codigo,
                name = nombre,
                price = precio,
                cantidad = cantidad
            )

            Thread {
                AppDatabase.getDatabase(applicationContext).productDao().updateProduct(updatedProduct)
                runOnUiThread {
                    Toast.makeText(this, "Producto actualizado", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }.start()
        }
    }

    private fun loadProduct() {
        Thread {
            val product = AppDatabase.getDatabase(applicationContext).productDao().getProductById(productId)
            runOnUiThread {
                if (product == null) {
                    Toast.makeText(this, "El producto ya no existe", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    binding.editTextCodigo.setText(product.codigo)
                    binding.editTextNombre.setText(product.name)
                    binding.editTextPrecio.setText(product.price.toString())
                    binding.editTextCantidad.setText(product.cantidad.toString())
                    binding.btnGuardar.isEnabled = true
                    binding.btnGuardar.alpha = 1f
                }
            }
        }.start()
    }
}




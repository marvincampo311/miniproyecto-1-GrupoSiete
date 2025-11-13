package com.example.miiproyecto1


import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import androidx.appcompat.app.AppCompatActivity
import com.example.miiproyecto1.databinding.ActivityAddProductBinding
import android.widget.Toast



class AddProductActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddProductBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddProductBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Toolbar con navegación atrás
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }

        // Input filters para restricciones
        binding.editTextCodigo.filters = arrayOf(InputFilter.LengthFilter(4))
        binding.editTextNombre.filters = arrayOf(InputFilter.LengthFilter(40))
        binding.editTextPrecio.filters = arrayOf(InputFilter.LengthFilter(20))
        binding.editTextCantidad.filters = arrayOf(InputFilter.LengthFilter(4))

        // Validación de formularios y habilitar botón Guardar
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
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { }
        }

        binding.editTextCodigo.addTextChangedListener(watcher)
        binding.editTextNombre.addTextChangedListener(watcher)
        binding.editTextPrecio.addTextChangedListener(watcher)
        binding.editTextCantidad.addTextChangedListener(watcher)

        // Acción del botón Guardar (aquí pendiente implementar guardado en DB)
        binding.btnGuardar.setOnClickListener {
            val codigo = binding.editTextCodigo.text.toString()
            val nombre = binding.editTextNombre.text.toString()
            val precio = binding.editTextPrecio.text.toString().toDoubleOrNull() ?: 0.0
            val cantidad = binding.editTextCantidad.text.toString().toIntOrNull() ?: 0

            val product = Product(codigo = codigo, name = nombre, price = precio, cantidad = cantidad)

            Thread {
                val db = AppDatabase.getDatabase(applicationContext)
                db.productDao().insertProduct(product)

                runOnUiThread {
                    Toast.makeText(this, "¡Producto guardado!", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }.start()
        }
    }
}

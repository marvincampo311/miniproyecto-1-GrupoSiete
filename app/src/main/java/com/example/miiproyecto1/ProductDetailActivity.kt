package com.example.miiproyecto1

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.miiproyecto1.databinding.ActivityProductDetailBinding
import java.text.NumberFormat
import java.util.Locale

class ProductDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProductDetailBinding
    private var productId: Int = -1
    private var currentProduct: Product? = null
    private val currencyFormat = NumberFormat.getCurrencyInstance(Locale("es", "CO"))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        productId = intent.getIntExtra(EXTRA_PRODUCT_ID, -1)
        if (productId == -1) {
            Toast.makeText(this, "Producto no encontrado", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        setupToolbar()
        setupListeners()
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

    private fun setupListeners() {
        binding.btnDelete.setOnClickListener { showDeleteConfirmation() }
        binding.fabEdit.setOnClickListener {
            val intent = Intent(this, EditProductActivity::class.java)
            intent.putExtra(EXTRA_PRODUCT_ID, productId)
            startActivity(intent)
        }
    }

    private fun loadProduct() {
        binding.btnDelete.isEnabled = false
        Thread {
            val product = AppDatabase.getDatabase(applicationContext).productDao().getProductById(productId)
            runOnUiThread {
                if (product == null) {
                    Toast.makeText(this, "El producto ya no existe", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    currentProduct = product
                    bindProduct(product)
                    binding.btnDelete.isEnabled = true
                }
            }
        }.start()
    }

    private fun bindProduct(product: Product) {
        binding.productName.text = product.name
        binding.productPrice.text = currencyFormat.format(product.price)
        binding.productQuantity.text = product.cantidad.toString()
        val total = product.price * product.cantidad
        binding.productTotal.text = currencyFormat.format(total)
    }

    private fun showDeleteConfirmation() {
        AlertDialog.Builder(this)
            .setTitle("Confirmar eliminación")
            .setMessage("¿Estás seguro de que deseas eliminar este producto?")
            .setNegativeButton("No") { dialog, _ -> dialog.dismiss() }
            .setPositiveButton("Sí") { dialog, _ ->
                dialog.dismiss()
                deleteProduct()
            }
            .show()
    }

    private fun deleteProduct() {
        val product = currentProduct ?: return
        Thread {
            AppDatabase.getDatabase(applicationContext).productDao().deleteProduct(product)
            runOnUiThread {
                Toast.makeText(this, "Producto eliminado", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, HomeActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                startActivity(intent)
                finish()
            }
        }.start()
    }

    companion object {
        const val EXTRA_PRODUCT_ID = "extra_product_id"
    }
}



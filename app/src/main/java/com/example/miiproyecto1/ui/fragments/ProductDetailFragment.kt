package com.example.miiproyecto1.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.miiproyecto1.databinding.FragmentProductDetailBinding
import com.example.miiproyecto1.data.local.AppDatabase
import com.example.miiproyecto1.data.local.Product
import com.example.miiproyecto1.HomeActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.NumberFormat
import java.util.Locale
import androidx.navigation.fragment.findNavController
import com.example.miiproyecto1.R

/**
 * PRODUCT DETAIL FRAGMENT - VER DETALLES DEL PRODUCTO
 *
 * ✅ LÓGICA IDÉNTICA pero con Coroutines y argumentos de Fragment
 */
@AndroidEntryPoint
class ProductDetailFragment : Fragment() {

    private lateinit var binding: FragmentProductDetailBinding
    private var productId: Int = -1
    private var currentProduct: Product? = null
    private val currencyFormat = NumberFormat.getCurrencyInstance(Locale("es", "CO"))

    companion object {
        const val EXTRA_PRODUCT_ID = "extra_product_id"

        fun newInstance(productId: Int): ProductDetailFragment {
            return ProductDetailFragment().apply {
                arguments = Bundle().apply {
                    putInt(EXTRA_PRODUCT_ID, productId)
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProductDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Obtener el ID desde arguments (Bundle)
        productId = arguments?.getInt(EXTRA_PRODUCT_ID, -1) ?: -1

        if (productId == -1) {
            Toast.makeText(requireContext(), "Producto no encontrado", Toast.LENGTH_SHORT).show()
            requireActivity().onBackPressed()
            return
        }

        setupToolbar()
        setupListeners()
        loadProduct()
    }

    /**
     * Configurar Toolbar
     */
    private fun setupToolbar() {
        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.toolbar)
        (requireActivity() as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }
    }

    /**
     * Configurar listeners de botones
     */
    /**
     * Configurar listeners de botones
     * ✅ Ahora usa Navigation en lugar de Intent
     */
    /**
     * Configurar listeners de botones
     */
    private fun setupListeners() {
        binding.btnDelete.setOnClickListener { showDeleteConfirmation() }
        binding.fabEdit.setOnClickListener {
            if (productId != -1) {
                // Navega a EditProductFragment con Bundle
                val bundle = Bundle().apply {
                    putInt("extra_product_id", productId)
                }
                findNavController().navigate(R.id.editProductFragment, bundle)
            }
        }
    }



    /**
     * Cargar el producto desde la BD con Coroutines
     */
    private fun loadProduct() {
        binding.btnDelete.isEnabled = false

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val product = withContext(Dispatchers.IO) {
                    AppDatabase.getDatabase(requireContext()).productDao().getProductById(productId)
                }

                if (product == null) {
                    Toast.makeText(requireContext(), "El producto ya no existe", Toast.LENGTH_SHORT).show()
                    requireActivity().onBackPressed()
                } else {
                    currentProduct = product
                    bindProduct(product)
                    binding.btnDelete.isEnabled = true
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * Mostrar los datos del producto en la UI
     */
    private fun bindProduct(product: Product) {
        binding.toolbar.title = product.name
        binding.productName.text = product.name
        binding.productPrice.text = currencyFormat.format(product.price)
        binding.productQuantity.text = product.cantidad.toString()
        val total = product.price * product.cantidad
        binding.productTotal.text = currencyFormat.format(total)
    }

    /**
     * Mostrar diálogo de confirmación antes de eliminar
     */
    private fun showDeleteConfirmation() {
        AlertDialog.Builder(requireContext())
            .setTitle("Confirmar eliminación")
            .setMessage("¿Estás seguro de que deseas eliminar este producto?")
            .setNegativeButton("No") { dialog, _ -> dialog.dismiss() }
            .setPositiveButton("Sí") { dialog, _ ->
                dialog.dismiss()
                deleteProduct()
            }
            .show()
    }

    /**
     * Eliminar el producto de la BD con Coroutines
     */
    private fun deleteProduct() {
        val product = currentProduct ?: return

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    AppDatabase.getDatabase(requireContext()).productDao().deleteProduct(product)
                }

                Toast.makeText(requireContext(), "Producto eliminado", Toast.LENGTH_SHORT).show()
                val intent = Intent(requireContext(), HomeActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                startActivity(intent)
                requireActivity().finish()
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error al eliminar: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

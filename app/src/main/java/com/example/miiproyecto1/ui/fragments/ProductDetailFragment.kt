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
import com.example.miiproyecto1.ui.viewmodel.HomeViewModel

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

    // ✅ Usaremos HomeViewModel para eliminar
    private lateinit var homeViewModel: HomeViewModel

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

        productId = arguments?.getInt(EXTRA_PRODUCT_ID, -1) ?: -1
        if (productId == -1) {
            Toast.makeText(requireContext(), "Producto no encontrado", Toast.LENGTH_SHORT).show()
            findNavController().popBackStack()
            return
        }

        // ✅ Inicializar HomeViewModel reutilizando la misma BD
        val db = AppDatabase.getDatabase(requireContext())
        homeViewModel = HomeViewModel(db)

        setupToolbar()
        setupListeners()
        loadProduct()
    }

    private fun setupToolbar() {
        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.toolbar)
        (requireActivity() as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun setupListeners() {
        binding.btnDelete.setOnClickListener { showDeleteConfirmation() }
        binding.fabEdit.setOnClickListener {
            if (productId != -1) {
                val bundle = Bundle().apply {
                    putInt(EXTRA_PRODUCT_ID, productId)
                }
                findNavController().navigate(R.id.editProductFragment, bundle)
            }
        }
    }

    // ✅ Carga sigue igual, usando Room + corrutinas desde el Fragment
    private fun loadProduct() {
        binding.btnDelete.isEnabled = false

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val product = withContext(Dispatchers.IO) {
                    AppDatabase.getDatabase(requireContext())
                        .productDao()
                        .getProductById(productId)
                }

                if (product == null) {
                    Toast.makeText(requireContext(), "El producto ya no existe", Toast.LENGTH_SHORT).show()
                    findNavController().popBackStack()
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

    private fun bindProduct(product: Product) {
        binding.toolbar.title = product.name
        binding.productName.text = product.name
        binding.productPrice.text = currencyFormat.format(product.price)
        binding.productQuantity.text = product.cantidad.toString()
        val total = product.price * product.cantidad
        binding.productTotal.text = currencyFormat.format(total)
    }

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

    // ✅ Ahora delega la eliminación al ViewModel
    private fun deleteProduct() {
        val product = currentProduct ?: return

        // Lógica en el ViewModel (usa corrutinas y Room)
        homeViewModel.deleteProduct(product)

        Toast.makeText(requireContext(), "Producto eliminado", Toast.LENGTH_SHORT).show()
        findNavController().popBackStack()   // vuelve al listado sin recrear HomeActivity
    }
}

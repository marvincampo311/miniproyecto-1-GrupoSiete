package com.example.miiproyecto1.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.miiproyecto1.R
import com.example.miiproyecto1.data.local.Product
import com.example.miiproyecto1.databinding.FragmentProductDetailBinding
import com.example.miiproyecto1.ui.viewmodel.ProductDetailViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.text.NumberFormat
import java.util.Locale

@AndroidEntryPoint
class ProductDetailFragment : Fragment() {

    private lateinit var binding: FragmentProductDetailBinding

    // Ahora usamos el remoteId (String) que viene de Firestore
    private var productRemoteId: String? = null

    private val currencyFormat = NumberFormat.getCurrencyInstance(Locale("es", "CO"))

    private val viewModel: ProductDetailViewModel by viewModels()

    companion object {
        const val EXTRA_PRODUCT_ID = "extra_product_id"

        fun newInstance(remoteId: String): ProductDetailFragment {
            return ProductDetailFragment().apply {
                arguments = Bundle().apply {
                    putString(EXTRA_PRODUCT_ID, remoteId)
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

        productRemoteId = arguments?.getString(EXTRA_PRODUCT_ID)
        if (productRemoteId.isNullOrEmpty()) {
            Toast.makeText(requireContext(), "Producto no encontrado", Toast.LENGTH_SHORT).show()
            findNavController().popBackStack()
            return
        }

        setupToolbar()
        setupListeners()
        observeViewModel()

        // Cargar producto desde Firestore usando remoteId
        viewModel.loadProduct(productRemoteId!!)
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
            productRemoteId?.let { id ->
                val bundle = Bundle().apply {
                    putString(EXTRA_PRODUCT_ID, id)
                }
                findNavController().navigate(R.id.editProductFragment, bundle)
            }
        }
    }

    private fun observeViewModel() {
        viewModel.product.observe(viewLifecycleOwner) { product ->
            if (product == null) {
                Toast.makeText(requireContext(), "El producto ya no existe", Toast.LENGTH_SHORT).show()
                findNavController().popBackStack()
            } else {
                bindProduct(product)
            }
        }

        viewModel.deleteSuccess.observe(viewLifecycleOwner) { success ->
            if (success) {
                Toast.makeText(requireContext(), "Producto eliminado", Toast.LENGTH_SHORT).show()
                findNavController().popBackStack()
            } else {
                Toast.makeText(requireContext(), "Error al eliminar", Toast.LENGTH_SHORT).show()
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

    private fun deleteProduct() {
        productRemoteId?.let { id ->
            viewModel.deleteProduct(id)
        }
    }
}

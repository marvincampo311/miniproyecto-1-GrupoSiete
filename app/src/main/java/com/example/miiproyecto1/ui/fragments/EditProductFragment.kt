package com.example.miiproyecto1.ui.fragments

import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.miiproyecto1.databinding.FragmentEditProductBinding
import com.example.miiproyecto1.data.local.AppDatabase
import com.example.miiproyecto1.data.local.Product
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EditProductFragment : Fragment() {

    private lateinit var binding: FragmentEditProductBinding
    private var productId: Int = -1

    companion object {
        const val EXTRA_PRODUCT_ID = "extra_product_id"

        fun newInstance(productId: Int): EditProductFragment {
            return EditProductFragment().apply {
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
        binding = FragmentEditProductBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        productId = arguments?.getInt(EXTRA_PRODUCT_ID, -1) ?: -1

        if (productId == -1) {
            Toast.makeText(requireContext(), "Producto no encontrado", Toast.LENGTH_SHORT).show()
            requireActivity().onBackPressed()
            return
        }

        setupToolbar()
        setupFilters()
        setupWatcher()
        setupSaveButton()
        loadProduct()
    }

    private fun setupToolbar() {
        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.toolbar)
        (requireActivity() as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }
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

            viewLifecycleOwner.lifecycleScope.launch {
                try {
                    withContext(Dispatchers.IO) {
                        AppDatabase.getDatabase(requireContext()).productDao().updateProduct(updatedProduct)
                    }

                    Toast.makeText(requireContext(), "Producto actualizado", Toast.LENGTH_SHORT).show()
                    requireActivity().onBackPressed()
                } catch (e: Exception) {
                    Toast.makeText(requireContext(), "Error al guardar: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun loadProduct() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val product = withContext(Dispatchers.IO) {
                    AppDatabase.getDatabase(requireContext()).productDao().getProductById(productId)
                }

                if (product == null) {
                    Toast.makeText(requireContext(), "El producto ya no existe", Toast.LENGTH_SHORT).show()
                    requireActivity().onBackPressed()
                } else {
                    binding.editTextCodigo.setText(product.codigo)
                    binding.editTextNombre.setText(product.name)
                    binding.editTextPrecio.setText(product.price.toString())
                    binding.editTextCantidad.setText(product.cantidad.toString())
                    binding.btnGuardar.isEnabled = true
                    binding.btnGuardar.alpha = 1f
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
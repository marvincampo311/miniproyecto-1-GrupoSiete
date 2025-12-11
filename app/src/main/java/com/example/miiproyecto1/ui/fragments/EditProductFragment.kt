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
import androidx.fragment.app.viewModels
import com.example.miiproyecto1.data.local.Product
import com.example.miiproyecto1.databinding.FragmentEditProductBinding
import com.example.miiproyecto1.ui.viewmodel.EditProductViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditProductFragment : Fragment() {

    private lateinit var binding: FragmentEditProductBinding
    private val viewModel: EditProductViewModel by viewModels()

    private var productRemoteId: String? = null

    companion object {
        const val EXTRA_PRODUCT_ID = "extra_product_id"

        fun newInstance(remoteId: String): EditProductFragment {
            return EditProductFragment().apply {
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
        binding = FragmentEditProductBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        productRemoteId = arguments?.getString(EXTRA_PRODUCT_ID)

        if (productRemoteId.isNullOrEmpty()) {
            Toast.makeText(requireContext(), "Producto no encontrado", Toast.LENGTH_SHORT).show()
            requireActivity().onBackPressed()
            return
        }

        setupToolbar()
        setupFilters()
        setupWatcher()
        setupSaveButton()
        observeViewModel()

        viewModel.loadProduct(productRemoteId!!)
    }

    private fun setupToolbar() {
        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.toolbar)
        (requireActivity() as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }
    }

    private fun setupFilters() {
        binding.tvCodigoDisplay.filters = arrayOf(InputFilter.LengthFilter(10))
        binding.editTextNombre.filters = arrayOf(InputFilter.LengthFilter(40))
        binding.editTextPrecio.filters = arrayOf(InputFilter.LengthFilter(20))
        binding.editTextCantidad.filters = arrayOf(InputFilter.LengthFilter(4))
    }

    private fun setupWatcher() {
        val watcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
               // val codigoOk = binding.tvCodigoDisplay.text?.length == 8
                val nombreOk = !binding.editTextNombre.text.isNullOrBlank()
                val precioOk = !binding.editTextPrecio.text.isNullOrBlank()
                val cantidadOk = !binding.editTextCantidad.text.isNullOrBlank()

                val habilitar = nombreOk && precioOk && cantidadOk
                binding.btnGuardar.isEnabled = habilitar
                binding.btnGuardar.alpha = if (habilitar) 1f else 0.5f
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }

        binding.tvCodigoDisplay.addTextChangedListener(watcher)
        binding.editTextNombre.addTextChangedListener(watcher)
        binding.editTextPrecio.addTextChangedListener(watcher)
        binding.editTextCantidad.addTextChangedListener(watcher)
    }

    private fun setupSaveButton() {
        binding.btnGuardar.setOnClickListener {
            val codigo = (binding.tvCodigoDisplay.tag as? String) ?: ""
            val nombre = binding.editTextNombre.text.toString()
            val precioStr = binding.editTextPrecio.text.toString()
            val cantidadStr = binding.editTextCantidad.text.toString()

            if (!viewModel.validateFields(codigo, nombre, precioStr, cantidadStr)) {
                Toast.makeText(requireContext(), "Revisa los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val updatedProduct = Product(
                codigo = codigo,
                name = nombre,
                price = precioStr.toDouble(),
                cantidad = cantidadStr.toInt(),
                remoteId = productRemoteId
            )

            productRemoteId?.let { id ->
                viewModel.updateProduct(id, updatedProduct)
            }
        }
    }

    private fun observeViewModel() {
        viewModel.product.observe(viewLifecycleOwner) { product ->
            if (product == null) {
                Toast.makeText(requireContext(), "El producto ya no existe", Toast.LENGTH_SHORT).show()
                requireActivity().onBackPressed()
            } else {
                binding.tvCodigoDisplay.tag = product.codigo

                binding.tvCodigoDisplay.setText("Id: ${product.codigo}")
                binding.editTextNombre.setText(product.name)
                binding.editTextPrecio.setText(product.price.toString())
                binding.editTextCantidad.setText(product.cantidad.toString())
                binding.btnGuardar.isEnabled = true
                binding.btnGuardar.alpha = 1f
            }
        }

        viewModel.updateSuccess.observe(viewLifecycleOwner) { success ->
            if (success) {
                Toast.makeText(requireContext(), "Producto actualizado", Toast.LENGTH_SHORT).show()
                requireActivity().onBackPressed()
            } else {
                Toast.makeText(requireContext(), "Error al guardar", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

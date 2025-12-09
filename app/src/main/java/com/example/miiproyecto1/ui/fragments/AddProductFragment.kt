package com.example.miiproyecto1.ui.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.miiproyecto1.R
import com.example.miiproyecto1.data.local.AppDatabase
import com.example.miiproyecto1.data.local.Product
import com.example.miiproyecto1.data.repository.ProductRepository
import com.example.miiproyecto1.databinding.FragmentAddProductBinding
import com.example.miiproyecto1.ui.viewmodel.AddProductViewModel

class AddProductFragment : Fragment() {

    private lateinit var binding: FragmentAddProductBinding
    private lateinit var viewModel: AddProductViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddProductBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val database = AppDatabase.getDatabase(requireContext())
        val repository = ProductRepository(database.productDao())
        viewModel = AddProductViewModel(repository)

        setupValidationWatcher()
        setupSaveButton()
        setupCancelButton()
        observeViewModel()
    }

    private fun setupValidationWatcher() {
        val watcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                validateForm()
            }
        }

        binding.editTextCodigo.addTextChangedListener(watcher)
        binding.editTextNombre.addTextChangedListener(watcher)
        binding.editTextPrecio.addTextChangedListener(watcher)
        binding.editTextCantidad.addTextChangedListener(watcher)
    }

    private fun validateForm() {
        val codigo = binding.editTextCodigo.text.toString().trim()
        val nombre = binding.editTextNombre.text.toString().trim()
        val precio = binding.editTextPrecio.text.toString().trim()
        val cantidad = binding.editTextCantidad.text.toString().trim()

        binding.btnGuardar.isEnabled = codigo.isNotEmpty() && nombre.isNotEmpty() &&
                precio.isNotEmpty() && cantidad.isNotEmpty()
    }

    private fun setupSaveButton() {
        binding.btnGuardar.setOnClickListener {
            val codigo = binding.editTextCodigo.text.toString().trim()
            val nombre = binding.editTextNombre.text.toString().trim()
            val precioStr = binding.editTextPrecio.text.toString().trim()
            val cantidadStr = binding.editTextCantidad.text.toString().trim()

            // Validar
            if (!viewModel.validateProduct(codigo, nombre, precioStr, cantidadStr)) {
                Toast.makeText(requireContext(), "Revisa los campos del formulario", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val precio = precioStr.toDouble()
            val cantidad = cantidadStr.toInt()

            val product = Product(
                codigo = codigo,
                name = nombre,
                price = precio,
                cantidad = cantidad
            )

            viewModel.saveProduct(product)
        }
    }

    private fun setupCancelButton() {
        binding.btnCancelar.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun observeViewModel() {
        viewModel.saveSuccess.observe(viewLifecycleOwner) { success ->
            if (success) {
                Toast.makeText(requireContext(), "Producto guardado", Toast.LENGTH_SHORT).show()
                findNavController().popBackStack()
            }
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            Toast.makeText(requireContext(), "Error: $error", Toast.LENGTH_SHORT).show()
        }
    }
}
package com.example.miiproyecto1.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.miiproyecto1.MainActivity
import com.example.miiproyecto1.ProductAdapter
import com.example.miiproyecto1.R
import com.example.miiproyecto1.data.local.AppDatabase
import com.example.miiproyecto1.data.local.Product
import com.example.miiproyecto1.databinding.FragmentHomeBinding
import com.example.miiproyecto1.ui.viewmodel.AuthViewModel
import com.example.miiproyecto1.ui.viewmodel.HomeViewModel
//import com.example.miiproyecto1.ui.viewmodel.HomeViewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import dagger.hilt.android.AndroidEntryPoint
import androidx.fragment.app.viewModels


@AndroidEntryPoint
class HomeFragment : Fragment() {

    // Compartido con la Activity (MainActivity / HomeActivity)
    private val authViewModel: AuthViewModel by viewModels()


    private lateinit var binding: FragmentHomeBinding
    private val viewModel: HomeViewModel by viewModels()
    private lateinit var adapter: ProductAdapter



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        val database = AppDatabase.getDatabase(requireContext())
//        val factory = HomeViewModelFactory(database)
//        viewModel = ViewModelProvider(this, factory).get(HomeViewModel::class.java)

        setupToolbar()
        setupFab()
        setupRecyclerView()
        observeViewModel()

        viewModel.observeProducts()
    }

    private fun setupToolbar() {
        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.toolbar)
        (requireActivity() as AppCompatActivity).supportActionBar?.title = "Inventario"

        binding.imageProfile.setOnClickListener {
            // Logout Firebase (asíncrono)
            authViewModel.logout()

            // Limpiar completamente el stack y ir a Login
            val intent = Intent(requireContext(), MainActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or
                        Intent.FLAG_ACTIVITY_CLEAR_TASK)
            }
            startActivity(intent)
        }
    }

    private fun setupFab() {
        binding.fabAdd.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_addProductFragment)
        }
    }

    private fun setupRecyclerView() {
        adapter = ProductAdapter(mutableListOf())

        adapter.onItemClick = { product ->
            val bundle = Bundle().apply {
                putString("extra_product_id", product.remoteId)
            }
            findNavController().navigate(
                R.id.action_homeFragment_to_productDetailFragment,
                bundle
            )
        }

        adapter.onDeleteClick = { product ->
            confirmDelete(product)
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter
    }

    private fun observeViewModel() {
        viewModel.loading.observe(viewLifecycleOwner) { isLoading ->
            binding.loadingIndicator.visibility =
                if (isLoading) View.VISIBLE else View.GONE
            binding.recyclerView.visibility =
                if (isLoading) View.GONE else View.VISIBLE
        }

        viewModel.products.observe(viewLifecycleOwner) { products ->
            adapter.apply {
                productList.clear()
                productList.addAll(products)
                notifyDataSetChanged()
            }
        }
    }

    private fun confirmDelete(product: Product) {
        AlertDialog.Builder(requireContext())
            .setTitle("Eliminar producto")
            .setMessage("¿Estás seguro de que quieres eliminar '${product.name}'?")
            .setPositiveButton("Eliminar") { _, _ ->
                deleteProduct(product)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun deleteProduct(product: Product) {
        product.remoteId?.let{ remoteId ->
            viewModel.deleteProduct(remoteId)
        }
        Toast.makeText(requireContext(), "Producto eliminado", Toast.LENGTH_SHORT).show()
    }
}

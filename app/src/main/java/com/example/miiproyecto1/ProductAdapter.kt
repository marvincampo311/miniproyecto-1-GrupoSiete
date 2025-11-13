package com.example.miiproyecto1

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.miiproyecto1.databinding.InventoryListItemBinding
import java.text.NumberFormat
import java.util.Locale

/**
 * Adaptador para mostrar la lista de productos en el RecyclerView de HomeActivity.
 * Usa InventoryListItemBinding generado a partir de inventory_list_item.xml.
 */
class ProductAdapter(private val productList: List<Product>) :
    RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    // Listener para manejar el clic en los ítems (CRITERIO 8)
    var onItemClick: ((Product) -> Unit)? = null

    inner class ProductViewHolder(private val binding: InventoryListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            // Configura el clic en todo el CardView (root)
            binding.root.setOnClickListener {
                // Invoca el listener de clic pasando el objeto Product de esta posición.
                onItemClick?.invoke(productList[adapterPosition])
            }
        }

        fun bind(product: Product) {
            // CRITERIO 5: Muestra los datos del producto
            // ¡ATENCIÓN! Usamos los IDs que definiste en tu XML: product_name, product_id, product_price

            binding.productName.text = product.name
            binding.productId.text = "ID: ${product.id}"

            // Formato de moneda para el precio (usando Locale para Colombia)
            val format = NumberFormat.getCurrencyInstance(Locale("es", "CO"))
            // Esto asegurará que se muestre el signo de moneda y formato correcto

            binding.productPrice.text = format.format(product.price)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        // Infla el layout del item usando View Binding
        val binding = InventoryListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(productList[position])
    }

    override fun getItemCount(): Int = productList.size
}


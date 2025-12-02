package com.example.miiproyecto1

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.miiproyecto1.databinding.InventoryListItemBinding
import com.example.miiproyecto1.data.local.Product
import java.text.NumberFormat
import java.util.Locale

class ProductAdapter(val productList: MutableList<Product>) :
    RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    var onItemClick: ((Product) -> Unit)? = null
    var onDeleteClick: ((Product) -> Unit)? = null

    inner class ProductViewHolder(private val binding: InventoryListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    onItemClick?.invoke(productList[adapterPosition])
                }
            }
        }

        fun bind(product: Product) {
            binding.productName.text = product.name
            binding.productId.text = "ID: ${product.id}"

            val format = NumberFormat.getCurrencyInstance(Locale("es", "CO"))
            binding.productPrice.text = format.format(product.price)

            // ✅ Si tu layout NO tiene btnDelete, comentar esto
            // binding.btnDelete?.setOnClickListener {
            //     onDeleteClick?.invoke(product)
            // }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = InventoryListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(productList[position])
    }

    override fun getItemCount(): Int = productList.size

    fun removeProduct(product: Product) {
        val position = productList.indexOfFirst { it.id == product.id }
        if (position != -1) {
            productList.removeAt(position)
            notifyItemRemoved(position)
        }
    }
}

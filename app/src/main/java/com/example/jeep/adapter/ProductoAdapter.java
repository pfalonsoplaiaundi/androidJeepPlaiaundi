package com.example.jeep.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.jeep.DetalleProductoActivity;
import com.example.jeep.R;
import com.example.jeep.cart.ShoppingCart;
import com.example.jeep.model.Producto;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Adaptador para el RecyclerView que muestra la lista de productos en el catálogo.
 */
public class ProductoAdapter extends RecyclerView.Adapter<ProductoAdapter.ProductoViewHolder> {

    private final Context context;
    private final List<Producto> allProducts;
    private List<Producto> displayedProducts;

    public ProductoAdapter(Context context, List<Producto> productos) {
        this.context = context;
        this.allProducts = new ArrayList<>(productos);
        this.displayedProducts = new ArrayList<>(productos);
    }

    @NonNull
    @Override
    public ProductoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_producto, parent, false);
        return new ProductoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductoViewHolder holder, int position) {
        Producto producto = displayedProducts.get(position);

        int totalStock = producto.getStock();
        int quantityInCart = ShoppingCart.getInstance().getQuantityOfProduct(producto.getId());
        boolean isAvailable = (totalStock - quantityInCart) > 0;
        setupStock(holder, isAvailable);

        DecimalFormat currencyFormat = new DecimalFormat("#,##0.00'€'");

        if (producto.getImageUrls() != null && !producto.getImageUrls().isEmpty()) {
            Glide.with(context).load(producto.getImageUrls().get(0)).centerCrop().into(holder.productImage);
        }

        setupDiscountSticker(holder, producto);
        setupPrices(holder, producto, currencyFormat);

        holder.addToCartButton.setOnClickListener(v -> {
            if ((producto.getStock() - ShoppingCart.getInstance().getQuantityOfProduct(producto.getId())) > 0) {
                ShoppingCart.getInstance().addProduct(producto);
                Toast.makeText(context, producto.getAlias() + " añadido al carrito", Toast.LENGTH_SHORT).show();
                notifyItemChanged(holder.getAdapterPosition());
            } else {
                Toast.makeText(context, "No hay más stock disponible", Toast.LENGTH_SHORT).show();
            }
        });
        
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, DetalleProductoActivity.class);
            intent.putExtra("PRODUCTO_SELECCIONADO", producto);
            context.startActivity(intent);
        });
    }

    /**
     * Filtra la lista de productos mostrados.
     */
    public void filter(String category, String query) {
        List<Producto> results = new ArrayList<>();

        // 1. Filtrar por categoría
        if (category == null || "Todos".equalsIgnoreCase(category)) {
            results.addAll(allProducts);
        } else {
            for (Producto product : allProducts) {
                if (product.getCategoria() != null && category.equalsIgnoreCase(product.getCategoria())) {
                    results.add(product);
                }
            }
        }

        // 2. Filtrar por texto de búsqueda sobre los resultados de la categoría
        if (query != null && !query.trim().isEmpty()) {
            String lowerCaseQuery = query.toLowerCase();
            List<Producto> searchResults = new ArrayList<>();
            for (Producto product : results) {
                boolean aliasMatches = product.getAlias() != null && product.getAlias().toLowerCase().contains(lowerCaseQuery);
                boolean descMatches = product.getDescripcion() != null && product.getDescripcion().toLowerCase().contains(lowerCaseQuery);
                boolean pvpMatches = String.valueOf(product.getPvp()).contains(lowerCaseQuery);
                if (aliasMatches || descMatches || pvpMatches) {
                    searchResults.add(product);
                }
            }
            results = searchResults;
        }

        displayedProducts = results;
        notifyDataSetChanged();
    }

    private void setupPrices(ProductoViewHolder holder, Producto producto, DecimalFormat format) {
        // ...
    }

    private void setupStock(ProductoViewHolder holder, boolean isAvailable) {
        // ...
    }

    private void setupDiscountSticker(ProductoViewHolder holder, Producto producto) {
        // ...
    }

    @Override
    public int getItemCount() {
        return displayedProducts.size();
    }

    static class ProductoViewHolder extends RecyclerView.ViewHolder {
        // ...
    }
}

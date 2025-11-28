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

/**
 * Adaptador para el RecyclerView que muestra la lista de productos en el catálogo.
 */
public class ProductoAdapter extends RecyclerView.Adapter<ProductoAdapter.ProductoViewHolder> {

    // -----------------------------------------------------------------------------------
    // Variables

    /** Contexto */
    private final Context context;

    /** Productos */
    private final List<Producto> allProducts;

    /** Productos mostrados */
    private List<Producto> displayedProducts;

    // Fin variables
    // -----------------------------------------------------------------------------------

    // -----------------------------------------------------------------------------------
    // Constructores

    /**
     * Constructor del adaptador.
     * @param context Contexto de la aplicación.
     * @param productos Lista de productos a mostrar.
     */
    public ProductoAdapter(Context context, List<Producto> productos) {
        this.context = context;
        this.allProducts = new ArrayList<>(productos);
        this.displayedProducts = new ArrayList<>(productos);
    }

    /**
     * Constructor del adaptador.
     *
     * @param parent   The ViewGroup into which the new View will be added after it is bound to
     *                 an adapter position.
     * @param viewType The view type of the new View.
     * @return Nuevo ViewHolder
     */
    @NonNull
    @Override
    public ProductoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_producto, parent, false);
        return new ProductoViewHolder(view);
    }

    /**
     * Vincula los datos de un producto a una vista de item específica.
     * @param holder ViewHolder de la vista.
     * @param position Posición del item en la lista.
     */
    @Override
    public void onBindViewHolder(@NonNull ProductoViewHolder holder, int position) {
        Producto producto = displayedProducts.get(position);

        int totalStock = producto.getStock();
        int quantityInCart = ShoppingCart.getInstance().getQuantityOfProduct(producto.getId());
        boolean isAvailable = (totalStock - quantityInCart) > 0;
        
        DecimalFormat currencyFormat = new DecimalFormat("#,##0.00'€'");

        if (producto.getImageUrls() != null && !producto.getImageUrls().isEmpty()) {
            Glide.with(context).load(producto.getImageUrls().get(0)).centerCrop().into(holder.productImage);
        }
        
        holder.productName.setText(producto.getAlias());

        setupStock(holder, isAvailable);
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

    private void setupStock(ProductoViewHolder holder, boolean isAvailable) {
        if (isAvailable) {
            holder.stockStatus.setVisibility(View.GONE);
            holder.addToCartButton.setVisibility(View.VISIBLE);
        } else {
            holder.stockStatus.setText("Agotado");
            holder.stockStatus.setVisibility(View.VISIBLE);
            holder.addToCartButton.setVisibility(View.GONE);
        }
    }
    
    private void setupDiscountSticker(ProductoViewHolder holder, Producto producto) {
        if (producto.getDctoPorcentual() != null && producto.getDctoPorcentual() > 0) {
            holder.discountSticker.setText(producto.getDctoPorcentual() + "%");
            holder.discountSticker.setVisibility(View.VISIBLE);
        } else if (producto.getDctoAbsoluto() != null && producto.getDctoAbsoluto() > 0) {
            holder.discountSticker.setText("-" + new DecimalFormat("#,##0.00'€'").format(producto.getDctoAbsoluto()));
            holder.discountSticker.setVisibility(View.VISIBLE);
        } else {
            holder.discountSticker.setVisibility(View.GONE);
        }
    }
    
    private void setupPrices(ProductoViewHolder holder, Producto producto, DecimalFormat currencyFormat) {
        if (producto.getPrecioAnterior() != null && producto.getPrecioAnterior() > 0) {
            holder.originalPrice.setText(currencyFormat.format(producto.getPrecioAnterior()));
            holder.originalPrice.setPaintFlags(holder.originalPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.originalPrice.setVisibility(View.VISIBLE);
            holder.finalPrice.setText(currencyFormat.format(producto.getPvp()));
        } else {
            holder.originalPrice.setVisibility(View.GONE);
            holder.finalPrice.setText(currencyFormat.format(producto.getPvp()));
        }
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

    @Override
    public int getItemCount() {
        return displayedProducts.size();
    }
    
    
    static class ProductoViewHolder extends RecyclerView.ViewHolder {
        ImageView productImage;
        TextView productName;
        TextView stockStatus;
        TextView discountSticker;
        TextView originalPrice;
        TextView finalPrice;
        ImageButton addToCartButton;

        public ProductoViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.product_image);
            productName = itemView.findViewById(R.id.product_name);
            stockStatus = itemView.findViewById(R.id.out_of_stock_text);
            discountSticker = itemView.findViewById(R.id.discount_sticker);
            originalPrice = itemView.findViewById(R.id.product_old_pvp);
            finalPrice = itemView.findViewById(R.id.product_pvp);
            addToCartButton = itemView.findViewById(R.id.add_to_cart_button);
        }
    }
}

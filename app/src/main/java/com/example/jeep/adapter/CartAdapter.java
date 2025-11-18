package com.example.jeep.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.jeep.DetalleProductoActivity;
import com.example.jeep.R;
import com.example.jeep.cart.CartItem;
import com.example.jeep.cart.ShoppingCart;
import com.example.jeep.model.Producto;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Adaptador para el RecyclerView que muestra la lista de items en el carrito de compra.
 */
public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private final Context context;
    private List<CartItem> cartItems = new ArrayList<>();

    public CartAdapter(Context context) {
        this.context = context;
    }

    /**
     * Actualiza la lista de items que muestra el adaptador.
     * @param cartItems La nueva lista de items del carrito.
     */
    public void setCartItems(List<CartItem> cartItems) {
        this.cartItems = cartItems;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_cart, parent, false);
        return new CartViewHolder(view);
    }

    /**
     * Vincula los datos de un CartItem a una vista de item específica.
     */
    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItem cartItem = cartItems.get(position);
        Producto producto = cartItem.getProducto();
        DecimalFormat currencyFormat = new DecimalFormat("#,##0.00'€'");

        // Poblar vistas con datos
        holder.productName.setText(producto.getAlias());
        holder.productPrice.setText(currencyFormat.format(producto.getFinalPrice()));
        holder.itemQuantity.setText(String.valueOf(cartItem.getQuantity()));
        holder.subtotal.setText(String.format("Subtotal: %s", currencyFormat.format(cartItem.getTotalPrice())));

        if (producto.getImageUrls() != null && !producto.getImageUrls().isEmpty()) {
            Glide.with(context).load(producto.getImageUrls().get(0)).centerCrop().into(holder.productImage);
        }

        // Lógica de visualización de stock
        int totalStock = producto.getStock();
        if (totalStock > 0 && totalStock <= 5) {
            holder.lowStockWarning.setText(String.format("¡Solo quedan %d!", totalStock));
            holder.lowStockWarning.setVisibility(View.VISIBLE);
        } else {
            holder.lowStockWarning.setVisibility(View.GONE);
        }

        // Desactivar botón de incrementar si se alcanza el stock máximo
        if (cartItem.getQuantity() >= totalStock) {
            holder.incrementButton.setEnabled(false);
            holder.incrementButton.setAlpha(0.5f);
        } else {
            holder.incrementButton.setEnabled(true);
            holder.incrementButton.setAlpha(1.0f);
        }

        // --- ASIGNACIÓN DE EVENTOS ---

        // Navegación a la pantalla de detalle
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, DetalleProductoActivity.class);
            intent.putExtra("PRODUCTO_SELECCIONADO", producto);
            context.startActivity(intent);
        });

        // Eventos que llaman al Singleton ShoppingCart
        holder.incrementButton.setOnClickListener(v -> ShoppingCart.getInstance().addProduct(producto));
        holder.decrementButton.setOnClickListener(v -> ShoppingCart.getInstance().decrementProduct(cartItem));
        holder.removeItemButton.setOnClickListener(v -> ShoppingCart.getInstance().removeProduct(producto));
    }

    @Override
    public int getItemCount() {
        return cartItems != null ? cartItems.size() : 0;
    }

    /**
     * ViewHolder para un item en la lista del carrito.
     */
    static class CartViewHolder extends RecyclerView.ViewHolder {
        ImageView productImage;
        TextView productName, productPrice, itemQuantity, subtotal, lowStockWarning;
        ImageButton incrementButton, decrementButton, removeItemButton;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.cart_product_image);
            productName = itemView.findViewById(R.id.cart_product_name);
            productPrice = itemView.findViewById(R.id.cart_product_price);
            itemQuantity = itemView.findViewById(R.id.cart_item_quantity);
            subtotal = itemView.findViewById(R.id.cart_item_subtotal);
            lowStockWarning = itemView.findViewById(R.id.low_stock_warning);
            incrementButton = itemView.findViewById(R.id.cart_increment_button);
            decrementButton = itemView.findViewById(R.id.cart_decrement_button);
            removeItemButton = itemView.findViewById(R.id.cart_remove_item_button);
        }
    }
}

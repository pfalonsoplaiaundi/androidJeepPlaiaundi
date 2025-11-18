package com.example.jeep;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jeep.adapter.CartAdapter;
import com.example.jeep.cart.CartItem;
import com.example.jeep.cart.ShoppingCart;
import com.example.jeep.repository.ProductRepository;

import java.text.DecimalFormat;
import java.util.List;

/**
 * Actividad que muestra el contenido del carrito de compra.
 */
public class CartActivity extends AppCompatActivity {

    private CartAdapter cartAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        Toolbar toolbar = findViewById(R.id.topAppBarCart);
        toolbar.setNavigationOnClickListener(v -> finish());

        RecyclerView recyclerView = findViewById(R.id.cart_items_recycler_view);
        cartAdapter = new CartAdapter(this);
        recyclerView.setAdapter(cartAdapter);

        observeCart();
        setupCheckoutButton();
    }

    private void setupCheckoutButton() {
        Button checkoutButton = findViewById(R.id.checkout_button);
        checkoutButton.setOnClickListener(v -> {
            List<CartItem> itemsToPurchase = ShoppingCart.getInstance().getCartItems().getValue();
            if (itemsToPurchase == null || itemsToPurchase.isEmpty()) {
                Toast.makeText(this, "El carrito está vacío", Toast.LENGTH_SHORT).show();
                return;
            }

            // CORREGIDO: Usar ID numérico
            for (CartItem item : itemsToPurchase) {
                ProductRepository.getInstance().updateStock(item.getProducto().getId(), item.getQuantity());
            }

            ShoppingCart.getInstance().clearCart();
            Toast.makeText(this, "¡Compra realizada con éxito!", Toast.LENGTH_LONG).show();
            finish();
        });
    }

    private void observeCart() {
        TextView totalPriceTextView = findViewById(R.id.total_price_text);
        TextView emptyCartMessage = findViewById(R.id.empty_cart_message);
        RecyclerView recyclerView = findViewById(R.id.cart_items_recycler_view);
        Button checkoutButton = findViewById(R.id.checkout_button);
        DecimalFormat currencyFormat = new DecimalFormat("#,##0.00'€'");

        ShoppingCart.getInstance().getCartItems().observe(this, cartItems -> {
            boolean isEmpty = cartItems == null || cartItems.isEmpty();
            emptyCartMessage.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
            recyclerView.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
            checkoutButton.setEnabled(!isEmpty);
            checkoutButton.setAlpha(isEmpty ? 0.5f : 1.0f);
            if (!isEmpty) {
                cartAdapter.setCartItems(cartItems);
            }
        });

        ShoppingCart.getInstance().getTotalPrice().observe(this, total -> {
            if (total != null) {
                totalPriceTextView.setText(currencyFormat.format(total));
            }
        });
    }
}

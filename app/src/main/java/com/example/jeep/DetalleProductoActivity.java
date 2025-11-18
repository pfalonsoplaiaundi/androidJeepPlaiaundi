package com.example.jeep;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.example.jeep.adapter.ImageCarouselAdapter;
import com.example.jeep.cart.ShoppingCart;
import com.example.jeep.model.Producto;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.badge.BadgeUtils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.DecimalFormat;

/**
 * Muestra la vista detallada de un único producto.
 */
public class DetalleProductoActivity extends AppCompatActivity {

    private Producto producto;
    private LinearLayout dotsContainer;
    private FloatingActionButton cartFab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        EdgeToEdge.enable(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_producto);

        // Gestiona el layout edge-to-edge para no solapar con las barras de sistema
        setupEdgeToEdge();

        // Recibe el objeto Producto desde la actividad anterior
        producto = (Producto) getIntent().getSerializableExtra("PRODUCTO_SELECCIONADO");

        if (producto == null) {
            Toast.makeText(this, "Error: No se pudo cargar el producto.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        dotsContainer = findViewById(R.id.dots_indicator_container);
        cartFab = findViewById(R.id.fab_carrito);

        // Inicia todos los componentes de la UI
        setupCarousel();
        setupCartBadge();
    }

    /**
     * Cada vez que la actividad vuelve al primer plano, se refresca la información
     * de stock y el estado de los botones, por si ha habido cambios.
     */
    @Override
    protected void onResume() {
        super.onResume();
        populateProductData();
        setupButtons();
    }

    /**
     * Rellena todas las vistas (TextViews) con la información del producto.
     * Calcula y muestra el stock real disponible.
     */
    private void populateProductData() {
        TextView alias = findViewById(R.id.product_alias);
        TextView category = findViewById(R.id.product_category);
        TextView pvp = findViewById(R.id.product_pvp_detail);
        TextView oldPvp = findViewById(R.id.product_old_pvp_detail);
        TextView description = findViewById(R.id.product_description);
        TextView stockInfo = findViewById(R.id.stock_info);
        DecimalFormat currencyFormat = new DecimalFormat("#,##0.00'€'");

        alias.setText(producto.getAlias());
        category.setText(producto.getCategoria());
        description.setText(producto.getDescripcion());

        // Usa los métodos del modelo para mostrar los precios correctos
        pvp.setText(currencyFormat.format(producto.getFinalPrice()));
        Double oldPriceValue = producto.getDisplayOldPrice();
        if (oldPriceValue != null) {
            oldPvp.setVisibility(View.VISIBLE);
            oldPvp.setText(currencyFormat.format(oldPriceValue));
            oldPvp.setPaintFlags(oldPvp.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            oldPvp.setVisibility(View.GONE);
        }

        // Lógica de stock en tiempo real
        int totalStock = producto.getStock();
        int quantityInCart = ShoppingCart.getInstance().getQuantityOfProduct(producto.getId());
        int availableStock = totalStock - quantityInCart;

        if (availableStock > 0) {
            stockInfo.setText(String.format("Disponibles: %d", availableStock));
            stockInfo.setTextColor(ContextCompat.getColor(this, android.R.color.holo_green_dark));
        } else {
            stockInfo.setText("Agotado");
            stockInfo.setTextColor(ContextCompat.getColor(this, android.R.color.holo_red_dark));
        }
    }

    /**
     * Configura los listeners y el estado de los botones de la pantalla.
     */
    private void setupButtons() {
        findViewById(R.id.button_volver).setOnClickListener(v -> finish());
        
        int availableStock = producto.getStock() - ShoppingCart.getInstance().getQuantityOfProduct(producto.getId());

        if (availableStock <= 0) {
            cartFab.setEnabled(false);
            cartFab.setAlpha(0.3f);
        } else {
            cartFab.setEnabled(true);
            cartFab.setAlpha(1.0f);
            cartFab.setOnClickListener(v -> {
                ShoppingCart.getInstance().addProduct(producto);
                Toast.makeText(this, "Añadido al carrito", Toast.LENGTH_SHORT).show();
                // Forzar refresco inmediato de la UI de stock
                onResume(); 
            });
        }
    }
    
    // --- Métodos de configuración de la UI ---

    private void setupEdgeToEdge() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content).getRootView(), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            View content = findViewById(R.id.content_scroll_view);
            content.setPadding(content.getPaddingLeft(), systemBars.top, content.getPaddingRight(), content.getPaddingBottom());
            View buttonVolver = findViewById(R.id.button_volver);
            ViewGroup.MarginLayoutParams buttonParams = (ViewGroup.MarginLayoutParams) buttonVolver.getLayoutParams();
            buttonParams.bottomMargin = systemBars.bottom + (int) (16 * getResources().getDisplayMetrics().density);
            buttonVolver.setLayoutParams(buttonParams);
            View fab = findViewById(R.id.fab_carrito);
            ViewGroup.MarginLayoutParams fabParams = (ViewGroup.MarginLayoutParams) fab.getLayoutParams();
            fabParams.bottomMargin = systemBars.bottom + (int) (16 * getResources().getDisplayMetrics().density);
            fab.setLayoutParams(fabParams);
            return WindowInsetsCompat.CONSUMED;
        });
    }

    private void setupCartBadge() {
        BadgeDrawable badge = BadgeDrawable.create(this);
        badge.setBackgroundColor(ContextCompat.getColor(this, R.color.jeep_red));
        badge.setBadgeTextColor(ContextCompat.getColor(this, R.color.white));
        ShoppingCart.getInstance().getCartItemCount().observe(this, count -> {
            cartFab.post(() -> {
                if (count != null && count > 0) {
                    badge.setNumber(count);
                    BadgeUtils.attachBadgeDrawable(badge, cartFab, null);
                } else {
                    BadgeUtils.detachBadgeDrawable(badge, cartFab);
                }
            });
        });
    }
    
    private void setupCarousel() {
        ViewPager2 viewPager = findViewById(R.id.image_carousel);
        ImageCarouselAdapter carouselAdapter = new ImageCarouselAdapter(this, producto.getImageUrls());
        viewPager.setAdapter(carouselAdapter);

        if (producto.getImageUrls() != null && producto.getImageUrls().size() > 1) {
            setupDotsIndicator(carouselAdapter.getItemCount());
            viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                @Override
                public void onPageSelected(int position) {
                    super.onPageSelected(position);
                    updateDotsIndicator(position);
                }
            });
        }
    }

    private void setupDotsIndicator(int count) {
        dotsContainer.removeAllViews();
        ImageView[] dots = new ImageView[count];
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(8, 0, 8, 0);
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new ImageView(this);
            dots[i].setImageDrawable(ContextCompat.getDrawable(this, R.drawable.carousel_dot_unselected));
            dots[i].setLayoutParams(layoutParams);
            dotsContainer.addView(dots[i]);
        }
        if(dots.length > 0){
            dots[0].setImageDrawable(ContextCompat.getDrawable(this, R.drawable.carousel_dot_selected));
        }
    }

    private void updateDotsIndicator(int index) {
        int childCount = dotsContainer.getChildCount();
        for (int i = 0; i < childCount; i++) {
            ImageView imageView = (ImageView) dotsContainer.getChildAt(i);
            if (i == index) {
                imageView.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.carousel_dot_selected));
            } else {
                imageView.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.carousel_dot_unselected));
            }
        }
    }
}

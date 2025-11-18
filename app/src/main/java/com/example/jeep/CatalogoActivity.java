package com.example.jeep;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jeep.adapter.ProductoAdapter;
import com.example.jeep.cart.ShoppingCart;
import com.example.jeep.model.Producto;
import com.example.jeep.repository.ProductRepository;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.badge.BadgeUtils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Actividad principal que muestra el catálogo de productos.
 */
public class CatalogoActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ProductoAdapter adapter;
    private String currentCategory = "Todos";
    private EditText searchBar;
    private FloatingActionButton cartFab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalogo);

        ProductRepository.getInstance().loadProducts(getApplicationContext());
        List<Producto> allProducts = ProductRepository.getInstance().getAllProducts();

        Toolbar toolbar = findViewById(R.id.topAppBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Catálogo");

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        cartFab = findViewById(R.id.fab_carrito);

        cartFab.setOnClickListener(v -> {
            Intent intent = new Intent(CatalogoActivity.this, CartActivity.class);
            startActivity(intent);
        });

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        setupDynamicMenu(allProducts);

        RecyclerView recyclerView = findViewById(R.id.recycler_view_productos);
        adapter = new ProductoAdapter(this, allProducts);
        recyclerView.setAdapter(adapter);

        setupSearch(toolbar);
        setupCartBadge();

        findViewById(R.id.button_volver).setOnClickListener(v -> navigateToMain());
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    /**
     * Extrae las categorías únicas y las añade al menú de navegación.
     */
    private void setupDynamicMenu(List<Producto> products) {
        Set<String> categories = new HashSet<>();
        if (products != null) {
            for (Producto p : products) {
                if (p.getCategoria() != null && !p.getCategoria().isEmpty()) {
                    categories.add(p.getCategoria());
                }
            }
        }
        List<String> sortedCategories = new ArrayList<>(categories);
        Collections.sort(sortedCategories);

        Menu menu = navigationView.getMenu();
        menu.clear();
        
        // Asegurarse de que el listener está puesto
        navigationView.setNavigationItemSelectedListener(this);

        // Añadir la opción "Todos"
        MenuItem allItem = menu.add(R.id.dynamic_group, Menu.NONE, 0, "Todos");
        allItem.setIcon(R.drawable.ic_jeep_placeholder);
        allItem.setCheckable(true);

        // Añadir el resto de categorías
        for (int i = 0; i < sortedCategories.size(); i++) {
            MenuItem catItem = menu.add(R.id.dynamic_group, Menu.NONE, i + 1, sortedCategories.get(i));
            catItem.setIcon(R.drawable.ic_jeep_placeholder);
            catItem.setCheckable(true);
        }
        
        // Marcar "Todos" como seleccionado por defecto
        menu.findItem(allItem.getItemId()).setChecked(true);
    }

    /**
     * Gestiona la selección de un item en el menú, filtrando por categoría.
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Marcar el item como seleccionado
        item.setChecked(true);

        // Actualizar categoría y filtrar
        currentCategory = item.getTitle().toString();
        adapter.filter(currentCategory, searchBar.getText().toString());

        // Actualizar título de la barra y cerrar el menú
        getSupportActionBar().setTitle("Todos".equalsIgnoreCase(currentCategory) ? "Catálogo" : currentCategory);
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
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

    private void setupSearch(Toolbar toolbar) {
        searchBar = toolbar.findViewById(R.id.search_bar);
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.filter(currentCategory, s.toString());
            }
            @Override public void afterTextChanged(Editable s) { }
        });
    }

    private void navigateToMain() {
        Intent intent = new Intent(CatalogoActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            navigateToMain();
        }
    }
}

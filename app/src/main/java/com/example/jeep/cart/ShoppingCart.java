package com.example.jeep.cart;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.jeep.model.Producto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Singleton que gestiona el estado global del carrito de compra.
 */
public class ShoppingCart {

    private static ShoppingCart instance;
    // CORREGIDO: La clave del mapa ahora es Integer
    private final Map<Integer, CartItem> cartItems = new HashMap<>();
    
    private final MutableLiveData<Integer> cartItemCount = new MutableLiveData<>(0);
    private final MutableLiveData<Double> totalPrice = new MutableLiveData<>(0.0);
    private final MutableLiveData<List<CartItem>> items = new MutableLiveData<>();

    private ShoppingCart() {}

    public static synchronized ShoppingCart getInstance() {
        if (instance == null) {
            instance = new ShoppingCart();
        }
        return instance;
    }

    public int getQuantityOfProduct(int productId) {
        CartItem item = cartItems.get(productId);
        return (item != null) ? item.getQuantity() : 0;
    }

    public void addProduct(Producto producto) {
        if (producto == null) return;
        if (producto.getStock() <= 0) return;

        CartItem item = cartItems.get(producto.getId());
        if (item != null) {
            if (item.getQuantity() < producto.getStock()) {
                item.incrementQuantity();
            }
        } else {
            item = new CartItem(producto);
            cartItems.put(producto.getId(), item);
        }
        notifyChanges();
    }

    public void removeProduct(Producto producto) {
        if (producto == null) return;
        cartItems.remove(producto.getId());
        notifyChanges();
    }

    public void decrementProduct(CartItem cartItem) {
        if (cartItem == null) return;
        cartItem.decrementQuantity();
        if (cartItem.getQuantity() <= 0) {
            cartItems.remove(cartItem.getProducto().getId());
        }
        notifyChanges();
    }

    public void clearCart() {
        cartItems.clear();
        notifyChanges();
    }

    public LiveData<Integer> getCartItemCount() { return cartItemCount; }
    public LiveData<Double> getTotalPrice() { return totalPrice; }
    public LiveData<List<CartItem>> getCartItems() { return items; }

    private void notifyChanges() {
        int count = 0;
        double total = 0;
        for (CartItem item : cartItems.values()) {
            count += item.getQuantity();
            total += item.getTotalPrice();
        }
        cartItemCount.setValue(count);
        totalPrice.setValue(total);
        items.setValue(new ArrayList<>(cartItems.values()));
    }
}

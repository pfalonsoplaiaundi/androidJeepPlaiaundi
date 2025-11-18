package com.example.jeep.cart;

import com.example.jeep.model.Producto;

/**
 * Representa una línea de item dentro del carrito de compra.
 * Asocia un objeto {@link Producto} con una cantidad específica.
 */
public class CartItem {
    private final Producto producto;
    private int quantity;

    public CartItem(Producto producto) {
        this.producto = producto;
        this.quantity = 1;
    }

    /**
     * Devuelve el objeto Producto asociado a este item del carrito.
     */
    public Producto getProducto() {
        return producto;
    }

    /**
     * Devuelve la cantidad de este producto en el carrito.
     */
    public int getQuantity() {
        return quantity;
    }

    /**
     * Establece la cantidad de este producto.
     */
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    /**
     * Incrementa la cantidad en uno.
     */
    public void incrementQuantity() {
        this.quantity++;
    }

    /**
     * Decrementa la cantidad en uno, sin bajar de cero.
     */
    public void decrementQuantity() {
        if (this.quantity > 0) {
            this.quantity--;
        }
    }

    /**
     * Calcula el precio total para esta línea (producto.getFinalPrice() * cantidad).
     * @return El precio total de esta línea de item.
     */
    public double getTotalPrice() {
        return producto.getFinalPrice() * quantity;
    }
}

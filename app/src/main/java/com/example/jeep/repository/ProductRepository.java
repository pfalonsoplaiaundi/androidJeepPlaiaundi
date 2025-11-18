package com.example.jeep.repository;

import android.content.Context;

import com.example.jeep.R;
import com.example.jeep.model.Producto;
import com.example.jeep.parser.XmlParser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Repositorio Singleton que actúa como una "fuente única de la verdad" para los datos de los productos.
 */
public class ProductRepository {
    private static ProductRepository instance;
    // CORREGIDO: La clave del mapa ahora es Integer
    private final Map<Integer, Producto> productMap = new HashMap<>();

    private ProductRepository() {}

    public static synchronized ProductRepository getInstance() {
        if (instance == null) {
            instance = new ProductRepository();
        }
        return instance;
    }

    public void loadProducts(Context context) {
        if (productMap.isEmpty()) {
            List<Producto> productList = XmlParser.parse(context, R.xml.productos);
            for (Producto p : productList) {
                productMap.put(p.getId(), p);
            }
        }
    }

    public List<Producto> getAllProducts() {
        return new ArrayList<>(productMap.values());
    }

    public Producto getProductById(int id) {
        return productMap.get(id);
    }

    public void updateStock(int productId, int quantitySold) {
        Producto product = getProductById(productId);
        if (product != null) {
            int newStock = product.getStock() - quantitySold;
            product.setStock(Math.max(0, newStock));
        }
    }
}

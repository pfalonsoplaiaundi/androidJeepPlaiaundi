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

    // -----------------------------------------------------------------------------------
    // Variables

    /** Singleton */
    private static ProductRepository instance;

    /** Mapa de productos */
    private final Map<Integer, Producto> productMap = new HashMap<>();

    // Fin variables
    // -----------------------------------------------------------------------------------

    // -----------------------------------------------------------------------------------
    // Constructor

    /** Constructor del repositorio. */
    private ProductRepository() {}

    // Fin constructor
    // -----------------------------------------------------------------------------------

    // -----------------------------------------------------------------------------------
    // Metodos

    /**
     * Obtiene la instancia del repositorio.
     *
     * @return La instancia del repositorio.
     */
    public static synchronized ProductRepository getInstance() {
        if (instance == null) {
            instance = new ProductRepository();
        }
        return instance;
    }

    /**
     * Carga los productos desde el archivo XML.
     * @param context Contexto de la aplicación.
     */
    public void loadProducts(Context context) {
        if (productMap.isEmpty()) {
            List<Producto> productList = XmlParser.parse(context, R.xml.productos);
            for (Producto p : productList) {
                productMap.put(p.getId(), p);
            }
        }
    }

    /**
     *
     * Obtiene todos los productos.
     *
     * @return Lista de productos.
     */
    public List<Producto> getAllProducts() {
        return new ArrayList<>(productMap.values());
    }

    /**
     * Obtiene un producto por su ID.
     *
     * @param id ID del producto.
     * @return Producto correspondiente al ID.
     */
    public Producto getProductById(int id) {
        return productMap.get(id);
    }

    /**
     * Actualiza el stock de un producto.
     * @param productId ID del producto.
     * @param quantitySold Cantidad vendida.
     */
    public void updateStock(int productId, int quantitySold) {
        Producto product = getProductById(productId);
        if (product != null) {
            int newStock = product.getStock() - quantitySold;
            product.setStock(Math.max(0, newStock));
        }
    }
}

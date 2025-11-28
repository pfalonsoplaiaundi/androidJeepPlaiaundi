package com.example.jeep.model;

import java.io.Serializable;
import java.util.List;

/**
 * Representa un único producto en el catálogo.
 */
public class Producto implements Serializable {
    private int id;
    private String categoria;
    private String alias;
    private String descripcion;
    private int stock;
    private double pvp;
    private List<String> imageUrls;

    private Double precioAnterior;
    private Integer dctoPorcentual;
    private Double dctoAbsoluto;

    public Producto() {}

    /**
     * Calcula y devuelve el precio final del producto aplicando el mejor descuento disponible.
     * @return El precio de venta final.
     */
    public double getFinalPrice() {
        double finalPrice = pvp;
        if (dctoPorcentual != null && dctoPorcentual > 0) {
            finalPrice = pvp * (1 - (dctoPorcentual / 100.0));
        } else if (dctoAbsoluto != null && dctoAbsoluto > 0) {
            finalPrice = pvp - dctoAbsoluto;
        }
        return Math.max(finalPrice, 0); // Asegura que el precio no sea negativo
    }

    // --- GETTERS Y SETTERS ---
    public int getId() { return id; }
    public String getCategoria() { return categoria; }
    public String getAlias() { return alias; }
    public String getDescripcion() { return descripcion; }
    public int getStock() { return stock; }
    public double getPvp() { return pvp; }
    public List<String> getImageUrls() { return imageUrls; }
    public Double getPrecioAnterior() { return precioAnterior; }
    public Integer getDctoPorcentual() { return dctoPorcentual; }
    public Double getDctoAbsoluto() { return dctoAbsoluto; }

    public void setId(int id) { this.id = id; }
    public void setCategoria(String categoria) { this.categoria = categoria; }
    public void setAlias(String alias) { this.alias = alias; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public void setStock(int stock) { this.stock = stock; }
    public void setPvp(double pvp) { this.pvp = pvp; }
    public void setImageUrls(List<String> imageUrls) { this.imageUrls = imageUrls; }
    public void setPrecioAnterior(Double precioAnterior) { this.precioAnterior = precioAnterior; }
    public void setDctoPorcentual(Integer dctoPorcentual) { this.dctoPorcentual = dctoPorcentual; }
    public void setDctoAbsoluto(Double dctoAbsoluto) { this.dctoAbsoluto = dctoAbsoluto; }
}

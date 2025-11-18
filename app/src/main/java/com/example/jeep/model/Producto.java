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

    public double getFinalPrice() {
        double pvp = getPvp();
        double discount = 0;
        if (dctoAbsoluto != null && dctoAbsoluto > 0) {
            discount = dctoAbsoluto;
        }
        if (dctoPorcentual != null && dctoPorcentual > 0) {
            double percentageDiscountValue = pvp * (dctoPorcentual / 100.0);
            if (percentageDiscountValue > discount) {
                discount = percentageDiscountValue;
            }
        }
        return pvp - discount;
    }

    public Double getDisplayOldPrice() {
        if (precioAnterior != null && precioAnterior > getFinalPrice()) {
            return precioAnterior;
        }
        if (getFinalPrice() < getPvp()) {
            return getPvp();
        }
        return null;
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

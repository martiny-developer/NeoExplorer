package com.project.neoexplorer.dto;

public class AsteroidDTO {

    private String nombre;
    private double diametroMinimo;
    private double diametroMaximo;
    private double velocidad;
    private double distancia;
    private boolean peligroso;

    public AsteroidDTO() {
    }

    public AsteroidDTO(
            String nombre,
            double diametroMinimo,
            double diametroMaximo,
            double velocidad,
            double distancia,
            boolean peligroso) {

        this.nombre = nombre;
        this.diametroMinimo = diametroMinimo;
        this.diametroMaximo = diametroMaximo;
        this.velocidad = velocidad;
        this.distancia = distancia;
        this.peligroso = peligroso;
    }

    public String getNombre() {
        return nombre;
    }

    public double getDiametroMinimo() {
        return diametroMinimo;
    }

    public double getDiametroMaximo() {
        return diametroMaximo;
    }

    public double getVelocidad() {
        return velocidad;
    }

    public double getDistancia() {
        return distancia;
    }

    public boolean isPeligroso() {
        return peligroso;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setDiametroMinimo(double diametroMinimo) {
        this.diametroMinimo = diametroMinimo;
    }

    public void setDiametroMaximo(double diametroMaximo) {
        this.diametroMaximo = diametroMaximo;
    }

    public void setVelocidad(double velocidad) {
        this.velocidad = velocidad;
    }

    public void setDistancia(double distancia) {
        this.distancia = distancia;
    }

    public void setPeligroso(boolean peligroso) {
        this.peligroso = peligroso;
    }
}
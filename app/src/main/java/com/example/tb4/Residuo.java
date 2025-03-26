package com.example.tb4;

import com.google.gson.annotations.SerializedName;

public class Residuo {
    @SerializedName("id")
    private int id;
    @SerializedName("nombre")
    private String nombre;
    @SerializedName("tipo")
    private String tipo;
    @SerializedName("descripcion")
    private String descripcion;

    // Constructor vacío (necesario para Retrofit)
    public Residuo() {
    }

    // Constructor con todos los campos (excepto id, ya que puede ser generado por la API)
    public Residuo(String nombre, String tipo, String descripcion) {
        this.nombre = nombre;
        this.tipo = tipo;
        this.descripcion = descripcion;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getTipo() {
        return tipo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    // Setters (si necesitas modificar los valores después de la creación)
    public void setId(int id) {
        this.id = id;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}
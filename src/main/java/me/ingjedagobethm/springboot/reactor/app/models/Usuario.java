package me.ingjedagobethm.springboot.reactor.app.models;

public class Usuario {
    private static int id0 = 0;
    private int id = 0;
    private String nombre;

    public Usuario(String nombre) {
        this.id0++;
        setId(this.id0);
        this.nombre = nombre;
    }

    public Usuario() {
        this.id0++;
        setId(this.id0);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    @Override
    public String toString() {
        return "Usuario{" + id + ". " +
                "nombre='" + nombre + '\'' +
                '}';
    }
}

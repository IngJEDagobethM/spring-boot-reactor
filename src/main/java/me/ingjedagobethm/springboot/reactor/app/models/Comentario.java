package me.ingjedagobethm.springboot.reactor.app.models;

import java.util.ArrayList;
import java.util.List;

public class Comentario {
    private List<String> comentarios = new ArrayList<>();

    public Comentario(List<String> comentarios) {
        this.comentarios = comentarios;
    }

    public Comentario() {
    }

    public List<String> getComentarios() {
        return comentarios;
    }

    public void setComentarios(List<String> comentarios) {
        this.comentarios = comentarios;
    }

    public void addComentario(String comentario){
        this.comentarios.add(comentario);
    }

    @Override
    public String toString() {
        return "comentarios=" + comentarios;
    }
}

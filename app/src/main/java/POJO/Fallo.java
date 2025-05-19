package POJO;

import java.io.Serializable;

public class Fallo implements Serializable {

    private int id;
    private String titulo;
    private String fecha;
    private String hora;
    private String descripcion;
    private String tipoFalloDescripcion;

    public Fallo(String titulo, String fecha, String hora, String descripcion, String tipoFalloDescripcion) {
        this.titulo = titulo;
        this.fecha = fecha;
        this.hora = hora;
        this.descripcion = descripcion;
        this.tipoFalloDescripcion = tipoFalloDescripcion;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTipoFalloDescripcion() {
        return tipoFalloDescripcion;
    }

    public void setTipoFalloDescripcion(String tipoFalloDescripcion) {
        this.tipoFalloDescripcion = tipoFalloDescripcion;
    }

    public String getTitulo() { return titulo; }
    public String getFecha() { return fecha; }
    public String getHora() { return hora; }
    public String getDescripcion(){ return descripcion;}

}

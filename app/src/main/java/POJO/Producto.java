package POJO;

public class Producto {

    public int dia;
    public int mes;
    public int year;


    private String nombre;
    private int cantidad;

    public void setCaducidad(String caducidad) {
        this.caducidad = caducidad;
    }

    private String caducidad;
    private int imagenResId; // ID del recurso drawable

    public Producto(String nombre, int cantidad, String caducidad, int imagenResId) {
        this.nombre = nombre;
        this.cantidad = cantidad;
        this.caducidad = caducidad;
        this.imagenResId = imagenResId;
    }

    public String getNombre() { return nombre; }
    public int getCantidad() { return cantidad; }
    public String getCaducidad() { return caducidad; }
    public int getImagenResId() { return imagenResId; }

}

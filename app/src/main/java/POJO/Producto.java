package POJO;

public class Producto {

    public int dia;
    public int mes;
    public int year;

    private int id;
    private String tipo;

    private String nombre;
    private int cantidad;
    private String caducidad;
    private int imagenResId; // ID del recurso drawable


    public Producto(int id, String tipo, String nombre, int cantidad, String caducidad, int imagenResId) {
        this.id = id;
        this.tipo = tipo;
        this.nombre = nombre;
        this.cantidad = cantidad;
        this.caducidad = caducidad;
        this.imagenResId = imagenResId;
    }

    public int getId() { return id; }
    public String getTipo() { return tipo; }
    public String getNombre() { return nombre; }
    public int getCantidad() { return cantidad; }
    public String getCaducidad() { return caducidad; }
    public int getImagenResId() { return imagenResId; }

    public void setCaducidad(String caducidad) {
        this.caducidad = caducidad;
    }

}

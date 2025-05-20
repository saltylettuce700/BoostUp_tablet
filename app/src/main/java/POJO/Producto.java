package POJO;

public class Producto {

    public int dia;
    public int mes;
    public int year;

    private int id;
    int id_inv_proteina;
    private String tipo;

    private String nombre;
    private int cantidad;
    private String caducidad;
    private int imagenResId; // ID del recurso drawable


    public Producto(int id, int id_inv_proteina, String tipo, String nombre, int cantidad, String caducidad, int imagenResId) {
        this.id = id;
        this.id_inv_proteina = id_inv_proteina;
        this.tipo = tipo;
        this.nombre = nombre;
        this.cantidad = cantidad;
        this.caducidad = caducidad;
        this.imagenResId = imagenResId;
    }

    public int getDia() {
        return dia;
    }

    public void setDia(int dia) {
        this.dia = dia;
    }

    public int getMes() {
        return mes;
    }

    public void setMes(int mes) {
        this.mes = mes;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setId_inv_proteina(int id_inv_proteina) {
        this.id_inv_proteina = id_inv_proteina;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public void setImagenResId(int imagenResId) {
        this.imagenResId = imagenResId;
    }

    public int getId() { return id; }
    public int getId_inv_proteina() {return id_inv_proteina;}
    public String getTipo() { return tipo; }
    public String getNombre() { return nombre; }
    public int getCantidad() { return cantidad; }
    public String getCaducidad() { return caducidad; }
    public int getImagenResId() { return imagenResId; }

    public void setCaducidad(String caducidad) {
        this.caducidad = caducidad;
    }

}

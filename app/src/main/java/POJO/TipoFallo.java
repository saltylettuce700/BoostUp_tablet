package POJO;

public class TipoFallo {

    private int id;
    private String nombre;

    public TipoFallo(int id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    // Para mostrar en el Spinner
    @Override
    public String toString() {
        return nombre;
    }
}

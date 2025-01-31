package POJO;

public class ReporteVenta {

    private String titulo;
    private String ganancias;
    private String pedidos;

    public ReporteVenta(String titulo, String ganancias, String pedidos) {
        this.titulo = titulo;
        this.ganancias = ganancias;
        this.pedidos = pedidos;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getGanancias() {
        return ganancias;
    }

    public String getPedidos() {
        return pedidos;
    }
}

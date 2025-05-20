package com.example.boostup_tablet.Adapter;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.boostup_tablet.R;

import java.util.Calendar;
import java.util.List;

import POJO.Producto;

public class InventarioAdapter extends  RecyclerView.Adapter<InventarioAdapter.InventarioViewHolder>  {

    private List<Producto> productos;
    private Context context;

    public InventarioAdapter(Context context, List<Producto> productos) {
        this.context = context;
        this.productos = productos;
    }

    @NonNull
    @Override
    public InventarioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_inventario, parent, false);
        return new InventarioViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InventarioViewHolder holder, int position) {
        Producto producto = productos.get(position);
        holder.tvProducto.setText(producto.getNombre());
        String unidad = producto.getTipo().equalsIgnoreCase("saborizante") ? "ml" : "g";
        String tipo = producto.getTipo();
        holder.tvCantidad.setText(producto.getCantidad() + " " + unidad);
        holder.tvCaducidad.setText(producto.getCaducidad());

        int resId = obtenerImagenPorProducto(producto.getTipo(), producto.getNombre());
        holder.imgProducto.setImageResource(resId != 0 ? resId : R.drawable.cross_icon);


        holder.btnReabastecer.setOnClickListener(v -> {
            // Aquí puedes manejar la lógica cuando se presiona el botón
            System.out.println("Reabasteciendo " + producto.getNombre());

            showReabastecerDialog(producto);
        });
    }

    @Override
    public int getItemCount() {
        return productos.size();
    }

    public static class InventarioViewHolder extends RecyclerView.ViewHolder {
        ImageView imgProducto;
        TextView tvProducto, tvCantidad, tvCaducidad;
        Button btnReabastecer;

        public InventarioViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProducto = itemView.findViewById(R.id.imgProducto);
            tvProducto = itemView.findViewById(R.id.tvProducto);
            tvCantidad = itemView.findViewById(R.id.tvCantidad);
            tvCaducidad = itemView.findViewById(R.id.tvCaducidad);
            btnReabastecer = itemView.findViewById(R.id.btnReabastecer);
        }
    }

    private void showReabastecerDialog(Producto producto) {

        Dialog dialog = new Dialog(context, R.style.BlurBackgroundDialog);

        // Inflar el diseño personalizado
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_reabastecer, null);
        dialog.setContentView(dialogView);



        // Configurar botones
        Button btnReabastecer = dialogView.findViewById(R.id.btnReabastecer);
        TextView tv_producto = dialogView.findViewById(R.id.tv_producto);

        EditText ET_caducidad = dialogView.findViewById(R.id.ET_caducidad);
        ImageView btnCerrar = dialogView.findViewById(R.id.btnCerrar);

        tv_producto.setText(producto.getNombre());

        ET_caducidad.setOnClickListener(view -> {
            // Obtener la fecha actual
            Calendar actual = Calendar.getInstance();
            int dia = actual.get(Calendar.DAY_OF_MONTH);
            int mes = actual.get(Calendar.MONTH);
            int anio = actual.get(Calendar.YEAR);

            // Crear el DatePickerDialog con la fecha actual como predeterminada
            DatePickerDialog datePickerDialog = new DatePickerDialog(context, (datePicker, year, month, dayOfMonth) -> {
                // Formatear la fecha seleccionada
                String fecha = dayOfMonth + "/" + (month + 1) + "/" + year;
                ET_caducidad.setText(fecha); // Mostrar la fecha en el EditText
                producto.setCaducidad(fecha); // Actualizar la caducidad en el objeto Producto
            }, anio, mes, dia);

            // Permitir solo fechas futuras
            datePickerDialog.getDatePicker().setMinDate(actual.getTimeInMillis());

            datePickerDialog.show();
        });


        btnReabastecer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Acción de continuar

                String nuevaCaducidad = ET_caducidad.getText().toString();
                if (!nuevaCaducidad.isEmpty()) {
                    producto.setCaducidad(nuevaCaducidad); // Actualizar cantidad
                    notifyDataSetChanged(); // Notificar cambio en el RecyclerView
                    dialog.dismiss();
                }

                dialog.dismiss();
                Toast.makeText(context, "REABASTECIENDO", Toast.LENGTH_SHORT).show();

            }
        });

        btnCerrar.setOnClickListener(v -> dialog.dismiss());

        // Mostrar el diálogo
        dialog.show();


    }

    private int obtenerImagenPorProducto(String tipo, String valorClave) {
        if (valorClave == null) return 0;

        valorClave = valorClave.toLowerCase();

        switch (tipo) {
            case "proteina":
                if (valorClave.contains("pure and natural")) return R.drawable.pure_natural_img;
                if (valorClave.contains("falcon")) return R.drawable.falcon;
                return 0;

            case "saborizante":
                if (valorClave.contains("fresa")||valorClave.contains("strawberry")) return R.drawable.strawberry_milk;
                if (valorClave.contains("chocolate")) return R.drawable.choco_milk;
                if (valorClave.contains("vainilla")|| valorClave.contains("vanilla")) return R.drawable.vanilla_milk;


                return 0;

            case "curcuma":
                if (valorClave.contains("nature heart")) return R.drawable.nature_heart_turmeric;

                return R.drawable.cross_icon;
        }

        return 0;
    }
}

package com.example.mytiendita;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ProductoViewHolder extends RecyclerView.ViewHolder {
    TextView textNombre, textPrecio, textStock;
    Button btnEditar, btnEliminar;
    public Button btnAgregarCarrito;

    public ProductoViewHolder(@NonNull View itemView) {
        super(itemView);
        textNombre = itemView.findViewById(R.id.textNombre);
        textPrecio = itemView.findViewById(R.id.textPrecio);
        textStock = itemView.findViewById(R.id.textStock);
        btnEditar = itemView.findViewById(R.id.btnEditar);
        btnEliminar = itemView.findViewById(R.id.btnEliminar);
        btnAgregarCarrito = itemView.findViewById(R.id.btnAgregarCarrito);

    }
}
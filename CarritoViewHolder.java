package com.example.mytiendita;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CarritoViewHolder extends RecyclerView.ViewHolder {

    public TextView textNombre, textCantidad, textPrecio;
    public Button buttonIncrease, buttonDecrease, buttonRemove;

    public CarritoViewHolder(@NonNull View itemView) {
        super(itemView);

        textNombre = itemView.findViewById(R.id.textNombre);
        textCantidad = itemView.findViewById(R.id.textCantidad);
        textPrecio = itemView.findViewById(R.id.textPrecio);

        buttonIncrease = itemView.findViewById(R.id.buttonIncrease);
        buttonDecrease = itemView.findViewById(R.id.buttonDecrease);
        buttonRemove = itemView.findViewById(R.id.buttonRemove);
    }
}
package com.example.mytiendita;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class ProductoAdapter extends RecyclerView.Adapter<ProductoViewHolder> {

    private Context context;
    private List<Producto> productos;
    private List<String> productoIds; // IDs de Firebase

    public ProductoAdapter(Context context, List<Producto> productos, List<String> productoIds) {
        this.context = context;
        this.productos = productos;
        this.productoIds = productoIds;
    }

    @NonNull
    @Override
    public ProductoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_producto_card, parent, false);
        return new ProductoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductoViewHolder holder, int position) {
        Producto producto = productos.get(position);
        String productoId = productoIds.get(position);

        holder.textNombre.setText(producto.getNombre());
        holder.textPrecio.setText("Precio: $" + producto.getPrecio());
        holder.textStock.setText("Stock: " + producto.getStock());

        holder.btnEditar.setOnClickListener(v -> {
            Intent intent = new Intent(context, EditProductActivity.class);
            intent.putExtra("productoId", productoId); // 🔥 Aquí enviamos el ID
            context.startActivity(intent);
        });

        holder.btnEliminar.setOnClickListener(v -> {
            FirebaseDatabase.getInstance().getReference("productos").child(productoId).removeValue();
            Toast.makeText(context, "Producto eliminado", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        return productos.size();
    }
}

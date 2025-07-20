package com.example.mytiendita;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

public class CarritoActivity extends AppCompatActivity {

    private RecyclerView recyclerCarrito;
    private FirebaseRecyclerAdapter<CarritoItem, CarritoViewHolder> adapter;
    private DatabaseReference carritoRef;
    private FirebaseAuth auth;


    private Button buttonComprar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carrito);
        buttonComprar = findViewById(R.id.buttonComprar);


        buttonComprar.setOnClickListener(v -> {
            Intent intent = new Intent(CarritoActivity.this, CheckoutActivity.class);
            startActivity(intent);
        });

        recyclerCarrito = findViewById(R.id.recyclerCarrito);
        recyclerCarrito.setLayoutManager(new LinearLayoutManager(this));

        auth = FirebaseAuth.getInstance();
        String uid = auth.getCurrentUser().getUid();
        carritoRef = FirebaseDatabase.getInstance().getReference("carritos").child(uid);
        TextView textPrecioTotal = findViewById(R.id.textTotal);
        calcularTotal(carritoRef, textPrecioTotal);

        FirebaseRecyclerOptions<CarritoItem> options =
                new FirebaseRecyclerOptions.Builder<CarritoItem>()
                        .setQuery(carritoRef, CarritoItem.class)
                        .build();

        adapter = new FirebaseRecyclerAdapter<CarritoItem, CarritoViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull CarritoViewHolder holder, int position, @NonNull CarritoItem item) {
                String itemId = getRef(position).getKey();

                holder.textNombre.setText(item.getNombre());
                holder.textCantidad.setText("Cantidad: " + item.getCantidad());
                holder.textPrecio.setText("Precio: $" + item.getPrecio());

                holder.buttonIncrease.setOnClickListener(v -> {
                    carritoRef.child(itemId).child("cantidad").setValue(item.getCantidad() + 1);
                });

                holder.buttonDecrease.setOnClickListener(v -> {
                    if (item.getCantidad() > 1) {
                        carritoRef.child(itemId).child("cantidad").setValue(item.getCantidad() - 1);
                    } else {
                        Toast.makeText(CarritoActivity.this, "Cantidad mínima: 1", Toast.LENGTH_SHORT).show();
                    }
                });

                holder.buttonRemove.setOnClickListener(v -> {
                    carritoRef.child(itemId).removeValue()
                            .addOnSuccessListener(unused -> Toast.makeText(CarritoActivity.this, "Eliminado", Toast.LENGTH_SHORT).show());
                });
            }

            @NonNull
            @Override
            public CarritoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_carrito_card, parent, false);
                return new CarritoViewHolder(view);
            }
        };

        recyclerCarrito.setAdapter(adapter);
        adapter.startListening();
    }
    private void calcularTotal(DatabaseReference carritoRef, TextView textPrecioTotal) {
        carritoRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                double total = 0.0;

                for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                    CarritoItem item = itemSnapshot.getValue(CarritoItem.class);
                    if (item != null && item.getCantidad() > 0 && item.getPrecio() > 0) {
                        total += item.getCantidad() * item.getPrecio();
                    }
                }

                textPrecioTotal.setText("Total: $" + total);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(CarritoActivity.this, "Error al calcular total", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (adapter != null) adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (adapter != null) adapter.stopListening();
    }
}
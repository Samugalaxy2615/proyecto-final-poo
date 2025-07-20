package com.example.mytiendita;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

public class Database extends AppCompatActivity {

    private static final String TAG = "DatabaseActivity";

    private RecyclerView recyclerView;
    private FirebaseRecyclerAdapter<Producto, ProductoViewHolder> adapter;
    private DatabaseReference productosRef;
    private Button buttonGoToAddProduct, buttonVolver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_database);

        recyclerView = findViewById(R.id.recyclerProductos);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        buttonGoToAddProduct = findViewById(R.id.buttonGoToAddProduct);
        buttonVolver = findViewById(R.id.buttonVolver);

        buttonVolver.setOnClickListener(v -> {
            Intent intent = new Intent(Database.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        });

        productosRef = FirebaseDatabase.getInstance().getReference("productos");

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "No hay sesión iniciada. Redirigiendo al login...", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, Login.class));
            finish();
            return;
        }

        String uid = currentUser.getUid();
        DatabaseReference rolRef = FirebaseDatabase.getInstance().getReference("usuarios").child(uid).child("rol");

        rolRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String rol = snapshot.getValue(String.class);

                    if ("admin".equals(rol)) {
                        buttonGoToAddProduct.setVisibility(View.VISIBLE);
                        buttonGoToAddProduct.setOnClickListener(v -> {
                            startActivity(new Intent(Database.this, AddProductActivity.class));
                        });
                    } else {
                        buttonGoToAddProduct.setVisibility(View.GONE);
                    }

                    configurarRecyclerView(rol);
                } else {
                    Toast.makeText(Database.this, "No se encontró el rol del usuario", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Database.this, "Error al obtener rol del usuario", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Error al leer rol", error.toException());
            }
        });
    }

    private void configurarRecyclerView(String rol) {
        FirebaseRecyclerOptions<Producto> options =
                new FirebaseRecyclerOptions.Builder<Producto>()
                        .setQuery(productosRef, Producto.class)
                        .build();

        adapter = new FirebaseRecyclerAdapter<Producto, ProductoViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ProductoViewHolder holder, int position, @NonNull Producto producto) {
                String id = getRef(position).getKey();

                holder.textNombre.setText("Nombre: " + producto.getNombre());
                holder.textPrecio.setText("Precio: $" + producto.getPrecio());
                holder.textStock.setText("Stock: " + producto.getStock());

                if ("admin".equals(rol)) {
                    holder.btnEditar.setVisibility(View.VISIBLE);
                    holder.btnEliminar.setVisibility(View.VISIBLE);
                    holder.btnAgregarCarrito.setVisibility(View.GONE);

                    holder.btnEditar.setOnClickListener(v -> {
                        Intent intent = new Intent(Database.this, EditProductActivity.class);
                        intent.putExtra("PRODUCT_ID", id);
                        startActivity(intent);
                    });

                    holder.btnEliminar.setOnClickListener(v -> {
                        productosRef.child(id).removeValue()
                                .addOnSuccessListener(unused ->
                                        Toast.makeText(Database.this, "Producto eliminado", Toast.LENGTH_SHORT).show())
                                .addOnFailureListener(e ->
                                        Log.e(TAG, "Error al eliminar producto", e));
                    });
                } else {
                    holder.btnEditar.setVisibility(View.GONE);
                    holder.btnEliminar.setVisibility(View.GONE);
                    holder.btnAgregarCarrito.setVisibility(View.VISIBLE);

                    holder.btnAgregarCarrito.setOnClickListener(v -> {
                        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                        DatabaseReference carritoRef = FirebaseDatabase.getInstance().getReference("carritos").child(uid);

                        carritoRef.child(id).child("idProducto").setValue(id);
                        carritoRef.child(id).child("nombre").setValue(producto.getNombre());
                        carritoRef.child(id).child("precio").setValue(producto.getPrecio());
                        carritoRef.child(id).child("cantidad").setValue(1);

                        Toast.makeText(Database.this, "Agregado al carrito", Toast.LENGTH_SHORT).show();
                    });
                }
            }

            @NonNull
            @Override
            public ProductoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_producto_card, parent, false);
                return new ProductoViewHolder(view);
            }
        };

        recyclerView.setAdapter(adapter);
        adapter.startListening();
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
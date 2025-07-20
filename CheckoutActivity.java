package com.example.mytiendita;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

import java.util.HashMap;
import java.util.Map;

public class CheckoutActivity extends AppCompatActivity {

    private TextView textSaldoActual, textTotalCarrito, textSaldoRestante;
    private Button buttonComprar;

    private double saldoActual = 0;
    private double totalCarrito = 0;
    private double saldoRestante = 0;

    private DatabaseReference userRef, carritoRef;
    private String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        textSaldoActual = findViewById(R.id.textSaldoActual);
        textTotalCarrito = findViewById(R.id.textTotalCarrito);
        textSaldoRestante = findViewById(R.id.textSaldoRestante);
        buttonComprar = findViewById(R.id.buttonComprar);

        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        userRef = FirebaseDatabase.getInstance().getReference("usuarios").child(uid);
        carritoRef = FirebaseDatabase.getInstance().getReference("carritos").child(uid);

        calcularTotalCarrito();

        buttonComprar.setOnClickListener(v -> realizarCompra());
    }

    private void calcularTotalCarrito() {
        carritoRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                totalCarrito = 0;
                for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                    CarritoItem item = itemSnapshot.getValue(CarritoItem.class);
                    if (item != null) {
                        totalCarrito += item.getPrecio() * item.getCantidad();
                    }
                }

                textTotalCarrito.setText("Total del carrito: $" + totalCarrito);

                userRef.child("saldo").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        saldoActual = snapshot.getValue(Double.class);
                        textSaldoActual.setText("Saldo actual: $" + saldoActual);
                        saldoRestante = saldoActual - totalCarrito;
                        textSaldoRestante.setText("Saldo después de compra: $" + saldoRestante);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(CheckoutActivity.this, "Error al obtener saldo", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(CheckoutActivity.this, "Error al cargar carrito", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void realizarCompra() {
        if (saldoRestante < 0) {
            Toast.makeText(this, "Saldo insuficiente", Toast.LENGTH_SHORT).show();
            return;
        }

        carritoRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Guardar la venta
                String idVenta = FirebaseDatabase.getInstance().getReference("ventas").push().getKey();
                DatabaseReference ventaRef = FirebaseDatabase.getInstance().getReference("ventas").child(idVenta);

                Map<String, Object> ventaData = new HashMap<>();
                ventaData.put("usuario", uid);
                ventaData.put("fecha", ServerValue.TIMESTAMP);
                ventaData.put("total", totalCarrito);

                Map<String, Object> productosVendidos = new HashMap<>();

                for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                    CarritoItem item = itemSnapshot.getValue(CarritoItem.class);
                    if (item == null) continue;

                    Map<String, Object> prod = new HashMap<>();
                    prod.put("nombre", item.getNombre());  // Asegúrate que CarritoItem tenga nombre
                    prod.put("cantidad", item.getCantidad());
                    prod.put("precio", item.getPrecio());

                    productosVendidos.put(itemSnapshot.getKey(), prod);
                }

                ventaData.put("productos", productosVendidos);

                ventaRef.setValue(ventaData).addOnSuccessListener(unused -> {
                    // Actualiza el saldo del usuario
                    userRef.child("saldo").setValue(saldoRestante).addOnSuccessListener(unused1 -> {
                        carritoRef.removeValue(); // Vacía el carrito
                        Toast.makeText(CheckoutActivity.this, "Compra realizada con éxito", Toast.LENGTH_LONG).show();
                        finish();
                    }).addOnFailureListener(e -> {
                        Toast.makeText(CheckoutActivity.this, "Error al actualizar saldo", Toast.LENGTH_SHORT).show();
                    });
                }).addOnFailureListener(e -> {
                    Toast.makeText(CheckoutActivity.this, "Error al guardar venta", Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(CheckoutActivity.this, "Error al leer carrito", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
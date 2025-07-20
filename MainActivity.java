package com.example.mytiendita;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

public class MainActivity extends AppCompatActivity {

    FirebaseAuth auth;
    FirebaseUser user;

    TextView textView;
    Button buttonLogout, buttonCatalog, buttonBuy, buttonDiscounts, buttonVerCarrito , buttonRecargar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        textView = findViewById(R.id.user_details);
        buttonLogout = findViewById(R.id.logout);
        buttonCatalog = findViewById(R.id.catalog);
        buttonBuy = findViewById(R.id.buttonBuy);
        buttonDiscounts = findViewById(R.id.discounts);
        buttonVerCarrito = findViewById(R.id.buttonVerCarrito);
        buttonRecargar = findViewById(R.id.buttonRecargar);

        // Ocultar todos los botones inicialmente
        buttonBuy.setVisibility(View.GONE);
        buttonVerCarrito.setVisibility(View.GONE);
        buttonCatalog.setVisibility(View.GONE);
        buttonRecargar.setVisibility(View.GONE);
        buttonLogout.setVisibility(View.VISIBLE);
        buttonDiscounts.setVisibility(View.VISIBLE);


        // Verificar si hay sesión iniciada
        if (user == null) {
            startActivity(new Intent(getApplicationContext(), Login.class));
            finish();
            return;
        }

        else {
            textView.setText(user.getEmail());

            // Consultar el rol del usuario en la base de datos
            DatabaseReference rolRef = FirebaseDatabase.getInstance()
                    .getReference("usuarios")
                    .child(user.getUid())
                    .child("rol");

            rolRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String rol = snapshot.getValue(String.class);
                        Log.d("ROL", "Rol detectado: " + rol); // ← DEBUG

                        if ("admin".equals(rol)) {
                            buttonCatalog.setVisibility(View.VISIBLE);
                            buttonLogout.setVisibility(View.VISIBLE);
                            buttonDiscounts.setVisibility(View.VISIBLE);
                            Log.d("VISIBILIDAD", "Botón Catalog visible");
                        } else if ("cliente".equals(rol)) {
                            buttonBuy.setVisibility(View.VISIBLE);
                            buttonRecargar.setVisibility(View.VISIBLE);
                            buttonVerCarrito.setVisibility(View.VISIBLE);
                            buttonLogout.setVisibility(View.VISIBLE);
                            buttonDiscounts.setVisibility(View.VISIBLE);
                            Log.d("VISIBILIDAD", "Botones Buy y VerCarrito visibles");
                        } else {
                            Toast.makeText(MainActivity.this, "Rol desconocido", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(MainActivity.this, "No se encontró el rol del usuario", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(MainActivity.this, "Error al obtener el rol", Toast.LENGTH_SHORT).show();
                }
            });
        }

        // Acción para botón Catalog (admin)
        buttonCatalog.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, Database.class);
            startActivity(intent);
        });

        // Acción para botón Buy (cliente)
        buttonBuy.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, Database.class);
            startActivity(intent);
        });

        // Acción para botón Ver Carrito (cliente)
        buttonVerCarrito.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CarritoActivity.class);
            startActivity(intent);
        });
        // Acción para botón Recargar (cliente)
        buttonRecargar.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, RecargarActivity.class);
            startActivity(intent);
        });

        // Acción para botón Discounts (para ambos)
        buttonDiscounts.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ResumenVentasActivity.class);
            startActivity(intent);
        });


        // Acción para Logout
        buttonLogout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(getApplicationContext(), Login.class);
            startActivity(intent);
            finish();
        });
    }
}
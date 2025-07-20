package com.example.mytiendita;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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
public class RecargarActivity extends AppCompatActivity {

    private EditText editMonto;
    private Button buttonRecargar;
    private DatabaseReference userRef;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recargar);

        editMonto = findViewById(R.id.editMonto);
        buttonRecargar = findViewById(R.id.buttonRecargar);

        user = FirebaseAuth.getInstance().getCurrentUser();
        userRef = FirebaseDatabase.getInstance().getReference("usuarios").child(user.getUid());

        buttonRecargar.setOnClickListener(v -> {
            String montoStr = editMonto.getText().toString().trim();

            if (montoStr.isEmpty()) {
                Toast.makeText(this, "Ingrese un monto", Toast.LENGTH_SHORT).show();
                return;
            }

            double monto = Double.parseDouble(montoStr);

            userRef.child("saldo").get().addOnSuccessListener(snapshot -> {
                double saldoActual = snapshot.exists() ? snapshot.getValue(Double.class) : 0;
                double nuevoSaldo = saldoActual + monto;

                userRef.child("saldo").setValue(nuevoSaldo)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(this, "Recarga exitosa. Nuevo saldo: $" + nuevoSaldo, Toast.LENGTH_LONG).show();
                            finish();
                        })
                        .addOnFailureListener(e -> Toast.makeText(this, "Error al actualizar saldo", Toast.LENGTH_SHORT).show());
            });
        });
    }
}
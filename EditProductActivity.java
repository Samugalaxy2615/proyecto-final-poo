package com.example.mytiendita;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class
EditProductActivity extends AppCompatActivity {

    private static final String TAG = "EditProductActivity";

    private EditText editTextProductName, editTextProductPrice, editTextProductStock;
    private Button buttonUpdateProduct;

    private DatabaseReference databaseProductsRef;
    private String productIdRecibido;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_producto);

        editTextProductName = findViewById(R.id.editTextProductName);
        editTextProductPrice = findViewById(R.id.editTextProductPrice);
        editTextProductStock = findViewById(R.id.editTextProductStock);
        buttonUpdateProduct = findViewById(R.id.buttonUpdateProduct);

        databaseProductsRef = FirebaseDatabase.getInstance().getReference("productos");

        productIdRecibido = getIntent().getStringExtra("PRODUCT_ID");

        if (TextUtils.isEmpty(productIdRecibido)) {
            Toast.makeText(this, "ID de producto no recibido", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        cargarProducto();

        buttonUpdateProduct.setOnClickListener(v -> actualizarProducto());

    }

    private void cargarProducto() {
        databaseProductsRef.child(productIdRecibido).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DataSnapshot snapshot = task.getResult();
                if (snapshot.exists()) {
                    Producto producto = snapshot.getValue(Producto.class);
                    if (producto != null) {
                        editTextProductName.setText(producto.getNombre());
                        editTextProductPrice.setText(String.valueOf(producto.getPrecio()));
                        editTextProductStock.setText(String.valueOf(producto.getStock()));
                    }
                } else {
                    Toast.makeText(this, "Producto no encontrado", Toast.LENGTH_SHORT).show();
                    finish();
                }
            } else {
                Toast.makeText(this, "Error al cargar producto", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Error al cargar", task.getException());
            }
        });
    }

    private void actualizarProducto() {
        String nombre = editTextProductName.getText().toString().trim();
        String precioStr = editTextProductPrice.getText().toString().trim();
        String stockStr = editTextProductStock.getText().toString().trim();

        if (TextUtils.isEmpty(nombre) || TextUtils.isEmpty(precioStr) || TextUtils.isEmpty(stockStr)) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        double precio;
        int stock;
        try {
            precio = Double.parseDouble(precioStr);
            stock = Integer.parseInt(stockStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Precio o stock inválido", Toast.LENGTH_SHORT).show();
            return;
        }

        Producto productoActualizado = new Producto(nombre, precio, stock);

        databaseProductsRef.child(productIdRecibido).setValue(productoActualizado)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this, "Producto actualizado", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(EditProductActivity.this, Database.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al actualizar", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Error al actualizar", e);
                });
    }
}
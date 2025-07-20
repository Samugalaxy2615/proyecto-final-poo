package com.example.mytiendita;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddProductActivity extends AppCompatActivity {

    private static final String TAG = "AddProductActivity";

    private EditText editTextProductName;
    private EditText editTextProductPrice;
    private EditText editTextProductStock;
    private Button buttonSaveProduct;
    private Button buttonBack;  // ✅ Botón "Volver"

    private DatabaseReference databaseProductsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_producto);

        editTextProductName = findViewById(R.id.nombre);
        editTextProductPrice = findViewById(R.id.precio);
        editTextProductStock = findViewById(R.id.stock);
        buttonSaveProduct = findViewById(R.id.buttonGoToAddProduct);
        buttonBack = findViewById(R.id.buttonVolver);

        databaseProductsRef = FirebaseDatabase.getInstance().getReference("productos");

        buttonSaveProduct.setOnClickListener(v -> saveProduct());

        buttonBack.setOnClickListener(v -> {
            Intent intent = new Intent(AddProductActivity.this, Database.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }

    private void saveProduct() {
        String name = editTextProductName.getText().toString().trim();
        String priceStr = editTextProductPrice.getText().toString().trim();
        String stockStr = editTextProductStock.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            editTextProductName.setError("Nombre requerido");
            editTextProductName.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(priceStr)) {
            editTextProductPrice.setError("Precio requerido");
            editTextProductPrice.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(stockStr)) {
            editTextProductStock.setError("Stock requerido");
            editTextProductStock.requestFocus();
            return;
        }

        double price;
        try {
            price = Double.parseDouble(priceStr);
        } catch (NumberFormatException e) {
            editTextProductPrice.setError("Precio inválido");
            editTextProductPrice.requestFocus();
            return;
        }

        int stock;
        try {
            stock = Integer.parseInt(stockStr);
        } catch (NumberFormatException e) {
            editTextProductStock.setError("Stock inválido");
            editTextProductStock.requestFocus();
            return;
        }

        String productId = databaseProductsRef.push().getKey();

        if (productId == null) {
            Toast.makeText(this, "Error al generar ID para el producto.", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Error: No se pudo generar productId con push().");
            return;
        }

        Producto nuevoProducto = new Producto(name, price, stock);

        databaseProductsRef.child(productId).setValue(nuevoProducto)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Producto guardado exitosamente", Toast.LENGTH_LONG).show();
                        Log.d(TAG, "Producto guardado con ID: " + productId);

                        // Limpiar los campos
                        editTextProductName.setText("");
                        editTextProductPrice.setText("");
                        editTextProductStock.setText("");
                    } else {
                        Toast.makeText(this, "Error al guardar producto: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        Log.e(TAG, "Error al guardar producto", task.getException());
                    }
                });
    }
}

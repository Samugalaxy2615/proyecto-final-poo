package com.example.mytiendita; // Asegúrate que tu package sea correcto

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DeleteProductActivity extends AppCompatActivity {

    private static final String TAG = "DeleteProductActivity";

    private EditText editTextProductName;
    private Button buttonDeleteProduct;

    private DatabaseReference databaseProductsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        editTextProductName = findViewById(R.id.editTextProductName);

        databaseProductsRef = FirebaseDatabase.getInstance().getReference("productos");

        buttonDeleteProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteProductByName();
            }
        });
    }

    private void deleteProductByName() {
        String name = editTextProductName.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            editTextProductName.setError("Nombre requerido");
            editTextProductName.requestFocus();
            return;
        }

        databaseProductsRef.orderByChild("nombre").equalTo(name)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot productSnapshot : dataSnapshot.getChildren()) {
                                productSnapshot.getRef().removeValue();
                            }
                            Toast.makeText(DeleteProductActivity.this, "Producto(s) eliminado(s)", Toast.LENGTH_LONG).show();
                            Log.d(TAG, "Producto(s) con nombre '" + name + "' eliminado(s)");
                            editTextProductName.setText("");
                        } else {
                            Toast.makeText(DeleteProductActivity.this, "No se encontró producto con ese nombre", Toast.LENGTH_LONG).show();
                            Log.w(TAG, "No existe producto con nombre: " + name);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(DeleteProductActivity.this, "Error al eliminar: " + databaseError.getMessage(), Toast.LENGTH_LONG).show();
                        Log.e(TAG, "Error en Firebase", databaseError.toException());
                    }
                });
    }
}
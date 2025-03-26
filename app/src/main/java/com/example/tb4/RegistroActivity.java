package com.example.tb4;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegistroActivity extends AppCompatActivity {

    private EditText etNombre, etDescripcion;
    private Spinner spinnerTipo;
    private Button btnGuardar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        etNombre = findViewById(R.id.etNombre);
        spinnerTipo = findViewById(R.id.spinnerTipo);
        etDescripcion = findViewById(R.id.etDescripcion);
        btnGuardar = findViewById(R.id.btnGuardar);

        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nombre = etNombre.getText().toString();
                String tipo = spinnerTipo.getSelectedItem().toString();
                String descripcion = etDescripcion.getText().toString();

                if (nombre.isEmpty() || descripcion.isEmpty()) {
                    Toast.makeText(RegistroActivity.this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
                    return;
                }

                Residuo nuevoResiduo = new Residuo(nombre, tipo, descripcion);

                ApiService apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);
                Call<Residuo> call = apiService.crearResiduo(nuevoResiduo);

                call.enqueue(new Callback<Residuo>() {
                    @Override
                    public void onResponse(Call<Residuo> call, Response<Residuo> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(RegistroActivity.this, "Residuo registrado en la API correctamente", Toast.LENGTH_SHORT).show();
                            etNombre.setText("");
                            etDescripcion.setText("");
                            spinnerTipo.setSelection(0);
                        } else {
                            Toast.makeText(RegistroActivity.this, "Error al registrar el residuo en la API", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Residuo> call, Throwable t) {
                        Toast.makeText(RegistroActivity.this, "Error de conexión con la API: " + t.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }
}
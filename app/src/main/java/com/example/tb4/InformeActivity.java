package com.example.tb4;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.content.FileProvider;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InformeActivity extends AppCompatActivity {

    private TableLayout tableLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_informe);

        tableLayout = findViewById(R.id.tableLayout);

        Button btnActualizar = findViewById(R.id.btnActualizar);
        Button btnModificar = findViewById(R.id.btnModificar);
        Button btnEliminar = findViewById(R.id.btnEliminar);
        Button btnPdf = findViewById(R.id.btnPdf);

        mostrarDatos();

        btnActualizar.setOnClickListener(v -> {
            mostrarDatos();
            Toast toast = Toast.makeText(InformeActivity.this, "Datos de la API actualizados", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        });

        btnModificar.setOnClickListener(v -> mostrarDialogoModificar());

        btnEliminar.setOnClickListener(v -> mostrarDialogoEliminar());

        btnPdf.setOnClickListener(v -> generarPDF());
    }

    private void mostrarDatos() {
        tableLayout.removeViews(1, tableLayout.getChildCount() - 1);

        ApiService apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);
        Call<List<Residuo>> call = apiService.obtenerResiduos();

        call.enqueue(new Callback<List<Residuo>>() {
            @Override
            public void onResponse(Call<List<Residuo>> call, Response<List<Residuo>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Residuo> listaResiduos = response.body();
                    int rowColor = Color.WHITE;
                    for (Residuo residuo : listaResiduos) {
                        TableRow dataRow = new TableRow(InformeActivity.this);
                        dataRow.setBackgroundColor(rowColor);

                        String[] data = {String.valueOf(residuo.getId()), residuo.getNombre(), residuo.getTipo(), residuo.getDescripcion()};
                        for (String value : data) {
                            TextView dataTextView = new TextView(InformeActivity.this);
                            dataTextView.setText(value);
                            dataTextView.setPadding(8, 8, 8, 8);
                            dataTextView.setBackgroundResource(R.drawable.cell_border);
                            dataRow.addView(dataTextView);
                        }
                        tableLayout.addView(dataRow);
                        rowColor = (rowColor == Color.WHITE) ? Color.LTGRAY : Color.WHITE;
                    }
                } else {
                    Toast.makeText(InformeActivity.this, "Error al obtener datos de la API", Toast.LENGTH_SHORT).show();
                    Log.e("InformeActivity", "Error al obtener datos de la API. Código: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Residuo>> call, Throwable t) {
                Toast.makeText(InformeActivity.this, "Error de conexión con la API: " + t.getMessage(), Toast.LENGTH_LONG).show();
                Log.e("InformeActivity", "Error de conexión: " + t.getMessage(), t);
            }
        });
    }

    private void mostrarDialogoModificar() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_modificar, null);
        builder.setView(dialogView);

        final EditText etId = dialogView.findViewById(R.id.etId);
        final EditText etNombre = dialogView.findViewById(R.id.etNombre);
        final Spinner spTipo = dialogView.findViewById(R.id.spTipo);
        final EditText etDescripcion = dialogView.findViewById(R.id.etDescripcion);

        Button btnAceptar = dialogView.findViewById(R.id.btnAceptar);
        Button btnCancelar = dialogView.findViewById(R.id.btnCancelar);

        final AlertDialog dialog = builder.create();

        btnAceptar.setOnClickListener(v -> {
            String idStr = etId.getText().toString();
            if (idStr.isEmpty()) {
                Toast.makeText(InformeActivity.this, "Por favor, ingrese el ID del residuo", Toast.LENGTH_SHORT).show();
                return;
            }
            int id = Integer.parseInt(idStr);
            String nombre = etNombre.getText().toString();
            String tipo = spTipo.getSelectedItem().toString();
            String descripcion = etDescripcion.getText().toString();

            Residuo residuoModificado = new Residuo(nombre, tipo, descripcion);

            ApiService apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);
            Call<Residuo> call = apiService.actualizarResiduo(id, residuoModificado);

            call.enqueue(new Callback<Residuo>() {
                @Override
                public void onResponse(Call<Residuo> call, Response<Residuo> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(InformeActivity.this, "Residuo modificado con éxito", Toast.LENGTH_SHORT).show();
                        mostrarDatos();
                        dialog.dismiss();
                    } else {
                        Toast.makeText(InformeActivity.this, "Error al modificar el residuo en la API. Código: " + response.code(), Toast.LENGTH_SHORT).show();
                        Log.e("InformeActivity", "Error al modificar el residuo en la API. Código: " + response.code() + ", Mensaje: " + response.errorBody());
                    }
                }

                @Override
                public void onFailure(Call<Residuo> call, Throwable t) {
                    Toast.makeText(InformeActivity.this, "Error de conexión con la API", Toast.LENGTH_SHORT).show();
                    Log.e("InformeActivity", "Error de conexión con la API", t);
                }
            });
        });

        btnCancelar.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void mostrarDialogoEliminar() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_eliminar, null);
        builder.setView(dialogView);

        final EditText etId = dialogView.findViewById(R.id.etId);

        Button btnConfirmar = dialogView.findViewById(R.id.btnConfirmar);
        Button btnCancelar = dialogView.findViewById(R.id.btnCancelar);

        final AlertDialog dialog = builder.create();

        btnConfirmar.setOnClickListener(v -> {
            String idStr = etId.getText().toString();
            if (idStr.isEmpty()) {
                Toast.makeText(InformeActivity.this, "Por favor, ingrese el ID del residuo", Toast.LENGTH_SHORT).show();
                return;
            }
            int id = Integer.parseInt(idStr);

            ApiService apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);
            Call<Void> call = apiService.eliminarResiduo(id);

            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(InformeActivity.this, "Residuo eliminado con éxito", Toast.LENGTH_SHORT).show();
                        mostrarDatos();
                        dialog.dismiss();
                    } else {
                        Toast.makeText(InformeActivity.this, "Error al eliminar el residuo de la API. Código: " + response.code(), Toast.LENGTH_SHORT).show();
                        Log.e("InformeActivity", "Error al eliminar el residuo de la API. Código: " + response.code() + ", Mensaje: " + response.errorBody());
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Toast.makeText(InformeActivity.this, "Error de conexión con la API", Toast.LENGTH_SHORT).show();
                    Log.e("InformeActivity", "Error de conexión con la API", t);
                }
            });
        });

        btnCancelar.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void generarPDF() {
        ApiService apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);
        Call<List<Residuo>> call = apiService.obtenerResiduos();

        call.enqueue(new Callback<List<Residuo>>() {
            @Override
            public void onResponse(Call<List<Residuo>> call, Response<List<Residuo>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Residuo> listaResiduos = response.body();
                    generarPdfConDatos(listaResiduos);
                } else {
                    Toast.makeText(InformeActivity.this, "Error al obtener datos de la API para el PDF", Toast.LENGTH_SHORT).show();
                    Log.e("InformeActivity", "Error al obtener datos de la API para el PDF. Código: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Residuo>> call, Throwable t) {
                Toast.makeText(InformeActivity.this, "Error de conexión con la API", Toast.LENGTH_SHORT).show();
                Log.e("InformeActivity", "Error de conexión con la API", t);
            }
        });
    }

    private void generarPdfConDatos(List<Residuo> residuos) {
        Document document = new Document(PageSize.A4.rotate());
        try {
            String fileName = "InformeResiduos.pdf";
            File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            File file = new File(downloadsDir, fileName);
            FileOutputStream outputStream = new FileOutputStream(file);
            PdfWriter.getInstance(document, outputStream);
            document.open();

            Paragraph titulo = new Paragraph("Informe de Residuos", new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD));
            titulo.setAlignment(Element.ALIGN_CENTER);
            document.add(titulo);

            document.add(new Paragraph(" ", new Font(Font.FontFamily.HELVETICA, 10))); // Un párrafo con un espacio

            PdfPTable table = new PdfPTable(4);
            table.addCell("ID");
            table.addCell("Nombre");
            table.addCell("Tipo");
            table.addCell("Descripcion");

            for (Residuo residuo : residuos) {
                table.addCell(String.valueOf(residuo.getId()));
                table.addCell(residuo.getNombre());
                table.addCell(residuo.getTipo());
                table.addCell(residuo.getDescripcion());
            }

            document.add(table);

            document.close();
            outputStream.close();

            mostrarNotificacion(file);
        } catch (DocumentException | IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error al generar PDF", Toast.LENGTH_SHORT).show();
        }
    }

    private void mostrarNotificacion(File file) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String channelId = "pdf_channel";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, "PDF Notifications", NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri uri = FileProvider.getUriForFile(InformeActivity.this, getPackageName() + ".provider", file);
        Log.d("Notification", "URI: " + uri.toString());
        intent.setDataAndType(uri, "application/pdf");
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.ic_pdf)
                .setContentTitle("PDF Generado")
                .setContentText("InformeResiduos.pdf generado con éxito")
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        notificationManager.notify(1, builder.build());
    }
}
package edu.uoc.resolvers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

/*
    Esta clase representa la pantalla de juego.
 */
public class ActividadPrincipal extends AppCompatActivity implements Runnable {
    PuzzleLayout pl;
    int numCortes = 2;
    int imagen = R.mipmap.pic_02;
    long tInicio, tFin, tDelta;
    String fechaActual;
    String patronFecha = "dd/MM/yyyy";
    SimpleDateFormat sdf;
    double segTranscurridos;
    private static final int SECOND_ACTIVITY_REQUEST_CODE = 0;
    private static final int NIVELES = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        pl = findViewById(R.id.tablero_juego);
        pl.setImage(imagen, numCortes);

        // Empezamos a contar el tiempo
        tInicio = System.currentTimeMillis();
        final BBDDHelper dbHelper = new BBDDHelper(this);
        pl.setOnCompleteCallback(new PuzzleLayout.OnCompleteCallback() {
            @Override
            public void onComplete() {
                // Paramos el tiempo
                tFin = System.currentTimeMillis();
                sdf = new SimpleDateFormat(patronFecha);
                fechaActual = sdf.format(new Date(tFin));
                tDelta = tFin - tInicio;
                segTranscurridos = tDelta / 1000.0;

                // Obtenemos la BBDD
                SQLiteDatabase db = dbHelper.getWritableDatabase();

                // Insertamos la puntuación en la BBDD
                ContentValues valores = new ContentValues();
                valores.put(BBDDEsquema.COLUMNA_FECHA, String.valueOf(fechaActual));
                valores.put(BBDDEsquema.COLUMNA_NIVEL, numCortes - 1);
                valores.put(BBDDEsquema.COLUMNA_PUNTOS, segTranscurridos);

                long idNuevaFila = db.insert(BBDDEsquema.NOMBRE_TABLA, null, valores);

                // Mostramos mensaje al completar puzzle
                Toast.makeText(ActividadPrincipal.this, "¡Bravo! Tu tiempo " + String.format("%.2f", segTranscurridos).replace(".", ",") + "s", Toast.LENGTH_SHORT).show();

                // Esperamos 3 segundos para cargar el siguiente puzzle
                pl.postDelayed(ActividadPrincipal.this, 3000);
            }
        });
    }

    // Este método crea el menú selección de la barra de acción
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_principal, menu);
        return true;
    }

    // Este método dispara la acción correspondiente al elegir cada opción del menú.
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.ayuda:
                // Se abre la WebView con la ayuda
                Intent ayuda = new Intent(this, ActividadAyuda.class);
                startActivity(ayuda);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void run() {
        numCortes++;
        imagen++;
        // Si llegamos al último puzzle muestra el dialogo del fin del juego
        // Si no carga el siguiente puzzle
        if(numCortes > NIVELES + 1){
            showDialog();
        }else {
            pl.setImage(imagen, numCortes);
        }
    }

    // Este método muestra el diálogo de finalización del juego.
    private void showDialog() {
        new AlertDialog.Builder(ActividadPrincipal.this)
                .setTitle(R.string.exito)
                .setMessage(R.string.reiniciar)
                .setPositiveButton(R.string.ok,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                numCortes = 2;
                                imagen = R.mipmap.pic_02;
                                pl.setImage(imagen, numCortes);
                            }
                        }).setNegativeButton(R.string.salir,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent i = new Intent(ActividadPrincipal.this, ActividadInicio.class);
                        startActivityForResult(i, SECOND_ACTIVITY_REQUEST_CODE);
                        finish();
                    }
                }).show();
    }
}

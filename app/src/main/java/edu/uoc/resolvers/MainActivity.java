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
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;


// Esta clase representa la pantalla de juego
public class MainActivity extends AppCompatActivity implements Runnable, View.OnTouchListener {
    PuzzleLayout puzzleLayout;
    TextView tvTips;
    ImageView ivTips;
    int squareRootNum = 2;
    int drawableId = R.mipmap.pic_02;
    long tStart;
    long tEnd;
    long tDelta;
    String currentDate;
    String pattern;
    SimpleDateFormat simpleDateFormat;
    double elapsedSeconds;
    private static final int SECOND_ACTIVITY_REQUEST_CODE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ivTips = findViewById(R.id.iv_tips);
        ivTips.setImageResource(drawableId);
        tvTips = findViewById(R.id.tv_tips);
        tvTips.setOnTouchListener(this);
        puzzleLayout = findViewById(R.id.activity_swipe_card);
        puzzleLayout.setImage(drawableId, squareRootNum);
        // Empezamos a contar el tiempo
        tStart = System.currentTimeMillis();
        final BBDDHelper dbHelper = new BBDDHelper(this);
        puzzleLayout.setOnCompleteCallback(new PuzzleLayout.OnCompleteCallback() {
            @Override
            public void onComplete() {
                // Paramos el tiempo
                tEnd = System.currentTimeMillis();
                pattern = "dd/MM/yyyy";
                simpleDateFormat = new SimpleDateFormat(pattern);
                currentDate = simpleDateFormat.format(new Date(tEnd));
                tDelta = tEnd - tStart;
                elapsedSeconds = tDelta / 1000.0;

                // Gets the data repository in write mode
                SQLiteDatabase db = dbHelper.getWritableDatabase();

                // Create a new map of values, where column names are the keys
                ContentValues values = new ContentValues();
                values.put(BBDDEsquema.COLUMNA_FECHA, String.valueOf(currentDate));
                values.put(BBDDEsquema.COLUMNA_NIVEL, squareRootNum - 1);
                values.put(BBDDEsquema.COLUMNA_PUNTOS, elapsedSeconds);

                // Insert the new row, returning the primary key value of the new row
                long newRowId = db.insert(BBDDEsquema.NOMBRE_TABLA, null, values);

                // Mostramos mensaje al completar puzzle
                Toast.makeText(MainActivity.this, "¡Bravo! Tu tiempo " + String.format("%.2f", elapsedSeconds).replace(".", ",") + "s", Toast.LENGTH_SHORT).show();
                // Esperamos 3 segundos para cargar el siguiente puzzle
                puzzleLayout.postDelayed(MainActivity.this, 3000);
            }
        });
    }

    // Creamos menú selección de la barra de acción
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    // Disparamos la acción correspondiente al elegir cada opción del menú
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.ayuda:
                // Se abre la WebView con la ayuda
                Intent help = new Intent(this, HelpActivity.class);
                startActivity(help);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void run() {
        squareRootNum++;
        drawableId++;
        // Si llegamos al último puzzle muestra el dialogo del fin del juego
        // Si no carga el siguiente puzzle
        if(squareRootNum > 3){
            showDialog();
        }else {
            ivTips.setImageResource(drawableId);
            puzzleLayout.setImage(drawableId, squareRootNum);
        }
    }

    private void showDialog() {
        new AlertDialog.Builder(MainActivity.this)
                .setTitle(R.string.success)
                .setMessage(R.string.restart)
                .setPositiveButton(R.string.ok,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                squareRootNum = 2;
                                drawableId = R.mipmap.pic_02;
                                ivTips.setImageResource(drawableId);
                                puzzleLayout.setImage(drawableId, squareRootNum);
                            }
                        }).setNegativeButton(R.string.exit,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent i = new Intent(MainActivity.this,StartActivity.class);
                        startActivityForResult(i, SECOND_ACTIVITY_REQUEST_CODE);
                        finish();
                    }
                }).show();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                ivTips.setVisibility(View.VISIBLE);
                break;
            default:
                ivTips.setVisibility(View.GONE);
        }
        return true;
    }
}

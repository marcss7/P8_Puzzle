package edu.uoc.resolvers;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

// Esta clase representa la pantalla de bienvenida
public class StartActivity extends AppCompatActivity {
    private Button startButton;
    private TextView scores;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        getWindow().getDecorView().setBackgroundColor(Color.argb(255, 0, 179, 241));
        startButton = findViewById(R.id.startButton);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMainActivity();
            }
        });

        scores = findViewById(R.id.scores);

        BBDDHelper dbHelper = new BBDDHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                BBDDEsquema.COLUMNA_FECHA,
                BBDDEsquema.COLUMNA_NIVEL,
                "MIN(" + BBDDEsquema.COLUMNA_PUNTOS + ") as min_time"
        };

        Cursor cursor = db.query(
                BBDDEsquema.NOMBRE_TABLA,   // The table to query
                projection,             // The array of columns to return (pass null to get all)
                null,              // The columns for the WHERE clause
                null,          // The values for the WHERE clause
                BBDDEsquema.COLUMNA_NIVEL,                   // don't group the rows
                null,                   // don't filter by row groups
                null               // The sort order
        );


        while(cursor.moveToNext()) {

            scores.append(cursor.getString(1) + "     " + cursor.getString(0) + " " + String.format("%.2f", cursor.getDouble(2)).replace(".", ",") +  "\n");
        }
        cursor.close();
    }

    // Al hacer clic en el botón nos lleva a la pantalla principal del juego
    public void openMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}

package edu.uoc.resolvers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

// Esta clase representa la pantalla de juego
public class MainActivity extends AppCompatActivity implements Runnable, View.OnTouchListener {
    PuzzleLayout puzzleLayout;
    TextView tvTips;
    ImageView ivTips;
    int squareRootNum = 2;
    int drawableId = R.mipmap.pic_02;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ivTips = (ImageView) findViewById(R.id.iv_tips);
        ivTips.setImageResource(drawableId);
        tvTips = (TextView) findViewById(R.id.tv_tips);
        tvTips.setOnTouchListener(this);
        puzzleLayout = (PuzzleLayout) findViewById(R.id.activity_swipe_card);
        puzzleLayout.setImage(drawableId, squareRootNum);
        puzzleLayout.setOnCompleteCallback(new PuzzleLayout.OnCompleteCallback() {
            @Override
            public void onComplete() {
                Toast.makeText(MainActivity.this, R.string.next, Toast.LENGTH_LONG).show();
                puzzleLayout.postDelayed(MainActivity.this, 800);
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
                //Toast.makeText(this, "Ayuda seleccionada", Toast.LENGTH_SHORT).show();
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
        if(squareRootNum > 10){
            Toast.makeText(MainActivity.this, R.string.complete, Toast.LENGTH_SHORT).show();
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

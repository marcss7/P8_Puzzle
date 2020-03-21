package edu.uoc.resolvers;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/*
    Esta clase representa la vista de la página de ayuda.
 */
public class ActividadAyuda extends AppCompatActivity {
    private WebView vistaAyuda;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actividad_ayuda);

        vistaAyuda = findViewById(R.id.actividadAyuda);
        vistaAyuda.setWebViewClient(new WebViewClient());
        vistaAyuda.loadUrl(getString(R.string.url_ayuda));
    }

    @Override
    public void onBackPressed() {
        if (vistaAyuda.canGoBack()){
            vistaAyuda.goBack();
        }else {
            super.onBackPressed();
        }
    }
}

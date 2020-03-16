package edu.uoc.resolvers;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

// Esta clase representa la vista de la página de ayuda
public class HelpActivity extends AppCompatActivity {
    private WebView helpView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        helpView = (WebView) findViewById(R.id.helpView);
        helpView.setWebViewClient(new WebViewClient());
        helpView.loadUrl(getString(R.string.url_ayuda));
    }

    @Override
    public void onBackPressed() {
        if (helpView.canGoBack()){
            helpView.goBack();
        }else {
            super.onBackPressed();
        }
    }
}

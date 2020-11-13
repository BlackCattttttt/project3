package com.need.mymall;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ProductDetailOnlyActivity extends AppCompatActivity {

    WebView webView;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail_only);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Mô tả");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        webView = findViewById(R.id.product_detail_body_webview);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            webView.setWebContentsDebuggingEnabled(true);
        }
        CookieManager.getInstance().setAcceptCookie(true);
        Intent intent = getIntent();
        String description = intent.getStringExtra("description");

        //webView.loadUrl("http://192.168.1.3/desciption.html");
        String html = "<!DOCTYPE html> <html lang=\"en-US\"><body>"
                + "<div id=\"google_translate_element\"></div>\n" +
                "<script type=\"text/javascript\">\n" +
                "function googleTranslateElementInit() {\n" +
                "  new google.translate.TranslateElement({pageLanguage: 'en'}, 'google_translate_element');\n" +
                "}\n" +
                "</script>\n" +
                "<script type=\"text/javascript\" src=\"http://translate.google.com/translate_a/element.js?cb=googleTranslateElementInit\"></script>"+ description + "</body></html>";
        String encodedHtml = Base64.encodeToString(html.getBytes(),
                Base64.NO_PADDING);
        //webView.loadData(encodedHtml, "text/html", "base64");
        webView.loadDataWithBaseURL("http://localhost/", html, "text/html", "utf-8", null);

        //webView.loadDataWithBaseURL(null, String.valueOf(Html.fromHtml(html,Html.FROM_HTML_MODE_LEGACY)),"text/html","UTF-8",null);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
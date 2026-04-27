package com.astralcore.multispace;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Version badge
        TextView tvVersion = findViewById(R.id.tvVersion);
        tvVersion.setText("v" + BuildConfig.VERSION_NAME);

        // ✅ GitHub Auto Update Check — app open wena hadama
        new UpdateChecker(this).checkForUpdate();

        // Contact buttons
        setupContactButtons();

        // Space cards click
        setupSpaceCards();
    }

    private void setupContactButtons() {
        // WhatsApp Channel
        findViewById(R.id.btnWhatsApp).setOnClickListener(v ->
                openUrl("https://whatsapp.com/channel/0029Vb6UYsDCxoArqy6JsX0l"));

        // Telegram
        findViewById(R.id.btnTelegram).setOnClickListener(v ->
                openUrl("https://t.me/nmd_coder"));

        // YouTube
        findViewById(R.id.btnYouTube).setOnClickListener(v ->
                openUrl("https://www.youtube.com/@team_astral_yt"));
    }

    private void setupSpaceCards() {
        View card1 = findViewById(R.id.cardSpace1);
        View card2 = findViewById(R.id.cardSpace2);
        View card3 = findViewById(R.id.cardSpace3);

        if (card1 != null) card1.setOnClickListener(v ->
                Toast.makeText(this, "🌌 Space Alpha Active", Toast.LENGTH_SHORT).show());
        if (card2 != null) card2.setOnClickListener(v ->
                Toast.makeText(this, "🌠 Space Beta Active", Toast.LENGTH_SHORT).show());
        if (card3 != null) card3.setOnClickListener(v ->
                Toast.makeText(this, "✨ Space Gamma Active", Toast.LENGTH_SHORT).show());
    }

    private void openUrl(String url) {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
        } catch (Exception e) {
            Toast.makeText(this, "Could not open link", Toast.LENGTH_SHORT).show();
        }
    }
}

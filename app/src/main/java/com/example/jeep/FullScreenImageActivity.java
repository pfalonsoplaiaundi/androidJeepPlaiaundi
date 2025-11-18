package com.example.jeep;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;

public class FullScreenImageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_image);

        PhotoView photoView = findViewById(R.id.fullscreen_image_view);
        String imageUrl = getIntent().getStringExtra("IMAGE_URL");

        if (imageUrl != null) {
            Glide.with(this)
                .load(imageUrl)
                .into(photoView);
        }

        // Un clic en la imagen cierra la actividad
        photoView.setOnClickListener(v -> finish());
    }
}

package com.example.apprealidadeaumentada;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button abrirCameraButton = findViewById(R.id.abrir_camera_button);
        abrirCameraButton.setOnClickListener(v -> abrirCamera());
    }

    private void abrirCamera() {
        Intent intent = new Intent(this, CameraActivity.class);
        startActivity(intent);
    }
}

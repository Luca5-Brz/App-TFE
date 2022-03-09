package com.lucas.app_tfe;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    Button mBtnBoissons;
    Button mBtnRepas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBtnBoissons = findViewById(R.id.main_button_boisson);
        mBtnRepas = findViewById(R.id.main_button_repas);
        
        
        mBtnBoissons.setOnClickListener(view -> {
            Toast.makeText(this, "Serveur Boissons", Toast.LENGTH_SHORT).show();
        });
        
        mBtnRepas.setOnClickListener(view -> {
            Toast.makeText(this, "Serveur Repas", Toast.LENGTH_SHORT).show();
        });
        
        

    }


}
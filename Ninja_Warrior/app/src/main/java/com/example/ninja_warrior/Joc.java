package com.example.ninja_warrior;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

public class Joc extends AppCompatActivity {

    String usuario = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        usuario = getIntent().getStringExtra("usuario");
        setContentView(R.layout.activity_joc);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        Toast.makeText(this, "USUARIO "+ usuario, Toast.LENGTH_SHORT).show();

    }

    public String getUsuario() {
        return usuario;
    }

}
package com.example.lordoftherings;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class PointsActivity extends AppCompatActivity {
    private TextView nombreres;
    private TextView puntuacion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_points);

        nombreres=findViewById(R.id.nombreres);
        puntuacion=findViewById(R.id.puntuacion);

        Integer puntos = getIntent().getIntExtra("puntos", 0);
        String nombre = getIntent().getStringExtra("nombre");
        String punt=String.valueOf(puntos);

        nombreres.setText(nombre);
        puntuacion.setText(punt);


    }
    public void onClick(View view){
        finish();
    }
}
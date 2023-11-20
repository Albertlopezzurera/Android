package com.example.lordoftherings;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {
    private String users[] = {"gandalf", "frodo", "saruman"};
    private int points[] = {315, 222, 489};
    private EditText nombre;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        nombre=findViewById(R.id.nombre);
    }
    public void onClick(View view){
        Intent intent = new Intent(view.getContext(), PointsActivity.class);
        String nom = nombre.getText().toString();
        int i=0;
        int paramPoints=0;
        while (i< users.length){
            if (users[i].equalsIgnoreCase(nom) ) {
                paramPoints=points[i];
            }
            i++;
        }
        intent.putExtra("nombre", nom);
        intent.putExtra("puntos", paramPoints);
        startActivity(intent);
    }
}
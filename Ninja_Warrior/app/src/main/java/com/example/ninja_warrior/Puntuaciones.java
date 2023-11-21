package com.example.ninja_warrior;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Puntuaciones extends AppCompatActivity {
    SharedPreferences shPref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_puntuaciones);
        shPref = getSharedPreferences("ninja_shpref", Context.MODE_PRIVATE);
        List<String> mejoresPuntuaciones = new ArrayList<>();
        for (Map.Entry<String, ?> entry : shPref.getAll().entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            if (value instanceof String) {
                // Manejar los valores de tipo String
                String puntuacion = (String) value;
                mejoresPuntuaciones.add(key + " - " + puntuacion);
            } else if (value instanceof Integer) {
                // Manejar los valores de tipo int
                int puntuacion = (int) value;
                mejoresPuntuaciones.add(key + " - " + puntuacion);
            }
        }
        Collections.sort(mejoresPuntuaciones, new Comparator<String>() {
            @Override
            public int compare(String puntuacion1, String puntuacion2) {
                // Obtener los valores de puntuación
                int puntuacionValue1 = Integer.parseInt(puntuacion1.split(" - ")[1]);
                int puntuacionValue2 = Integer.parseInt(puntuacion2.split(" - ")[1]);
                // Ordenar en orden descendente
                return puntuacionValue2 - puntuacionValue1;
            }
        });
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, mejoresPuntuaciones);
        ListView listView = findViewById(R.id.listview);
        listView.setAdapter(adapter);
    }


}
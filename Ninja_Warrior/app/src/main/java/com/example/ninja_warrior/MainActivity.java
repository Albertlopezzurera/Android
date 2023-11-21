package com.example.ninja_warrior;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.CheckBoxPreference;
import androidx.preference.PreferenceManager;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    TextView tvNinja;
    AnimatorSet tvNinjaSet;
    Button bJugar, bPuntuaciones, bSalir;
    String usuario = "";
    MediaPlayer mediaPlayer;
    Boolean musicaSonando = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvNinja = findViewById(R.id.tvNinjaWarrior);
        tvNinjaSet = (AnimatorSet) AnimatorInflater.loadAnimator(this,R.animator.anim_tv_ninja);
        tvNinjaSet.setTarget(tvNinja);
        tvNinjaSet.start();
        bJugar=findViewById(R.id.bJugar);
        bPuntuaciones=findViewById(R.id.bPuntuaciones);
        bSalir=findViewById(R.id.bSalir);
    }

    @Override
    protected void onResume() {
        super.onResume();
        try{
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
            boolean isOpcion1Checked = sharedPreferences.getBoolean("musica", true);
            comprobarMusica(isOpcion1Checked);
        }catch (Exception e){

        }

    }

    public void comprobarMusica(boolean isOpcion1Checked) {
        if (isOpcion1Checked==true) {
            if (musicaSonando==false) {
                mediaPlayer = MediaPlayer.create(this, R.raw.musica_inicio);
                mediaPlayer.start();
                musicaSonando = true;
            }
        }else {
            mediaPlayer.stop();
            musicaSonando=false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_opciones, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_inf:
                alertaInformacion();
                return true;

            case R.id.action_config:
                Intent intent = new Intent(this, PreferencesScreen.class);
                startActivity(intent);
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    private void alertaInformacion() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Información de la aplicación")
                .setMessage("Hola, encantado! Mi nombre es Albert Lopez del curso de DAM del Institut Marianao. \n Esta aplicación es un juego que trata de Ninjas. \n Espero que te guste mi aplicacion. \n Salu2!")
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Acción a realizar cuando se presiona el botón Aceptar
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
    public void actividadPuntuaciones(View view){
        Intent intent = new Intent(this, Puntuaciones.class);
        intent.putExtra("usuario", usuario);
        startActivity(intent);
    }
    public void pedirUsuarioJuego(View view) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Nombre jugador");
        alert.setMessage("Por favor introduzca su nombre");

        // Set an EditText view to get user input
        final EditText input = new EditText(this);
        alert.setView(input);
        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                usuario = input.getText().toString();
                if (!usuario.isEmpty()) {// Verificar que se haya ingresado un nombre de usuario
                    empezarJuego(usuario);
                } else {
                    Toast.makeText(MainActivity.this, "Por favor, introduzca un nombre de usuario.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        alert.show();
    }
    private void empezarJuego(String usuario) {
        if (musicaSonando==true){
            mediaPlayer.stop();
            musicaSonando=false;
        }
        Intent intent = new Intent(this, Joc.class);
        intent.putExtra("usuario", usuario);
        startActivity(intent);
    }
    public void salirJuego(View view){
        finish();
    }
}
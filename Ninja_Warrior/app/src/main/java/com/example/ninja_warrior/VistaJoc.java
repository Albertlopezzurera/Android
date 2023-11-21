package com.example.ninja_warrior;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import androidx.preference.PreferenceManager;

import java.util.Locale;
import java.util.Vector;

public class VistaJoc extends View {

    private final Vector<Grafics> objectius;
    // //// NINJA //////
    private Grafics ninja;// Gràfic del ninja
    private int girNinja; // Increment de direcció
    private float acceleracioNinja; // augment de velocitat
    // Increment estàndard de gir i acceleració
    private static final int INC_GIR = 5;
    private static final float INC_ACCELERACIO = 0.5f;
    // //// THREAD I TEMPS //////
// Thread encarregat de processar el joc
    private ThreadJoc thread = new ThreadJoc();
    //Cada quant temps volem processar canvis (ms)
    private static int PERIODE_PROCES = 50;
    // Quan es va realitzar l'últim procés
    private long ultimProces = 0;
    private float mX=0, mY=0;
    private boolean llancament = false;
    // //// LLANÇAMENT //////
    private Grafics ganivet;
    private static int INC_VELOCITAT_GANIVET = 12;
    private boolean ganivetActiu =false;
    private int tempsGanivet;
    private Drawable drawableObjectiu[] = new Drawable[8];
    Drawable drawableNinja, drawableGanivet, drawableEnemic, drawbleNinja;
    SoundPool soundPool;
    int soundLlancament, soundExplosio;
    int puntuacion = 0;
    String usuario = "";
    MediaPlayer mpJuego;

    public VistaJoc(Context context, AttributeSet attrs){
        super(context,attrs);
        usuario = ((Joc) getContext()).getUsuario();
        drawableGanivet = context.getResources().getDrawable(R.drawable.ganivet, null);
        drawableEnemic = context.getResources().getDrawable(R.drawable.ninja_enemic, null);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String tipoNinja = sharedPreferences.getString("tipo_ninja" , "ninja1");
        if (tipoNinja.equals("ninja2")){
            drawbleNinja = context.getResources().getDrawable(R.drawable.ninja02, null);
        }else if (tipoNinja.equals("ninja3")){
            drawbleNinja = context.getResources().getDrawable(R.drawable.ninja03, null);
        }else{
            drawbleNinja = context.getResources().getDrawable(R.drawable.ninja01, null);
        }
        drawableObjectiu[0] = context.getResources().
                getDrawable(R.drawable.cap_ninja, null); //cap
        drawableObjectiu[1] = context.getResources().
                getDrawable(R.drawable.cos_ninja, null); //cos
        drawableObjectiu[2] = context.getResources().
                getDrawable(R.drawable.cua_ninja, null);
        AudioAttributes attributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();
        soundPool = new SoundPool.Builder()
                .setMaxStreams(2)
                .setAudioAttributes(attributes)
                .build();
        soundLlancament = soundPool.load(context, R.raw.llancament, 1);
        soundExplosio = soundPool.load(context, R.raw.explosio, 1);
        ganivet = new Grafics(this, drawableGanivet);
        ninja = new Grafics(this, drawbleNinja);
        String enemigos = sharedPreferences.getString("cantidad_enemigos", "5");
        int cantidadEnemigos = Integer.valueOf(enemigos);
        objectius = new Vector<Grafics>();
        for (int i = 0; i < cantidadEnemigos; i++) {
            Grafics objectiu = new Grafics(this, drawableEnemic);
            objectiu.setIncY(Math.random() * 4 - 2);
            objectiu.setIncX(Math.random() * 4 - 2);
            objectiu.setAngle((int) (Math.random() * 360));
            objectiu.setRotacio((int) (Math.random() * 8 - 4));
            objectius.add(objectiu);
        }
    }

    @Override
    public boolean onKeyDown(int codiTecla, KeyEvent event) {
        super.onKeyDown(codiTecla, event);
        // Suposem que processarem la pulsació
        boolean procesada = true;
        switch (codiTecla) {
            case KeyEvent.KEYCODE_DPAD_UP:
                acceleracioNinja = +INC_ACCELERACIO;
                break;
            case KeyEvent.KEYCODE_DPAD_DOWN:
                acceleracioNinja = -INC_ACCELERACIO;
                break;
            case KeyEvent.KEYCODE_DPAD_LEFT:
                girNinja = -INC_GIR;
                break;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                girNinja = +INC_GIR;
                break;
            case KeyEvent.KEYCODE_DPAD_CENTER:
            case KeyEvent.KEYCODE_ENTER:
                disparaGanivet();
                break;
            default:
                // Si estem aquí, no hi ha pulsació que ens interessi
                procesada = false;
                break;
        }
        return procesada;
    }
    @Override
    public boolean onKeyUp(int codigoTecla, KeyEvent evento) {
        super.onKeyUp(codigoTecla, evento);
        // Suposem que processarem la pulsació
        boolean procesada = true;
        switch (codigoTecla) {
            case KeyEvent.KEYCODE_DPAD_UP:
                acceleracioNinja = 0;
                break;
            case KeyEvent.KEYCODE_DPAD_DOWN:
                acceleracioNinja = 0;
                break;
            case KeyEvent.KEYCODE_DPAD_LEFT:
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                girNinja = 0;
                break;
            default:
                // Si estem aquí, no hi ha pulsació que ens interessi
                procesada = false;
                break;
        }
        return procesada;
    }

    @Override
    public boolean onTouchEvent (MotionEvent event) {
        super.onTouchEvent(event);
        float x = event.getX();
        float y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                llancament =true;
                break;
            case MotionEvent.ACTION_MOVE:
                float dx = Math.abs(x - mX);
                float dy = Math.abs(y - mY);
                if (dy<6 && dx>6){
                    girNinja = Math.round((x - mX) / 2);
                    llancament = false;
                } else if (dx<6 && dy>6){
                    acceleracioNinja = Math.round((mY - y) / 25);
                    llancament = false;
                }
                break;
            case MotionEvent.ACTION_UP:
                girNinja = 0;
                acceleracioNinja = 0;
                if (llancament){
                    disparaGanivet();
                }
                break;
        }
        mX=x; mY=y;
        return true;
    }
    @Override
    protected void onSizeChanged(int ancho, int alto,
                                 int ancho_anter, int alto_anter) {
        super.onSizeChanged(ancho, alto, ancho_anter, alto_anter);
        // Una vegada que coneixem el nostre ample i alt situem els objectius de
        // forma aleatória
        ninja.setPosX(ancho/2);
        ninja.setPosY(alto/2);
        for (Grafics objectiu : objectius) {
            do{
                objectiu.setPosX(Math.random()*(ancho-objectiu.getAmplada()));
                objectiu.setPosY(Math.random()*(alto-objectiu.getAltura()));
            } while(objectiu.distancia(ninja) < (ancho+alto)/5);
        }
        ultimProces= System.currentTimeMillis();
        thread.start();
    }
    @Override
    synchronized protected void onDraw(Canvas canvas) {

        super.onDraw(canvas);
        for (Grafics objetiu : objectius) {
            objetiu.dibuixaGrafic(canvas);
        }
        ninja.dibuixaGrafic(canvas);
        if (ganivetActiu){
            ganivet.dibuixaGrafic(canvas);
        }
    }

    synchronized protected void actualitzaMoviment() {
        long instant_actual = System.currentTimeMillis();
        // No facis res si el període de procés no s'ha complert.
        if(ultimProces + PERIODE_PROCES > instant_actual) {
            return;
        }
        // Per una execució en temps real calculem retard
        double retard = (instant_actual - ultimProces) / PERIODE_PROCES;
        ultimProces = instant_actual; // Per a la propera vegada
        // Actualitzem velocitat i direcció del personatge Ninja a partir de
        // girNinja i acceleracioNinja (segons l'entrada del jugador)
        ninja.setAngle((int) (ninja.getAngle() + girNinja * retard));
        double nIncX = ninja.getIncX() + acceleracioNinja *
                Math.cos(Math.toRadians(ninja.getAngle())) * retard;
        double nIncY = ninja.getIncY() + acceleracioNinja *
                Math.sin(Math.toRadians(ninja.getAngle())) * retard;
        ninja.incrementaPos(retard);

        // Actualitzem si el módul de la velocitat no és més gran que el màxim
        if (Math.hypot(nIncX,nIncY) <= Grafics.getMaxVelocitat()){
            ninja.setIncX(nIncX);
            ninja.setIncY(nIncY);
        }
        // Actualitzem posicions X i Y
        ninja.incrementaPos(retard);
        for(Grafics objectiu : objectius) {
            objectiu.incrementaPos(retard);
            if (ninja.getPosX() + ninja.getAmplada() >= objectiu.getPosX() &&
                    ninja.getPosX() <= objectiu.getPosX() + objectiu.getAmplada() &&
                    ninja.getPosY() + ninja.getAltura() >= objectiu.getPosY() &&
                    ninja.getPosY() <= objectiu.getPosY() + objectiu.getAltura()) {
                // Colisión detectada, finalizar partida
                finalizarPartida(puntuacion);
                break; // Opcional: salir del bucle si solo quieres detectar una colisión
            }
        }
        if (ganivetActiu) {
            ganivet.incrementaPos(retard);
            tempsGanivet -=retard;
            if (tempsGanivet < 0) {
                ganivetActiu = false;
            } else {
                for (int i = 0; i < objectius.size(); i++) {
                    if (ganivet.getPosX() + ganivet.getAmplada() >= objectius.get(i).getPosX() &&
                            ganivet.getPosX() <= objectius.get(i).getPosX() + objectius.get(i).getAmplada() &&
                            ganivet.getPosY() + ganivet.getAltura() >= objectius.get(i).getPosY() &&
                            ganivet.getPosY() <= objectius.get(i).getPosY() + objectius.get(i).getAltura()) {
                        destrueixObjectiu(i);
                        break;
                    }
                }

            }
        }
    }

    class ThreadJoc extends Thread {
        @Override
        public void run() {
            while (true) {
                actualitzaMoviment();
            }
        }
    }

    private void destrueixObjectiu(int i) {
        soundPool.play(soundExplosio, 1f, 1f, 1, 0, 1f);
        int numParts = 3;
        if(objectius.get(i).getDrawable()== drawableEnemic){
            for(int n = 0; n < numParts; n++){
                Grafics objectiu = new Grafics(this, drawableObjectiu[n]);
                objectiu.setPosX(objectius.get(i).getPosX());
                objectiu.setPosY(objectius.get(i).getPosY());
                objectiu.setIncX(Math.random()*7-3);
                objectiu.setIncY(Math.random()*7-3);
                objectiu.setAngle((int)(Math.random()*360));
                objectiu.setRotacio((int)(Math.random()*8-4));
                objectius.add(objectiu);
            }
        }
        puntuacion++;
        objectius.remove(i);
        ganivetActiu =false;

    }
    private void disparaGanivet() {
        soundPool.play(soundLlancament, 1f, 1f, 1, 0, 1f);
        ganivet.setPosX(ninja.getPosX()+ ninja.getAmplada()/2-ganivet.getAmplada()/2);
        ganivet.setPosY(ninja.getPosY()+ ninja.getAltura()/2-ganivet.getAltura()/2);
        ganivet.setAngle(ninja.getAngle());
        ganivet.setIncX(Math.cos(Math.toRadians(ganivet.getAngle())) *
                INC_VELOCITAT_GANIVET);
        ganivet.setIncY(Math.sin(Math.toRadians(ganivet.getAngle())) *
                INC_VELOCITAT_GANIVET);
        tempsGanivet = (int) Math.min(this.getWidth() / Math.abs( ganivet.getIncX()),
                this.getHeight() / Math.abs(ganivet.getIncY())) - 2;
        ganivetActiu = true;
    }

    private void finalizarPartida(int puntuacion) {
        guardarUsuario(usuario, puntuacion);
        ((Activity) getContext()).finish();
    }

    private void guardarUsuario(String usuario, int puntuacion) {
        SharedPreferences shPref = getContext().getSharedPreferences("ninja_shpref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = shPref.edit();

        String puntuacionActual = shPref.getString(usuario.toLowerCase(Locale.ROOT), null);
        if (puntuacionActual != null) {
            int puntuacionAnterior = Integer.parseInt(puntuacionActual);
            if (puntuacion > puntuacionAnterior) {
                editor.putString(usuario.toLowerCase(Locale.ROOT), String.valueOf(puntuacion));
                editor.apply();
            }
        } else {
            editor.putString(usuario.toLowerCase(Locale.ROOT), String.valueOf(puntuacion));
            editor.apply();
        }
    }


}
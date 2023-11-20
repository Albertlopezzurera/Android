package com.example.reproductormusica;

import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    SeekBar seekBar;
    ListView listview;
    ImageButton bPlay, bStop, bRepeat, bNext, bAtras, bPause;
    EditText etTitulo;
    int position;
    List<Cancion> songs = new ArrayList<>();
    MediaPlayer mp = new MediaPlayer();
    long duration;
    long songDuration=0;
    private Handler handler = new Handler();
    boolean mpIsPaused = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        seekBar = findViewById(R.id.seekBar);
        bPlay = findViewById(R.id.repeatButton);
        bAtras = findViewById(R.id.pauseButton);
        bRepeat = findViewById(R.id.stopButton);
        bStop = findViewById(R.id.playButton);
        bNext = findViewById(R.id.nextButton);
        bPause = findViewById(R.id.atrasButton);
        etTitulo = findViewById(R.id.etTitulo);
        listview = findViewById(R.id.listview);

            searchAllMP3();
            ArrayAdapter<Cancion> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, songs);
            listview.setAdapter(adapter);
            adapter.notifyDataSetChanged();

            listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    playSongAtPosition(position);
                }
            });
        }

    private void playSongAtPosition(int position) {
        this.position = position;
        Cancion cancionBuscar = songs.get(position);
        etTitulo.setText(cancionBuscar.toString());
        reproducir(cancionBuscar.getUri());
    }

    private void reproducir(Uri songUri) {
        try {
            mp.reset();
            mp.setDataSource(MainActivity.this, songUri);
            mp.prepare();
            mp.start();
            Toast.makeText(MainActivity.this, "REPRODUCCIÓN EN MARCHA", Toast.LENGTH_SHORT).show();
            seekBarContinue();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(MainActivity.this, "ERROR AL REPRODUCIR CANCIÓN", Toast.LENGTH_SHORT).show();
        }
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            public void onCompletion(MediaPlayer arg0) {
                Toast.makeText(MainActivity.this, "REPRODUCCIÓN FINALIZADA", Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void searchAllMP3() {
        String[] projection = {
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.DATA};

        Cursor cursor = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                projection, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID));
                String artist = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));
                String title = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));
                duration = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));
                Uri uri = Uri.parse(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA))) ;
                songs.add(new Cancion(id,artist,title,duration,uri));
            }while(cursor.moveToNext());
            cursor.close();
        }
    }

    private void seekBarContinue() {
        songDuration = mp.getDuration();
        seekBar.setMax((int) songDuration);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mp.isPlaying()) {
                    seekBar.setProgress(mp.getCurrentPosition());
                    handler.postDelayed(this, 1000);
                }
            }
        }, 100);
    }

    public void buttonPlay(View view) {
    if (mpIsPaused==false){
        if (songs.isEmpty()) {
            Toast.makeText(this, "No se encontraron canciones.", Toast.LENGTH_SHORT).show();
            return;
        }
        reproducir(songs.get(position).getUri());
    }else {
        mp.start();
        seekBarContinue();
        mpIsPaused=false;
    }
    }

    public void buttonPause(View view) {
        mp.pause();
        mpIsPaused = true;
    }

    public void buttonStop(View view) {
        seekBar.setProgress(0);
        mpIsPaused=false;
        mp.reset();
    }

    public void buttonRepeat(View view) {
        if (mp != null) {
            mp.seekTo(0);
            mp.start();
            seekBar.setProgress(0);
        }
    }

    public void buttonNext(View view) {
            mp.reset();
        if (position < songs.size()) {
            playSongAtPosition(++position);
        }else {
            Toast.makeText(this, "No hay mas canciones para reproducir.", Toast.LENGTH_SHORT).show();
        }
    }

    public void buttonAtras(View view) {
        mp.reset();
        if (position > 0 && songs.size() != 0 ) {
            playSongAtPosition(--position);
        }else {
            Toast.makeText(this, "No hay mas canciones para reproducir.", Toast.LENGTH_SHORT).show();
        }
    }
}

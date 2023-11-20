package com.example.basedadesregister;

import static com.example.basedadesregister.UserDataBaseContract.UsersTable.COLUMN_COURSE;
import static com.example.basedadesregister.UserDataBaseContract.UsersTable.COLUMN_CYCLE;
import static com.example.basedadesregister.UserDataBaseContract.UsersTable.COLUMN_NAME;
import static com.example.basedadesregister.UserDataBaseContract.UsersTable.COLUMN_SURNAME;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.sql.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    EditText edName;
    EditText edSurname;
    EditText etIdCard;
    Spinner spCycle;
    RadioButton rbFirst;
    RadioButton rbSecond;
    Button bAdd;
    Button bRemove;
    Button bUpdate;
    Button bFindDNI;
    Button bFindCycle;
    Button bFindCourse;
    UsersSQLiteHelper dbHelper;
    SQLiteDatabase db;
    RadioGroup rg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edName = findViewById(R.id.edName);
        edSurname = findViewById(R.id.edSurname);
        etIdCard = findViewById(R.id.etIdCard);
        spCycle = findViewById(R.id.spCycle);
        rbFirst = findViewById(R.id.rbFirst);
        rbSecond = findViewById(R.id.rbSecond);
        bAdd = findViewById(R.id.bAdd);
        bRemove = findViewById(R.id.bRemove);
        bUpdate = findViewById(R.id.bUpdate);
        bFindDNI = findViewById(R.id.bFindDNI);
        bFindCycle = findViewById(R.id.bFindCycle);
        bFindCourse = findViewById(R.id.bFindCourse);
        rg = findViewById(R.id.rg);
        dbHelper = new UsersSQLiteHelper(this);
        db = dbHelper.getWritableDatabase();

        String[] ciclos = {"ASIX", "DAM", "DAW"};
        Spinner spCycle = findViewById(R.id.spCycle);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, ciclos);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCycle.setAdapter(adapter);

    }


    public void guardar(View view) {
        String idcard = etIdCard.getText().toString();
        String name = edName.getText().toString().toLowerCase();
        String surname = edSurname.getText().toString().toLowerCase();
        String ciclo = (String) spCycle.getSelectedItem();
        if (!"".equals(idcard) && !"".equals(name) && !"".equals(surname) && rbFirst.isChecked() || rbSecond.isChecked()) {
            int selectedId = rg.getCheckedRadioButtonId();
            String curso = null;
            if (selectedId == 1) {
                curso = "first";
            } else {
                curso = "second";
            }
            ContentValues values = new ContentValues();
            values.put(UserDataBaseContract.UsersTable.COLUMN_ID, idcard);
            values.put(COLUMN_NAME, name);
            values.put(COLUMN_SURNAME, surname);
            values.put(COLUMN_CYCLE, ciclo);
            values.put(COLUMN_COURSE, curso);
            long newRowId = db.insert(UserDataBaseContract.UsersTable.TABLE, null, values);
            Toast.makeText(this, "ALUMNE AFEGIT CORRECTAMENT", Toast.LENGTH_LONG).show();
            datosVacios();
        }
    }

    public void datosVacios() {
        etIdCard.setText("");
        edName.setText("");
        edSurname.setText("");
        rbFirst.setChecked(false);
        rbSecond.setChecked(false);
    }

    public void findID(View view) {
        String nombre = null;
        String apellido = null;
        String ciclo = null;
        String curso = null;
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String idcard = etIdCard.getText().toString();

        if ("".equals(idcard)){
            Toast.makeText(this, "EL CAMPO ID NO PUEDE ESTAR VACIO", Toast.LENGTH_SHORT).show();
        }
        else {
            String[] projection = {COLUMN_NAME, COLUMN_SURNAME, COLUMN_CYCLE, COLUMN_COURSE};
            String selection = UserDataBaseContract.UsersTable.COLUMN_ID + " = ?";
            String[] selectionArgs = {idcard};

            String sortOrder = COLUMN_NAME + " DESC";
            Cursor cursor = db.query(
                    UserDataBaseContract.UsersTable.TABLE,
                    projection,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    sortOrder
            );

            while (cursor.moveToNext()) {
                nombre = cursor.getString(
                        cursor.getColumnIndexOrThrow(COLUMN_NAME));
                apellido = cursor.getString(
                        cursor.getColumnIndexOrThrow(COLUMN_SURNAME));
                ciclo = cursor.getString(
                        cursor.getColumnIndexOrThrow(COLUMN_CYCLE));
                curso = cursor.getString(
                        cursor.getColumnIndexOrThrow(COLUMN_COURSE));
            }

            if (cursor.getCount() == 0) {
                Toast.makeText(this, "NO SE HA ENCONTRADO", Toast.LENGTH_SHORT).show();
                cursor.close();

            } else {
                edName.setText(nombre.toLowerCase());
                edSurname.setText(apellido.toLowerCase());
                if ("ASIX".equals(ciclo)) {
                    spCycle.setSelection(0);
                } else if ("DAM".equals(ciclo)) {
                    spCycle.setSelection(1);
                } else {
                    spCycle.setSelection(2);
                }
                if ("first".equals(curso)) {
                    rbFirst.setChecked(true);
                    rbSecond.setChecked(false);
                } else {
                    rbSecond.setChecked(true);
                    rbFirst.setChecked(false);
                }
                cursor.close();
            }
        }
}

    public void borrarAlumne(View view){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String idcard = etIdCard.getText().toString();
        if ("".equals(idcard)){
            Toast.makeText(this, "EL CAMPO ID NO PUEDE ESTAR VACIO", Toast.LENGTH_SHORT).show();
        }
        else {
            String selection = UserDataBaseContract.UsersTable.COLUMN_ID + " LIKE ?";
            String[] selectionArgs = {idcard};
            int deletedRows = db.delete(UserDataBaseContract.UsersTable.TABLE, selection, selectionArgs);
            if (deletedRows==1){
                Toast.makeText(this, "CONTACTO BORRADO", Toast.LENGTH_SHORT).show();
                datosVacios();
            }
            else {
                Toast.makeText(this, "NO SE HA ENCONTRADO", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void modificarAlumno(View view){
        String idcard = etIdCard.getText().toString();
        String name = edName.getText().toString().toLowerCase();
        String surname = edSurname.getText().toString().toLowerCase();
        String ciclo = (String) spCycle.getSelectedItem();
        if (!"".equals(idcard) && !"".equals(name) && !"".equals(surname) && rbFirst.isChecked() || rbSecond.isChecked()) {
            int selectedId = rg.getCheckedRadioButtonId();
            String curso = null;
            if (selectedId == 1) {
                curso = "first";
            } else {
                curso = "second";
            }
            ContentValues values = new ContentValues();
            values.put(UserDataBaseContract.UsersTable.COLUMN_ID, idcard);
            values.put(COLUMN_NAME, name);
            values.put(COLUMN_SURNAME, surname);
            values.put(COLUMN_CYCLE, ciclo);
            values.put(COLUMN_COURSE, curso);
            String selection = UserDataBaseContract.UsersTable.COLUMN_ID + " = ?";
            String[] selectionArgs = {idcard};
            int resultado = db.update(UserDataBaseContract.UsersTable.TABLE,
                    values,
                    selection,
                    selectionArgs);

            if (resultado>0){
                Toast.makeText(this, "ACTUALIZADO CORRECTAMENTE", Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(this, "NO SE HA ACTUALIZADO NINGUN CAMPO", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void consultarCiclo (View view){
        String ciclo = (String) spCycle.getSelectedItem();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] projection = {COLUMN_NAME, COLUMN_SURNAME, COLUMN_CYCLE, COLUMN_COURSE};
        String selection = COLUMN_CYCLE + " = ?";
        String[] selectionArgs = {ciclo};

        String sortOrder = COLUMN_NAME + " DESC";
        Cursor cursor = db.query(
                UserDataBaseContract.UsersTable.TABLE,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
        ArrayList<String> dni = new ArrayList<>();
        ArrayList<String> nombre = new ArrayList<>();
        ArrayList<String> apellido = new ArrayList<>();
        ArrayList<String> ciclo2 = new ArrayList<>();
        ArrayList<String> curso2 = new ArrayList<>();

        while (cursor.moveToNext()) {
            dni.add(cursor.getString(
                    cursor.getColumnIndexOrThrow(UserDataBaseContract.UsersTable.COLUMN_ID)));
            nombre.add(cursor.getString(
                    cursor.getColumnIndexOrThrow(COLUMN_NAME)));
            apellido.add(cursor.getString(
                    cursor.getColumnIndexOrThrow(COLUMN_SURNAME)));
            ciclo2.add(cursor.getString(
                    cursor.getColumnIndexOrThrow(COLUMN_CYCLE)));
            curso2.add(cursor.getString(
                    cursor.getColumnIndexOrThrow(COLUMN_COURSE)));
        }
        cursor.close();
        OutputStreamWriter ficheroCiclo = null;
        try {
            ficheroCiclo = new OutputStreamWriter(openFileOutput(ciclo, Context.MODE_PRIVATE));
            for (int i=0; i<dni.size(); i++){
                ficheroCiclo.write("DNI: "+dni.get(i)+ " NOMBRE: "+ nombre.get(i)+" APELLIDO :"+apellido.get(i)+" CICLO: "+ciclo2.get(i)+" CURSO:"+curso2.get(i)+"\n");
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (ficheroCiclo!=null){
                    ficheroCiclo.close();
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }
    public void consultarCicloCurso (View view){
        String ciclo = (String) spCycle.getSelectedItem();
        int selectedId = rg.getCheckedRadioButtonId();
        String curso = null;
        if (selectedId == 1) {
            curso = "first";
        } else if (selectedId==2){
            curso = "second";
        }else {
            curso = null;
        }
        if (curso!=null){
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            String[] projection = {COLUMN_NAME, COLUMN_SURNAME, COLUMN_CYCLE, COLUMN_COURSE};
            String selection = COLUMN_CYCLE + " = ? AND" + COLUMN_COURSE + " = "+ curso;
            String[] selectionArgs = {ciclo};

            String sortOrder = COLUMN_NAME + " DESC";
            Cursor cursor = db.query(
                    UserDataBaseContract.UsersTable.TABLE,
                    projection,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    sortOrder
            );
            ArrayList<String> dni = new ArrayList<>();
            ArrayList<String> nombre = new ArrayList<>();
            ArrayList<String> apellido = new ArrayList<>();
            ArrayList<String> ciclo2 = new ArrayList<>();
            ArrayList<String> curso2 = new ArrayList<>();

            while (cursor.moveToNext()) {
                dni.add(cursor.getString(
                        cursor.getColumnIndexOrThrow(UserDataBaseContract.UsersTable.COLUMN_ID)));
                nombre.add(cursor.getString(
                        cursor.getColumnIndexOrThrow(COLUMN_NAME)));
                apellido.add(cursor.getString(
                        cursor.getColumnIndexOrThrow(COLUMN_SURNAME)));
                ciclo2.add(cursor.getString(
                        cursor.getColumnIndexOrThrow(COLUMN_CYCLE)));
                curso2.add(cursor.getString(
                        cursor.getColumnIndexOrThrow(COLUMN_COURSE)));
            }
            cursor.close();
            OutputStreamWriter ficheroCicloCurso = null;
            try {
                ficheroCicloCurso = new OutputStreamWriter(openFileOutput(ciclo, Context.MODE_PRIVATE));
                for (int i=0; i<dni.size(); i++){
                    ficheroCicloCurso.write("DNI: "+dni.get(i)+ " NOMBRE: "+ nombre.get(i)+" APELLIDO :"+apellido.get(i)+" CICLO: "+ciclo2.get(i)+" CURSO:"+curso2.get(i)+"\n");
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (ficheroCicloCurso!=null){
                        ficheroCicloCurso.close();
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        }else {
            Toast.makeText(this, "EL CURSO DEBE ESTAR MARCADO", Toast.LENGTH_SHORT).show();
        }
    }
    }

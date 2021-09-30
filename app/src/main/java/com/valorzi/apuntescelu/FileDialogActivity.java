package com.valorzi.apuntescelu;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.provider.OpenableColumns;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Objects;

public class FileDialogActivity extends AppCompatActivity {
    private static final int CODE_READ = 42;
    private static final int CODE_WRITE = 43;
    private static final int CODE_TREE = 44;
    private static final int CODE_RENAME = 45;
    private static final int OPEN_FILE = 1;
    private static final int SAVE_FILE = 2;
    private String archivoActual;
    private int evento = 0;
    private String texto = "";
    private EditText nombreArchivo;
    private EditText editText1;

    protected  void Cancelar(){
       LaunchEditor(0,"",texto);
    }

    protected  void LaunchEditor(int evento, String rutaArchivo, String texto){
        Intent intentMain = new Intent(FileDialogActivity.this, MainActivity.class);
        intentMain.putExtra("keyEvento",0);
        intentMain.putExtra("keyArchivo","");
        intentMain.putExtra("keyText",texto);
        setResult(0,intentMain);
        startActivity(intentMain);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_dialog2);
        nombreArchivo=(EditText) findViewById(R.id.nombreArchivo);
        editText1=(EditText) findViewById(R.id.editText1);
        Inicio();
    }
    protected  void Inicio() {

        archivoActual = "";
        evento = 0;
        try {
            Intent myIntent = getIntent(); // gets the previously created intent
            evento = myIntent.getIntExtra("keyEvento", 0);
            texto = myIntent.getStringExtra("keyText");
            if(evento == OPEN_FILE)
                showOpenFileChooser();
            else if(evento == SAVE_FILE)
                showSaveFileChooser();
        } catch (Exception e) {
            LanzarAlerta("Error","Error al desplegar el Explorador de Archivos");
            Cancelar();
        }

    }
    private void showOpenFileChooser() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);
     //   intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        try {
            startActivityForResult(Intent.createChooser(intent, "Select a File to Upload"), OPEN_FILE);
        } catch (android.content.ActivityNotFoundException ex) {
            // Potentially direct the user to the Market with a Dialog
            LanzarAlerta("Error","Error al desplegar el Explorador de Archivos");
        }
    }

    private void showSaveFileChooser(){
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
      //  intent.putExtra(Intent.EXTRA_TITLE, archivoActual);
        try {
            startActivityForResult(Intent.createChooser(intent, "Select a Directory to save"), SAVE_FILE);
        } catch (android.content.ActivityNotFoundException ex) {
            // Potentially direct the user to the Market with a Dialog
            LanzarAlerta("Error","Error al desplegar el Explorador de Archivos");
        }
    }


    private String readTextFromUri(Uri uri) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        try (InputStream inputStream =
                     getContentResolver().openInputStream(uri);
             BufferedReader reader = new BufferedReader(
                     new InputStreamReader(Objects.requireNonNull(inputStream)))) {
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
        }
        return stringBuilder.toString();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK) {
            if (requestCode == OPEN_FILE) {
                Uri uri = data.getData();
                String uriString = uri.toString();
                File myFile = new File(uriString);
                String path = myFile.getAbsolutePath();
                String displayName = null;
                displayName = myFile.getName();
                try {
                    texto = readTextFromUri(uri);
                    LaunchEditor(0, path, texto);
                } catch (IOException e) {
                    LanzarAlerta("Error","Error al Abrir");
                }
            }
            else if (requestCode == SAVE_FILE) {
                Uri uri = data.getData();
                try {
                    OutputStream output = getBaseContext().getContentResolver().openOutputStream(uri);
                    output.write(texto.getBytes());
                    output.flush();
                    output.close();
                    String uriString = uri.toString();
                    File myFile = new File(uriString);
                    String path = myFile.getAbsolutePath();
                    LaunchEditor(0, path, texto);
                }
                catch(IOException e) {
                    LanzarAlerta("Error","Error al Guardar");
                }
            }
        }

    }
    private void LanzarAlerta(String Titulo,String Mensaje){
        new AlertDialog.Builder(this)
                .setTitle(Titulo)
                .setMessage(Mensaje)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                     Cancelar();
                    }
                }).create().show();
    }
}
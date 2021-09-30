package com.valorzi.apuntescelu;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;





import com.valorzi.apuntescelu.R.layout;

import android.content.Intent;
import android.opengl.Visibility;
import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.storage.StorageManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.EditText;
import android.widget.ScrollView;


public class MainActivity extends Activity {
    private static final int OPEN_FILE = 1;
    private static final int SAVE_FILE = 2;
    private EditText editText1;
    private EditText nombreArchivo;

    private String archivoActual,clave,Portapapeles;
    private int evento;
    private HorizontalScrollView Barra1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Inicio();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//	getMenuInflater().inflate(R.menu.main, menu);
        Barra1.setVisibility((Barra1.getVisibility()==View.VISIBLE)?View.GONE:View.VISIBLE);
        return false;
    }
    protected void Inicio(){
        editText1=(EditText) findViewById(R.id.editText1);
        nombreArchivo=(EditText) findViewById(R.id.nombreArchivo);
        Barra1=(HorizontalScrollView)findViewById(R.id.Barra1);


        archivoActual="";
        clave="";
        Portapapeles="";
        evento=0;
            Intent myIntent = getIntent(); // gets the previously created intent
            if (myIntent.hasExtra("keyEvento"))
            try{
                evento = myIntent.getIntExtra("keyEvento", 0);
                editText1.setText(myIntent.getStringExtra("keyText"));
                archivoActual = myIntent.getStringExtra("keyArchivo");
            }
            catch(Exception e) {

            }
    }
    public void BtAbrir(View v){
        evento=OPEN_FILE;
        Intent fileDialogIntent = new Intent(MainActivity.this, FileDialogActivity.class); // gets the previously created intent
        fileDialogIntent.putExtra("keyEvento", evento);
        fileDialogIntent.putExtra("keyText",editText1.getText().toString());
        startActivity(fileDialogIntent);
    }

    public void BtGuardarComo(View v){
        evento=SAVE_FILE;
        Intent fileDialogIntent = new Intent(MainActivity.this, FileDialogActivity.class); // gets the previously created intent
        fileDialogIntent.putExtra("keyEvento", evento);
        fileDialogIntent.putExtra("keyText",editText1.getText().toString());
        startActivity(fileDialogIntent);

    }

    public void BtGuardar(View v){
        if(archivoActual.length()==0){
            BtGuardarComo(v);
        }
        else{
     //       _guardarFichero(directorioActual,archivoActual);
        }
    }



    public void BtNuevo(View v){
        archivoActual="";
        editText1.setText("");
    }
    public void Aviso(String s){
        AlertDialog.Builder alerta=new AlertDialog.Builder(MainActivity.this);
        alerta.setPositiveButton(getString(R.string.label_ok), new DialogInterface.OnClickListener() {
            @Override public void onClick(DialogInterface dialog, int which) {}
        });
        alerta.setMessage(s).setCancelable(false).create().show();
    }

    public void BtOk(View v){
        String nArchivo=nombreArchivo.getText().toString();
        if(nArchivo.indexOf('.')<0)nArchivo=nArchivo+".txt";
     //   guardarFichero(directorioActual,nArchivo);
        evento=0;
    }

    public void BtAcerca(View v){
        LanzarAlerta(getString(R.string.content_version), getString(R.string.content_about));
    }
    public void BtAyuda(View v){
        LanzarAlerta(getString(R.string.content_version), getString(R.string.content_ayuda));
    }
    public void BtBuscar(View v){
        Confirmar(getString(R.string.label_buscar),clave,6);
    }
    public void BtBuscarSiguiente(View v){
        int Pos=editText1.getText().toString().indexOf(clave, editText1.getSelectionStart());
        if(Pos<0)Pos=editText1.getText().toString().indexOf(clave);
        if(Pos>=0)editText1.setSelection(Pos+clave.length());
    }
    public void IrA(View v){
        Confirmar(getString(R.string.label_ir_a),String.valueOf(editText1.getSelectionStart()),5);
    }
    public void BtPropiedades(View v){
        LanzarAlerta(getString(R.string.label_propiedades), Propiedades(archivoActual));
    }
    public void Pegar(View v){
        int inicio=editText1.getSelectionStart();int fin=editText1.getSelectionEnd();
        if(inicio>fin){int aux=fin;fin=inicio;inicio=aux;}
        if(inicio<0)return;
        String s=	editText1.getText().toString().substring(0,inicio);
        s=s+Portapapeles+editText1.getText().toString().substring(fin);
        editText1.setText(s);
        editText1.setSelection(inicio+Portapapeles.length());
    }
    public void Copiar(View v){
        int inicio=editText1.getSelectionStart();int fin=editText1.getSelectionEnd();
        if(inicio>fin){int aux=fin;fin=inicio;inicio=aux;}
        if(inicio<0)return;
        Portapapeles=editText1.getText().toString().substring(inicio,fin);
    }
    public void Cortar(View v){
        int inicio=editText1.getSelectionStart();int fin=editText1.getSelectionEnd();
        if(inicio>fin){int aux=fin;fin=inicio;inicio=aux;}
        if(inicio<0)return;
        Portapapeles=editText1.getText().toString().substring(inicio, fin);
        String s=editText1.getText().toString().substring(0,inicio);
        s=s+(editText1.getText().toString().substring(fin,editText1.getText().length()));
        editText1.setText(s);
    }


    private String Propiedades(String ruta) {
        String p = "";
        try {
            File f = new File(ruta);
            p = f.getName() + "\n" + getString(R.string.label_chars) + String.valueOf(editText1.length()) + ".\n" + getString(R.string.label_size);
            long tam = f.length();
            if (tam > 1000000) {
                p = p + String.valueOf(tam) + " mb.\n";
            } else if (tam > 1000) {
                p = p + String.valueOf(tam) + " kb.\n";
            } else {
                p = p + String.valueOf(tam) + " bytes.\n";
            }
            if (f.canRead())
                p = p + getString(R.string.label_read) + getString(R.string.label_si) + ".\n";
            else p = p + getString(R.string.label_read) + getString(R.string.label_no) + ".\n";
            if (f.canWrite())
                p = p + getString(R.string.label_write) + getString(R.string.label_si) + ".\n";
            else p = p + getString(R.string.label_write) + getString(R.string.label_no) + ".\n";

            p = p + f.getAbsolutePath() + "\n";
        } catch (Exception e) {
            p = "Error en el archivo";
        }
        return p;
    }
    private void guardarFichero(String ruta,String nombre){
        int salida=0;
        try
        {
            File f = new File(ruta,nombre);
            if(f.exists()){
                LanzarAlerta("Alerta","SobreEscribir el fichero?");
                return;
            }


            archivoActual=f.getAbsolutePath();++salida;
            archivoActual=f.getName();++salida;

            OutputStreamWriter fout =new OutputStreamWriter(new FileOutputStream(f));++salida;
            archivoActual=f.getName();++salida;
            fout.write( editText1.getText().toString());++salida;
            archivoActual=f.getName();++salida;
            fout.close();++salida;
            archivoActual=f.getName();++salida;
        }
        catch (Exception ex)
        {
            Log.e(String.valueOf(salida), "Error en el fichero");
        }
    }
    private void _guardarFichero(String ruta,String nombre){
        try
        {
            File f = new File(ruta, nombre);
            //directorioActual=f.getAbsolutePath();
            archivoActual=f.getName();
            if(editText1.getText().toString().length()==0&&nombre.length()>0){f.delete();archivoActual="";return;}
            OutputStreamWriter fout =new OutputStreamWriter(new FileOutputStream(f));
            fout.write( editText1.getText().toString());
            fout.close();
        }
        catch (Exception ex)
        {
            Log.e(ruta+nombre, "Error al escribir el fichero");
        }
    }
    private void LanzarAlerta(String Titulo,String Mensaje){
        new AlertDialog.Builder(this)
                .setTitle(Titulo)
                .setMessage(Mensaje)
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                    }
                }).create().show();
    }

    private void Confirmar(String Titulo,final String valor,final int Evento){
        final EditText entrada = new EditText(this);
        entrada.setText(valor);
        new AlertDialog.Builder(this)
                .setTitle(Titulo)
                .setView(entrada)
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        switch(Evento){
                            case 6:
                                clave=entrada.getText().toString();
                                //	  if(clave==valor){editText1.setSelection(editText1.getSelectionStart()+1);}
                                int pos=editText1.getText().toString().indexOf(clave,0);
                                if(pos>=0)editText1.setSelection(pos);
                                break;
                            case 5:
                                try{
                                    int Pos=	Integer.valueOf(entrada.getText().toString());
                                    Log.e("Pos",String.valueOf(Pos));
                                    if(Pos>=0&&Pos<=editText1.length())
                                        editText1.setSelection(Pos);
                                }catch(Exception e){}
                                break;
                        }
                    }
                }).create().show();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            //   guardarConfiguracion();
        } catch (Exception e) {e.printStackTrace();}
    }
    @Override
    public void onBackPressed() {
     finish();
    }

}


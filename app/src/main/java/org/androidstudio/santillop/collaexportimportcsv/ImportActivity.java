package org.androidstudio.santillop.collaexportimportcsv;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.opencsv.CSVReader;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by santi on 8/02/19.
 */

public class ImportActivity extends AppCompatActivity {
    TextView lblFitxer;
    EditText txfRuta;
    List<String[]> data;
    Button btnImportar;
    ArrayList<Membre> membres;
    ArrayList<Quota> quotes;
    String CSV_PATH;
    boolean ENCONTRAT = true;
    TextView textView;
    Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import);

        Toolbar toolbar = (Toolbar) findViewById(R.id.appbar);
        toolbar.setTitle("Importar Base de Dades");

        lblFitxer = (TextView) findViewById(R.id.lblFitxer);
        txfRuta = (EditText) findViewById(R.id.txfFitxer);
        btnImportar = (Button) findViewById(R.id.btnImportar);
        textView = (TextView) findViewById(R.id.textView);
        Spinner spinner = (Spinner) findViewById(R.id.spinner);

        txfRuta.setFocusable(false);
        lblFitxer.setText("Posa la ruta del fitxer a importar en el camp de text. Ací tens una llista desplegable per sel·leccionar la ruta del fitxer " +
                "en el cas de que es trobe en l'arrel de l'emmagatzematge intern.");
        String[] ruta = {"- -Tria la una opció --","/storage/emulated/0/fitxer.csv"};
        ArrayAdapter aa = new ArrayAdapter(this,android.R.layout.simple_spinner_item,ruta);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(aa);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(i!=0){
                    txfRuta.setText((String) adapterView.getItemAtPosition(i));
                } else{
                    txfRuta.setText("");
                    txfRuta.setHint("Format /storage/emulated/0/fitxer.csv");
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        mostraInstruccions();

        btnImportar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CSV_PATH = txfRuta.getText().toString();
                //Assignem la llista llegida del fitxer
                data = readCsv(CSV_PATH);
                Toast.makeText(ImportActivity.this, CSV_PATH, Toast.LENGTH_SHORT).show();
                boolean semaf;
                semaf = validaDadesFitxer();
                if((ENCONTRAT) && (semaf)){
                    //Esborrem totes les dades
                    borrarDades("Colla.sqlite");
                    //Declarem la llista de membres
                    membres = new ArrayList<>();
                    //Llegir les files i plenar la llista de membres
                    plenaLlistaMembres();
                    //Recorrem la llista de membres i les afegim a la BBDD
                    inserirMembres("Colla.sqlite");
                    //Consultem el ID per afegir el camp a la llista de membres
                    consultaID("Colla.sqlite");
                    //Cridem funcio per inserir les quotes de cada membre
                    inserirQuotesMembre("Colla.sqlite");

                    //Dialeg importar
                    final ProgressDialog progressDialog = new ProgressDialog(ImportActivity.this);
                    progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    progressDialog.setTitle("Important la Base de dades");
                    progressDialog.setMessage("Espereu si us plau...");

                    progressDialog.show();

                    new Thread(new Runnable() {
                        public void run() {
                            try {
                                //---simulate doing something lengthy---
                                Thread.sleep(5000);
                                //---dismiss the dialog---
                                progressDialog.dismiss();

                                ImportActivity.this.runOnUiThread(new Runnable() {
                                    public void run() {
                                        try {
                                            Thread.sleep(500);
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                        Toast.makeText(ImportActivity.this, "Les dades s'han importat amb èxit!", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(getApplicationContext(), TablaActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                });
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();

                    //Llegir la llista
                    //mostraTot();
                }

            }
        });

    }

    public final List<String[]> readCsv(/*Context context,*/ String csv_path) {
        List<String[]> questionList = new ArrayList<String[]>();
        //AssetManager assetManager = context.getAssets();

        try {
            /*InputStream csvStream = assetManager.open(csv_path);
            InputStreamReader csvStreamReader = new InputStreamReader(csvStream);
            CSVReader csvReader = new CSVReader(csvStreamReader);*/
            CSVReader csvReader = new CSVReader(new FileReader(csv_path));

            String[] line;

            // throw away the header
            csvReader.readNext();

            while ((line = csvReader.readNext()) != null) {
                questionList.add(line);
            }


        } catch (FileNotFoundException f){
            f.printStackTrace();
            ENCONTRAT = false;
            Toast.makeText(ImportActivity.this, "ERROR! El fitxer no existeix. " + ENCONTRAT, Toast.LENGTH_SHORT).show();

        } catch (IOException e) {
            e.printStackTrace();
            ENCONTRAT = false;
            Toast.makeText(ImportActivity.this, "ERROR! El fitxer no existeix. " + ENCONTRAT, Toast.LENGTH_SHORT).show();
        }
        return questionList;
    }

    //Funcio per esborrar la BBDD
    public void borrarDades(String BBDD) {
        //Connectem a la BBDD en mode escriptura
        SQLiteGestor bdg = null;
        bdg = new SQLiteGestor(this, BBDD, null, 1);
        SQLiteDatabase bd = bdg.getWritableDatabase();
        bd.execSQL("DELETE FROM membre;");
        bd.execSQL("DELETE FROM quota;");
        bd.close();
        bdg.close();
    }

    //Funció per plenar la BBDD
    public void inserirMembres(String BBDD) {
        //Connectem a la BBDD en mode escriptura
        SQLiteGestor bdg = null;
        bdg = new SQLiteGestor(this, BBDD, null, 1);
        SQLiteDatabase bd = bdg.getWritableDatabase();

        //recorrem la llista de membres
        for (int i = 0; i < membres.size(); i++) {
            //Afegim en vbles els camps dels membres
            String nom = membres.get(i).getNom();
            String cognoms = membres.get(i).getCognoms();
            String correu = membres.get(i).getCorreu();
            String telefon = membres.get(i).getTelefon();

            //Inserim els membres a la base de dades
            bd.execSQL("INSERT INTO `membre` (`nom`,`cognoms`,`correu`,`telefon`) VALUES ('" + nom + "', '" + cognoms + "', '"
                    + correu + "', '" + telefon + "');");

        }

        bd.close();
        bdg.close();
    }
    //Fem una consulta de IDS per afegir-lo a una llista de membres
    public void consultaID(String BBDD){

        //Connectem a la BBDD en mode lectura
        SQLiteGestor bdg = null;
        bdg = new SQLiteGestor(this, BBDD, null, 1);
        SQLiteDatabase bd = bdg.getReadableDatabase();

        //Fem la consulta per el ID
        Cursor rs = bd.rawQuery("SELECT ID FROM membre;", null);
        int cont = 0;
        while (rs.moveToNext()){
            int ID = rs.getInt(0);
            //Asignem a la llista el ID
            membres.get(cont).setID(ID);
            cont++;
        }

        Log.d("LONG MEM",""+membres.size());
        rs.close();
        bd.close();
        bdg.close();
    }

    public void inserirQuotesMembre(String BBDD){
        //Connectem a la BBDD en mode escriptura
        SQLiteGestor bdg = null;
        bdg = new SQLiteGestor(this, BBDD, null, 1);
        SQLiteDatabase bd = bdg.getWritableDatabase();

        //Comptador membres
        int i=0;
        //Recorrem els membres
        for(Membre m: membres){
            //Declarem les quotes de cada membre
            ArrayList <Quota> quotes = membres.get(i).getQuotes();
            for(int j=0; j<quotes.size(); j++){
                //Inserim a la BBDD les quotes de cada membre
                bd.execSQL("INSERT INTO quota (`mes`,`quantitat`, `id_membre`) VALUES(" + quotes.get(j).getMes() + ", " + quotes.get(j).getQuantitat()
                            + ", " + membres.get(i).getID() + ");");
            }

            i++;
        }
        bd.close();
        bdg.close();

    }

    //Llegim el fitxer i plenem la llista de membres
    public void plenaLlistaMembres() {
        //recorrem la List
        for (int i = 0; i < data.size(); i++) {
            //Cada fila la afegim a una llista de cadenes
            String[] line = data.get(i);

            //assignem en vbles les columnes de cada fila
            //Noms i cognoms
            String nom = line[0];
            String cognoms = line[1];
            String correu = line[2];
            String telefon = line[3];


            quotes = new ArrayList<>();
            //Recorrem des de la posicio 5 fins a la fi
            int mes = 0;
            for (int j=4; j<line.length; j++){
                mes++;
                quotes.add(new Quota(mes,Integer.parseInt(line[j])));
            }
            //Sara Tirado Polo
            membres.add(new Membre(nom, cognoms, correu, telefon, quotes));

        }

    }
    //Llegim el fitxer i validem les dades
    public boolean validaDadesFitxer(){
        boolean semaf = true;
        //recorrem la List
        for (int i = 0; i < data.size(); i++) {
            //Cada fila la afegim a una llista de cadenes
            String[] line = data.get(i);
            Log.d("QUOTES", line[0] + " YA");
            Log.d("LONG LINE", ""+line.length);
            //Comprovem que cada fila te una longitud de 16
            if((line.length > 16) && (semaf)){
                semaf = false;
                Toast.makeText(getApplicationContext(), "ERROR! El fitxer ha de tindre 16 columnes.",Toast.LENGTH_SHORT).show();
            }
            //Comprovem que el camp nom i cognoms esta ple
            if((line[0].isEmpty()) && (semaf)){
                semaf = false;
                Toast.makeText(getApplicationContext(), "ERROR! Camp nom obligatori.",Toast.LENGTH_SHORT).show();
            }
            if((line[1].isEmpty()) && (semaf)){
                semaf = false;
                Toast.makeText(getApplicationContext(), "ERROR! Camp cognoms obligatori.",Toast.LENGTH_SHORT).show();
            }
            //Comprovem el format del correu
            if((!line[2].isEmpty()) && (checkEmail(line[2]) == false) && (semaf)){
                semaf = false;  //assignem la vble falsa
                Toast.makeText(getApplicationContext(), "ERROR! Format del correu erroni.", Toast.LENGTH_SHORT).show();
            }
            //Comprovem el format del telefon
            if ((!line[3].isEmpty()) && (line[3].length() != 9)  && (semaf == true)){
                //Si longitud caracters es 9
                if((line[3].length() == 9) && (semaf)){
                    //Comprovem que els caracters son digits
                    char carTel;
                    for(int j=0; j<line[3].length(); j++){
                        carTel = line[3].charAt(j);
                        //Si hi ha un caracter que no es digit posem la vble false
                        if(!Character.isDigit(carTel)){
                            semaf = false;
                        }
                    }
                    //Si hi ha un caracter no digit posem el missatge de error
                    if(!semaf){
                        semaf = false;
                        Toast.makeText(getApplicationContext(), "ERROR en telefon! Has d'introduïr dígits.", Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    semaf = false;
                    Toast.makeText(getApplicationContext(), "ERROR en telefon! El nombre de dígits té que ser 9.", Toast.LENGTH_SHORT).show();
                }

            }
            for(int j=0; j<line.length; j++){
                //Controlem les quotes
                if(j>=4){
                    //Si les quotes estan buides
                    if((line[j].isEmpty()) && (semaf)){
                        semaf = false;
                        Toast.makeText(getApplicationContext(), "ERROR en les quotes! Els camps son obligatoris.", Toast.LENGTH_SHORT).show();
                    }
                    //Si els camps contenen caracters no digits
                    if((!line[j].isEmpty()) && (semaf)){
                        for(int z=0; z<line[j].length(); z++){
                            char carQ = line[j].charAt(z);
                            if(!Character.isDigit(carQ)){
                                semaf = false;
                            }
                        }
                        if(!semaf){
                            Toast.makeText(getApplicationContext(), "ERROR en les quotes! Has d'introduïr dígits.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

            }
        }
        return semaf;
    }
    /*public void mostraTot(){
        for(int i=0; i<membres.size(); i++){
            textView.append("ID: " + membres.get(i).getID());
            textView.append("\nNom: " + membres.get(i).getNom());
            textView.append("\nCognoms: " + membres.get(i).getCognoms());
            textView.append("\nCorreu: " + membres.get(i).getCorreu());
            textView.append("\nTelefon: " + membres.get(i).getTelefon());
            textView.append("\nQUOTES\n");
            for(int j=0; j<membres.get(i).getQuotes().size(); j++){
                textView.append("" + membres.get(i).getQuotes().get(j).getMes() + "-" + membres.get(i).getQuotes().get(j).getQuantitat() + "\n");
            }
            textView.append("\n");
        }
    }*/
    public void mostraInstruccions(){
        textView.append("Per veure on es troba el fitxer ves a l'explorador d'arxius i sel·lecciona" +
                " l'opció detalls o propietats mantenint-lo premut per veure " +
                "la ruta del fitxer i copia-la al camp de text.");

    }
    //Funció per al format del correu
    public static boolean checkEmail (String email) {

        // Establecer el patron
        Pattern p = Pattern.compile("[-\\w\\.]+@[\\.\\w]+\\.\\w+");

        // Asociar el string al patron
        Matcher m = p.matcher(email);

        // Comprobar si encaja
        return m.matches();

    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if (keyCode == event.KEYCODE_BACK) {
            Intent intent = new Intent(getApplicationContext(), TablaActivity.class);
            startActivity(intent);
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

}

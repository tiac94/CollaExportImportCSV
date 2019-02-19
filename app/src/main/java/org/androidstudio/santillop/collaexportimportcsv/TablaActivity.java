package org.androidstudio.santillop.collaexportimportcsv;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.Toast;

import com.opencsv.CSVWriter;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by santi on 12/07/17.
 */

public class TablaActivity extends AppCompatActivity {
    ArrayList <Membre> membres;
    ArrayList <Quota> quotes;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tabla);
        Toolbar toolbar = (Toolbar) findViewById(R.id.appbar);
        toolbar.setTitle("Deutes");
        setSupportActionBar(toolbar);
        Tabla tabla = new Tabla(this, (TableLayout) findViewById(R.id.tabla));
        //Agreguem les capçaleres
        tabla.agregarCabecera(R.array.cabecera_tabla);
        //Fem la consulta a la base de dades i planem la llista de membres i quotes
        plenaLlistaMembres();
        //Mostrem les dades per pantalla amb una taula
        mostraDadesTaula(tabla);

        Button b4 = (Button) findViewById(R.id.export);
        b4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                exportar();
            }
        });
    }

    //Funcio per plenar la llista de membres
    public void plenaLlistaMembres(){
        membres = new ArrayList<>();
        SQLiteGestor bdg = null;
        bdg = new SQLiteGestor(this, "Colla.sqlite", null, 1);
        SQLiteDatabase bd = bdg.getReadableDatabase();
        //Fem la consulta per traure el nom i cognoms dels membres
        Cursor rs = bd.rawQuery("SELECT * FROM membre ORDER BY nom, cognoms", null);

        while (rs.moveToNext()) {
            //Guardem en variables el nom i cognoms dels membres
            String nom = rs.getString(1);
            String cognoms = rs.getString(2);
            String correu = rs.getString(3);
            String telefon = rs.getString(4);
            //Fem la consulta per traure les quotes de cada membre
            Cursor rs2 = bd.rawQuery("SELECT mes, quantitat FROM membre, quota WHERE membre.ID = id_membre" +
                    " AND nom = '" + nom + "' AND cognoms = '" + cognoms + "'", null);
            //Declarem el llistat de les quotes
            quotes = new ArrayList<Quota>();
            while (rs2.moveToNext()) {
                //Guardem en variables el mes i quantitat de les quotes
                int mes = rs2.getInt(0);
                int quantitat = rs2.getInt(1);
                //Afegim al llistat el mes i la quantitat de les quotes
                quotes.add(new Quota(mes, quantitat));
            }
            //Afegim al llistat de membres el nom, cognoms i quotes de cada membre
            membres.add(new Membre(nom, cognoms, correu, telefon, quotes));
            rs2.close();
        }

        //Tanquem la BBDD
        rs.close();
        bd.close();
        bdg.close();
    }

    public void mostraDadesTaula(Tabla tabla){
        //Recorrem els membres i les seues quotes
        for (int i = 0; i < membres.size(); i++) {
            //Declarem una llista per guardar els noms dels membres i les seues quotes en una fila
            ArrayList<String> elementos = new ArrayList<String>();
            //Afegim a la llista els noms i cognoms dels membres
            elementos.add(membres.get(i).getNom() + " " + membres.get(i).getCognoms());
            for (int j=0; j<membres.get(i).getQuotes().size(); j++){
                //Afegim a la llista la quantitat de quotes que han pagat en cada mes
                elementos.add(""+membres.get(i).getQuotes().get(j).getQuantitat());

            }
            //cridem a aquesta funcio per afegir la llista en una fila de la taula
            tabla.agregarFilaTabla(elementos);
        }
    }
    //Funció per exportar les dades a una fulla de càlcul
    private void exportar(){
        //Declarem un objecte de la clase Calendar per traure l'any i mes actual
        Calendar c1 = Calendar.getInstance();
        String any = String.valueOf(c1.get(Calendar.YEAR));
        String mes = String.valueOf(c1.get(Calendar.MONTH)+1);
        //Nom del fitxer de la fulla de càlcul
        String csv = android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + "/quotes" + any + mes + ".csv";
        //String csv = getFilesDir().getAbsolutePath() + "/prova.csv";
        //t.setText(csv);
        //Creem un objecte de la clase CSVWriter
        CSVWriter writer = null;
        try {
            //Declarem l'objecte de la clase CSVWriter
            writer = new CSVWriter(new FileWriter(csv));
            //Declarem una llista de llistes de cadenes
            List<String[]> data = new ArrayList<String[]>();
            //Afegim a la llista la capçalera de la taula
            data.add(new String[] {"Nom", "Cognoms", "Correu", "Telefon", "Gener", "Febrer", "Març", "Abril", "Maig", "Juny", "Juliol",
                    "Agost", "Septembre", "Octubre", "Novembre", "Desembre"});

            //Escrivim els noms
            for (int i = 0; i < membres.size(); i++) {
                //data.add(new String[] {membres.get(i).getNom() + " " + membres.get(i).getCognoms()});
                //Creem un ArrayList de cadenes. La longitud és el nº de membres + 3
                String [] arrCad = new String[membres.get(i).getQuotes().size()+4];
                //En la primera columna posarem els noms i cognoms
                arrCad[0] = membres.get(i).getNom();
                arrCad[1] = membres.get(i).getCognoms();
                arrCad[2] = membres.get(i).getCorreu();
                arrCad[3] = membres.get(i).getTelefon();
                //Bucle per recorrer les quotes de cada membre
                for (int j = 0; j < membres.get(i).getQuotes().size(); j++) {
                    //En les altres columnes posarem les quotes que ha pagat cada membre per mesos
                    arrCad[j+4] = ""+membres.get(i).getQuotes().get(j).getQuantitat();
                }
                //Afegim la llista de cadena a tota una fila en l'arxiu
                data.add(arrCad);
            }
            //Amb aquest mètode assignem la llista a l'objecte de la classe CSVWriter
            writer.writeAll(data);
            //Tanquem l'objecte
            writer.close();
            //Ací posarem un diàleg
            dialegHoritzontal(csv);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void dialegHoritzontal(final String csv){
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setIcon(R.drawable.ic_assignment_returned_black_24dp);
        progressDialog.setTitle("Descarregant el fitxer...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);

        progressDialog.show();
        progressDialog.setProgress(0);
        new Thread(new Runnable(){
            public void run(){
                for (int i=1; i<=15; i++) {
                    try {
                        //---simulate doing something lengthy---
                        Thread.sleep(500);
                        //---update the dialog---
                        progressDialog.incrementProgressBy((int)(100/10));


                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                progressDialog.dismiss();
                TablaActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        //Missatges
                        String cad2 = "Les dades s'han exportat amb èxit!";
                        String cad3 = "El fitxer s'ha desat en" + csv;

                        Toast.makeText(getApplicationContext(), cad2, Toast.LENGTH_SHORT).show();
                        Toast.makeText(getApplicationContext(), cad3, Toast.LENGTH_LONG).show();

                    }
                });
            }
        }).start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.principal, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_import)
        {
            //Anem a l'activitat DadesContactesActivity
            Intent intent = new Intent(TablaActivity.this, ImportActivity.class);
            startActivity(intent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

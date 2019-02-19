package org.androidstudio.santillop.collaexportimportcsv;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class SQLiteGestor extends SQLiteOpenHelper {
    public SQLiteGestor(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public SQLiteGestor(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String sql = "CREATE  TABLE membre ("
                + "ID INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL ,"
                + "nom TEXT NOT NULL," +
                " cognoms TEXT NOT NULL," +
                " correu TEXT, telefon TEXT)";

        String sql2 = "CREATE TABLE \"quota\" (" +
                "\"ID\" INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL ," +
                " \"mes\" INTEGER NOT NULL ," +
                " \"quantitat\" INTEGER NOT NULL , " +
                "\"pagat\" BOOL NOT NULL  DEFAULT FALSE," +
                " id_membre INTEGER NOT NULL," +
                " FOREIGN KEY (id_membre) REFERENCES membre(ID)  )";


        sqLiteDatabase.execSQL(sql);
        sqLiteDatabase.execSQL(sql2);
        //Inserim les dades dels membres
        sqLiteDatabase.execSQL("INSERT INTO `membre`(`ID`,`nom`,`cognoms`,`correu`, `telefon`) VALUES (" + 1
                + ",'Andreu','Boltanya Pitarch','mailo@gmail.com', '964012345');");
        sqLiteDatabase.execSQL("INSERT INTO `membre`(`ID`,`nom`,`cognoms`,`correu`, `telefon`) VALUES (" + 2
                + ",'Pedro','Amor Beltran','mailo2@gmail.com', '964123456');");
        sqLiteDatabase.execSQL("INSERT INTO `membre`(`ID`,`nom`,`cognoms`,`correu`, `telefon`) VALUES (" + 3
                + ",'Arcadi','Montes Gasulla','mailo3@gmail.com', '964234567');");
        sqLiteDatabase.execSQL("INSERT INTO `membre`(`ID`,`nom`,`cognoms`,`correu`, `telefon`) VALUES (" + 4
                + ",'Pep','Gimeno Bernat','mailo4@gmail.com', '964345678');");
        sqLiteDatabase.execSQL("INSERT INTO `membre`(`ID`,`nom`,`cognoms`,`correu`, `telefon`) VALUES (" + 5
                + ",'Pedro','Garrido Delas','mailo5@gmail.com', '964456789');");
        sqLiteDatabase.execSQL("INSERT INTO `membre`(`ID`,`nom`,`cognoms`,`correu`, `telefon`) VALUES (" + 6
                + ",'Paco','Llopis','mailo6@gmail.com', '964567890');");
        sqLiteDatabase.execSQL("INSERT INTO `membre`(`ID`,`nom`,`cognoms`,`correu`, `telefon`) VALUES (" + 7
                + ",'Dani','Polo Segarra','mailo7@gmail.com', '964678901');");
        sqLiteDatabase.execSQL("INSERT INTO `membre`(`ID`,`nom`,`cognoms`,`correu`, `telefon`) VALUES (" + 8
                + ",'Daniela','Polo Segarra','mailo8@gmail.com', '964789012');");
        sqLiteDatabase.execSQL("INSERT INTO `membre`(`ID`,`nom`,`cognoms`,`correu`, `telefon`) VALUES (" + 9
                + ",'Arantxa','Pulido Benafeli','mailo9@gmail.com', '964890123');");
        sqLiteDatabase.execSQL("INSERT INTO `membre`(`ID`,`nom`,`cognoms`,`correu`, `telefon`) VALUES (" + 10
                + ",'Jorge','Roig Mendoza','mailo10@gmail.com', '964901234');");
        sqLiteDatabase.execSQL("INSERT INTO `membre`(`ID`,`nom`,`cognoms`,`correu`, `telefon`) VALUES (" + 11
                + ",'Sara','Tirado Polo','mailo11@gmail.com', '964012345');");
        sqLiteDatabase.execSQL("INSERT INTO `membre`(`ID`,`nom`,`cognoms`,`correu`, `telefon`) VALUES (" + 12
                + ",'Bartolo','Bartali Polo','mailo12@gmail.com', '964098765');");
        sqLiteDatabase.execSQL("INSERT INTO `membre`(`ID`,`nom`,`cognoms`,`correu`, `telefon`) VALUES (" + 13
                + ",'Diego','Polo Segarra','mailo13@gmail.com', '964987654');");

        //Inserim les quotes dels membres
        String insertQuotes = "";
        int idQuota = 0;
        //Recorrem els ids dels membres
        for(int id=0; id<13; id++){
            //Recorrem els mesos i es el nombre del mes
            for(int i=0; i<12; i++){
                Log.d("IDquota", ""+(idQuota+1));
                idQuota += 1;
                sqLiteDatabase.execSQL("INSERT INTO `quota`(`ID`,`mes`,`quantitat`,`id_membre`) VALUES (" + idQuota
                    + "," + (i+1) + ",0," + (id+1) + "); ");
            }
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        String sql = "DROP TABLE quota";
        String sql2 = "DROP TABLE membre";
        sqLiteDatabase.execSQL(sql);
        sqLiteDatabase.execSQL(sql2);
    }
}
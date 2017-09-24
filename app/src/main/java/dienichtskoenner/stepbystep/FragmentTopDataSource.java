package dienichtskoenner.stepbystep;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Created by thomasslawik on 24.09.17.
 */

public class FragmentTopDataSource {

    private static final String LOG_TAG = FragmentTop.class.getSimpleName();

    private SQLiteDatabase database;
    private FragmentTopDbHelper dbHelper;


    public FragmentTopDataSource(Context context){
        Log.d(LOG_TAG,"Datasource erzeugt jetzt den dbHelper.");
        dbHelper = new FragmentTopDbHelper(context);
    }

    public void open() {
        Log.d(LOG_TAG, "Eine Referenz auf die Datenbank wird jetzt angefragt.");
        database = dbHelper.getWritableDatabase();
        Log.d(LOG_TAG, " Datenbank-Referenz erhalten. Pfad zur Datenbank: "+ database.getPath());
    }

    public void close(){
        dbHelper.close();
        Log.d(LOG_TAG, "Datenbank mit Hilfe des DbHelpers geschlossen.");
    }













}

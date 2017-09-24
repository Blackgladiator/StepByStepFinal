package dienichtskoenner.stepbystep;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by thomasslawik on 24.09.17.
 */

public class FragmentTopDbHelper extends SQLiteOpenHelper {

    private static final String LOG_TAG =  FragmentTop.class.getSimpleName();



    public FragmentTopDbHelper(Context context) {
        super(context, Database.DB_NAME, null, Database.DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}

package dienichtskoenner.stepbystep;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Pair;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by thomasslawik on 17.09.17.
 */

public class Database extends SQLiteOpenHelper{

    private static Database sInstance;

    public final static String DB_NAME = "steps";
    public final static int DB_VERSION = 1;



    public Database(final Context context) {
        super(context, DB_NAME, null, DB_VERSION);

    }

    public static synchronized Database getInstance(Context context){
        if ( sInstance == null){
            sInstance = new Database(context.getApplicationContext());
        }
        return sInstance;
    }

    @Override
    public void onCreate(final SQLiteDatabase db) {

        db.execSQL("CREATE TABLE " + DB_NAME + " (date INTEGER, steps INTEGER)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion == 0){
            db.execSQL("CREATE TABLE " + DB_NAME + "1 (date INTEGER, steps INTEGER)");
            db.execSQL("INSERT INTO " + DB_NAME + "1 (date, steps) SELECT date, steps FROM "+
            DB_NAME);
            db.execSQL("DROP TABLE " + DB_NAME);
            db.execSQL("ALTER TABLE " + DB_NAME + "1 RENAME TO " + DB_NAME + "");
        }

    }

    public Cursor query(final String[] columns, final String selection,
                        final String[] selectionArgs, final String groupBy, final String having,
                        final String orderBy, final String limit){
        return getReadableDatabase()
                .query(DB_NAME,columns,selection,selectionArgs,groupBy,having,orderBy,limit);
    }

    public void insertNewDay(long date, int steps){

        getWritableDatabase().beginTransaction();
        try {
            Cursor c = getReadableDatabase().query(DB_NAME, new String[]{"date"}, "date = ?",
                    new String[]{String.valueOf(date)},null,null,null);
            if(c.getCount() == 0 && steps >=0){

                //yesterdays steps
                addToLastEntry(steps);


                //todays steps
                ContentValues contentValues = new ContentValues();
                contentValues.put("date", date);
                contentValues.put("steps", -steps);
                getWritableDatabase().insert(DB_NAME,null, contentValues);
            }
            c.close();
            getWritableDatabase().setTransactionSuccessful();
        }finally {
            getWritableDatabase().endTransaction();
        }
    }


    public void addToLastEntry(int steps) {
        if (steps > 0){
            getWritableDatabase().execSQL("UPDATE " + DB_NAME + " SET steps = steps + " + steps +
            " WHERE date = (SELECT MAX(date) FROM " + DB_NAME + ")");
        }
    }

    // Maximum count of steps walked in one day
     public int getRecordSteps(){
         Cursor c = getReadableDatabase()
                 .query(DB_NAME, new String[]{"MAX(steps)"}, "date > 0", null, null, null, null);
         c.moveToFirst();
         int rec = c.getInt(0);
         c.close();
         return rec;
     }

     // Maximum count of steps + date

    public Pair<Date, Integer> getRecordData() {
        Cursor c = getReadableDatabase()
                .query(DB_NAME, new String[]{"date, steps"}, "date > 0", null, null, null,
                        "steps DESC", "1");
        c.moveToFirst();
        Pair<Date, Integer> p = new Pair<Date, Integer>(new Date(c.getLong(0)), c.getInt(1));
        c.close();
        return p;
    }


    //get the number of steps for a date.

    public int getSteps(final long date) {
        Cursor c = getReadableDatabase()
                .query(DB_NAME, new String[]{"steps"}, "date = ?",
                        new String[]{String.valueOf(date)}, null, null, null);
        c.moveToFirst();
        int re;
        if (c.getCount() == 0) re = Integer.MIN_VALUE;
        else re = c.getInt(0);
        c.close();
        return re;
    }


    // get the last Entry from the newest Date.

    public List<Pair<Long, Integer>> getLastEntries(int num) {
        Cursor c = getReadableDatabase()
                .query(DB_NAME, new String[]{"date", "steps"}, "date > 0", null,null,null,
                        "date DESC", String.valueOf(num));
        int max = c.getCount();
        List<Pair<Long, Integer>> result = new ArrayList<>(max);
        if (c.moveToFirst()){
            do {
                result.add(new Pair<Long, Integer>(c.getLong(0),c.getInt(1)));
            }while (c.moveToNext());
        }
        return result;
    }


    //Saves 'steps since boot'

    public void saveCurrentSteps(int steps) {
        ContentValues values = new ContentValues();
        values.put("steps", steps);
        if(getWritableDatabase().update(DB_NAME, values, "date = -1", null) == 0){
            values.put("date", -1);
            getWritableDatabase().insert(DB_NAME,null,values);

        }

    }

    public int getCurrentSteps() {
        int re = getSteps(-1);
        return re == Integer.MIN_VALUE ? 0 : re;
    }







































}
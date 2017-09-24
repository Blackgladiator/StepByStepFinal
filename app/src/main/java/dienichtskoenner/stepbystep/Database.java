package dienichtskoenner.stepbystep;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.util.Pair;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by thomasslawik on 17.09.17.
 */

public class Database extends SQLiteOpenHelper{

    private static final String LOG_TAG =  FragmentTop.class.getSimpleName();

    private static Database sInstance;

    public final static String DB_NAME = "steps.db";
    public final static int DB_VERSION = 1;

    public static final String TABLE_STEPS = "steps";


    public static final String COLUMN_ID = "id";
    public static final String COLUMN_STEPS = "steps";
    public static final String COLUMN_DATE = "date";


    private SQLiteOpenHelper dbHelper;
    private SQLiteDatabase db;

    public static final String SQL_CREATE =
            "CREATE TABLE " + TABLE_STEPS +
                    "(" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_STEPS + " INTEGER NOT NULL, " +
                    COLUMN_DATE + " TEXT NOT NULL);";



    public Database(final Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        Log.d(LOG_TAG, "DbHelper hat die Datenbank : "+ getDatabaseName() + " erzeugt.");

    }

    public static synchronized Database getInstance(Context context){
        if ( sInstance == null){
            sInstance = new Database(context.getApplicationContext());
        }
        return sInstance;
    }

    @Override
    public void onCreate(final SQLiteDatabase db) {
        try {
            Log.d(LOG_TAG, "Die Tabelle wird mit SQL-Befehl: " + SQL_CREATE + "angelegt.");
            db.execSQL(SQL_CREATE);
        }
        catch (Exception e){
            Log.e(LOG_TAG, "Fehler beim Anlegen der Tabelle: " + e.getMessage());
        }

      //  db.execSQL("CREATE TABLE " + DB_NAME + " (date INTEGER, steps INTEGER)");

    }



    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {



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
                try {


                    //yesterdays steps
                    addToLastEntry(steps);


                    //todays steps
                    ContentValues contentValues = new ContentValues();
                    contentValues.put("date", date);
                    contentValues.put("steps", -steps);
                    getWritableDatabase().insert(DB_NAME, null, contentValues);
                }catch (NullPointerException d){

                }
            }
            c.close();
            if (BuildConfig.DEBUG){
                Logger.getLogger("insertDay " + date + " / " + steps);


            }
            getWritableDatabase().setTransactionSuccessful();
        }finally {
            getWritableDatabase().endTransaction();
        }
    }

    public long insertSteps(int steps){
        ContentValues insSteps = new ContentValues();
        insSteps.put(COLUMN_STEPS,steps);
        long insertID = db.insert(DB_NAME,null,insSteps);

        return insertID;
    }






    public void open() throws SQLException{
        try {
            Log.d(LOG_TAG, "Eine Referenz auf die Datenbank wird jetzt angefragt.");
            db = dbHelper.getWritableDatabase();

        }catch (SQLException e){
            db = dbHelper.getReadableDatabase();
        }

    }

    public void close(){

        db.close();
        Log.d(LOG_TAG, " Datenbank mit Hilfe des DbHelpers geschlossen.");
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


    public int getStepsSevenDaysAgo(final long date) {
        Cursor c = getReadableDatabase()
                .query(DB_NAME, new String[]{"steps"}, "date = ?",
                        new String[]{String.valueOf(date)}, null, null, null);
        c.moveToFirst();
        c.move(7);
        int re;
        if (c.getCount() == 0) re = Integer.MIN_VALUE;
        else re = c.getInt(0);
        c.close();
        return re;
    }

    public int getStepsSixDaysAgo(final long date) {
        Cursor c = getReadableDatabase()
                .query(DB_NAME, new String[]{"steps"}, "date = ?",
                        new String[]{String.valueOf(date)}, null, null, null);
        c.moveToFirst();
        c.move(6);
        int re;
        if (c.getCount() == 0) re = Integer.MIN_VALUE;
        else re = c.getInt(0);
        c.close();
        return re;
    }

    public int getStepsFiveDaysAgo(final long date) {
        Cursor c = getReadableDatabase()
                .query(DB_NAME, new String[]{"steps"}, "date = ?",
                        new String[]{String.valueOf(date)}, null, null, null);
        c.moveToFirst();
        c.move(5);
        int re;
        if (c.getCount() == 0) re = Integer.MIN_VALUE;
        else re = c.getInt(0);
        c.close();
        return re;
    }

    public int getStepsFourDaysAgo(final long date) {
        Cursor c = getReadableDatabase()
                .query(DB_NAME, new String[]{"steps"}, "date = ?",
                        new String[]{String.valueOf(date)}, null, null, null);
        c.moveToFirst();
        c.move(4);
        int re;
        if (c.getCount() == 0) re = Integer.MIN_VALUE;
        else re = c.getInt(0);
        c.close();
        return re;
    }

    public int getStepsThreeDaysAgo(final long date) {
        Cursor c = getReadableDatabase()
                .query(DB_NAME, new String[]{"steps"}, "date = ?",
                        new String[]{String.valueOf(date)}, null, null, null);
        c.moveToFirst();
        c.move(3);
        int re;
        if (c.getCount() == 0) re = Integer.MIN_VALUE;
        else re = c.getInt(0);
        c.close();
        return re;
    }

    public int getStepsTwoDaysAgo(final long date) {
        Cursor c = getReadableDatabase()
                .query(DB_NAME, new String[]{"steps"}, "date = ?",
                        new String[]{String.valueOf(date)}, null, null, null);
        c.moveToFirst();
        c.move(2);
        int re;
        if (c.getCount() == 0) re = Integer.MIN_VALUE;
        else re = c.getInt(0);
        c.close();
        return re;
    }


    public int getStepsOneDayAgo(final long date) {
        Cursor c = getReadableDatabase()
                .query(DB_NAME, new String[]{"steps"}, "date = ?",
                        new String[]{String.valueOf(date)}, null, null, null);
        c.moveToFirst();
        c.move(1);
        int re;
        if (c.getCount() == 0) re = Integer.MIN_VALUE;
        else re = c.getInt(0);
        c.close();
        return re;
    }


    //get the number of steps for a date.

    public int getSteps(final long date) {
        Cursor c = getReadableDatabase()
                .query(DB_NAME, new String[]{"steps"}, "date = ?",
                        new String[]{String.valueOf(date)}, null, null, null);
        c.moveToFirst();
        int re;
        if (c.getCount() == 0) {
            re = Integer.MIN_VALUE;
        } else{ re = c.getInt(0);
        c.close();}
        Log.d(LOG_TAG," Steps wurden gegetted");
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



    public class StepsDBOpenHelper extends  SQLiteOpenHelper{

        private static final String DATABASE_CREATE = "create table "
                + DB_NAME + " (" + COLUMN_ID
                + " integer primary key autoincrement, " + COLUMN_STEPS
                +" integer  steps, " + COLUMN_DATE + " text);";

        public StepsDBOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DATABASE_CREATE);

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }






































}
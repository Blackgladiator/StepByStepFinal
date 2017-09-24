package dienichtskoenner.stepbystep;


import android.app.AlarmManager;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Date;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;



/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentTop extends Fragment implements SensorEventListener {




    private Database db = new Database(getActivity());


    SensorManager sensorManager;
    private final static int MICROSECONDS_IN_HOUR = 60000000*60;

    private  int steps;
    private  int lastSaveSteps;
    private  long lastSaveTime;
    private  int stepsDisplay;
    private long startTime;



    boolean running = false;
    private TextView tv_steps;
    private TextView distanceValue;
    private TextView tempo;
    private TextView calories;
    private Date date ;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_top, container, false);
    }


    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tv_steps = (TextView) getView().findViewById(R.id.currentDay);
        distanceValue = (TextView) getView().findViewById(R.id.distanceValue);
        tempo = (TextView) getView().findViewById(R.id.tempoValue);
        calories = (TextView) getView().findViewById(R.id.caloriesValue);

        sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);


    }

    @Override
    public void onStart() {
        super.onStart();
        running = true;
        startTime= (long) System.currentTimeMillis()/1000/60;
        lastSaveSteps= steps;

       



    }




    @Override
    public void onDestroy() {
        super.onDestroy();
        running = false;
        db.saveCurrentSteps(steps);
        stepsDisplay = 0;

    }

    @Override
    public void onResume() {
        super.onResume();
        running = true;
        Sensor countSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        lastSaveTime = System.currentTimeMillis()+ AlarmManager.INTERVAL_DAY;
        startTime = System.currentTimeMillis();



        if (countSensor != null) {
            sensorManager.registerListener(this, countSensor, SensorManager.SENSOR_DELAY_UI);

        } else {
            Toast.makeText(getActivity(), "Sensor not found", Toast.LENGTH_SHORT).show();

        }
    }

    @Override
    public void onPause() {
        super.onPause();
        running = false;
        sensorManager.unregisterListener(this);
    }

    public void  checkMidnight(){
        try {
            if (System.currentTimeMillis() == getMidgnight()){

                stepsDisplay = steps-lastSaveSteps;
            }
        }catch (NullPointerException e){

        }

    }








    public long getMidgnight(){
        Calendar c = new GregorianCalendar();
        c.setTimeZone(TimeZone.getTimeZone("Europe/Berlin"));
        c.set(Calendar.HOUR_OF_DAY,0);
        c.set(Calendar.MINUTE,0);
        c.set(Calendar.SECOND,0);

        return c.getTimeInMillis();
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (running) {
            checkMidnight();


            steps= (int) sensorEvent.values[0]-lastSaveSteps;



            if (System.currentTimeMillis() == getMidgnight()){
                if (stepsDisplay != 0) {
                    db.insertNewDay(UtilC.getToday(), stepsDisplay);
                    startTime= (int) System.currentTimeMillis()/1000/60;
                }
               try {
                   db.saveCurrentSteps(stepsDisplay);
               }catch (NullPointerException e){

               }

                db.addToLastEntry(stepsDisplay);
                lastSaveSteps =(int) sensorEvent.values[0];
                steps =  (int) sensorEvent.values[0]- lastSaveSteps;
                stepsDisplay = steps;




            }else{
              stepsDisplay = steps;
                lastSaveSteps= steps;
            }

            tv_steps.setText(String.valueOf(stepsDisplay));
            distanceValue.setText(String.valueOf((int)(stepsDisplay * 75)/100) +" m");
            calories.setText(String.valueOf((int)(stepsDisplay* 0.06)+ " kcal"));
            tempo.setText(String.valueOf((int) ((((stepsDisplay*75))/((int)(60))))+"gaggl"));


        }


    }




    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}



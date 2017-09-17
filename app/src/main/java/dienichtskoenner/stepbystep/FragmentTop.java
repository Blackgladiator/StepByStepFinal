package dienichtskoenner.stepbystep;


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



/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentTop extends Fragment implements SensorEventListener {



    SensorManager sensorManager;
    private final static int MICROSECONDS_IN_ONE_MINUTE = 60000000;

    private static int steps;
    private static int lastSaveSteps;
    private static long lastSaveTime;

    boolean running = false;
    private TextView tv_steps;
    private TextView distanceValue;
    private TextView tempo;
    private TextView calories;

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
    public void onResume() {
        super.onResume();
        running = true;
        Sensor countSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
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
        //sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (running) {
            tv_steps.setText(String.valueOf(sensorEvent.values[0]));
            distanceValue.setText(String.valueOf((sensorEvent.values[0] * 75)/100) +" m");
            calories.setText(String.valueOf((sensorEvent.values[0]* 0.06)+ " kcal"));
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}






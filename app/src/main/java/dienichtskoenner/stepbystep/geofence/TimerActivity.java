package dienichtskoenner.stepbystep.geofence;


import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.TimeUnit;

import dienichtskoenner.stepbystep.Chart;
import dienichtskoenner.stepbystep.R;

/**
 * Created by c.lisik on 05.09.2017.
 * A high part of this Code was used by a tutorial from //http://www.androidtutorialshub.com/android-count-down-timer-tutorial/
 */

public class TimerActivity extends AppCompatActivity implements View.OnClickListener{


    private long timeCountInMilliSeconds = 1 * 60000;
    private long currentTime;


    private enum TimerStatus {
        STARTED,
        STOPPED
    }

    private TimerStatus timerStatus = TimerStatus.STOPPED;

    private ProgressBar progressBarCircle;
    private EditText editTextMinute;
    private TextView textViewTime;
    private ImageView imageViewReset;
    private ImageView imageViewStartStop;
    private CountDownTimer countDownTimer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.drawable.logo);

        initViews();
        initListeners();
        //getTime();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){

        switch (item.getItemId()){
            case R.id.action_bar_chart:
                Intent intent = new Intent(this, Chart.class);
                startActivity(intent);
                break;
            case R.id.action_bar_geofence:break;

            default:return super.onOptionsItemSelected(item);
        }

        return false;
    }

    private void initViews() {
        progressBarCircle = (ProgressBar) findViewById(R.id.progressBarCircle);
        editTextMinute = (EditText) findViewById(R.id.editTextMinute);
        textViewTime = (TextView) findViewById(R.id.textViewTime);
        imageViewReset = (ImageView) findViewById(R.id.imageViewReset);
        imageViewStartStop = (ImageView) findViewById(R.id.imageViewStartStop);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        //getTime();
    }
/*
    public void getTime(){

        TestClass testClass=new TestClass();

        if(editTextMinute==null){
            testClass.setTime("no time");

        }else{

            if(editTextMinute.getText().toString().isEmpty()){

                testClass.setTime("no time");

            }else{
                long startTime=(Long.parseLong(editTextMinute.getText().toString())*60000)-currentTime;
                String timeDifference=hmsTimeFormatter(startTime);

                testClass.setTime(timeDifference);
            }
        }

    }
*/
    private void initListeners() {
        imageViewReset.setOnClickListener(this);
        imageViewStartStop.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imageViewReset:
                reset();
                break;
            case R.id.imageViewStartStop:
                startStop();

                break;
        }
    }

    private void reset() {
        stopCountDownTimer();
        startCountDownTimer();
    }


    private void startStop() {
        if (timerStatus == TimerStatus.STOPPED) {

            setTimerValues();

            setProgressBarValues();
            imageViewReset.setVisibility(View.VISIBLE);
            imageViewStartStop.setImageResource(R.drawable.icon_stop);
            editTextMinute.setEnabled(false);
            timerStatus = TimerStatus.STARTED;

            startCountDownTimer();

        } else {
            imageViewReset.setVisibility(View.GONE);

            imageViewStartStop.setImageResource(R.drawable.icon_start);

            editTextMinute.setEnabled(true);

            timerStatus = TimerStatus.STOPPED;
            stopCountDownTimer();
        }
    }

    private void setTimerValues() {
        int time = 0;
        if (!editTextMinute.getText().toString().isEmpty()) {

            time = Integer.parseInt(editTextMinute.getText().toString().trim());
        } else {
            Toast.makeText(getApplicationContext(), "Please enter minutes", Toast.LENGTH_LONG).show();
        }

        timeCountInMilliSeconds = time * 60 * 1000;
    }

    private void startCountDownTimer() {

        countDownTimer = new CountDownTimer(timeCountInMilliSeconds, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

                textViewTime.setText(hmsTimeFormatter(millisUntilFinished));
                progressBarCircle.setProgress((int) (millisUntilFinished / 1000));

            }

            @Override
            public void onFinish() {

                textViewTime.setText(hmsTimeFormatter(timeCountInMilliSeconds));

                setProgressBarValues();

                imageViewReset.setVisibility(View.GONE);
                imageViewStartStop.setImageResource(R.drawable.icon_start);
                editTextMinute.setEnabled(true);
                timerStatus = TimerStatus.STOPPED;
            }

        }.start();
        countDownTimer.start();
    }

    private void stopCountDownTimer() {
        countDownTimer.cancel();
    }

    private void setProgressBarValues() {

        progressBarCircle.setMax((int) timeCountInMilliSeconds / 1000);
        progressBarCircle.setProgress((int) timeCountInMilliSeconds / 1000);
    }


    private String hmsTimeFormatter(long milliSeconds) {

        currentTime=milliSeconds;

        String hms = String.format("%02d:%02d:%02d",
                TimeUnit.MILLISECONDS.toHours(milliSeconds),
                TimeUnit.MILLISECONDS.toMinutes(milliSeconds) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(milliSeconds)),
                TimeUnit.MILLISECONDS.toSeconds(milliSeconds) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliSeconds)));

        return hms;
    }


}
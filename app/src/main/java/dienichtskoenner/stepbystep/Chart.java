package dienichtskoenner.stepbystep;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;

import java.util.ArrayList;

/**
 * Created by felixgross on 06.09.17.
 * {https://github.com/PhilJay/MPAndroidChart}
 */

public class Chart extends AppCompatActivity {

    private  Database db;

    BarChart barChart;

    final String[] bars = new String[] {"Today", "1 Day Ago", "2 Days Ago", "3 Days Ago", "4 Days Ago", "5 Days Ago", "6 Days Ago", "7 Days Ago"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.drawable.logo);

        initDatabase();

        barChart = (BarChart) findViewById(R.id.bargraph);

        initBarChart();
    }

    private void initDatabase() {
        db = new Database(this);
        db.open();
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
                break;
            case R.id.action_bar_location:
                break;
            case R.id.action_bar_geofence:
                break;

            default:
                return super.onOptionsItemSelected(item);
        }
        return false;
    }

    private void initBarChart(){

        ArrayList<BarEntry> barEntries = new ArrayList<>();
        try{
            barEntries.add(new BarEntry(0f, db.getSteps(UtilC.getToday())));
            barEntries.add(new BarEntry(1f, 32));
            barEntries.add(new BarEntry(2f, 43));
            barEntries.add(new BarEntry(3f, 43));
            barEntries.add(new BarEntry(4f, 34));
            barEntries.add(new BarEntry(5f, 54));
            barEntries.add(new BarEntry(6f, 54));
            barEntries.add(new BarEntry(7f, 65));
        }catch (NullPointerException e){

        }

        BarDataSet barDataSet = new BarDataSet (barEntries, "Steps per Day");

        ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();

        dataSets.add(barDataSet);

        ArrayList<String> theDays = new ArrayList<>();
        theDays.add("Today");
        theDays.add("1 Day Ago");
        theDays.add("2 Days Ago");
        theDays.add("3 Days Ago");
        theDays.add("4 Days Ago");
        theDays.add("5 Days Ago");
        theDays.add("6 Days Ago");
        theDays.add("7 Days Ago");

        BarData theData = new BarData(dataSets);
        theData.setBarWidth(0.9f);
        barChart.setData(theData);
        barChart.setFitBars(true);
        barChart.setTouchEnabled(true);
        barChart.setDragEnabled(true);
        barChart.setScaleEnabled(true);
        barChart.invalidate();
        barChart.getDescription().setEnabled(false);
        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextSize(0.9f);
        xAxis.setDrawAxisLine(true);
        xAxis.setDrawGridLines(false);
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return bars[(int) value];
            }
        });
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        db.close();
    }


}

package dienichtskoenner.stepbystep;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

/**
 * Created by felixgross on 06.09.17.
 */

public class Chart extends AppCompatActivity {

    BarChart barChart;
    ArrayList<String> dates;
    Random random;
    ArrayList<BarEntry> barEntries;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.drawable.logo);


        barChart = (BarChart) findViewById(R.id.barchart);

        createRandomBarGraph ("2016/05/05", "2016/06/01");

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

    public void createRandomBarGraph (String Date1, String Date2){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd");

        try {
            Date date1 = simpleDateFormat.parse(Date1);
            Date date2 = simpleDateFormat.parse(Date2);

            Calendar mDate1 = Calendar.getInstance();
            Calendar mDate2 = Calendar.getInstance();

            mDate1.clear();
            mDate2.clear();

            mDate1.setTime(date1);
            mDate2.setTime(date2);

            dates = new ArrayList<>();
            dates = getList(mDate1, mDate2);

            barEntries = new ArrayList<>();
            float max = 0f;
            float value = 0f;
            random = new Random();
            for (int j = 0; j < dates.size(); j++){
                max = 100f;
                value = random.nextFloat() * max;
                barEntries.add(new BarEntry(value, j));
            }

        }catch(ParseException e){
            e.printStackTrace();
        }
        BarDataSet barDataSet = new BarDataSet(barEntries, "Dates");

        ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();

        dataSets.add(barDataSet);

        BarData barData = new BarData(dataSets);
        barChart.setData(barData);
    }

    public ArrayList<String> getList (Calendar startDate, Calendar endDate){
        ArrayList<String> list = new ArrayList<>();
        while (startDate.compareTo(endDate) <= 0){
            list.add(getDate(startDate));
            startDate.add(Calendar.DAY_OF_MONTH, 1);
        }
        return list;
    }

    public String getDate (Calendar cld){
        String curDate = cld.get(Calendar.YEAR) + "/" + (cld.get(Calendar.MONTH) + 1) + "/" + cld.get(Calendar.DAY_OF_MONTH);

        try {
            Date date = new SimpleDateFormat("yyyy/MM/dd").parse(curDate);
            curDate = new SimpleDateFormat("yyyy/MM/dd").format(date);
        }catch(ParseException e){
            e.printStackTrace();
        }
        return curDate;
    }


}

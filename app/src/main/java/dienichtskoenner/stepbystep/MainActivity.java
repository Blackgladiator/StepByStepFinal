package dienichtskoenner.stepbystep;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import dienichtskoenner.stepbystep.geofence.GeoFenceMainActivity;

public class MainActivity extends AppCompatActivity {

    private FragmentManager manager = getSupportFragmentManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.drawable.logo);

        initFragments();
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
                Intent intent = new Intent(MainActivity.this, Chart.class);
                startActivity(intent);
                break;
            case R.id.action_bar_geofence:
                Intent intentGeoFence=new Intent(MainActivity.this, GeoFenceMainActivity.class);
                startActivity(intentGeoFence);break;

            default:return super.onOptionsItemSelected(item);
        }

        return false;
    }

    private void initFragments() {

        FragmentTop fragmentTop = new FragmentTop();
        manager.beginTransaction()
                .replace(R.id.layoutTop,fragmentTop,fragmentTop.getTag())
                .commit();

        FragmentBottom fragmentBottom=new FragmentBottom();
        manager.beginTransaction()
                .replace(R.id.layoutBottom,fragmentBottom,fragmentBottom.getTag())
                .commit();

        FragmentBottomNoInternet fragmentBottomNoInternet=new FragmentBottomNoInternet();
        manager.beginTransaction()
                .replace(R.id.layoutBottomNoInternet,fragmentBottomNoInternet,fragmentBottomNoInternet.getTag())
                .commit();
    }

}


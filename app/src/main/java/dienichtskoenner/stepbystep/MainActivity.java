package dienichtskoenner.stepbystep;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

    private FragmentManager manager = getSupportFragmentManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
        int id = item.getItemId();
        if (id == android.R.id.home){
            finish();
        }

        switch (item.getItemId()){
            case R.id.action_bar_chart:
                Intent intent = new Intent(MainActivity.this, Chart.class);
                startActivity(intent);
                break;
        }

        return super.onOptionsItemSelected(item);
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


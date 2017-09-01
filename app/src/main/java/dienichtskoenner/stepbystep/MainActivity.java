package dienichtskoenner.stepbystep;

import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

    private FragmentManager manager = getSupportFragmentManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setTitle("Step By Step");
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
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
        return super.onOptionsItemSelected(item);
    }

    private void initFragments() {

        FragmentTop fragmentTop = new FragmentTop();
        manager.beginTransaction()
                .replace(R.id.layoutTop,fragmentTop,fragmentTop.getTag())
                .commit();

        FragmentBottom fragmentBottom=new FragmentBottom();
        manager.beginTransaction()
                .replace(R.id.layoutBottom,fragmentBottom,fragmentTop.getTag())
                .commit();
    }

}


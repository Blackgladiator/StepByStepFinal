package dienichtskoenner.stepbystep;

import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    private FragmentManager manager = getSupportFragmentManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initFragments();

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


package dienichtskoenner.stepbystep;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentBottomNoInternet extends Fragment implements View.OnClickListener{

    ImageView refreshActivityIcon;
    TextView refreshText;

    public FragmentBottomNoInternet() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_bottom_no_internet, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        refreshActivityIcon=(ImageView) getView().findViewById(R.id.refreshIcon);
        refreshText=(TextView) getView().findViewById(R.id.refreshText);

        refreshActivityIcon.setOnClickListener(this);
        refreshText.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        getActivity().finish();
        startActivity(getActivity().getIntent());
    }
}

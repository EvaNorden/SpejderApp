package eva.spejder;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.eva.backend2.gameApi.model.Game;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import eva.spejderapp.R;
import eva.spejderapp.SingletonApp;


/**
 * A simple {@link Fragment} subclass.
 */
public class FindPostFrag extends Fragment implements View.OnClickListener {
    private View rod;
    private Button skip;
    private GoogleMap map;
    private MapView mapView;
    private LatLng HAMBURG = new LatLng(53.558, 9.927);
    private LatLng postPosition;
    private Game game;

    public FindPostFrag() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rod = inflater.inflate(R.layout.find_post_frag, container, false);

        skip = (Button)rod.findViewById(R.id.skip);
        skip.setOnClickListener(this);

        if (getArguments() != null) {
            int index = getArguments().getInt("gameIndex");
            game = SingletonApp.getData().scoutGames.get(index);
            int postNo = game.getPostCounter();
            postPosition = new LatLng(game.getPosts().get(postNo).getLatitude(), game.getPosts().get(postNo).getLongitude());
        }

        mapView = (MapView) rod.findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();//needed to get the map to display immediately

        MapsInitializer.initialize(getActivity());

        map = mapView.getMap();

        Marker post = map.addMarker(new MarkerOptions().position(postPosition)
                .title("" + game.getPosts().get(0).getNumber()));

        // Move the camera instantly to hamburg with a zoom of 15.
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(postPosition, 10));

        // Zoom in, animating the camera.
        map.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);

        return rod;
    }

    @Override
    public void onClick(View v) {
        if (v==skip){
            SolvePostFrag fragment = new SolvePostFrag();
            Bundle args = new Bundle();
            args.putInt("gameIndex",SingletonApp.getData().scoutGames.indexOf(game));
            fragment.setArguments(args);
            getFragmentManager().beginTransaction()
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .replace(R.id.main, fragment)
                    .addToBackStack(null)
                    .commit();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
}

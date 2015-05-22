package eva.spejder;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.eva.backend2.gameApi.model.Game;
import com.eva.backend2.gameApi.model.Post;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import eva.spejderapp.MainAct;
import eva.spejderapp.R;
import eva.spejderapp.SingletonApp;

/**
 * Fragment der viser en posts placering på en af fire måder
 * Geofence oprettet på posten
 */
public class FindPostFrag extends Fragment implements View.OnClickListener {
    private Button skip;
    private TextView tv;
    private MapView mapView;
    private LatLng postPosition;

    public FindPostFrag() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Game game = SingletonApp.getData().activeGame;
        Post post = game.getPosts().get(game.getPostCounter());
        postPosition = new LatLng(post.getLatitude(), post.getLongitude());

        if (savedInstanceState == null) {
            SingletonApp.geofenceHelper.googleApiClient.connect();
            SingletonApp.geofenceHelper.addGeofence(postPosition.latitude, postPosition.longitude);
        }

        View view; // To forskellige views alt efter hvilken måde placeringen skal vises på
        if (game.getDifficultyLevel() == 0 || game.getDifficultyLevel() > 3) {
            view = inflater.inflate(R.layout.find_post_frag_0, container, false);

            mapView = (MapView) view.findViewById(R.id.map);
            mapView.onCreate(savedInstanceState);
            mapView.onResume();//needed to get the map to display immediately

            MapsInitializer.initialize(getActivity());

            GoogleMap map = mapView.getMap();
            map.addMarker(new MarkerOptions().position(postPosition).title("" + post.getNumber()));
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(postPosition, 10));
            map.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);
        } else {
            view = inflater.inflate(R.layout.find_post_frag_1, container, false);

            tv = (TextView) view.findViewById(R.id.textView);

            String text = "";
            if (game.getDifficultyLevel() == 1) {
                text = post.getLocation();
            } else if (game.getDifficultyLevel() == 2) {
                text = "N " + postPosition.latitude + "\nE " + postPosition.longitude;
            } else if (game.getDifficultyLevel() == 3) {
                placementUpdate();
            }
            tv.setText(text);
        }

        skip = (Button) view.findViewById(R.id.skip);
        skip.setOnClickListener(this);
        skip.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(getActivity(), "Tryk her for at gå videre med løbet uden at finde posten (kun i test version)", Toast.LENGTH_LONG).show();
                return true;
            }
        });

        ((MainAct) getActivity()).getSupportActionBar().setTitle("Find Post " + post.getNumber());

        return view;
    }

    @Override
    public void onClick(View v) {
        if (v == skip) {
            SingletonApp.geofenceHelper.removeGeofencesHandler();
            SolvePostFrag fragment = new SolvePostFrag();
            getFragmentManager().beginTransaction()
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .replace(R.id.main, fragment)
                    .addToBackStack(null)
                    .commit();
        }
    }

    private void placementUpdate(){
        new AsyncTask<Void, Void, Void>() {
            ProgressDialog dialog = new ProgressDialog(getActivity());

            @Override
            protected void onPreExecute() {
                dialog.setIndeterminate(true); // drejende hjul
                dialog.setTitle("Venter på placering");
                dialog.setIcon(R.drawable.kfum_mork_trans1);
                dialog.setCancelable(false);
                dialog.show();
            }

            @Override
            protected Void doInBackground(Void... params) {
                while (SingletonApp.geofenceHelper.getCurrentLocation() == null); // venter på placering så retning og afstand kan udregnes
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                dialog.dismiss();
                Location l = SingletonApp.geofenceHelper.getCurrentLocation();
                float[] result = new float[2];
                Location.distanceBetween(l.getLatitude(), l.getLongitude(), postPosition.latitude, postPosition.longitude, result);
                tv.setText("Retning: " + String.format("%.1f", result[1]) + " grader\nAfstand: " + String.format("%.1f", result[0]) + " m");
            }
        }.execute();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mapView != null) mapView.onResume();

    }

    @Override
    public void onPause() {
        super.onPause();
        if (mapView != null) mapView.onPause();
    }

    @Override
    public void onDestroy() {
        SingletonApp.geofenceHelper.googleApiClient.disconnect();
        if (mapView != null) mapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        if (mapView != null) mapView.onLowMemory();
    }
}
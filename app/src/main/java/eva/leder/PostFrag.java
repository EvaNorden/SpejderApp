package eva.leder;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.eva.backend2.gameApi.model.Game;
import com.eva.backend2.gameApi.model.Post;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import eva.spejderapp.MainAct;
import eva.spejderapp.R;
import eva.spejderapp.SingletonApp;

/**
 * A simple {@link Fragment} subclass.
 */
public class PostFrag extends Fragment implements View.OnClickListener, View.OnLongClickListener {
    private EditText postName, postNr, postContent, postLocation;
    private Button savePost, erasePost;
    private Game game;
    private Post post;

    public PostFrag() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater i, ViewGroup container, Bundle savedInstanceState) {
        View view = i.inflate(R.layout.post_frag, container, false);

        postName = (EditText) view.findViewById(R.id.postName);
        postNr = (EditText) view.findViewById(R.id.postNumber);
        postContent = (EditText) view.findViewById(R.id.postDescription);
        postLocation = (EditText) view.findViewById(R.id.postLocation);
        savePost = (Button) view.findViewById(R.id.savePost);
        erasePost = (Button) view.findViewById(R.id.erasePost);

        savePost.setOnClickListener(this);
        erasePost.setOnClickListener(this);

        if (getArguments() != null) {
            int index = getArguments().getInt("gameIndex");
            game = SingletonApp.getData().games.get(index);
            if (getArguments().containsKey("postIndex")) {
                index = getArguments().getInt("postIndex");
                post = game.getPosts().get(index);

                postName.setText(post.getName());
                postNr.setText(post.getNumber().toString());
                postContent.setText(post.getDescription());
                postLocation.setText(post.getLocation());
            }
        }

        if (SingletonApp.prefs.getBoolean("need_help", true)) {
            savePost.setOnLongClickListener(this);
            erasePost.setOnLongClickListener(this);
        }

        ((MainAct) getActivity()).getSupportActionBar().setTitle("Post");

        return view;
    }

    @Override
    public boolean onLongClick(View v) {
        if (v == savePost)
            Toast.makeText(getActivity(), "Tryk her for at gemme en post i dit løb", Toast.LENGTH_LONG).show();
        else if (v == erasePost)
            Toast.makeText(getActivity(), "Tryk her for at slette en post fra dit løb", Toast.LENGTH_LONG).show();
        return true;
    }

    @Override
    public void onClick(View v) {
        if (v == savePost) {
            Boolean sameNo = false;
            for (Post p : game.getPosts()) {
                if (p.getNumber().equals(Integer.parseInt(postNr.getText().toString())) && p != post) {
                    Toast.makeText(getActivity(), "En post med samme nummer findes allerede", Toast.LENGTH_LONG).show();
                    sameNo = true;
                }
            }
            if (!sameNo) {
                String address = postLocation.getText().toString();
                getGPSFromAddress(address);
            }
        } else if (v == erasePost) {
            if (post != null) {
                game.getPosts().remove(post);
            }
            getActivity().onBackPressed();
        }
    }

    public void getGPSFromAddress(String locationAddress) {
        Geocoder geocoder = new Geocoder(getActivity().getApplicationContext(), Locale.getDefault());
        double lat = 0;
        double lng = 0;
        String foundAddress = "";
        try {
            List<Address> addressList = geocoder.getFromLocationName(locationAddress, 1);
            if (addressList != null && addressList.size() > 0) {
                Address address = addressList.get(0);
                lat = address.getLatitude();
                lng = address.getLongitude();
            }

            addressList = geocoder.getFromLocation(lat, lng, 1);
            if (addressList != null && addressList.size() > 0) {
                Address address = addressList.get(0);
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                    sb.append(address.getAddressLine(i)).append("\n");
                }
                sb.append(address.getCountryName());
                foundAddress = sb.toString();
            }
        } catch (IOException e) {
            Log.e("GEO", "Unable to connect to Geocoder", e);
            return;
        }

        AlertDialog.Builder builderSingle = new AlertDialog.Builder(getActivity());
        builderSingle.setTitle("Er lokationen korrekt?");
        builderSingle.setMessage(foundAddress);

        final String finalFoundAddress = foundAddress;
        final double finalLat = lat;
        final double finalLng = lng;
        builderSingle.setPositiveButton("Ja",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        System.out.println("Adresse korrekt");
                        if (post == null) {
                            post = new Post();
                            game.getPosts().add(post);
                        }
                        post.setName(postName.getText().toString());
                        post.setDescription(postContent.getText().toString());
                        post.setNumber(Integer.parseInt(postNr.getText().toString()));
                        post.setLocation(finalFoundAddress);
                        post.setLatitude(finalLat);
                        post.setLongitude(finalLng);
                        getActivity().onBackPressed();
                    }
                });

        builderSingle.setNegativeButton("Nej",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        System.out.println("Adresse forkert");
                        dialog.dismiss();
                    }
                });

        builderSingle.show();
    }
}
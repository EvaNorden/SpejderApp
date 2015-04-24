package eva.leder;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.eva.backend.gameApi.model.Game;
import com.eva.backend.gameApi.model.Post;

import eva.logik.GeocodingLocation;
import eva.logik.LocationAddress;
import eva.spejderapp.R;
import eva.spejderapp.SingletonApp;

/**
 * A simple {@link Fragment} subclass.
 */
public class PostFrag extends Fragment implements View.OnClickListener {
    private View rod;
    private EditText postName, postNr, postContent, postLocation;
    private Button postImage, savePost;
    private Game game;
    private Post post;

    public PostFrag() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater i, ViewGroup container, Bundle savedInstanceState) {
        rod = i.inflate(R.layout.post_frag, container, false);

        postName = (EditText) rod.findViewById(R.id.postName);
        postNr = (EditText) rod.findViewById(R.id.postNumber);
        postContent = (EditText) rod.findViewById(R.id.postDescription);
        postLocation = (EditText) rod.findViewById(R.id.postLocation);
        postImage = (Button) rod.findViewById(R.id.postImage);
        savePost = (Button) rod.findViewById(R.id.savePost);

        postImage.setOnClickListener(this);
        savePost.setOnClickListener(this);

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

        return rod;
    }

    @Override
    public void onClick(View v) {
        if (v == postImage) {
            // kommer senere
        } else if (v == savePost) {
            if (post == null) {
                post = new Post();
                game.getPosts().add(post);
            }
            String address = postLocation.getText().toString();

            GeocodingLocation locationAddress = new GeocodingLocation();
            locationAddress.getAddressFromLocation(address, getActivity().getApplicationContext(), new GeocoderHandler());
        }
    }

    private class GeocoderHandler extends Handler {
        @Override
        public void handleMessage(Message message) {
            String locationAddress;
            switch (message.what) {
                case 1:
                    Bundle bundle = message.getData();
                    locationAddress = bundle.getString("address");

                    double latitude = bundle.getDouble("lat");
                    double longitude = bundle.getDouble("lng");
                    System.out.println("Coor: " + latitude + "    " + longitude);
                    post.setLatitude(latitude);
                    post.setLongitude(longitude);
                    System.out.println("Post: " + post.toString());

                    LocationAddress lAddress = new LocationAddress();
                    lAddress.getAddressFromLocation(latitude, longitude, getActivity(), new GeocoderHandler2());
                    break;
                default:
                    locationAddress = null;
            }
            //latLongTV.setText(locationAddress);
        }
    }

    private class GeocoderHandler2 extends Handler {
        @Override
        public void handleMessage(Message message) {
            String locationAddress;
            switch (message.what) {
                case 1:
                    Bundle bundle = message.getData();
                    locationAddress = bundle.getString("foundAddress");
                    post.setLocation(locationAddress);
                    System.out.println("Post lok: " + post.getLocation());
                    break;
                default:
                    locationAddress = null;
            }
            AlertDialog.Builder builderSingle = new AlertDialog.Builder(getActivity());
            builderSingle.setTitle("Er lokationen korrekt?");
            builderSingle.setMessage(locationAddress);

            builderSingle.setNegativeButton("Nej",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            System.out.println("Adresse forkert");
                            post.setLocation("");
                            post.setLongitude(0.0);
                            post.setLatitude(0.0);
                            dialog.dismiss();
                            // Nej valgt --> tilbage trykket --> Crash => måske grundet post oprettet men intet gemt i den
                        }
                    });

            builderSingle.setPositiveButton("Ja",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            System.out.println("Adresse korrekt");
                            post.setName(postName.getText().toString());
                            post.setDescription(postContent.getText().toString());
                            post.setNumber(Integer.parseInt(postNr.getText().toString()));
                            getActivity().onBackPressed();
                        }
                    });
            builderSingle.show();

            /*if (!(post.getLocation().equals(""))) {
                post.setName(postName.getText().toString());
                post.setDescription(postContent.getText().toString());
                post.setNumber(Integer.parseInt(postNr.getText().toString()));
                //post.setLocation(postLocation.getText().toString());

                getActivity().onBackPressed();
            }*/
        }
    }
}

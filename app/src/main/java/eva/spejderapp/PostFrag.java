package eva.spejderapp;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.eva.backend.gameApi.model.Game;
import com.eva.backend.gameApi.model.Post;

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
            if (post != null) {
                post.setName(postName.getText().toString());
                post.setDescription(postContent.getText().toString());
                post.setNumber(Integer.parseInt(postNr.getText().toString()));
                post.setLocation(postLocation.getText().toString());
            } else {
                post = new Post();
                post.setName(postName.getText().toString());
                post.setNumber(Integer.parseInt(postNr.getText().toString()));
                post.setDescription(postContent.getText().toString());
                post.setLocation(postLocation.getText().toString());

                game.getPosts().add(post);
            }
            getActivity().onBackPressed();
        }
    }
}

package eva.spejderapp;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.eva.backend.gameApi.model.Game;
import com.eva.backend.gameApi.model.Post;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.json.JsonFactory;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;


public class CreateGameFrag extends Fragment implements View.OnClickListener, AdapterView.OnItemClickListener {
    ArrayList<String> postNames;
    private View rod;
    private EditText gameName;
    private Button addPost, diffLevel, saveGame;
    private Game game;
    private ListView postList;

    public CreateGameFrag() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater i, ViewGroup container, Bundle savedInstanceState) {
        rod = i.inflate(R.layout.create_game_frag, container, false);

        gameName = (EditText) rod.findViewById(R.id.gameName);
        addPost = (Button) rod.findViewById(R.id.addPost);
        diffLevel = (Button) rod.findViewById(R.id.difficultyLevel);
        saveGame = (Button) rod.findViewById(R.id.saveGame);
        postList = (ListView) rod.findViewById(R.id.postList);

        addPost.setOnClickListener(this);
        diffLevel.setOnClickListener(this);
        saveGame.setOnClickListener(this);

        if (getArguments() != null) {
            int index = getArguments().getInt("gameIndex");
            game = SingletonApp.getData().games.get(index);

            if (getArguments().getBoolean("oldGame")) {
                gameName.setText(game.getName());
            }

            postNames = new ArrayList<String>();
            for (Post post : game.getPosts()) {
                postNames.add(post.getName());
            }

            ArrayAdapter adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, android.R.id.text1, postNames);

            postList.setOnItemClickListener(this);
            postList.setAdapter(adapter);
        }

        return rod;
    }

    @Override
    public void onClick(View v) {
        if (v == addPost) {
            Bundle args = new Bundle();
            args.putInt("gameIndex", SingletonApp.getData().games.indexOf(game));
            PostFrag fragment = new PostFrag();
            fragment.setArguments(args);
            getFragmentManager().beginTransaction()
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .replace(R.id.main, fragment)
                    .addToBackStack(null)
                    .commit();
        } else if (v == diffLevel) {
            // show list of difficulties
        } else if (v == saveGame) {
            game.setName(gameName.getText().toString());
            SingletonApp.gemData();
            System.out.println("ToString: " + game.toString());
            try {
                JsonFactory jf = new AndroidJsonFactory();
                game.setFactory(jf);
                System.out.println("ToPrettyString: "+game.toPrettyString());

                Gson gson = new Gson();
                Game g = gson.fromJson(game.toPrettyString(), Game.class);
                g.setFactory(jf);
                System.out.println("ToPrettyString2: "+g.toString());
                System.out.println("Hertil String");

            } catch (IOException e) {
                e.printStackTrace();
            }

            getActivity().onBackPressed();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Bundle args = new Bundle();
        args.putInt("gameIndex", SingletonApp.getData().games.indexOf(game));
        args.putInt("postIndex", position);
        PostFrag fragment = new PostFrag();
        fragment.setArguments(args);
        getFragmentManager().beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .replace(R.id.main, fragment)
                .addToBackStack(null)
                .commit();
    }
}

package eva.leder;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.eva.backend2.gameApi.model.Game;
import com.eva.backend2.gameApi.model.Post;
import com.eva.backend2.solutionApi.model.Solution;

import java.util.ArrayList;

import eva.spejderapp.MainAct;
import eva.spejderapp.R;
import eva.spejderapp.SingletonApp;

public class CreateGameFrag extends Fragment implements View.OnClickListener, AdapterView.OnItemClickListener {
    ArrayList<String> postNames;
    private View view;
    private EditText gameName;
    private Button addPost, diffLevel, saveGame, eraseGame;
    private Game game;
    private ListView postList;

    public CreateGameFrag() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater i, ViewGroup container, Bundle savedInstanceState) {
        view = i.inflate(R.layout.create_game_frag, container, false);

        gameName = (EditText) view.findViewById(R.id.gameName);
        addPost = (Button) view.findViewById(R.id.addPost);
        diffLevel = (Button) view.findViewById(R.id.difficultyLevel);
        saveGame = (Button) view.findViewById(R.id.saveGame);
        postList = (ListView) view.findViewById(R.id.postList);
        eraseGame = (Button) view.findViewById(R.id.eraseGame);

        addPost.setOnClickListener(this);
        diffLevel.setOnClickListener(this);
        saveGame.setOnClickListener(this);
        eraseGame.setOnClickListener(this);

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

        ((MainAct) getActivity()).getSupportActionBar().setTitle("Løb");

        return view;
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
            AlertDialog.Builder builderSingle = new AlertDialog.Builder(getActivity());
            builderSingle.setIcon(R.drawable.kfum_mork_trans1);
            builderSingle.setTitle("Vælg hvor svært det skal være at finde posterne:");
            final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(),
                    android.R.layout.select_dialog_singlechoice);
            arrayAdapter.add("Kort med markør");
            arrayAdapter.add("Adresse");
            arrayAdapter.add("GPS-koordinater");
            arrayAdapter.add("Retning og afstand");

            builderSingle.setNegativeButton("cancel",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

            builderSingle.setAdapter(arrayAdapter,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            game.setDifficultyLevel(which);
                        }
                    });
            builderSingle.show();
        } else if (v == saveGame) {
            game.setName(gameName.getText().toString());
            getActivity().onBackPressed();
        } else if (v == eraseGame) {
            SingletonApp.getData().games.remove(game);
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
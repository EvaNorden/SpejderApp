package eva.leder;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.eva.backend2.gameApi.model.Game;
import com.eva.backend2.gameApi.model.Post;

import java.util.ArrayList;

import eva.spejderapp.MainAct;
import eva.spejderapp.R;
import eva.spejderapp.SingletonApp;

/**
 * Fragment som giver mulighed for at oprette og ændre løb
 */
public class GameFrag extends Fragment implements View.OnClickListener, AdapterView.OnItemClickListener, View.OnLongClickListener {
    ArrayList<String> postNames;
    private EditText gameName;
    private Button addPost, diffLevel, saveGame, eraseGame;
    private Game game;

    public GameFrag() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater i, ViewGroup container, Bundle savedInstanceState) {
        View view = i.inflate(R.layout.create_game_frag, container, false);

        gameName = (EditText) view.findViewById(R.id.gameName);
        addPost = (Button) view.findViewById(R.id.addPost);
        diffLevel = (Button) view.findViewById(R.id.difficultyLevel);
        saveGame = (Button) view.findViewById(R.id.saveGame);
        ListView postList = (ListView) view.findViewById(R.id.postList);
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

        if (SingletonApp.prefs.getBoolean("need_help", true)) {
            postList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    Toast.makeText(getActivity(), "Tryk på en post for at redigere den", Toast.LENGTH_LONG).show();
                    return true;
                }
            });
            addPost.setOnLongClickListener(this);
            diffLevel.setOnLongClickListener(this);
            saveGame.setOnLongClickListener(this);
            eraseGame.setOnLongClickListener(this);
        }

        ((MainAct) getActivity()).getSupportActionBar().setTitle("Løb");

        return view;
    }

    @Override
    public boolean onLongClick(View v) {
        if (v == addPost)
            Toast.makeText(getActivity(), "Tryk her for at tilføje en post til dit løb", Toast.LENGTH_LONG).show();
        else if (v == diffLevel)
            Toast.makeText(getActivity(), "Tryk her for at vælge hvor svært det skal være at finde posterne i dit løb", Toast.LENGTH_LONG).show();
        else if (v == saveGame)
            Toast.makeText(getActivity(), "Tryk her for at gemme dit løb på telefonen", Toast.LENGTH_LONG).show();
        else if (v == eraseGame)
            Toast.makeText(getActivity(), "Tryk her for at slette dit løb fra telefonen", Toast.LENGTH_LONG).show();
        return true;
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
            String[] diffs = {"Kort med markør","Adresse","GPS-koordinater","Retning og afstand"};

            if (game.getDifficultyLevel() == null)
                game.setDifficultyLevel(0);

            AlertDialog.Builder builderSingle = new AlertDialog.Builder(getActivity());
            builderSingle.setIcon(R.drawable.kfum_mork_trans1)
                    .setTitle("Find post sværhedsgrad")
                    .setSingleChoiceItems(diffs,game.getDifficultyLevel(),new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            game.setDifficultyLevel(which);
                            dialog.dismiss();
                        }
                    })
                    .setNegativeButton("cancel",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .show();
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
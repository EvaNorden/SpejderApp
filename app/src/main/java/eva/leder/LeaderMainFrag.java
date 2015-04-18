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
import android.widget.ListView;

import com.eva.backend.gameApi.model.Game;
import com.eva.backend.gameApi.model.Post;

import java.util.ArrayList;

import eva.spejderapp.R;
import eva.spejderapp.SingletonApp;

public class LeaderMainFrag extends Fragment implements View.OnClickListener, AdapterView.OnItemClickListener {
    private ArrayList<String> gameNames;
    private View rod;
    private Button newGame, startGame, endGame;
    private ListView gameList;

    public LeaderMainFrag() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater i, ViewGroup container, Bundle savedInstanceState) {
        rod = i.inflate(R.layout.leader_main_frag, container, false);

        newGame = (Button) rod.findViewById(R.id.newGame);
        startGame = (Button) rod.findViewById(R.id.startGame);
        endGame = (Button) rod.findViewById(R.id.endGame);
        gameList = (ListView) rod.findViewById(R.id.gameList);

        newGame.setOnClickListener(this);
        startGame.setOnClickListener(this);
        endGame.setOnClickListener(this);

        gameNames = new ArrayList<String>();
        for (Game game : SingletonApp.getData().games) {
            if (game.getName()!=null) {
                gameNames.add(game.getName());
            } else {
                SingletonApp.getData().games.remove(game);
            }
        }

        ArrayAdapter adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, android.R.id.text1, gameNames);

        gameList.setOnItemClickListener(this);
        gameList.setAdapter(adapter);

        return rod;
    }

    @Override
    public void onClick(View v) {
        if (v == newGame) {
            Game g = new Game();
            g.setPosts(new ArrayList<Post>());
            SingletonApp.getData().games.add(g);
            Bundle args = new Bundle();
            args.putInt("gameIndex", SingletonApp.getData().games.indexOf(g));
            CreateGameFrag fragment = new CreateGameFrag();
            fragment.setArguments(args);
            getFragmentManager().beginTransaction()
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .replace(R.id.main, fragment)
                    .addToBackStack(null)
                    .commit();
        } else if (v == startGame) {
            AlertDialog.Builder builderSingle = new AlertDialog.Builder(getActivity());
            builderSingle.setIcon(R.drawable.ic_launcher);
            builderSingle.setTitle("Vælg et løb:");
            final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(),
                    android.R.layout.select_dialog_singlechoice);
            final ArrayList<Game> gamesForUpload = new ArrayList<Game>();
            for (Game game : SingletonApp.getData().games) {
                arrayAdapter.add(game.getName());
                gamesForUpload.add(game);
            }
            for (Game game : SingletonApp.getData().onlineGames) {
                arrayAdapter.remove(game.getName());
                gamesForUpload.remove(game);
            }

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
                            new UpEndpointsAsyncTask(getActivity(), gamesForUpload.get(which)).execute();
                            SingletonApp.getData().onlineGames.add(gamesForUpload.get(which));
                        }
                    });
            builderSingle.show();
        } else if (v == endGame) {
            AlertDialog.Builder builderSingle = new AlertDialog.Builder(getActivity());
            builderSingle.setIcon(R.drawable.ic_launcher);
            builderSingle.setTitle("Vælg et løb:");
            final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(),
                    android.R.layout.select_dialog_singlechoice);
            for (Game game : SingletonApp.getData().onlineGames) {
                arrayAdapter.add(game.getName());
            }

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
                            new DownEndpointsAsyncTask(getActivity(), SingletonApp.getData().onlineGames.get(which)).execute();
                            SingletonApp.getData().onlineGames.remove(which);
                            SingletonApp.gemData();
                        }
                    });
            builderSingle.show();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Bundle args = new Bundle();
        args.putInt("gameIndex", position);
        args.putBoolean("oldGame", true);
        CreateGameFrag fragment = new CreateGameFrag();
        fragment.setArguments(args);
        getFragmentManager().beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .replace(R.id.main, fragment)
                .addToBackStack(null)
                .commit();
    }
}

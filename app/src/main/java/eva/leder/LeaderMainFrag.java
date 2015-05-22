package eva.leder;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.eva.backend2.gameApi.GameApi;
import com.eva.backend2.gameApi.model.Game;
import com.eva.backend2.gameApi.model.Post;
import com.eva.backend2.solutionApi.model.Solution;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;

import java.io.IOException;
import java.util.ArrayList;

import eva.spejderapp.MainAct;
import eva.spejderapp.R;
import eva.spejderapp.SingletonApp;

/**
 * Fragment der er hovedmenu for leder delen af appen
 */
public class LeaderMainFrag extends Fragment implements View.OnClickListener,
        AdapterView.OnItemClickListener, View.OnLongClickListener {
    private Button newGame, startGame, endGame;

    public LeaderMainFrag() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater i, ViewGroup container, Bundle savedInstanceState) {
        View view = i.inflate(R.layout.leader_main_frag, container, false);

        newGame = (Button) view.findViewById(R.id.newGame);
        startGame = (Button) view.findViewById(R.id.startGame);
        endGame = (Button) view.findViewById(R.id.endGame);
        ListView gameList = (ListView) view.findViewById(R.id.gameList);

        newGame.setOnClickListener(this);
        startGame.setOnClickListener(this);
        endGame.setOnClickListener(this);

        ArrayList<String> gameNames = new ArrayList<String>();
        for (Game game : SingletonApp.getData().games) {
            if (game.getName() != null) {
                gameNames.add(game.getName());
            } else {
                SingletonApp.getData().games.remove(game);
            }
        }

        ArrayAdapter adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, android.R.id.text1, gameNames);

        gameList.setOnItemClickListener(this);
        gameList.setAdapter(adapter);

        if (SingletonApp.prefs.getBoolean("need_help", true)) {
            gameList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    Toast.makeText(getActivity(), "Tryk på et løb for at redigere det", Toast.LENGTH_LONG).show();
                    return true;
                }
            });
            newGame.setOnLongClickListener(this);
            startGame.setOnLongClickListener(this);
            endGame.setOnLongClickListener(this);
        }

        ((MainAct) getActivity()).getSupportActionBar().setTitle("Leder Menu");

        return view;
    }

    @Override
    public boolean onLongClick(View v) {
        if (v == newGame)
            Toast.makeText(getActivity(), "Tryk her for at oprette et nyt løb der senere kan lægges på nettet", Toast.LENGTH_LONG).show();
        else if (v == startGame)
            Toast.makeText(getActivity(), "Tryk her for at vælge et løb der lægges online så andre kan deltage i det", Toast.LENGTH_LONG).show();
        else if (v == endGame)
            Toast.makeText(getActivity(), "Tryk her for at vælge et løb der ikke længere skal være tilgængeligt online", Toast.LENGTH_LONG).show();
        return true;
    }

    @Override
    public void onClick(View v) {
        if (v == newGame) {
            Game g = new Game();
            g.setPosts(new ArrayList<Post>());
            SingletonApp.getData().games.add(g);
            Bundle args = new Bundle();
            args.putInt("gameIndex", SingletonApp.getData().games.indexOf(g));
            GameFrag fragment = new GameFrag();
            fragment.setArguments(args);
            getFragmentManager().beginTransaction()
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .replace(R.id.main, fragment)
                    .addToBackStack(null)
                    .commit();
        } else if (v == startGame) { // if games are up register GCM once
            AlertDialog.Builder builderSingle = new AlertDialog.Builder(getActivity());
            builderSingle.setIcon(R.drawable.kfum_mork_trans1);
            builderSingle.setTitle("Vælg et løb:");
            final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(),
                    android.R.layout.select_dialog_item);
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
                            upEndpointsAsyncTask(gamesForUpload.get(which));
                            SingletonApp.getData().onlineGames.add(gamesForUpload.get(which));
                        }
                    });
            builderSingle.show();
        } else if (v == endGame) {
            AlertDialog.Builder builderSingle = new AlertDialog.Builder(getActivity());
            builderSingle.setIcon(R.drawable.kfum_mork_trans1);
            builderSingle.setTitle("Vælg et løb:");
            final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(),
                    android.R.layout.select_dialog_item);
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
                            downEndpointsAsyncTask(SingletonApp.getData().onlineGames.get(which));
                            SingletonApp.getData().onlineGames.remove(which);
                            if (SingletonApp.getData().onlineGames.size() == 0) {
                                SingletonApp.getData().solutions = new ArrayList<Solution>();
                            }
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
        GameFrag fragment = new GameFrag();
        fragment.setArguments(args);
        getFragmentManager().beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .replace(R.id.main, fragment)
                .addToBackStack(null)
                .commit();
    }

    private void downEndpointsAsyncTask(final Game g) {
        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... params) {
                GameApi.Builder builder = new GameApi.Builder(AndroidHttp.newCompatibleTransport(), new AndroidJsonFactory(), null)
                        .setRootUrl("https://teak-blueprint-89907.appspot.com/_ah/api/");

                try {
                    System.out.println("ID: " + g.getId());
                    builder.build().remove(g.getId()).execute();
                    return true;
                } catch (IOException e) {
                    System.out.println(e);
                    return false;
                }
            }

            @Override
            protected void onPostExecute(Boolean result) {
                if (result) {
                    System.out.println("Game removed from server");
                    Toast.makeText(getActivity(), "Løb offline", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getActivity(), "Fejl. Er du forbundet til internettet?", Toast.LENGTH_LONG).show();
                }
            }
        }.execute();
    }

    private void upEndpointsAsyncTask(final Game gg) {
        new AsyncTask<Void, Void, Game>() {
            @Override
            protected Game doInBackground(Void... params) {
                GameApi.Builder builder = new GameApi.Builder(AndroidHttp.newCompatibleTransport(), new AndroidJsonFactory(), null)
                        .setRootUrl("https://teak-blueprint-89907.appspot.com/_ah/api/");

                try {
                    Game g = builder.build().insert(gg).execute();
                    Long id = g.getId();
                    gg.setId(id);
                    return g;
                } catch (IOException e) {
                    System.out.println(e);
                    return null;
                }
            }

            @Override
            protected void onPostExecute(Game result) {
                if (result != null) {
                    System.out.println("Game put up: " + result.getName() + " ID: " + result.getId());
                    Toast.makeText(getActivity(), "Løb online", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getActivity(), "Fejl. Er du forbundet til internettet?", Toast.LENGTH_LONG).show();
                }
            }
        }.execute();
    }
}

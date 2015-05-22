package eva.spejder;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
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
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import eva.spejderapp.MainAct;
import eva.spejderapp.R;
import eva.spejderapp.SingletonApp;

/**
 * Fragment som er spejderhovedmenuen hvor tilgængelige løb vises og et kan startes
 */
public class ScoutMainFrag extends Fragment implements AdapterView.OnItemClickListener {
    private ListView games;
    private Button download;

    public ScoutMainFrag() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.scout_main_frag, container, false);

        download = (Button) view.findViewById(R.id.download);
        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listEndpointsAsyncTask();
            }
        });
        if (SingletonApp.prefs.getBoolean("need_help", true)) {
            download.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Toast.makeText(getActivity(), "Forsøg at hente løb igen", Toast.LENGTH_LONG).show();
                    return true;
                }
            });
        }

        if (SingletonApp.getData().activeGame != null) {
            FindPostFrag fragment = new FindPostFrag();
            getFragmentManager().beginTransaction()
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .replace(R.id.main, fragment)
                    .commit(); // If added to backstack you can't go back
            return view;
        }

        SingletonApp.getData().solution = null;

        games = (ListView) view.findViewById(R.id.availableGames);

        games.setOnItemClickListener(this);

        if (SingletonApp.prefs.getBoolean("need_help", true)) {
            games.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    Toast.makeText(getActivity(), "Tryk på et løb for at starte det", Toast.LENGTH_LONG).show();
                    return true;
                }
            });
        }

        if (savedInstanceState == null) { // hent kun fra nettet hvis det er et friskt fragment
            listEndpointsAsyncTask();
        } else {
            updateGameList(SingletonApp.getData().scoutGames);
        }

        ((MainAct) getActivity()).getSupportActionBar().setTitle("Spejder Menu");

        return view;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //Sorting Post list
        Game game = SingletonApp.getData().scoutGames.get(position);
        List<Post> posts = game.getPosts();
        System.out.println("Poster: " + posts);
        Collections.sort(posts, new Comparator<Post>() {
            @Override
            public int compare(Post post1, Post post2) {
                return post1.getNumber().compareTo(post2.getNumber());
            }
        });
        System.out.println("Poster: " + posts);

        game.setPostCounter(0);
        SingletonApp.getData().activeGame = game;

        FindPostFrag fragment = new FindPostFrag();
        getFragmentManager().beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .replace(R.id.main, fragment)
                .addToBackStack(null)
                .commit();

        ((MainAct) getActivity()).supportInvalidateOptionsMenu();
    }

    private void listEndpointsAsyncTask() {
        new AsyncTask<Void, Void, List<Game>>() {
            private ProgressDialog dialog;

            @Override
            protected void onPreExecute() {
                dialog = new ProgressDialog(getActivity());
                dialog.setIndeterminate(true); // drejende hjul
                dialog.setTitle("Henter løb");
                dialog.setIcon(R.drawable.kfum_mork_trans1);
                dialog.setMessage("Vent venligst");
                dialog.setCancelable(false);
                dialog.show();
            }

            @Override
            protected List<Game> doInBackground(Void... params) {
                GameApi.Builder builder = new GameApi.Builder(AndroidHttp.newCompatibleTransport(), new AndroidJsonFactory(), null)
                        .setRootUrl("https://teak-blueprint-89907.appspot.com/_ah/api/");

                try {
                    return builder.build().list().execute().getItems();
                } catch (IOException e) {
                    System.out.println(e);
                    return null;
                }
            }

            @Override
            protected void onPostExecute(List<Game> result) {
                System.out.println("Downloaded Game list: " + result);
                dialog.dismiss();
                if (result != null) {
                    SingletonApp.getData().scoutGames = result;
                    download.setVisibility(View.GONE);
                    updateGameList(result);
                } else {
                    Toast.makeText(getActivity(), "Download mislykkedes\nTjek din internetforbindelse", Toast.LENGTH_LONG).show();
                    SingletonApp.getData().scoutGames = new ArrayList<Game>();
                    download.setVisibility(View.VISIBLE); // Da kommunikationen fejlede skal det være muligt at prøve igen
                }
            }
        }.execute();
    }

    public void updateGameList(List<Game> result) {
        ArrayList<String> gameNames = new ArrayList<String>();
        for (Game game : result) {
            gameNames.add(game.getName());
        }
        ArrayAdapter adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, android.R.id.text1, gameNames);
        games.setAdapter(adapter);
    }
}
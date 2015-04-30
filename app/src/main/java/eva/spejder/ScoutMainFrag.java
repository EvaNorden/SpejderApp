package eva.spejder;


import android.app.FragmentTransaction;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.eva.backend2.gameApi.model.Game;
import com.eva.backend2.gameApi.model.Post;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import eva.spejderapp.R;
import eva.spejderapp.SingletonApp;

/**
 * A simple {@link Fragment} subclass.
 */
public class ScoutMainFrag extends Fragment implements AdapterView.OnItemClickListener{
    private ArrayList<String> gameNames;
    private View rod;
    private ListView games;

    public ScoutMainFrag() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rod = inflater.inflate(R.layout.scout_main_frag, container, false);

        games = (ListView)rod.findViewById(R.id.availableGames);

        games.setOnItemClickListener(this);

        new ListEndpointsAsyncTask(this).execute();

        return rod;
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //Sorting Post list
        Game game = SingletonApp.getData().scoutGames.get(position);
        List<Post> posts = game.getPosts();
        System.out.println("Poster: "+posts);
        Collections.sort(posts, new Comparator<Post>() {
            @Override
            public int compare(Post post1, Post post2) {
                return post1.getNumber().compareTo(post2.getNumber());
            }
        });
        System.out.println("Poster: "+posts);

        game.setPostCounter(0);
        SingletonApp.getData().activeGame = game;
        SingletonApp.gemData();

        // register GCM

        FindPostFrag fragment = new FindPostFrag();
        getFragmentManager().beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .replace(R.id.main, fragment)
                .addToBackStack(null)
                .commit();
    }

    public void updateGameList(List<Game> result) {
        SingletonApp.getData().scoutGames = result;
        gameNames = new ArrayList<String>();
        for (Game game : result) {
            gameNames.add(game.getName());
        }
        ArrayAdapter adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, android.R.id.text1, gameNames);
        games.setAdapter(adapter);
    }
}

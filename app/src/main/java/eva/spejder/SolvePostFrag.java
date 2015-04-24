package eva.spejder;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.eva.backend.gameApi.model.Game;
import com.eva.backend.gameApi.model.Post;
import com.eva.backend.solutionApi.SolutionApi;
import com.eva.backend.solutionApi.model.Solution;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;

import java.io.IOException;

import eva.spejderapp.R;
import eva.spejderapp.SingletonApp;

/**
 * A simple {@link Fragment} subclass.
 */
public class SolvePostFrag extends Fragment implements View.OnClickListener {
    private View view;
    private Button send;
    private EditText solution;
    private TextView headline,postdescription;
    private Game game;
    private Post post;
    private Solution postSolution;


    public SolvePostFrag() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.solve_post_frag, container, false);

        send = (Button)view.findViewById(R.id.sendButton);
        solution = (EditText)view.findViewById(R.id.solution);
        headline = (TextView)view.findViewById(R.id.headline);
        postdescription = (TextView)view.findViewById(R.id.postDescription);

        send.setOnClickListener(this);

        if (getArguments() != null){
            int index = getArguments().getInt("gameIndex");
            game = SingletonApp.getData().scoutGames.get(index);
            post = game.getPosts().get(game.getPostCounter());

            headline.setText("Post "+post.getNumber()+": "+post.getName());
            postdescription.setText(post.getDescription());
        }

        return view;
    }


    @Override
    public void onClick(View v) {
        postSolution = new Solution();
        postSolution.setGameId(game.getId());
        postSolution.setPostNumber(post.getNumber());
        postSolution.setMessage(solution.getText().toString());

        new AsyncTask<Void, Void, Void>(){
            @Override
            protected Void doInBackground(Void... params) {
                SolutionApi.Builder builder = new SolutionApi.Builder(AndroidHttp.newCompatibleTransport(), new AndroidJsonFactory(), null)
                            .setRootUrl("https://teak-blueprint-89907.appspot.com/_ah/api/");

                try {
                    Solution s = builder.build().insert(postSolution).execute();
                    Long id = s.getId();
                    postSolution.setId(id);
                    return null;
                } catch (IOException e) {
                    System.out.println(e);
                    return null;
                }
            }
        }.execute();
    }
}

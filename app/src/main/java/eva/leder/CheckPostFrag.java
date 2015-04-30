package eva.leder;


import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.eva.backend2.gameApi.model.Game;
import com.eva.backend2.gameApi.model.Post;
import com.eva.backend2.solutionApi.SolutionApi;
import com.eva.backend2.solutionApi.model.Solution;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;

import java.io.IOException;

import eva.spejderapp.R;
import eva.spejderapp.SingletonApp;

/**
 * A simple {@link Fragment} subclass.
 */
public class CheckPostFrag extends Fragment implements View.OnClickListener{
    private View view;
    private Solution solution;
    private Game game;
    private Post post;
    private Button denied,approved;
    private TextView headline,description,solutionTV;

    public CheckPostFrag() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.check_post_frag, container, false);

        denied = (Button)view.findViewById(R.id.denied);
        denied.setOnClickListener(this);
        approved = (Button)view.findViewById(R.id.approved);
        approved.setOnClickListener(this);

        headline = (TextView)view.findViewById(R.id.headline);
        description = (TextView)view.findViewById(R.id.description);
        solutionTV = (TextView)view.findViewById(R.id.solution);

        if (getArguments()!=null) {
            int i = getArguments().getInt("solutionIndex",2000);
            System.out.println("Løsningsindex: "+i);
            solution = SingletonApp.getData().solutions.get(i);
            System.out.println("Løsning der skal tjekkes: "+solution);
            for (Game g : SingletonApp.getData().onlineGames) {
                if (g.getId().equals(solution.getGameId())) {
                    game = g;
                }
            }
            for (Post p : game.getPosts()){
                if (p.getNumber().equals(solution.getPostNumber())) {
                    post = p;
                }
            }
        }

        headline.setText("Tjek Post "+solution.getPostNumber());
        description.setText(post.getDescription());
        solutionTV.setText(solution.getMessage());

        return view;
    }

    @Override
    public void onClick(View v) {
        if (v==denied){
            updateSolution(1);
        } else if (v==approved){
            updateSolution(2);
        }
    }

    private void updateSolution(int status) {
        solution.setApproved(status);

        new AsyncTask<Solution, Void, Solution>() {
            @Override
            protected Solution doInBackground(Solution... params) {
                SolutionApi.Builder builder = new SolutionApi.Builder(AndroidHttp.newCompatibleTransport(), new AndroidJsonFactory(), null)
                        .setRootUrl("https://teak-blueprint-89907.appspot.com/_ah/api/");

                Solution sol = (Solution)params[0];

                try {
                    Solution s = builder.build().update(sol.getId(),sol).execute();
                    return s;
                } catch (IOException e) {
                    System.out.println(e);
                    return null;
                }
            }

            @Override
            protected void onPostExecute(Solution sol) {
                System.out.println("Solution checked: "+sol);
                getActivity().onBackPressed();
            }
        }.execute(solution);
    }
}

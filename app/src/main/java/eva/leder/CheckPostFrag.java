package eva.leder;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.eva.backend2.gameApi.model.Game;
import com.eva.backend2.gameApi.model.Post;
import com.eva.backend2.solutionApi.SolutionApi;
import com.eva.backend2.solutionApi.model.Solution;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import eva.spejderapp.MainAct;
import eva.spejderapp.R;
import eva.spejderapp.SingletonApp;

/**
 * A simple {@link Fragment} subclass.
 */
public class CheckPostFrag extends Fragment implements View.OnClickListener, View.OnLongClickListener {
    private Solution solution;
    private Game game;
    private Post post;
    private Button denied, approved;

    public CheckPostFrag() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.check_post_frag, container, false);

        denied = (Button) view.findViewById(R.id.denied);
        approved = (Button) view.findViewById(R.id.approved);

        denied.setOnClickListener(this);
        approved.setOnClickListener(this);

        ImageView imageView = (ImageView) view.findViewById(R.id.imageView);

        TextView description = (TextView) view.findViewById(R.id.description);
        TextView solutionTV = (TextView) view.findViewById(R.id.solution);

        if (SingletonApp.prefs.getBoolean("need_help", true)) {
            denied.setOnLongClickListener(this);
            approved.setOnLongClickListener(this);
        }

        if (getArguments() != null) {
            int i = getArguments().getInt("solutionIndex", -1);
            System.out.println("Løsningsindex: " + i);
            solution = SingletonApp.getData().solutions.get(i);
            System.out.println("Løsning der skal tjekkes: " + solution);
            for (Game g : SingletonApp.getData().onlineGames) {
                System.out.println("O.G. id: " + g.getId());
                System.out.println("S. G. id: " + solution.getGameId());
                if (g.getId().equals(solution.getGameId())) {
                    game = g;
                }
            }
            for (Post p : game.getPosts()) {
                if (p.getNumber().equals(solution.getPostNumber())) {
                    post = p;
                }
            }

            ((MainAct) getActivity()).getSupportActionBar().setTitle("Tjek Post " + solution.getPostNumber() + ": " + post.getName());
            description.setText(post.getDescription());
            solutionTV.setText(solution.getMessage());

            if (solution.getImage() != null) {
                byte[] b = Base64.decode(solution.getImage(), Base64.DEFAULT);
                ByteArrayInputStream bais = new ByteArrayInputStream(b);
                Bitmap bitmap = BitmapFactory.decodeStream(bais);
                imageView.setImageBitmap(bitmap);
            }
        }

        return view;
    }

    @Override
    public boolean onLongClick(View v) {
        if (v == denied)
            Toast.makeText(getActivity(), "Tryk her for at afvise løsningen, så deltageren skal sende en ny løsning", Toast.LENGTH_LONG).show();
        else if (v == approved)
            Toast.makeText(getActivity(), "Tryk her for at godkende løsningen, så deltageren kan starte næste post", Toast.LENGTH_LONG).show();

        return true;
    }

    @Override
    public void onClick(View v) {
        if (v == denied) {
            updateSolution(1);
        } else if (v == approved) {
            updateSolution(2);
        }
    }

    private void updateSolution(int status) {
        SingletonApp.getData().solutions.remove(solution);
        solution.setApproved(status);
        solution.setImage("");

        new AsyncTask<Void, Void, Solution>() {
            ProgressDialog dialog;

            @Override
            protected void onPreExecute() {
                dialog = new ProgressDialog(getActivity());
                dialog.setIndeterminate(true); // drejende hjul
                dialog.setTitle("Sender svar");
                dialog.setIcon(R.drawable.kfum_mork_trans1);
                dialog.setMessage("Vent venligst");
                dialog.setCancelable(false);
                dialog.show();
            }

            @Override
            protected Solution doInBackground(Void... params) {
                SolutionApi.Builder builder = new SolutionApi.Builder(AndroidHttp.newCompatibleTransport(), new AndroidJsonFactory(), null)
                        .setRootUrl("https://teak-blueprint-89907.appspot.com/_ah/api/");

                try {
                    return builder.build().update(solution.getId(), solution).execute();
                } catch (IOException e) {
                    System.out.println(e);
                    return null;
                }
            }

            @Override
            protected void onPostExecute(Solution sol) {
                System.out.println("Solution checked: " + sol);
                dialog.dismiss();
                getActivity().onBackPressed();
            }
        }.execute();
    }
}
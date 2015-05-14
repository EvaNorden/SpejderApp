package eva.spejder;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.eva.backend2.gameApi.model.Game;
import com.eva.backend2.gameApi.model.Post;
import com.eva.backend2.solutionApi.SolutionApi;
import com.eva.backend2.solutionApi.model.Solution;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import eva.spejderapp.MainAct;
import eva.spejderapp.R;
import eva.spejderapp.SingletonApp;

/**
 * A simple {@link Fragment} subclass.
 */
public class SolvePostFrag extends Fragment implements View.OnClickListener, View.OnLongClickListener {
    private Button send, image;
    private EditText solution;
    private Game game;
    private Post post;
    private Solution postSolution;
    private String encodedImage;

    public SolvePostFrag() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.solve_post_frag, container, false);

        send = (Button) view.findViewById(R.id.sendButton);
        image = (Button) view.findViewById(R.id.image);
        solution = (EditText) view.findViewById(R.id.solution);
        TextView postdescription = (TextView) view.findViewById(R.id.description);

        send.setOnClickListener(this);
        image.setOnClickListener(this);

        if (SingletonApp.prefs.getBoolean("need_help", true)) {
            send.setOnLongClickListener(this);
            image.setOnLongClickListener(this);
        }

        postSolution = SingletonApp.getData().solution;

        game = SingletonApp.getData().activeGame;
        post = game.getPosts().get(game.getPostCounter());

        ((MainAct) getActivity()).getSupportActionBar().setTitle("Post " + post.getNumber() + ": " + post.getName());
        postdescription.setText(post.getDescription());

        return view;
    }

    @Override
    public boolean onLongClick(View v) {
        if (v == image)
            Toast.makeText(getActivity(), "Tryk her for at tage et billede som bliver sendt med post-løsningen", Toast.LENGTH_LONG).show();
        else if (v == send)
            Toast.makeText(getActivity(), "Tryk her for at sende din post-løsning til godkendelse", Toast.LENGTH_LONG).show();
        return true;
    }

    @Override
    public void onClick(View v) {
        if (v == image) {
            Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            // Vi vil have billedet gemt i vores content provider:
            i.putExtra(MediaStore.EXTRA_OUTPUT, MyContentProvider.URI);
            startActivityForResult(i, 1004);
        } else if (v == send) {
            if (postSolution != null) {
                //SingletonApp.getData().solutions.remove(postSolution);
                postSolution.setGameId(game.getId());
                postSolution.setPostNumber(post.getNumber());
                postSolution.setMessage(solution.getText().toString());
                postSolution.setApproved(0);
                postSolution.setImage(encodedImage);
            } else {
                postSolution = new Solution();
                postSolution.setGameId(game.getId());
                postSolution.setPostNumber(post.getNumber());
                postSolution.setMessage(solution.getText().toString());
                postSolution.setApproved(0);
                postSolution.setImage(encodedImage);
            }

            SingletonApp.getData().solution = postSolution;

            new AsyncTask<Void, Void, Boolean>() {
                ProgressDialog dialog = new ProgressDialog(getActivity());

                @Override
                protected void onPreExecute() {
                    dialog.setIndeterminate(true); // drejende hjul
                    dialog.setTitle("Sender løsning");
                    dialog.setIcon(R.drawable.kfum_mork_trans1);
                    dialog.setMessage("Vent venligst");
                    dialog.setCancelable(false);
                    dialog.show();
                }

                @Override
                protected Boolean doInBackground(Void... params) {
                    SolutionApi.Builder builder = new SolutionApi.Builder(AndroidHttp.newCompatibleTransport(), new AndroidJsonFactory(), null)
                            .setRootUrl("https://teak-blueprint-89907.appspot.com/_ah/api/");

                    try {
                        Solution s;
                        if (postSolution.getId() == null) {
                            s = builder.build().insert(postSolution).execute();
                        } else {
                            s = builder.build().update(postSolution.getId(), postSolution).execute();
                        }
                        Long id = s.getId();
                        postSolution.setId(id);
                        return true;
                    } catch (IOException e) {
                        System.out.println(e);
                        return false;
                    }
                }

                @Override
                protected void onPostExecute(Boolean b) {
                    dialog.dismiss();
                    if (b) {
                        Toast.makeText(getActivity(), "Løsning sendt afsted", Toast.LENGTH_LONG).show();
                        getActivity().finish();
                    } else {
                        Toast.makeText(getActivity(), "Løsning ikke afsendt!\nTjek din internetforbindelse\nog prøv igen", Toast.LENGTH_LONG).show();
                    }
                }
            }.execute();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1004) {
            System.out.println(requestCode + " gav resultat " + resultCode + " med data=" + data);

            if (resultCode == Activity.RESULT_OK) {
                try {
                    Bitmap photo = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), MyContentProvider.URI);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    photo.compress(Bitmap.CompressFormat.JPEG, 50, baos);
                    byte[] b = baos.toByteArray();
                    encodedImage = Base64.encodeToString(b, Base64.DEFAULT);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

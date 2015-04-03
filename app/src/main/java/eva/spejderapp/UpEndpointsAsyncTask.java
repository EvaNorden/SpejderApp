package eva.spejderapp;

import android.content.Context;
import android.os.AsyncTask;

import com.eva.backend.gameApi.GameApi;
import com.eva.backend.gameApi.model.Game;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;

import java.io.IOException;

class UpEndpointsAsyncTask extends AsyncTask<Void, Void, Game> {
    private static GameApi myApiService = null;
    private Context context;
    private Game game;

    UpEndpointsAsyncTask(Context context, Game game) {
        this.context = context;
        this.game = game;
    }

    @Override
    protected Game doInBackground(Void... params) {
        if (myApiService == null) { // Only do this once
            /*GameApi.Builder builder = new GameApi.Builder(AndroidHttp.newCompatibleTransport(),
                    new AndroidJsonFactory(), null)
                    // options for running against local devappserver
                    // - 10.0.2.2 is localhost's IP address in Android emulator
                    // - turn off compression when running against local devappserver
                    .setRootUrl("http://10.0.2.2:8080/_ah/api/")
                    .setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
                        @Override
                        public void initialize(AbstractGoogleClientRequest<?> abstractGoogleClientRequest) throws IOException {
                            abstractGoogleClientRequest.setDisableGZipContent(true);
                        }
                    });*/
            // end options for devappserver

            GameApi.Builder builder = new GameApi.Builder(AndroidHttp.newCompatibleTransport(), new AndroidJsonFactory(), null)
                    .setRootUrl("https://teak-blueprint-89907.appspot.com/_ah/api/");

            myApiService = builder.build();
        }

        try {
            Game g = myApiService.insert(game).execute();
            Long id = g.getId();
            game.setId(id);
            return g;
        } catch (IOException e) {
            System.out.println(e);
            return new Game();
        }
    }

    @Override
    protected void onPostExecute(Game result) {
        //Toast.makeText(context, q.getWho() + " : " + q.getWhats(), Toast.LENGTH_LONG).show();
        System.out.println("Noget: " + result.getName() + " ID: " + result.getId());
    }
}
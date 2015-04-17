package eva.spejderapp;

import android.os.AsyncTask;

import com.eva.backend.gameApi.GameApi;
import com.eva.backend.gameApi.model.Game;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import eva.spejder.ScoutMainFrag;

/**
 * Created by Eva on 16-04-2015.
 */
public class ListEndpointsAsyncTask extends AsyncTask<Void, Void, List<Game>> {
    private static GameApi myApiService = null;
    private ScoutMainFrag context;

    ListEndpointsAsyncTask(ScoutMainFrag context) {
        this.context = context;
    }

    @Override
    protected List<Game> doInBackground(Void... params) {
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
            return myApiService.list().execute().getItems();
        } catch (IOException e) {
            System.out.println(e);
            return Collections.EMPTY_LIST;
        }
    }

    @Override
    protected void onPostExecute(List<Game> result) {
        System.out.println("Noget: " + result);
        context.updateGameList(result);
    }
}

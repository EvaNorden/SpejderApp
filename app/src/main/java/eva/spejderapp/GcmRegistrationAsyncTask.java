package eva.spejderapp;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.eva.backend2.registration.Registration;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Eva on 24-04-2015.
 */
public class GcmRegistrationAsyncTask extends AsyncTask<Void, Void, String> {
    private static final String SENDER_ID = "384879969890"; // my project's number
    private static Registration regService = null;
    int i;
    private GoogleCloudMessaging gcm;
    private Context context;

    public GcmRegistrationAsyncTask(Context context) {
        this.context = context;
    }

    @Override
    protected String doInBackground(Void... params) {
        if (regService == null) {
            Registration.Builder builder = new Registration.Builder(AndroidHttp.newCompatibleTransport(), new AndroidJsonFactory(), null)
                    .setRootUrl("https://teak-blueprint-89907.appspot.com/_ah/api/");

            regService = builder.build();
        }

        String msg = "";
        Long waitTime = 1000L;
        for (i = 0; i < 10; i++) {
            try {
                if (gcm == null) {
                    gcm = GoogleCloudMessaging.getInstance(context);
                }
                String regId = gcm.register(SENDER_ID);
                msg = "Device registered, registration ID=" + regId;

                // You should send the registration ID to your server over HTTP,
                // so it can use GCM/HTTP or CCS to send messages to your app.
                // The request to your server should be authenticated if your app
                // is using accounts.
                regService.register(regId).execute();
                break;
            } catch (IOException ex) {
                ex.printStackTrace();
                msg = "Error: " + ex.getMessage();
                if (i == 0) {
                    this.publishProgress();
                }
            }
            try {
                Logger.getLogger("REGISTRATION").log(Level.INFO, "Waiting for connecting. Sleeping " + waitTime / 1000 + " sec");
                Thread.sleep(waitTime);
                waitTime = waitTime * 2;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return msg;
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        Toast.makeText(context, "Tjek din internetforbindelse", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onPostExecute(String msg) {
        if (i >= 10) {
            Toast.makeText(context, "Netværkskommunikation fejlede!\nAppen virker ikke optimalt", Toast.LENGTH_LONG).show();
            Logger.getLogger("REGISTRATION").log(Level.INFO, msg);
        } else {
            SingletonApp.prefs.edit().putBoolean("GCM_reg", true).commit();
            Toast.makeText(context, "Registreret på serveren", Toast.LENGTH_LONG).show();
            Logger.getLogger("REGISTRATION").log(Level.INFO, msg);
        }
    }
}

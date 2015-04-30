package eva.spejderapp;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.eva.backend2.gameApi.model.Game;
import com.eva.backend2.solutionApi.SolutionApi;
import com.eva.backend2.solutionApi.model.Solution;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Eva on 24-04-2015.
 */
public class GcmIntentService extends IntentService {
    private int solIndex;

    public GcmIntentService() {
        super("GcmIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.
        String messageType = gcm.getMessageType(intent);

        if (extras != null && !extras.isEmpty()) {  // has effect of unparcelling Bundle
            // Since we're not using two way messaging, this is all we really to check for
            if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                Logger.getLogger("GCM_RECEIVED").log(Level.INFO, extras.toString());

                showToast(extras.getString("message"));
                reactOnSolution(extras.getString("message"));
            }
        }
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    protected void showToast(final String message) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void reactOnSolution(String id) {
        final Long solId = Long.parseLong(id);
        new AsyncTask<Void, Void, Solution>() {
            @Override
            protected Solution doInBackground(Void... params) {
                SolutionApi.Builder builder = new SolutionApi.Builder(AndroidHttp.newCompatibleTransport(), new AndroidJsonFactory(), null)
                        .setRootUrl("https://teak-blueprint-89907.appspot.com/_ah/api/");

                try {
                    Solution s = builder.build().get(solId).execute();
                    System.out.println("Modtaget løsning: "+s);
                    return s;
                } catch (IOException e) {
                    System.out.println(e);
                    return null;
                }
            }

            @Override
            protected void onPostExecute(Solution sol) {
                SingletonApp.getData().solutions.add(sol);
                solIndex = SingletonApp.getData().solutions.indexOf(sol);
                System.out.println("Gemt løsning: "+SingletonApp.getData().solutions.get(solIndex));
                if (sol.getApproved() == 0) {
                    for (Game g : SingletonApp.getData().onlineGames) {
                        if (sol.getGameId().equals(g.getId())) {
                            // starte gennemse løsning fragment/notification...
                            noti("check_post","En post er løst","Tjek løsningen nu",0);
                        }
                    }
                } else if (sol.getGameId().equals(SingletonApp.getData().activeGame.getId())) {
                    if (sol.getApproved() == 1) {
                        // Notifikation: "Ikke godkendt" --> starte SolvePostFrag igen med samme post
                        noti("same_post","Post ikke godkendt","Prøv igen",1);
                    } else {
                        // Notifikation: "Godkendt" --> starte FindPostFrag med næste post
                        SingletonApp.getData().activeGame.setPostCounter(SingletonApp.getData().activeGame.getPostCounter() + 1);
                        if (SingletonApp.getData().activeGame.getPostCounter() >= SingletonApp.getData().activeGame.getPosts().size()) {
                            SingletonApp.getData().activeGame = null;
                            // unregister GCM
                            noti("game_over","Tillykke","Alle poster i løbet er gennemført",3);
                        } else {
                            noti("next_post", "Post godkendt", "Find næste post", 2);
                        }
                    }
                } else {
                    SingletonApp.getData().solutions.remove(sol);
                }

                SingletonApp.gemData();
            }
        }.execute();
    }

    private void noti(String action, String title, String text, int i) {
        // prepare intent which is triggered if the notification is selected
        Intent intent = new Intent(getApplicationContext(), MainAct.class);
        intent.setAction(action);
        intent.putExtra("solutionIndex",solIndex);
        System.out.println("Løs index puttet i intent: "+solIndex);
        PendingIntent pIntent = PendingIntent.getActivity(getApplicationContext(), solIndex, intent, 0); // PendingIntent's skal være forskellige ellers slårs de sammen

        // build notification
        Notification n = new Notification.Builder(getApplicationContext())
                .setContentTitle(title)
                .setContentText(text)
                .setSmallIcon(R.drawable.logo)
                .setContentIntent(pIntent)
                .setAutoCancel(true).getNotification(); // Deprecated as of API level 16

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(i, n);
    }
}

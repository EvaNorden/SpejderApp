package eva.spejderapp;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.api.client.json.gson.GsonFactory;

import java.io.IOException;

import eva.spejder.GeofenceHelper;

/**
 * Her kan foretages fælles initialisering
 */
public class SingletonApp extends Application {
    public static SharedPreferences prefs;
    public static SingletonApp instance;
    public static GeofenceHelper geofenceHelper;
    private static ProgramData data;
    private static GsonFactory factory = new GsonFactory();

    public static ProgramData getData() {
        return data;
    }

    public static void gemData() {
        String data;
        try {
            data = factory.toString(SingletonApp.data);
            prefs.edit().putString("data", data).apply();
            System.out.println("Data gemt: " + data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        System.out.println("Startet på ny");
        instance = this;
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        geofenceHelper = new GeofenceHelper(getApplicationContext());

        // Programdata der skal være indlæst ved opstart
        if (prefs.contains("data")) {
            try {
                data = factory.fromString(prefs.getString("data", ""), ProgramData.class);
                System.out.println(data);
            } catch (IOException e) {
                data = new ProgramData(); // fil fandtes ikke eller data var inkompatible
                System.out.println("Fejl: programdata oprettet fra ny: " + e);
            }
        } else {
            data = new ProgramData();
            System.out.println("programdata oprettet fra ny");
        }
    }
}

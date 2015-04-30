package eva.spejderapp;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.api.client.json.gson.GsonFactory;

import java.io.IOException;
import java.util.ArrayList;

import eva.logik.ProgramData;

/**
 * Her kan foretages fælles initialisering.
 * Resten af programmet bliver først initialiseret efter at objektet og
 * kaldet til metoden onCreate() er afsluttet, så det er vigtigt kun at
 * udføre de allermest nødvendige ting her.
 */
public class SingletonApp extends Application {
    public static SharedPreferences prefs;
    public static SingletonApp instance;
    public static ArrayList<Runnable> observatører = new ArrayList<Runnable>();
    private static ProgramData data;
    private static GsonFactory factory = new GsonFactory();

    public static ProgramData getData() {
        return data;
    }

    public static void gemData() {
        String j1 = null;
        try {
            j1 = factory.toString(data);
            prefs.edit().putString("data",j1).commit();
            System.out.println("Data gemt: "+j1);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void notifyObservatører() {
        for (Runnable r : observatører) r.run();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        // Programdata der skal være indlæst ved opstart
        if (prefs.contains("data")) {
            try {
                data = factory.fromString(prefs.getString("data",""), ProgramData.class);
                System.out.println(data);
            } catch (IOException e) {
                data = new ProgramData(); // fil fandtes ikke eller data var inkompatible
                System.out.println("Fejl: programdata oprettet fra ny: " + e);
            }
        } else {
            data = new ProgramData();
            System.out.println("programdata oprettet fra ny");
        }

        observatører.add(new Runnable() {
            @Override
            public void run() {
                SingletonApp.gemData();
            }
        });
    }
}

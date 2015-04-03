package eva.spejderapp;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;

import eva.logik.ProgramData;
import eva.logik.Serialisering;

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

    public static ProgramData getData() {
        return data;
    }

    public static void gemData() {
        try {
            Serialisering.gem(data, instance.getFilesDir() + "/programdata.ser");
        } catch (IOException ex) {
            ex.printStackTrace();
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
        try {
            data = (ProgramData) Serialisering.hent(getFilesDir() + "/programdata.ser");
            Log.d("data", "" + data);
            System.out.println("programdata indlæst fra fil");
        } catch (Exception ex) {
            data = new ProgramData(); // fil fandtes ikke eller data var inkompatible
            System.out.println("programdata oprettet fra ny: " + ex);
        }

        observatører.add(new Runnable() {
            @Override
            public void run() {
                SingletonApp.gemData();
            }
        });
    }
}

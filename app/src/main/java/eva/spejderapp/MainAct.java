package eva.spejderapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class MainAct extends Activity implements DialogInterface.OnClickListener, Runnable {
    private final String[] items = {"Spejder", "Leder"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_act);
        if (savedInstanceState == null) {
            String type = SingletonApp.prefs.getString("type", "null");
            if (type.equals("null")) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Vælg brugertype");
                builder.setItems(items, this);
                AlertDialog alert = builder.create();
                alert.show();
            } else if (type.equals("Spejder")) {
                System.out.println("Spejder");
            } else if (type.equals("Leder")) {
                LeaderMainFrag fragment = new LeaderMainFrag();
                getFragmentManager().beginTransaction()
                        .add(R.id.main, fragment)
                        .commit();
            }
        }
        SingletonApp.observatører.add(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SingletonApp.gemData();
        SingletonApp.observatører.remove(this);
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        SingletonApp.prefs.edit().putString("type", items[which]).commit();
        if (items[which].equals("Spejder")) {
            System.out.println("Spejder");
        } else if (items[which].equals("Leder")) {
            LeaderMainFrag fragment = new LeaderMainFrag();
            getFragmentManager().beginTransaction()
                    .add(R.id.main, fragment)
                    .commit();
        }
    }

    @Override
    public void run() {

    }
}

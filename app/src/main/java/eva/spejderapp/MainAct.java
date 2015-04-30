package eva.spejderapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import eva.leder.CheckPostFrag;
import eva.leder.LeaderMainFrag;
import eva.spejder.FindPostFrag;
import eva.spejder.ScoutMainFrag;
import eva.spejder.SolvePostFrag;

public class MainAct extends Activity implements DialogInterface.OnClickListener, Runnable {
    private final String[] items = {"Spejder", "Leder"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_act);
        if (getIntent().getAction().equals("check_post")) {
            int index = getIntent().getIntExtra("solutionIndex",1000);
            System.out.println("løs Er dette 1000: "+index);
            CheckPostFrag fragment = new CheckPostFrag();
            Bundle args = new Bundle();
            args.putInt("solutionIndex", index);
            fragment.setArguments(args);
            getFragmentManager().beginTransaction()
                    .add(R.id.main, fragment)
                    .commit(); // Finde en bedre måde at starte notifikationsfragmenter
        } else if (getIntent().getAction().equals("same_post")) {
            int index = getIntent().getIntExtra("solutionIndex",1000);
            SolvePostFrag fragment = new SolvePostFrag();
            Bundle args = new Bundle();
            args.putInt("solutionIndex", index);
            fragment.setArguments(args);
            getFragmentManager().beginTransaction()
                    .add(R.id.main, fragment)
                    .commit(); // Finde en bedre måde at starte notifikationsfragmenter
        } else if (getIntent().getAction().equals("next_post")) {
            int index = getIntent().getIntExtra("solutionIndex",1000);
            FindPostFrag fragment = new FindPostFrag();
            Bundle args = new Bundle();
            args.putInt("solutionIndex", index);
            fragment.setArguments(args);
            getFragmentManager().beginTransaction()
                    .add(R.id.main, fragment)
                    .commit(); // Finde en bedre måde at starte notifikationsfragmenter
        } else if (getIntent().getAction().equals("game_over")){
            ScoutMainFrag fragment = new ScoutMainFrag();
            getFragmentManager().beginTransaction()
                    .add(R.id.main, fragment)
                    .commit(); // Finde en bedre måde at starte notifikationsfragmenter
        } else if (savedInstanceState == null) {
            String type = SingletonApp.prefs.getString("type", "null");
            type = "null";
            if (type.equals("null")) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Vælg brugertype");
                builder.setItems(items, this);
                AlertDialog alert = builder.create();
                alert.show();
            } else if (type.equals("Spejder")) {
                System.out.println("Spejder");
                ScoutMainFrag fragment = new ScoutMainFrag();
                getFragmentManager().beginTransaction()
                        .add(R.id.main, fragment)
                        .commit();
            } else if (type.equals("Leder")) {
                LeaderMainFrag fragment = new LeaderMainFrag();
                getFragmentManager().beginTransaction()
                        .add(R.id.main, fragment)
                        .commit();
            }
            SingletonApp.observatører.add(this);
            new GcmRegistrationAsyncTask(this).execute();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
      //  SingletonApp.gemData();
        SingletonApp.observatører.remove(this);
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        SingletonApp.prefs.edit().putString("type", items[which]).commit();
        if (items[which].equals("Spejder")) {
            System.out.println("Spejder");
            ScoutMainFrag fragment = new ScoutMainFrag();
            getFragmentManager().beginTransaction()
                    .add(R.id.main, fragment)
                    .commit();
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

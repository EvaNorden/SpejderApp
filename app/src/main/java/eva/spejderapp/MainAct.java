package eva.spejderapp;

import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import eva.leder.CheckPostFrag;
import eva.leder.LeaderMainFrag;
import eva.spejder.FindPostFrag;
import eva.spejder.ScoutMainFrag;
import eva.spejder.SolvePostFrag;

public class MainAct extends ActionBarActivity implements DialogInterface.OnClickListener {
    private final String[] items = {"Spejder", "Leder"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_act);
        getSupportActionBar().setTitle("Spejderløb");
        getSupportActionBar().setIcon(R.drawable.kfum_mork_trans1);

        if (getIntent().getAction().equals("check_post")) {
            int index = getIntent().getIntExtra("solutionIndex", -1);
            CheckPostFrag fragment = new CheckPostFrag();
            Bundle args = new Bundle();
            args.putInt("solutionIndex", index);
            fragment.setArguments(args);
            getFragmentManager().beginTransaction()
                    .add(R.id.main, fragment)
                    .commit();
        } else if (getIntent().getAction().equals("same_post")) {
            SolvePostFrag fragment = new SolvePostFrag();
            getFragmentManager().beginTransaction()
                    .add(R.id.main, fragment)
                    .commit();
        } else if (getIntent().getAction().equals("next_post")) {
            FindPostFrag fragment = new FindPostFrag();
            getFragmentManager().beginTransaction()
                    .add(R.id.main, fragment)
                    .commit();
        } else if (getIntent().getAction().equals("game_over")) {
            ScoutMainFrag fragment = new ScoutMainFrag();
            getFragmentManager().beginTransaction()
                    .add(R.id.main, fragment)
                    .commit();
        } else if (getIntent().getAction().equals("geofence")) {
            System.out.println("GEO intent modtaget i main");
            SingletonApp.geofenceHelper.removeGeofencesHandler();
            SolvePostFrag fragment = new SolvePostFrag();
            getFragmentManager().beginTransaction()
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .replace(R.id.main, fragment)
                    .addToBackStack(null)
                    .commit();
        } else if (savedInstanceState == null) {
            String type = SingletonApp.prefs.getString("type", "null");
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

            if (!SingletonApp.prefs.getBoolean("GCM_reg", false)) {
                System.out.println("Forsøger GCM registrering");
                new GcmRegistrationAsyncTask(this).execute();
            }
            if (SingletonApp.prefs.getBoolean("need_help", true)) {
                Toast.makeText(this, "Langt tryk på knapper hjælper", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        SingletonApp.gemData();
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        if (SingletonApp.prefs.getString("type", "null").equals("Spejder") && SingletonApp.getData().activeGame != null) {
            menu.getItem(1).setVisible(true);
        } else {
            menu.getItem(1).setVisible(false);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.indstillinger) {
            item.setVisible(false);
            getSupportActionBar().setDisplayShowHomeEnabled(false);
            getFragmentManager().beginTransaction()
                    .replace(R.id.main, new PrefFrag())
                    .addToBackStack(null)
                    .commit();
            return true;
        } else if (id == android.R.id.home) {
            Intent i = getBaseContext().getPackageManager().getLaunchIntentForPackage(getBaseContext().getPackageName());
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
            return true;
        } else if (id == R.id.endGame) {
            SingletonApp.getData().activeGame = null;
            SingletonApp.geofenceHelper.googleApiClient.disconnect();
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancelAll();
            Intent i = getBaseContext().getPackageManager().getLaunchIntentForPackage(getBaseContext().getPackageName());
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() > 0) {
            getFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }

    public static class PrefFrag extends PreferenceFragment {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.pref);

            ((MainAct) getActivity()).getSupportActionBar().setTitle("Instillinger");
            ((MainAct) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            ((MainAct) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            ((MainAct) getActivity()).supportInvalidateOptionsMenu();
        }

    }
}
package com.nfcalarmclock;

import android.os.Bundle;
import android.content.Intent;
import android.widget.Toast;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.util.Log;
import java.util.List;

// import android.support.v4.app.DialogFragment;
// import android.app.TimePickerDialog;
// import android.app.Dialog;
// import android.widget.TimePicker;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;


/**
 * @brief The application's main activity.
 */
public class MainActivity
    extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FloatingActionButton addalarm = this.getAddAlarmButton();
        RecyclerView alarmlist = this.getAlarmList();
        AlarmAdapter adapter = new AlarmAdapter(this);
        setupAddAlarmButton(addalarm);
        setupAlarmList(alarmlist, adapter);
        buildAlarmList(adapter);
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
    }

    /**
     * @brief Create the options menu in the action bar.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();
        switch (id)
        {
        case android.R.id.home:
            Toast.makeText(this, "Yo this is the home",
                           Toast.LENGTH_LONG).show();
            return true;
        case R.id.menu_settings:
            Intent intent = new Intent(getApplicationContext(),
                                       SettingsActivity.class);
            startActivity(intent);
            return true;
        default:
            Toast.makeText(this, "Yo this is the default thing", 
                           Toast.LENGTH_LONG).show();
            break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Adding few alarm for testing
     */
    private void buildAlarmList(AlarmAdapter adapter)
    {
        List<Alarm> alarmList = adapter.getAlarms();
        alarmList.add(new Alarm("True Romance", 13, 0));
        alarmList.add(new Alarm("Xscpae", 8, 1));
        alarmList.add(new Alarm("Maroon 5", 11, 2));
        alarmList.add(new Alarm("Born to Die", 12, 3));
        alarmList.add(new Alarm("Honeymoon", 14, 4));
        alarmList.add(new Alarm("I Need a Doctor", 1, 5));
        alarmList.add(new Alarm("Loud", 11, 6));
        alarmList.add(new Alarm("Legend", 14, 7));
        alarmList.add(new Alarm("Hello", 11, 8));
        alarmList.add(new Alarm("Greatest Hits", 17, 9));
        adapter.notifyDataSetChanged();
    }

    /**
     * @brief Setup the Add Alarm button.
     */
    private void setupAddAlarmButton(FloatingActionButton button)
    {
        button.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    Intent intent = new Intent(getApplicationContext(),
                                               AlarmAddActivity.class);
                    startActivity(intent);
                    // AddAlarmFragment addalarm = new AddAlarmFragment();
                    // AlarmDaysDialogFragment days = new AlarmDaysDialogFragment();
                    // FragmentManager manager = MainActivity.this.getSupportFragmentManager();
                    // FragmentTransaction transaction = manager.beginTransaction();
                    // transaction.add(addalarm, "AddAlarm");
                    // transaction.add(days, "DaysAlarm");
                    // // transaction.add(R.id.fragment_yo, addalarm);
                    // // transaction.replace(R.id.fragment_main, addalarm);
                    // // transaction.addToBackStack(null);
                    // transaction.commit();
                    // // AddAlarmFragment timePicker = new AddAlarmFragment();
                    // // timePicker.show(getSupportFragmentManager(), "time picker");
                }
            });
    }

    /**
     * @brief Setup the alarm list content layout.
     */
    private void setupAlarmList(RecyclerView alarmlist, AlarmAdapter adapter)
    {
        RecyclerView.LayoutManager layoutmanager = new LinearLayoutManager(this);
        alarmlist.setLayoutManager(layoutmanager);
        alarmlist.setItemAnimator(new DefaultItemAnimator());
        alarmlist.setAdapter(adapter);
    }

    /**
     * @brief Return the Add Alarm button.
     * 
     * @return Floating action button.
     */
    private FloatingActionButton getAddAlarmButton()
    {
        return (FloatingActionButton) findViewById(R.id.fab_add_alarm);
    }

    /**
     * @brief Return the layout containing the list of alarms.
     * 
     * @return RecyclerView.
     */
    private RecyclerView getAlarmList()
    {
        return (RecyclerView) findViewById(R.id.content_alarm_list);
    }


}

// AddAlarmFragment addalarm = new AddAlarmFragment();
// FragmentManager manager = getSupportFragmentManager();
// FragmentTransaction transaction = manager.beginTransaction();
// transaction.replace(R.id.fragment_main, addalarm);
// transaction.addToBackStack(null);
// transaction.commit();

// Snackbar.make(view, "Display About",
//               Snackbar.LENGTH_LONG)
//     .setAction("Action", null).show();

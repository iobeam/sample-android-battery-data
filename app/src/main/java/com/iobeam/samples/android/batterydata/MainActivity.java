package com.iobeam.samples.android.batterydata;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.widget.TextView;

import com.iobeam.api.ApiException;
import com.iobeam.api.client.Iobeam;
import com.iobeam.api.resource.DataStore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;


public class MainActivity extends Activity {

    /** Iobeam Parameters **/
    private static final long PROJECT_ID = -1; // Your PROJECT_ID
    private static final String PROJECT_TOKEN = null; // PROJECT_TOKEN (w/ write-access)
    private static final String DEVICE_ID = null; // Specify your DEVICE_ID
    /**/

    private Iobeam iobeam;
    private DataStore store;

    private TextView batteryLevel;
    private TextView batteryStats;

    private int currentBatteryLevel = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        batteryLevel = (TextView)findViewById(R.id.battery_level);
        batteryStats = (TextView)findViewById(R.id.battery_stats);

        String path = this.getFilesDir().getAbsolutePath();

        /** iobeam: Initialize the client library */
        // You will need to register your DEVICE_ID first (e.g., via the CLI)
        iobeam = new Iobeam.Builder(PROJECT_ID, PROJECT_TOKEN).saveIdToPath(path)
                .setDeviceId(DEVICE_ID).build();

        /** iobeam: Define data schema for transmissions */
        store = iobeam.createDataStore("battery_level", "temperature", "voltage");
    }

    private BroadcastReceiver batteryInfoReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            int  health = intent.getIntExtra(BatteryManager.EXTRA_HEALTH,0);
            int  level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL,0);
            int  plugged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED,0);
            boolean  present = intent.getExtras().getBoolean(BatteryManager.EXTRA_PRESENT);
            int  scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE,0);
            int  status = intent.getIntExtra(BatteryManager.EXTRA_STATUS,0);
            String  technology = intent.getExtras().getString(BatteryManager.EXTRA_TECHNOLOGY);
            int  temperature = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE,0);
            int  voltage = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE,0);

            if ((currentBatteryLevel == -1) || (Math.abs(level - currentBatteryLevel) >= 1)) {
                currentBatteryLevel = level;

                /** Iobeam: Capture data point, send to API */
                Map<String, Object> values = new HashMap<String, Object>();
                values.put("battery_level", currentBatteryLevel);
                values.put("temperature", ((double)temperature/10));
                values.put("voltage", voltage);
                store.add(values);

                try {
                    iobeam.sendAsync();
                } catch (ApiException e) {
                    e.printStackTrace();
                }
                /**/

            }

            batteryLevel.setText(level + "");
            batteryStats.setText(
                    "Time: " + Calendar.getInstance().getTime() + "\n" +
                    "Level: "+ level + "/" + scale + "\n" +
                    "Health: "+ health + "\n"+
                    "Plugged: " + plugged + "\n" +
                    "Present: " + present + "\n" +
                    "Status: " + status + "\n" +
                    "Technology: " + technology + "\n" +
                    "Temperature: " + ((double)temperature/10) + " °C\n" +
                    "Voltage: " + voltage + "\n");
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        this.registerReceiver(this.batteryInfoReceiver,	new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
    }

    protected void onPause() {
        super.onPause();
        this.unregisterReceiver(this.batteryInfoReceiver);
    }

}

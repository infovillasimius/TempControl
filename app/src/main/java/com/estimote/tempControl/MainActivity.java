package com.estimote.tempControl;
import android.os.AsyncTask;
import android.os.Bundle;

import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.estimote.coresdk.common.requirements.SystemRequirementsChecker;
import com.estimote.coresdk.recognition.packets.EstimoteTelemetry;
import com.estimote.coresdk.service.BeaconManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;


//
// Running into any issues? Drop us an email to: contact@estimote.com
//

public class MainActivity extends AppCompatActivity {

    static final String LOG_TAG = MainActivity.class.getSimpleName();
    private BeaconManager beaconManager;
    private boolean scan = false;
    TextView vedi;
    TextView serverIP;
    TextView startTimestampView;
    TextView stopTimestampView;
    EditText timestampStart;
    EditText timestampTo;
    int counter = 0;
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button stop = (Button) findViewById(R.id.stop);
        Button start = (Button) findViewById(R.id.start);
        start.requestFocus();
        Button query = (Button) findViewById(R.id.queryButton);
        Button setServerIP = (Button) findViewById(R.id.setServerIP);
        Button reset = (Button) findViewById(R.id.reset);
        serverIP = (TextView) findViewById(R.id.serverIP);
        serverIP.setText(NetConnect.getBaseUrl());
        vedi = (TextView) findViewById(R.id.vedi);
        timestampStart = (EditText) findViewById(R.id.timestampFrom);
        timestampTo = (EditText) findViewById(R.id.timestampTo);
        startTimestampView=(TextView) findViewById(R.id.startTimestampView);
        stopTimestampView=(TextView) findViewById(R.id.stopTimestampView);
        beaconManager = new BeaconManager(this);
        beaconManager.setForegroundScanPeriod(1100, 0);

        //Visualize date from timestamp
        long b= Long.parseLong(timestampStart.getText().toString());
        Timestamp t1=new Timestamp(b*1000);
        startTimestampView.setText(R.string.start_timestamp_view);
        startTimestampView.append("\n"+dateFormat.format(t1));

        b= Long.parseLong(timestampTo.getText().toString());
        Timestamp t2=new Timestamp(b*1000);
        stopTimestampView.setText(R.string.stop_timestamp_view);
        stopTimestampView.append("\n"+dateFormat.format(t2));

        timestampStart.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String a=s.toString();
                long b= Long.parseLong(a);
                Timestamp t1=new Timestamp(b*1000);

                startTimestampView.setText(R.string.start_timestamp_view);
                startTimestampView.append("\n"+dateFormat.format(t1));

            }
        });

        timestampTo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String a=s.toString();
                long b= Long.parseLong(a);
                Timestamp t1=new Timestamp(b*1000);

                stopTimestampView.setText(R.string.stop_timestamp_view);
                stopTimestampView.append("\n"+dateFormat.format(t1));

            }
        });


        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!scan) {
                    Snackbar.make(view, "Scan started...", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    scan = true;
                    vedi.setText(R.string.scansione);
                } else {
                    Snackbar.make(view, "Scan already started...", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }

            }
        });

        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (scan) {
                    Snackbar.make(view, "Scan stopped...", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    scan = false;
                } else {
                    Snackbar.make(view, "Scan already stopped...", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }

            }
        });

        query.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scan = false;

                AsyncTask<String, String, String> execute = new QueryConnect().execute(timestampStart.getText().toString(), timestampTo.getText().toString());

        try {
            final String s = execute.get();
            DecimalFormat df = new DecimalFormat("#,##0.00;(#,##0.00)");

            JSONObject json = new JSONObject(s);

            double averageTemp=json.getDouble("Average temperature");
            int movements=json.getInt("Movements");
            int data=json.getInt("n");
            double before=json.getDouble("Average Temp before moves");
            double deltaT=json.getDouble("Average Delta T");
            double after=json.getDouble("Average Temp after moves");
            double min=json.getDouble("Average minutes to reach the previous temperature");
            int working=json.getInt("Minutes of working");
            double moveTime=json.getDouble("Average minutes between moves");
            double energy=json.getDouble("Total Energy");
            vedi.setText("Total number of data:      " + data+"\n");
            vedi.append ("Avg temperature:           " + df.format(averageTemp) +"\n");
            vedi.append ("Movements:                 " + movements +"\n");
            vedi.append ("Avg minutes between moves: " + df.format(moveTime)+"\n");
            vedi.append ("Minutes of working         " + working+"\n");
            vedi.append ("Avg temp before moves:     " + df.format(before)+"\n");
            vedi.append ("Avg Delta T:               " + df.format(deltaT)+"\n");
            vedi.append ("Avg temp after moves:      " + df.format(after)+"\n");
            vedi.append ("Avg minutes to reach the   " +
                       "\nprevious temperature:      " + df.format(min)+"\n");
            vedi.append ("Total energy used (Kwh):   " + df.format(energy)+"\n");


        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }


            }
        });

        setServerIP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean result = NetConnect.setBaseUrl(serverIP.getText().toString()) && QueryConnect.setBaseUrl(serverIP.getText().toString());
                if (result) {
                    Snackbar.make(view, R.string.ip_updated, Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                } else {
                    Snackbar.make(view, R.string.error_server_ip, Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }

            }
        });

        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timestampStart.setText(R.string.start_timestamp);
                timestampTo.setText(R.string.stop_timestamp);
                vedi.setText(R.string.hello);
                scan = false;
            }
        });



        beaconManager.setTelemetryListener(new BeaconManager.TelemetryListener() {
            @Override
            public void onTelemetriesFound(List<EstimoteTelemetry> telemetries) {

                if (scan) {
                    for (EstimoteTelemetry tlm : telemetries) {
                        Log.d("TELEMETRY", "beaconID: " + tlm.deviceId +
                                ", temperature: " + tlm.temperature + " °C");


                        String key = "" + tlm.timestamp.getTime();
                        String data = tlm.toString() + "\n";
                        if (counter > 49) {
                            counter = 0;
                            vedi.setText(R.string.scansione);
                        }
                        counter++;
                        vedi.append(key + " - " + data);

                        //vedi.append("Date " + dateFormat.format(tlm.timestamp) + " - temperature: " + tlm.temperature + " °C - Moving = " + tlm.motionState +" - Md "+tlm.motionDuration +"Acc. "+tlm.accelerometer  + "\n" );
                        //" - Timestamp " + tlm.timestamp.getTime() +

                        AsyncTask<String, String, String> execute = new NetConnect().execute(key, data);
                        try {
                            final String s = execute.get();
                            vedi.append(s + "\n");
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        }

                    }
                }
            }
        });

        //

        //vedi.setText("Scansione: \n");
        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                beaconManager.startTelemetryDiscovery();

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();

        SystemRequirementsChecker.checkWithDefaultDialogs(this);

    }



    @Override protected void onStart() {
        super.onStart();
        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                beaconManager.startTelemetryDiscovery();

            }
        });
    }

    @Override protected void onStop() {
        super.onStop();
        beaconManager.stopTelemetryDiscovery();
    }



}

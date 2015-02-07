/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.bluetoothmanager;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.example.android.bluetoothchat.R;
import com.example.android.bluetoothmanager.database.LogsDAO;
import com.example.android.bluetoothmanager.helper.AsyncResponse;
import com.example.android.bluetoothmanager.helper.LocationHelper;
import com.example.android.bluetoothmanager.helper.RequestTask;
import com.example.android.bluetoothmanager.helper.ResponseParser;
import com.example.android.bluetoothmanager.helper.Utils;
import com.example.android.bluetoothmanager.mail.GMailSender;
import com.example.android.bluetoothmanager.model.Entry;
import com.example.android.bluetoothmanager.model.LogEntryProtocol;

import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;


/**
 * This fragment controls Bluetooth to communicate with other devices.
 */
public class BluetoothManagerFragment extends Fragment implements AsyncResponse{

    private static final String TAG = "BluetoothManagerFragment";

    // Intent request codes
    private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
    private static final int REQUEST_ENABLE_BT = 3;

    // Layout Views
    private ListView mConversationView;
    private String mConnectedDeviceName = null;
    private SimpleAdapter mConversationArrayAdapter;
    private BluetoothAdapter mBluetoothAdapter = null;
    private BluetoothManagerService mChatService = null;
    private ResponseParser responseParser = null;
    private LogsDAO logsDAO;
    private final ScheduledExecutorService scheduler =
            Executors.newScheduledThreadPool(3);
    private LocationManager locationManager ;
    private Location lastLocation = null;

    private Entry oldResponse = null;
    SharedPreferences app_preferences;
    private String address;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        // Get local Bluetooth adapter
        responseParser = new ResponseParser();
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        logsDAO = new LogsDAO(getActivity());
        logsDAO.open();
        // If the adapter is null, then Bluetooth is not supported
        if (mBluetoothAdapter == null) {
            FragmentActivity activity = getActivity();
            Toast.makeText(activity, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            activity.finish();
        }

        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            Utils.displayPromptForEnablingGPS(getActivity());
        }
        lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if(lastLocation==null){
            lastLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }
        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider.
                if(LocationHelper.isBetterLocation(location, lastLocation)) {
                    lastLocation = location;
                }
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {}

            public void onProviderEnabled(String provider) {}

            public void onProviderDisabled(String provider) {}
        };
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 100, locationListener);
    }


    @Override
    public void onStart() {
        super.onStart();
        app_preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        // If BT is not on, request that it be enabled.
        // setupChat() will then be called during onActivityResult
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
            // Otherwise, setup the chat session
        } else if (mChatService == null) {
            setupChat();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mChatService != null) {
            mChatService.stop();
        }
        logsDAO.close();
    }

    @Override
    public void onResume() {
        super.onResume();
        logsDAO.open();
        // Performing this check in onResume() covers the case in which BT was
        // not enabled during onStart(), so we were paused to enable it...
        // onResume() will be called when ACTION_REQUEST_ENABLE activity returns.
        if (mChatService != null) {
            // Only if the state is STATE_NONE, do we know that we haven't started already
            if (mChatService.getState() == BluetoothManagerService.STATE_NONE) {
                // Start the Bluetooth chat services
                mChatService.start();
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_bluetooth_chat, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        mConversationView = (ListView) view.findViewById(R.id.in);
    }

    /**
     * Set up the UI and background operations for chat.
     */
    private void setupChat() {
        Log.d(TAG, "setupChat()");
        initializeViewAddapter();
        /*new*/
        mConversationView.setAdapter(mConversationArrayAdapter);

        // Initialize the BluetoothManagerService to perform bluetooth connections
        mChatService = new BluetoothManagerService(getActivity(), mHandler);

    }

    private void initializeViewAddapter() {
        String[] keys = new String[]{
                "Pressure",
                "Temperature",
                "Speed",
                "Slump",
                "Volume",
                "Yield",
                "Viscosity"
        };
        String[] values = new String[]{
                "0",
                "0",
                "0",
                "0",
                "0",
                "0",
                "0"
        };
        List<HashMap<String, String>> aList = new ArrayList<HashMap<String, String>>();

        for (int i = 0; i < 7; i++) {
            HashMap<String, String> hm = new HashMap<String, String>();
            hm.put("keys", "" + keys[i]);
            hm.put("values", "" + values[i]);
            aList.add(hm);
        }

        // Keys used in Hashmap
        String[] from = {"keys", "values"};
        int[] to = {R.id.keys, R.id.values};
        mConversationArrayAdapter = new SimpleAdapter(getActivity(), aList, R.layout.readings_list_layout, from, to);
    }

    /**
     * Updates the status on the action bar.
     *
     * @param resId a string resource ID
     */
    private void setStatus(int resId) {
        FragmentActivity activity = getActivity();
        if (null == activity) {
            return;
        }
        final ActionBar actionBar = activity.getActionBar();
        if (null == actionBar) {
            return;
        }
        actionBar.setSubtitle(resId);
    }

    /**
     * Updates the status on the action bar.
     *
     * @param subTitle status
     */
    private void setStatus(CharSequence subTitle) {
        FragmentActivity activity = getActivity();
        if (null == activity) {
            return;
        }
        final ActionBar actionBar = activity.getActionBar();
        if (null == actionBar) {
            return;
        }
        actionBar.setSubtitle(subTitle);
    }

    /**
     * The Handler that gets information back from the BluetoothManagerService
     */
    private final Handler mHandler = new Handler() {
        public int retryCount = 0;
        public boolean truckNoSet = false;
        public long lastSentSms = 0;

        @Override
        public void handleMessage(Message msg) {
            FragmentActivity activity = getActivity();
            switch (msg.what) {
                case Constants.MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case BluetoothManagerService.STATE_CONNECTED:
                            truckNoSet = false;
                            retryCount = 0;
                            setStatus(getString(R.string.title_connected_to, mConnectedDeviceName));
                            //  mConversationArrayAdapter.clear();
                            Runnable beeper = new Runnable() {
                                public void run() {
                                    String streamXml = "";
                                    streamXml = "+++";
                                    streamXml += "<DsMain>";
                                    streamXml += "<Command>";
                                    streamXml += "GetTruckInfo";
                                    streamXml += "</Command>";
                                    streamXml += "</DsMain>";
                                    streamXml += "%%%";
                                    sendMessage(streamXml);
                                }
                            };
                            ScheduledFuture<?> scheduledFuture = scheduler.scheduleAtFixedRate(beeper, 0, 3, TimeUnit.SECONDS);
                            break;
                        case BluetoothManagerService.STATE_CONNECTING:
                            setStatus(R.string.title_connecting);
                            break;
                        case BluetoothManagerService.STATE_LISTEN:
                        case BluetoothManagerService.STATE_NONE:
                            setStatus(R.string.title_not_connected);
                            break;
                    }
                    break;
                case Constants.MESSAGE_WRITE:
                    byte[] writeBuf = (byte[]) msg.obj;
                    // construct a string from the buffer
                    String writeMessage = new String(writeBuf);
                    Log.i("m", "message sent:" + writeMessage);
                    break;
                case Constants.MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    // construct a string from the valid bytes in the buffer
                    String readMessage = new String(readBuf, 0, msg.arg1);
                    Entry data = responseParser.parse(readMessage);
                    if (data != null) {
                        data.setLatitude("" + lastLocation.getLatitude());
                        data.setLongitude("" + lastLocation.getLongitude());
                        updateView(data);
                        sendAlert(data);
                        storeData(data);
                        sendData(data);
                        if (!truckNoSet) {
                            app_preferences.edit().putString(mConnectedDeviceName, data.getTruckNo()).apply();
                            setStatus(getString(R.string.truckNo) + " " + data.getTruckNo());
                            truckNoSet = true;
                        }
                    }
                    break;
                case Constants.MESSAGE_DEVICE_NAME:
                    // save the connected device's name
                    mConnectedDeviceName = msg.getData().getString(Constants.DEVICE_NAME);
                    if (null != activity) {
                        Toast.makeText(activity, "Connected to "
                                + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                    }
                    break;
                case Constants.MESSAGE_TOAST:
                    if (null != activity) {
                        Toast.makeText(activity, msg.getData().getString(Constants.TOAST),
                                Toast.LENGTH_SHORT).show();
                    }
                    break;
                case Constants.CONNECT_FAILED:
                    if (null != activity && retryCount < 5) {
                        connectDevice(address, true);
                    } else {
                        Toast.makeText(activity, msg.getData().getString(Constants.TOAST),
                                Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }

        private void storeData(Entry data) {
            logsDAO.createEntry(data);
        }

        private void sendAlert(Entry data) {
            try {
                if (Double.valueOf(data.getSpeed()) > Integer.valueOf(app_preferences.getString(getString(R.string.pref_speed_threshold_key), "9999")) && System.currentTimeMillis() > lastSentSms) {
                    lastSentSms = System.currentTimeMillis() + 1000 * 60 * 3;
                    SmsManager.getDefault().sendTextMessage(app_preferences.getString(getString(R.string.pref_phone_no_key), "0040742402669"), null, "speed limit reached", null, null);
                }

            } catch (NumberFormatException e) {
                //donothing
            }
        }

        private void updateView(Entry data) {
            HashMap<String, String> item = (HashMap<String, String>) mConversationArrayAdapter.getItem(6);
            item.put("values", data.getViscosity());
            item = (HashMap<String, String>) mConversationArrayAdapter.getItem(5);
            item.put("values", data.getYield());
            item = (HashMap<String, String>) mConversationArrayAdapter.getItem(4);
            item.put("values", data.getVolume());
            item = (HashMap<String, String>) mConversationArrayAdapter.getItem(3);
            item.put("values", data.getSlump());
            item = (HashMap<String, String>) mConversationArrayAdapter.getItem(2);
            item.put("values", data.getSpeed());
            item = (HashMap<String, String>) mConversationArrayAdapter.getItem(1);
            item.put("values", data.getTempProbe());
            item = (HashMap<String, String>) mConversationArrayAdapter.getItem(0);
            item.put("values", data.getPressure());
            mConversationArrayAdapter.notifyDataSetChanged();
        }

        public void sendMessage(String message) {
            // Check that we're actually connected before trying anything
            if (mChatService.getState() != BluetoothManagerService.STATE_CONNECTED) {
                Toast.makeText(getActivity(), R.string.not_connected, Toast.LENGTH_SHORT).show();
                return;
            }

            // Check that there's actually something to send
            if (message.length() > 0) {
                // Get the message bytes and tell the BluetoothManagerService to write
                byte[] send = message.getBytes();
                mChatService.write(send);
            }
        }
    };

    private void sendData(Entry data) {
        if(Utils.isNetworkAvailable(this.getActivity())) {
            new RequestTask(BluetoothManagerFragment.this).execute(data);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, final Intent data) {
        switch (requestCode) {
            case REQUEST_CONNECT_DEVICE_SECURE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    AsyncTask<Void, Void, Void> asyncTask = new AsyncTask<Void, Void, Void>() {

                        @Override
                        protected Void doInBackground(Void... params) {
                            connectDevice(data, true);
                            return null;
                        }
                    };
                    asyncTask.execute();

                }
                break;
            case REQUEST_ENABLE_BT:
                // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK) {
                    // Bluetooth is now enabled, so set up a chat session
                    setupChat();
                } else {
                    // User did not enable Bluetooth or an error occurred
                    Log.d(TAG, "BT not enabled");
                    Toast.makeText(getActivity(), R.string.bt_not_enabled_leaving,
                            Toast.LENGTH_SHORT).show();
                    getActivity().finish();
                }
        }
    }

    /**
     * Establish connection with other divice
     *
     * @param data   An {@link Intent} with {@link DeviceListActivity#EXTRA_DEVICE_ADDRESS} extra.
     * @param secure Socket Security type - Secure (true) , Insecure (false)
     */
    private void connectDevice(Intent data, boolean secure) {
        // Get the device MAC address
        address = data.getExtras()
                .getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
        // Get the BluetoothDevice object
        String address1 = app_preferences.getString(address, address);
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address1);
        // Attempt to connect to the device
        mChatService.connect(device, secure);
    }

    private void connectDevice(String address, boolean secure) {
        // Get the BluetoothDevice object
        String address1 = app_preferences.getString(address, address);
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address1);

        // Attempt to connect to the device
        mChatService.connect(device, secure);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.bluetooth_chat, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.secure_connect_scan: {
                // Launch the DeviceListActivity to see devices and do scan
                Intent serverIntent = new Intent(getActivity(), DeviceListActivity.class);
                startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_SECURE);
                return true;
            }
            case R.id.action_settings: {
                Intent serverIntent = new Intent(getActivity(), SettingsActivity.class);
                startActivity(serverIntent);
                return true;
            }
            case R.id.export_data: {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        getActivity());

                // set title
                alertDialogBuilder.setTitle("Achtung!");

                // set dialog message
                alertDialogBuilder
                        .setMessage("Export data ?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                new RequestTask(BluetoothManagerFragment.this).execute(new Entry());

                                AsyncTask<Void, Void, Void> asyncTask = new AsyncTask<Void, Void, Void>() {

                                    @Override
                                    protected Void doInBackground(Void... params) {
                                        List<Entry> allEntries = logsDAO.getAllEntries();
                                        String export = "";
                                        for (Entry allEntry : allEntries) {
                                            export += allEntry.getRawData() + "\n";
                                        }
                                        GMailSender sender = new GMailSender("cristian.v.sechel@gmail.com", "Haidinamo1298");
                                        try {
                                            File file = new java.io.File((getActivity()
                                                    .getApplicationContext().getFileStreamPath("test.txt")
                                                    .getPath()));
                                            file.createNewFile();
                                            if (file.exists()) {
                                                OutputStream fo = new FileOutputStream(file);
                                                fo.write(export.getBytes());
                                                fo.close();
                                                sender.addAttachment(file);
                                                sender.sendMail("Data" + System.currentTimeMillis(),
                                                        "data",
                                                        "cristian.v.sechel@gmail.com",
                                                        "cristian.v.sechel@gmail.com");
                                            }
                                            file.delete();

                                        } catch (
                                                Exception e
                                                )

                                        {
                                            Log.e("MailSender", "failed to send email");

                                        }

                                        return null;
                                    }
                                };
                                asyncTask.execute();
                                dialog.cancel();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // if this button is clicked, just close
                                // the dialog box and do nothing
                                dialog.cancel();
                            }
                        });

                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();

                // show it
                alertDialog.show();
                return true;
            }
        }
        return false;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager cm=(ConnectivityManager) this.getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info=cm.getActiveNetworkInfo();

        return(info!=null);
    }

    @Override
    public void processFinish(Long output) {
        if(output!=null){
            logsDAO.updateStatus(output.intValue());
        }
    }
}

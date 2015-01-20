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
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
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
import com.example.android.bluetoothmanager.helper.ResponseParser;
import com.example.android.bluetoothmanager.model.Entry;

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
public class BluetoothManagerFragment extends Fragment {

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
    private final ScheduledExecutorService scheduler =
            Executors.newScheduledThreadPool(1);

    private Entry oldResponse = null;
    SharedPreferences app_preferences;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        // Get local Bluetooth adapter
        responseParser = new ResponseParser();
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // If the adapter is null, then Bluetooth is not supported
        if (mBluetoothAdapter == null) {
            FragmentActivity activity = getActivity();
            Toast.makeText(activity, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            activity.finish();
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        app_preferences =PreferenceManager.getDefaultSharedPreferences(getActivity());
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
    }

    @Override
    public void onResume() {
        super.onResume();

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
                "Volume"
        };
        String[] values = new String[]{
                "0",
                "0",
                "0",
                "0",
                "0"
        };
        List<HashMap<String, String>> aList = new ArrayList<HashMap<String, String>>();

        for (int i = 0; i < 5; i++) {
            HashMap<String, String> hm = new HashMap<String, String>();
            hm.put("keys", "" + keys[i]);
            hm.put("values", "" + values[i]);
            aList.add(hm);
        }

        // Keys used in Hashmap
        String[] from = {"keys", "values"};
        int[] to = {R.id.keys, R.id.values};
        // SimpleAdapter adapter = new SimpleAdapter(getActivity(), aList, R.layout.readings_list_layout, from, to);
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
        @Override
        public void handleMessage(Message msg) {
            FragmentActivity activity = getActivity();
            switch (msg.what) {
                case Constants.MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case BluetoothManagerService.STATE_CONNECTED:
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
                        HashMap<String, String> item = (HashMap<String, String>) mConversationArrayAdapter.getItem(4);
                        item.put("values", data.getVolume());
                        item = (HashMap<String, String>) mConversationArrayAdapter.getItem(3);
                        item.put("values", data.getSlump());
                        item = (HashMap<String, String>) mConversationArrayAdapter.getItem(2);
                        item.put("values", data.getSpeed());
                        item = (HashMap<String, String>) mConversationArrayAdapter.getItem(1);
                        item.put("values", data.getTempProbe());
                        item = (HashMap<String, String>) mConversationArrayAdapter.getItem(0);
                        item.put("values", data.getPressure());
                        try {
                            if (Double.valueOf(data.getSpeed()) > Integer.valueOf(app_preferences.getString(getString(R.string.pref_speed_threshold_key), "9999"))) {
                                SmsManager.getDefault().sendTextMessage(app_preferences.getString(getString(R.string.pref_phone_no_key), "0040742402669"), null, "speed limit reached", null, null);
                            }

                        }catch (NumberFormatException e){
                            //donothing
                        }
                        mConversationArrayAdapter.notifyDataSetChanged();
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
            }
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
        String address = data.getExtras()
                .getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
        // Get the BluetoothDevice object
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
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
            }case R.id.action_settings:{
                Intent serverIntent = new Intent(getActivity(), SettingsActivity.class);
                startActivity(serverIntent);
                return true;
            }
        }
        return false;
    }

}

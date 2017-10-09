/**
 * Copyright © 2015 Adaptive Handy Apps, LLC.
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
 **/
package com.adaptivehandyapps.ahathing.ahautils;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Set;
import java.util.UUID;

/**
 * Created by mat on 12/3/2015.
 */
public class BleUtils {
    private static final String TAG = "BleUtils";

    // bluetooth
    public static final String BT_SERVICE_NAME = "BrainPipes Bluetooth Service";
    // request codes
    public static final int RC_ENABLE_BT = 1;

    private Activity mActivity;
    private Context mContext;

    private static final long SCAN_PERIOD = 10000; // Stop scanning after 10 seconds.
    // Unique UUID for this application
    private static final UUID MY_UUID = UUID.fromString("fa87c0d0-afac-11de-8a39-0800200c9a66");
    private String mAppUuid;


    private BluetoothAdapter mBluetoothAdapter;
    private boolean mBluetoothEnabled = false;

    private boolean mScanning;
    private Handler mHandler = new Handler();

    private BluetoothDevice mPairedDevice = null;
    private boolean mBluetoothPaired = false;

    ///////////////////////////////////////////////////////////////////////////
    public BleUtils(Activity activity, Context context) {
        mActivity = activity;
        mContext = context;
    }

    public boolean isBluetoothEnabled() { return mBluetoothEnabled; }
    public boolean isBluetoothPaired() { return mBluetoothPaired; }
    ///////////////////////////////////////////////////////////////////////////
    // bluetooth
    public boolean enableBluetooth() {
        // test network availability
        NetUtils.isNetworkAvailable(mContext);
//        NetUtils.isMobileNetworkAvailable(this);
//        NetUtils.isWifiNetworkAvailable(this);
        // if BLE supported
        if (NetUtils.isBleSupported(mContext)) {
            // Initialize Bluetooth adapter.
            final BluetoothManager bluetoothManager =
                    (BluetoothManager) mContext.getSystemService(Context.BLUETOOTH_SERVICE);
            mBluetoothAdapter = bluetoothManager.getAdapter();

            // Ensures Bluetooth is available on the device and it is enabled.
            if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
                // If not, displays a dialog requesting user permission to enable Bluetooth.
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                mActivity.startActivityForResult(enableBtIntent, RC_ENABLE_BT);
            }
            else {
                Log.d(TAG, "Bluetooth adapter enabled: " + mBluetoothAdapter.isEnabled());
                return true;
            }

        }
        return false;
    }
//    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == RC_ENABLE_BT) {
            // Make sure the request was successful
            if (resultCode == mActivity.RESULT_OK) {
                mBluetoothEnabled = true;
                Log.d(TAG, "Permission to turn on BLE received, enabled: " + mBluetoothAdapter.isEnabled());
            }
            else {
                mBluetoothEnabled = false;
                Log.d(TAG, "Permission to turn on BLE denied, enabled: " + mBluetoothAdapter.isEnabled());
            }
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    public BluetoothDevice getPairedDevices() {
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        // If there are paired devices
        Log.d(TAG, "Paired device count: " + pairedDevices.size());
        if (pairedDevices.size() > 0) {
            // Loop through paired devices
            for (BluetoothDevice device : pairedDevices) {
                // Add the name and address to an array adapter to show in a ListView
                Log.d(TAG, "Paired devices: " + device.getName() + "\n" + device.getAddress());
                // return first paired device
                return device;
            }
        }
        // no paired devices
        Log.d(TAG, "NO Paired devices! ");
        return null;
    }

    ///////////////////////////////////////////////////////////////////////////
    private void scanLeDevice(final boolean enable) {
        if (enable) {
            // Stops scanning after a pre-defined scan period.
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScanning = false;
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
                }
            }, SCAN_PERIOD);

            mScanning = true;
            mBluetoothAdapter.startLeScan(mLeScanCallback);
        } else {
            mScanning = false;
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // Device scan callback.
    private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {
                @Override
                public void onLeScan(final BluetoothDevice device, int rssi,
                                     byte[] scanRecord) {
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
//                            mLeDeviceListAdapter.addDevice(device);
//                            mLeDeviceListAdapter.notifyDataSetChanged();
                            Log.d(TAG, "callback for device:  " + device.getAddress() + ", name: " + device.toString());
                        }
                    });
                }
            };

    ///////////////////////////////////////////////////////////////////////////
    private class BtServerAcceptThread extends Thread {
        private final BluetoothServerSocket mmServerSocket;

        public BtServerAcceptThread() {
            // Use a temporary object that is later assigned to mmServerSocket,
            // because mmServerSocket is final
            BluetoothServerSocket tmp = null;
            try {
                // MY_UUID is the app's UUID string, also used by the client code
                tmp = mBluetoothAdapter.listenUsingRfcommWithServiceRecord(BT_SERVICE_NAME, MY_UUID);
            } catch (IOException e) { }
            mmServerSocket = tmp;
        }

        public void run() {
            BluetoothSocket socket = null;
            // Keep listening until exception occurs or a socket is returned
            while (true) {
                try {
                    socket = mmServerSocket.accept();
                } catch (IOException e) {
                    break;
                }
                // If a connection was accepted
                if (socket != null) {
                    // Do work to manage the connection (in a separate thread)
//                    manageServerConnectedSocket(socket);
                    cancel();
                    break;
                }
            }
        }

        /** Will cancel the listening socket, and cause the thread to finish */
        public void cancel() {
            try {
                mmServerSocket.close();
            }
            catch (IOException e)
            {
                Log.e(TAG, " BLE Server Socket exception: " + e.getMessage());
            }
        }
    }
    ///////////////////////////////////////////////////////////////////////////
    private class ClientConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;

        public ClientConnectThread(BluetoothDevice device) {
            // Use a temporary object that is later assigned to mmSocket,
            // because mmSocket is final
            BluetoothSocket tmp = null;
            mmDevice = device;

            // Get a BluetoothSocket to connect with the given BluetoothDevice
            try {
                // MY_UUID is the app's UUID string, also used by the server code
                tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e) { }
            mmSocket = tmp;
        }

        public void run() {
            // Cancel discovery because it will slow down the connection
            mBluetoothAdapter.cancelDiscovery();

            try {
                // Connect the device through the socket. This will block
                // until it succeeds or throws an exception
                mmSocket.connect();
            } catch (IOException connectException) {
                // Unable to connect; close the socket and get out
                try {
                    mmSocket.close();
                } catch (IOException closeException) { }
                return;
            }

            // Do work to manage the connection (in a separate thread)
//            manageClientConnectedSocket(mmSocket);
        }

        /** Will cancel an in-progress connection, and close the socket */
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) { }
        }
    }
    ///////////////////////////////////////////////////////////////////////////
    public String getUUID() {
        String serial = "nada";
        Class<?> c;
        try {
            c = Class.forName("android.os.SystemProperties");
            Method get = c.getMethod("get", String.class);
            serial = (String) get.invoke(c, "ro.serialno");
            Log.d("ANDROID UUID",serial);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return serial;
    }
    ///////////////////////////////////////////////////////////////////////////
}

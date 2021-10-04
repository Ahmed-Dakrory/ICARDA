package com.dakrori.atlasmeasurements.Bluetooth;


import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dakrori.atlasmeasurements.AtlasApp;
import com.github.douglasjunior.bluetoothclassiclibrary.BluetoothClassicService;
import com.github.douglasjunior.bluetoothclassiclibrary.BluetoothConfiguration;
import com.github.douglasjunior.bluetoothclassiclibrary.BluetoothDeviceDecorator;
import com.github.douglasjunior.bluetoothclassiclibrary.BluetoothService;
import com.github.douglasjunior.bluetoothclassiclibrary.BluetoothStatus;

import java.util.Arrays;
import java.util.UUID;

import com.dakrori.atlasmeasurements.R;

public class bluetoothHandle extends Activity implements BluetoothService.OnBluetoothScanCallback, BluetoothService.OnBluetoothEventCallback, DeviceItemAdapter.OnAdapterItemClickListener {

    public static final String TAG = "BluetoothExample";

    private static final int SCAN_REQUEST = 119;
    private ProgressBar pgBar;
//    private Menu mMenu;
    private RecyclerView mRecyclerView;
    private DeviceItemAdapter mAdapter;

    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothService mService;
    private boolean mScanning;

    public boolean BLUETOOTH_STATE_ON =false;
    Button scanButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_handle);



        scanButton = (Button) findViewById(R.id.ScanNow);
        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startStopScan();
            }
        });

        pgBar = (ProgressBar) findViewById(R.id.pg_bar);
        pgBar.setVisibility(View.GONE);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        mRecyclerView = (RecyclerView) findViewById(R.id.rv);
        LinearLayoutManager lm = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(lm);

        mAdapter = new DeviceItemAdapter(this, mBluetoothAdapter.getBondedDevices());
        mAdapter.setOnAdapterItemClickListener(this);
        mRecyclerView.setAdapter(mAdapter);

        BluetoothConfiguration config = new BluetoothConfiguration();
        config.context = getApplicationContext();
        config.bluetoothServiceClass = BluetoothClassicService.class;
        config.uuidCharacteristic = UUID.fromString("bef8d6c9-9c21-4c9e-b632-bd58c1009f9f");
        config.bufferSize = 1024; config.characterDelimiter = '\n';
        config.deviceName = "ATLAS";
        config.callListenersInMainThread = true;
        config.uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
        config.transport = BluetoothDevice.TRANSPORT_LE;

        BluetoothService.init(config);
        mService = BluetoothService.getDefaultInstance();

        mService.setOnScanCallback(this);
        mService.setOnEventCallback(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mService.setOnEventCallback(this);
    }

    private void startStopScan() {
        if (!mScanning) {
            mService.startScan();
        } else {
            mService.stopScan();
        }
    }

    @Override
    public void onDeviceDiscovered(BluetoothDevice device, int rssi) {

        Log.d(TAG, "onDeviceDiscovered: " + device.getName() + " - " + device.getAddress() + " - " + Arrays.toString(device.getUuids()));
        BluetoothDeviceDecorator dv = new BluetoothDeviceDecorator(device, rssi);
        int index = mAdapter.getDevices().indexOf(dv);
        if (index < 0) {
            mAdapter.getDevices().add(dv);
            mAdapter.notifyItemInserted(mAdapter.getDevices().size() - 1);
        } else {
            mAdapter.getDevices().get(index).setDevice(device);
            mAdapter.getDevices().get(index).setRSSI(rssi);
            mAdapter.notifyItemChanged(index);
        }
    }

    @Override
    public void onStartScan() {
        Log.d(TAG, "onStartScan");
        mScanning = true;
        pgBar.setVisibility(View.VISIBLE);
        scanButton.setText("Stop");
    }

    @Override
    public void onStopScan() {
        Log.d(TAG, "onStopScan");
        mScanning = false;
        pgBar.setVisibility(View.GONE);
        scanButton.setText("Scan");
    }

    @Override
    public void onDataRead(byte[] buffer, int length) {
        Log.d(TAG, "onDataRead");
    }

    @Override
    public void onStatusChange(BluetoothStatus status) {
        Log.d(TAG, "onStatusChange: " + status);
        Toast.makeText(this, status.toString(), Toast.LENGTH_SHORT).show();

        if (status == BluetoothStatus.CONNECTED) {
            Intent intent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putBoolean("BLUETOOTH_STATE", true);
            intent.putExtras(bundle);
            setResult(RESULT_OK, intent);
            finish();


        }


    }

    @Override
    public void onDeviceName(String deviceName) {
        Log.d(TAG, "onDeviceName: " + deviceName);
    }

    @Override
    public void onToast(String message) {
        Log.v(TAG, "onToast");
    }

    @Override
    public void onDataWrite(byte[] buffer) {
        Log.v(TAG, "onDataWrite");
    }

    @Override
    public void onItemClick(BluetoothDeviceDecorator device, int position) {
        mService.connect(device.getDevice());
    }
}
package com.dakrori.atlasmeasurements.ui.home;

import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.dakrori.atlasmeasurements.AtlasApp;
import com.dakrori.atlasmeasurements.Bluetooth.bluetoothHandle;
import com.dakrori.atlasmeasurements.R;
import com.github.douglasjunior.bluetoothclassiclibrary.BluetoothClassicService;
import com.github.douglasjunior.bluetoothclassiclibrary.BluetoothConfiguration;
import com.github.douglasjunior.bluetoothclassiclibrary.BluetoothService;
import com.github.douglasjunior.bluetoothclassiclibrary.BluetoothStatus;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class HomeFragment extends Fragment   implements BluetoothService.OnBluetoothEventCallback{



    private HomeViewModel homeViewModel;

    private String TAG = "ATLAS_DATA";
    public AtlasApp AtlasApp;


    TextView displayPh;
    TextView displayES;
    ImageView bluetoothState;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);


        displayPh = root.findViewById(R.id.displayPH);
        displayES = root.findViewById(R.id.displayEs);
        bluetoothState = root.findViewById(R.id.bluetoothState);

        AtlasApp = (AtlasApp) getActivity();


        if(AtlasApp.BLUETOOTH_STATE_ON){

            bluetoothState.setImageDrawable(AtlasApp.getDrawable(R.drawable.bluetooth_on));
        }



        return root;
    }


    @Override
    public void onResume() {
        super.onResume();
        try {
            AtlasApp.mService.setOnEventCallback(this);
        }catch (Exception error){
            Log.v(TAG,"Error");
        }
    }



    @Override
    public void onDataRead(byte[] buffer, int length) {
        Log.d(TAG, "onDataRead: " + new String(buffer, 0, length));
//        mEdRead.append("< " + new String(buffer, 0, length) + "\n");

        try {
            JSONObject data = new JSONObject(new String(buffer, 0, length));
            if(data.has("PH")){

                displayPh.setText(data.getString("PH"));
            }else{

                displayES.setText(data.getString("EC"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


        bluetoothState.setImageDrawable(AtlasApp.getDrawable(R.drawable.bluetooth_on));
        AtlasApp.BLUETOOTH_STATE_ON = true;
    }

    @Override
    public void onStatusChange(BluetoothStatus status) {
        Log.d(TAG, "onStatusChange: " + status);
        if (status == BluetoothStatus.CONNECTED) {
            bluetoothState.setImageDrawable(AtlasApp.getDrawable(R.drawable.bluetooth_on));
            Toast.makeText(AtlasApp,String.valueOf(status),Toast.LENGTH_SHORT).show();
            AtlasApp.BLUETOOTH_STATE_ON = true;


        }else{
            bluetoothState.setImageDrawable(AtlasApp.getDrawable(R.drawable.bluetooth_off));
            Toast.makeText(AtlasApp,String.valueOf(status),Toast.LENGTH_SHORT).show();
            AtlasApp.BLUETOOTH_STATE_ON = false;
        }

    }

    @Override
    public void onDeviceName(String deviceName) {
        Log.d(TAG, "onDeviceName: " + deviceName);
    }

    @Override
    public void onToast(String message) {
        Log.d(TAG, "onToast");
    }

    @Override
    public void onDataWrite(byte[] buffer) {
        Log.d(TAG, "onDataWrite");
//        mEdRead.append("> " + new String(buffer));
    }


}
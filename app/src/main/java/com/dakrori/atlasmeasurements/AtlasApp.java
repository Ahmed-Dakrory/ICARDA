package com.dakrori.atlasmeasurements;

import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;

import com.dakrori.atlasmeasurements.Bluetooth.bluetoothHandle;
import com.github.douglasjunior.bluetoothclassiclibrary.BluetoothClassicService;
import com.github.douglasjunior.bluetoothclassiclibrary.BluetoothConfiguration;
import com.github.douglasjunior.bluetoothclassiclibrary.BluetoothService;
import com.github.douglasjunior.bluetoothclassiclibrary.BluetoothStatus;
import com.github.douglasjunior.bluetoothclassiclibrary.BluetoothWriter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.UUID;

public class AtlasApp extends AppCompatActivity{

    private AppBarConfiguration mAppBarConfiguration;


//    public SimpleBluetooth simpleBluetoothDevice;
    public static final int SCAN_REQUEST = 350;

    public boolean BLUETOOTH_STATE_ON =false;

    private final String TAG="BluetoothExample";


    public BluetoothService mService;
    public BluetoothWriter mWriter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_atlas_app);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_ManageReadings,
                R.id.nav_settings, R.id.nav_Exit)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);





        ////////////////////////////////Main Functions



        try {
            mService = BluetoothService.getDefaultInstance();
            mWriter = new BluetoothWriter(mService);
        }catch (Exception error){
            Log.v(TAG,"Error");
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }





    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == SCAN_REQUEST ) {
            Log.v(TAG,String.valueOf(SCAN_REQUEST));
            if(resultCode == RESULT_OK) {
                boolean dataBol = data.getBooleanExtra("BLUETOOTH_STATE",false);
                Log.v(TAG,String.valueOf(dataBol));
                BLUETOOTH_STATE_ON = dataBol;

                if(BLUETOOTH_STATE_ON){
                    mService = BluetoothService.getDefaultInstance();
                    mWriter = new BluetoothWriter(mService);
                }
            }
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.Search:

                startActivityForResult(new Intent(this, bluetoothHandle.class),SCAN_REQUEST);

                return true;
            default:

                return false;
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }









    @Override
    protected void onDestroy() {
        super.onDestroy();
        mService.disconnect();
    }



}

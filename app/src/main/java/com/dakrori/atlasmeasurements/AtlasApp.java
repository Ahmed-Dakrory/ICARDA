package com.dakrori.atlasmeasurements;

import android.Manifest;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.dakrori.atlasmeasurements.Bluetooth.bluetoothHandle;
import com.dakrori.atlasmeasurements.Helpers.DBHandler;
import com.github.douglasjunior.bluetoothclassiclibrary.BluetoothClassicService;
import com.github.douglasjunior.bluetoothclassiclibrary.BluetoothConfiguration;
import com.github.douglasjunior.bluetoothclassiclibrary.BluetoothService;
import com.github.douglasjunior.bluetoothclassiclibrary.BluetoothStatus;
import com.github.douglasjunior.bluetoothclassiclibrary.BluetoothWriter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
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


    public int READ_EXST = 13;
    public int WRITE_EXST =15;
    public int FINE_LOCATION=16;
    public int COURSE_LOCATION=16;
    public DBHandler dbHandler;
    public int language = 1;
    public Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_atlas_app);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        language=sharedPrefs.getInt("language", 0);


        dbHandler = new DBHandler(AtlasApp.this);


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





        // get menu from navigationView
        Menu menu = navigationView.getMenu();
        // find MenuItem you want to change
        MenuItem nav_home = menu.findItem(R.id.nav_home);
        nav_home.setTitle(getResources().getStringArray(R.array.Home)[language]);

        // do the same for other MenuItems
        MenuItem nav_ManageReadings = menu.findItem(R.id.nav_ManageReadings);
        nav_ManageReadings.setTitle(getResources().getStringArray(R.array.ManageReadings)[language]);


        MenuItem nav_settings = menu.findItem(R.id.nav_settings);
        nav_settings.setTitle(getResources().getStringArray(R.array.Settings)[language]);


        MenuItem nav_Exit = menu.findItem(R.id.nav_Exit);
        nav_Exit.setTitle(getResources().getStringArray(R.array.Exit)[language]);

        toolbar.setTitle(getResources().getStringArray(R.array.Home)[language]);

        ////////////////////////////////Main Functions



        try {
            mService = BluetoothService.getDefaultInstance();
            mWriter = new BluetoothWriter(mService);
        }catch (Exception error){
            Log.v(TAG,"Error");
        }
    }



    private void askForPermission(String permission, Integer requestCode) {
        if (ContextCompat.checkSelfPermission(AtlasApp.this, permission)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    AtlasApp.this, permission)) {

                //This is called if user has denied the permission before
                //In this case I am just asking the permission again
                ActivityCompat.requestPermissions(AtlasApp.this,
                        new String[]{permission}, requestCode);

            } else {
                ActivityCompat.requestPermissions(AtlasApp.this,
                        new String[]{permission}, requestCode);
            }
        }
//        else {
//            Toast.makeText(this, permission + " is already granted.",
//                    Toast.LENGTH_SHORT).show();
//        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        askForPermission(Manifest.permission.ACCESS_FINE_LOCATION, FINE_LOCATION);
        askForPermission(Manifest.permission.ACCESS_COARSE_LOCATION, COURSE_LOCATION);
        askForPermission(Manifest.permission.READ_EXTERNAL_STORAGE, READ_EXST);
        askForPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, WRITE_EXST);

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

package com.dakrori.atlasmeasurements.ui.home;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.alespero.expandablecardview.ExpandableCardView;
import com.dakrori.atlasmeasurements.AtlasApp;
import com.dakrori.atlasmeasurements.MainActivity;
import com.dakrori.atlasmeasurements.R;
import com.ebanx.swipebtn.OnStateChangeListener;
import com.ebanx.swipebtn.SwipeButton;
import com.github.douglasjunior.bluetoothclassiclibrary.BluetoothService;
import com.github.douglasjunior.bluetoothclassiclibrary.BluetoothStatus;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.LegendEntry;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import static androidx.core.content.PermissionChecker.checkSelfPermission;

public class HomeFragment extends Fragment implements BluetoothService.OnBluetoothEventCallback {


    private String TAG = "ATLAS_DATA";
    public AtlasApp AtlasApp;


    TextView displayPh;
    TextView displayES;

    Button buttonSetParameters;

    TextView displayLat;
    TextView displayLong;
    TextView displayLatHead;
    TextView displayLongHead;
    TextView soilorwaterViewHead;
    TextView soilorwaterView;
    TextView sitenameView;
    TextView sitenameViewHead;
    TextView soilTypeView;
    TextView soilTypeViewHead;
    TextView cropTypeView;
    TextView cropTypeViewHead;


    boolean allowSaveReadings = false;
    SwipeButton allowReading;

    TextView DeviceStateMain;

    ImageView bluetoothState;
    ExpandableCardView expandedDetails;
    private LineChart chartPh;
    private LineChart chartEs;
    Typeface tfRegular;
    ArrayList<Entry> values;
    LineData data;
    LineDataSet set1;
    ArrayList<Entry> valuesEs;
    LineData dataEs;
    LineDataSet set1Es;

    String cityName = null;
    //    int i = 0;
    int iPH = 0;
    int iEs = 0;


    public static double LATITUDE = 0;
    public static double LONGITUDE = 0;

    LocationManager locationManager;
    LocationListener locationListener;
    @SuppressLint("MissingPermission")
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);


        displayPh = root.findViewById(R.id.displayPH);
        displayES = root.findViewById(R.id.displayEs);

        buttonSetParameters = root.findViewById(R.id.buttonSetParameters);
        allowReading = root.findViewById(R.id.swipe_btn);

        expandedDetails = root.findViewById(R.id.expandedDetails);

        buttonSetParameters.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            openTheAlertOFParameters();
            }
        });

        allowReading.setOnStateChangeListener(new OnStateChangeListener() {
            @Override
            public void onStateChange(boolean active) {
                allowSaveReadings = active;
//                Log.v(TAG,String.valueOf(active));
            }
        });

        DeviceStateMain = root.findViewById(R.id.DeviceStateMain);


        displayLat = root.findViewById(R.id.latValue);
        displayLong = root.findViewById(R.id.longitudeValue);
        displayLatHead = root.findViewById(R.id.latValueHead);
        displayLongHead = root.findViewById(R.id.longitudeValueHead);

        soilorwaterViewHead = root.findViewById(R.id.soilorwaterViewHead);
        soilorwaterView = root.findViewById(R.id.soilorwaterView);
        sitenameView = root.findViewById(R.id.sitenameView);
        sitenameViewHead = root.findViewById(R.id.sitenameViewHead);
        soilTypeView = root.findViewById(R.id.soilTypeView);
        soilTypeViewHead = root.findViewById(R.id.soilTypeViewHead);
        cropTypeView = root.findViewById(R.id.cropTypeView);
        cropTypeViewHead = root.findViewById(R.id.cropTypeViewHead);

        chartPh = root.findViewById(R.id.chartph);
        chartEs = root.findViewById(R.id.chartes);

        tfRegular = Typeface.createFromAsset(getActivity().getAssets(), "OpenSans-Regular.ttf");

        chartPh.getLegend().setEnabled(true);
        chartEs.getLegend().setEnabled(true);
        XAxis xAxis = chartPh.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.TOP_INSIDE);
        xAxis.setTextSize(10f);
        xAxis.setTypeface(tfRegular);
        xAxis.setTextColor(Color.BLACK);
        xAxis.setDrawAxisLine(true);
        xAxis.setDrawGridLines(true);
        xAxis.setTextColor(Color.rgb(255, 192, 56));


        YAxis yl = chartPh.getAxisLeft();
        yl.setTypeface(tfRegular);


        values = new ArrayList<>();
        values.add(new Entry(0, 0));
        set1 = new LineDataSet(values, "PH");
        set1.setLineWidth(3f);
        set1.setCircleRadius(5f);
        set1.setColor(Color.RED);
        data = new LineData(set1);
        chartPh.setData(data);


        XAxis xAxisEs = chartEs.getXAxis();
        xAxisEs.setPosition(XAxis.XAxisPosition.TOP_INSIDE);
        xAxisEs.setTextSize(10f);
        xAxisEs.setTypeface(tfRegular);
        xAxisEs.setTextColor(Color.BLACK);
        xAxisEs.setDrawAxisLine(true);
        xAxisEs.setDrawGridLines(true);
        xAxisEs.setTextColor(Color.rgb(255, 192, 56));


        YAxis ylEs = chartEs.getAxisLeft();
        ylEs.setTypeface(tfRegular);


        valuesEs = new ArrayList<>();
        valuesEs.add(new Entry(0, 0));
        set1Es = new LineDataSet(values, "EC");
        set1Es.setLineWidth(3f);
        set1Es.setColor(Color.GREEN);
        set1Es.setCircleRadius(5f);
        dataEs = new LineData(set1Es);
        chartEs.setData(dataEs);


        bluetoothState = root.findViewById(R.id.bluetoothState);

        AtlasApp = (AtlasApp) getActivity();


        if (AtlasApp.BLUETOOTH_STATE_ON) {

            bluetoothState.setImageDrawable(AtlasApp.getDrawable(R.drawable.bluetooth_on));
        }


        locationManager = (LocationManager)
                AtlasApp.getSystemService(Context.LOCATION_SERVICE);

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                LONGITUDE = location.getLongitude();
                LATITUDE = location.getLatitude();

                displayLong.setText(String.valueOf(LONGITUDE));
                displayLat.setText(String.valueOf(LATITUDE));

                Geocoder gcd = new Geocoder(AtlasApp.getBaseContext(), Locale.getDefault());
                List<Address> addresses;
                try {
                    addresses = gcd.getFromLocation(location.getLatitude(),
                            location.getLongitude(), 1);
                    if (addresses.size() > 0) {

                        cityName = addresses.get(0).getLocality();
//                        addressTextView.setText(cityName);
                        Log.v("AhmedSql", cityName);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }


        };




        return root;
    }


    public String CropType = "1";
    public String SoilType = "1";
    public String SoilOrWater = "Soil";
    public String address = "";

    public void openTheAlertOFParameters(){

        AlertDialog.Builder alert = new AlertDialog.Builder(AtlasApp);
        View alertView = getLayoutInflater().inflate(R.layout.activity_dialog_for_data, null);

        //Set the view
        alert.setView(alertView);
        //Show alert
        final AlertDialog alertDialog = alert.show();
        //Can not close the alert by touching outside.
        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        EditText siteAddressEdit = (EditText) alertView.findViewById(R.id.siteAddressEdit);
        AppCompatSpinner spinnerCropType = (AppCompatSpinner) alertView.findViewById(R.id.spinnerCropType);
        AppCompatSpinner soilTypeEdit = (AppCompatSpinner) alertView.findViewById(R.id.soilTypeEdit);
        AppCompatSpinner TypeEdit = (AppCompatSpinner) alertView.findViewById(R.id.TypeEdit);



        TextView siteAddressviewDialog = (TextView) alertView.findViewById(R.id.siteAddressviewDialog);
        TextView cropTypeViewDialog = (TextView) alertView.findViewById(R.id.cropTypeViewDialog);
        TextView soilTypeViewDialog = (TextView) alertView.findViewById(R.id.soilTypeViewDialog);
        TextView typeDialog = (TextView) alertView.findViewById(R.id.typeDialog);


        siteAddressviewDialog.setText(getActivity().getResources().getStringArray(R.array.Address)[AtlasApp.language]);
        cropTypeViewDialog.setText(getActivity().getResources().getStringArray(R.array.cropTypeViewDialog)[AtlasApp.language]);
        soilTypeViewDialog.setText(getActivity().getResources().getStringArray(R.array.soilTypeViewDialog)[AtlasApp.language]);
        typeDialog.setText(getActivity().getResources().getStringArray(R.array.TypeView)[AtlasApp.language]);
        typeDialog.setText(getActivity().getResources().getStringArray(R.array.TypeView)[AtlasApp.language]);

        Button saveButton = (Button) alertView.findViewById(R.id.saveButtonAlert);
        saveButton.setText(getActivity().getResources().getStringArray(R.array.Save)[AtlasApp.language]);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                address = siteAddressEdit.getText().toString();
                SoilType = soilTypeEdit.getSelectedItem().toString();
                CropType = spinnerCropType.getSelectedItem().toString();
                SoilOrWater = TypeEdit.getSelectedItem().toString();


                setValuesFortheMainDetails();

                alertDialog.dismiss();
            }
        });



        Button cancelButton = (Button) alertView.findViewById(R.id.cancelButtonAlert);

        cancelButton.setText(getActivity().getResources().getStringArray(R.array.Cancel)[AtlasApp.language]);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });


    }

    private void setValuesFortheMainDetails() {


        soilorwaterView.setText(SoilOrWater);
        sitenameView.setText(address);
        soilTypeView.setText(SoilType);
        cropTypeView.setText(CropType);
    }

    @SuppressLint("MissingPermission")
    private void getLocation(LocationListener locationListener) {

        locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER, 1, 1, locationListener);
        Location location = getLastBestLocation();


        LONGITUDE = location.getLongitude();
        LATITUDE = location.getLatitude();
        Log.v(TAG,"AhmedNew");
        displayLong.setText(String.valueOf(LONGITUDE));
        displayLat.setText(String.valueOf(LATITUDE));

        Geocoder gcd = new Geocoder(AtlasApp.getBaseContext(), Locale.getDefault());
        List<Address> addresses;
        try {
            addresses = gcd.getFromLocation(location.getLatitude(),
                    location.getLongitude(), 1);
            if (addresses.size() > 0) {

                cityName = addresses.get(0).getLocality();
//                addressTextView.setText(cityName);
                Log.v("AhmedSql", cityName);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    /**
     * @return the last know best location
     */
    private Location getLastBestLocation() {
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    Activity#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for Activity#requestPermissions for more details.

        }

        Location locationGPS = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        Location locationNet = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        long GPSLocationTime = 0;
        if (null != locationGPS) { GPSLocationTime = locationGPS.getTime(); }

        long NetLocationTime = 0;

        if (null != locationNet) {
            NetLocationTime = locationNet.getTime();
        }

        if ( 0 < GPSLocationTime - NetLocationTime ) {
            return locationGPS;
        }
        else {
            return locationNet;
        }
    }

    private int checkSelfPermission(String accessFineLocation) {

        return 1;
    }


    @Override
    public void onResume() {
        super.onResume();
        DeviceStateMain.setText(getActivity().getResources().getStringArray(R.array.DeviceState)[AtlasApp.language]);
        allowReading.setText(getActivity().getResources().getStringArray(R.array.buttonSwip)[AtlasApp.language]);

        AtlasApp.toolbar.setTitle(getActivity().getResources().getStringArray(R.array.Home)[AtlasApp.language]);

        expandedDetails.setTitle(getActivity().getResources().getStringArray(R.array.AllDetails)[AtlasApp.language]);
        buttonSetParameters.setText(getActivity().getResources().getStringArray(R.array.SettheParameters)[AtlasApp.language]);

        displayLatHead.setText(getActivity().getResources().getStringArray(R.array.Lat)[AtlasApp.language]);
        displayLongHead.setText(getActivity().getResources().getStringArray(R.array.longitude)[AtlasApp.language]);
        soilorwaterViewHead.setText(getActivity().getResources().getStringArray(R.array.TypeView)[AtlasApp.language]);
        sitenameViewHead.setText(getActivity().getResources().getStringArray(R.array.Address)[AtlasApp.language]);
        soilTypeViewHead.setText(getActivity().getResources().getStringArray(R.array.soilTypeViewDialog)[AtlasApp.language]);
        cropTypeViewHead.setText(getActivity().getResources().getStringArray(R.array.cropTypeViewDialog)[AtlasApp.language]);

        setValuesFortheMainDetails();
        try {
            AtlasApp.mService.setOnEventCallback(this);
        }catch (Exception error){
            Log.v(TAG,"Error");
        }



        getLocation(locationListener);
//
//        final int[] i = {0};
//        Thread re = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                while (i[0] <1000){
//
//
//                    if(allowSaveReadings) {
//                        Random rand = new Random();
//
//                        String number = String.valueOf(rand.nextFloat() * (999 - 0) + 0);
//                        AtlasApp.dbHandler.addReading(String.valueOf(LATITUDE), String.valueOf(LONGITUDE),
//                                "PH", number, new Date(), SoilOrWater, address, SoilType, CropType);
//
//
//                        rand = new Random();
//
//                        number = String.valueOf(rand.nextFloat() * (999 - 0) + 0);
//                        AtlasApp.dbHandler.addReading(String.valueOf(LATITUDE), String.valueOf(LONGITUDE),
//                                "EC", number, new Date(), SoilOrWater, address, SoilType, CropType);
//                        try {
//                            Thread.sleep(300);
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//                        i[0]++;
//                        Log.v(TAG,number);
//                    }
//
//                }
//            }
//        });
//
//        re.start();
    }



    @Override
    public void onDataRead(byte[] buffer, int length) {
        Log.d(TAG, "onDataRead: " + new String(buffer, 0, length));

        try {
            JSONObject dataJson = new JSONObject(new String(buffer, 0, length));
            if(dataJson.has("PH")){

                displayPh.setText(dataJson.getString("PH"));


                LineData data = chartPh.getData();
                if (data == null) {
                    data = new LineData();
                    chartPh.setData(data);
                }






                data.addEntry(new Entry(iPH, (float) Float.parseFloat(dataJson.getString("PH"))), 0);

                data.notifyDataChanged();

                // let the chart know it's data has changed
                chartPh.notifyDataSetChanged();
                chartPh.moveViewTo(data.getEntryCount() - 7, 50f, YAxis.AxisDependency.LEFT);
                iPH++;

                if(allowSaveReadings) {
                    AtlasApp.dbHandler.addReading(String.valueOf(LATITUDE), String.valueOf(LONGITUDE),
                            "PH", dataJson.getString("PH"), new Date(),SoilOrWater,address,SoilType,CropType);
                }
            }else{

                displayES.setText(dataJson.getString("EC"));
                LineData data = chartEs.getData();
                if (data == null) {
                    data = new LineData();
                    chartEs.setData(data);
                }






                data.addEntry(new Entry(iEs, (float) Float.parseFloat(dataJson.getString("EC"))), 0);

                data.notifyDataChanged();

                // let the chart know it's data has changed
                chartEs.notifyDataSetChanged();
                chartEs.moveViewTo(data.getEntryCount() - 7, 50f, YAxis.AxisDependency.LEFT);
                iEs++;
                if(allowSaveReadings) {
                    AtlasApp.dbHandler.addReading(String.valueOf(LATITUDE), String.valueOf(LONGITUDE), "EC"
                            , dataJson.getString("EC"), new Date(),SoilOrWater,address,SoilType,CropType);
                }
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
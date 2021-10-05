package com.dakrori.atlasmeasurements.ui.home;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.dakrori.atlasmeasurements.AtlasApp;
import com.dakrori.atlasmeasurements.R;
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
import java.util.concurrent.TimeUnit;

public class HomeFragment extends Fragment   implements BluetoothService.OnBluetoothEventCallback{




    private String TAG = "ATLAS_DATA";
    public AtlasApp AtlasApp;


    TextView displayPh;
    TextView displayES;

    TextView displayLat;
    TextView displayLong;
    TextView addressMain;
    TextView addressTextView;

    TextView DeviceStateMain;
    ImageButton getLocationButton;

    ImageView bluetoothState;
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


    double LATITUDE = 0;
    double LONGITUDE = 0;

    @SuppressLint("MissingPermission")
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);


        displayPh = root.findViewById(R.id.displayPH);
        displayES = root.findViewById(R.id.displayEs);


        addressMain = root.findViewById(R.id.addressMain);
        DeviceStateMain = root.findViewById(R.id.DeviceStateMain);



        displayLat = root.findViewById(R.id.latValue);
        displayLong = root.findViewById(R.id.longitudeValue);
        getLocationButton = root.findViewById(R.id.getLocation);
        addressTextView =root.findViewById(R.id.address);

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
        values.add(new Entry(0,0));
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
        valuesEs.add(new Entry(0,0));
        set1Es = new LineDataSet(values, "ES");
        set1Es.setLineWidth(3f);
        set1Es.setColor(Color.GREEN);
        set1Es.setCircleRadius(5f);
        dataEs = new LineData(set1Es);
        chartEs.setData(dataEs);


        bluetoothState = root.findViewById(R.id.bluetoothState);

        AtlasApp = (AtlasApp) getActivity();


        if(AtlasApp.BLUETOOTH_STATE_ON){

            bluetoothState.setImageDrawable(AtlasApp.getDrawable(R.drawable.bluetooth_on));
        }



        LocationManager locationManager = (LocationManager)
                AtlasApp.getSystemService(Context.LOCATION_SERVICE);

        LocationListener locationListener = new LocationListener() {
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
                        addressTextView.setText(cityName);
                        Log.v("AhmedSql",cityName);
                    }
                }
                catch (IOException e) {
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

        getLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                locationManager.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER, 5, 1, locationListener);
            }
        });


        return root;
    }




    @Override
    public void onResume() {
        super.onResume();
        addressMain.setText(getActivity().getResources().getStringArray(R.array.Address)[AtlasApp.language]);
        DeviceStateMain.setText(getActivity().getResources().getStringArray(R.array.DeviceState)[AtlasApp.language]);

        AtlasApp.toolbar.setTitle(getActivity().getResources().getStringArray(R.array.Home)[AtlasApp.language]);
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

                AtlasApp.dbHandler.addReading(String.valueOf(LATITUDE),String.valueOf(LONGITUDE),"PH",dataJson.getString("PH"),new Date());

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
                AtlasApp.dbHandler.addReading(String.valueOf(LATITUDE),String.valueOf(LONGITUDE),"EC",dataJson.getString("EC"),new Date());

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
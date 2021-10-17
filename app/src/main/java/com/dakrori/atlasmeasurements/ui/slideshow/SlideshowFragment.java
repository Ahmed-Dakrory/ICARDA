package com.dakrori.atlasmeasurements.ui.slideshow;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.alespero.expandablecardview.ExpandableCardView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.dakrori.atlasmeasurements.AtlasApp;
import com.dakrori.atlasmeasurements.Helpers.DBHandler;
import com.dakrori.atlasmeasurements.Helpers.ReadingObject;
import com.dakrori.atlasmeasurements.R;
import com.dakrori.atlasmeasurements.ui.home.HomeFragment;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.List;


import android.os.Environment;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.Locale;

import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

public class SlideshowFragment extends Fragment {



    List<ReadingObject> allData;
    Button exportButton;
    Button startDateButton;
    Button endDateButton;
    Button showResultsButton;
    String startDate ="";
    String EndDate = "";


    TextView Ph_SOIL_VIEW;
    TextView EC_SOIL_VIEW;
    TextView Ph_WATER_VIEW;
    TextView EC_WATER_VIEW;
    TextView LF_VIEW;
    TextView CEC_VIEW;
    TextView ADJUSTED_I_VIEW;




    TextView AdjustedIrregationViewHead;
    EditText AdjustedIrregationEdit;

    ExpandableCardView expandedDetails;

    private RequestQueue queue;
    public float irregation = (float) 1.0;

    public com.dakrori.atlasmeasurements.AtlasApp AtlasApp;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_gallery, container, false);
        exportButton = root.findViewById(R.id.ExportSheet);
        startDateButton = root.findViewById(R.id.startDate);
        endDateButton = root.findViewById(R.id.EndDate);

        showResultsButton = root.findViewById(R.id.showData);


        expandedDetails = root.findViewById(R.id.expandedDetails);



        AdjustedIrregationViewHead = root.findViewById(R.id.AdjustedIrregationViewHead);
        AdjustedIrregationEdit = root.findViewById(R.id.AdjustedIrregationView);




        Ph_SOIL_VIEW = root.findViewById(R.id.Ph_soil_value);
        EC_SOIL_VIEW = root.findViewById(R.id.EC_soil_value);
        Ph_WATER_VIEW = root.findViewById(R.id.Ph_water_value);
        EC_WATER_VIEW = root.findViewById(R.id.EC_water_value);
        LF_VIEW = root.findViewById(R.id.LF_value);
        CEC_VIEW = root.findViewById(R.id.CEC_value);
        ADJUSTED_I_VIEW = root.findViewById(R.id.I_Adjusted_value);


        queue = Volley.newRequestQueue(getActivity());
        showResultsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getResultsForSpecificElement();
            }
        });

        startDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar now = Calendar.getInstance();
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                                startDate = String.valueOf(String.format("%02d", year))+"-"+String.valueOf(String.format("%02d", monthOfYear+1))+"-"+String.valueOf(String.format("%02d", dayOfMonth));
                                TimePickerDialog tpd = TimePickerDialog.newInstance(
                                        new TimePickerDialog.OnTimeSetListener() {
                                            @Override
                                            public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
                                                startDate = startDate+" "+String.valueOf(String.format("%02d", hourOfDay))+":"+String.valueOf(String.format("%02d", minute))+":"+String.valueOf("00");
                                                startDateButton.setText(startDate);
                                            }
                                        },
                                        now.get(Calendar.HOUR_OF_DAY),
                                        now.get(Calendar.MINUTE),
                                        false
                                );
                                tpd.show(AtlasApp.getSupportFragmentManager(), "Timepickerdialog");
                            }
                        },
                        now.get(Calendar.YEAR), // Initial year selection
                        now.get(Calendar.MONTH), // Initial month selection
                        now.get(Calendar.DAY_OF_MONTH) // Inital day selection
                );


                dpd.show(AtlasApp.getSupportFragmentManager(), "Datepickerdialog");
            }
        });

        endDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar now = Calendar.getInstance();
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                                EndDate = String.valueOf(String.format("%02d", year))+"-"+String.valueOf(String.format("%02d", monthOfYear+1))+"-"+String.valueOf(String.format("%02d", dayOfMonth));
                                TimePickerDialog tpd = TimePickerDialog.newInstance(
                                        new TimePickerDialog.OnTimeSetListener() {
                                            @Override
                                            public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
                                                EndDate = EndDate+" "+String.valueOf(String.format("%02d", hourOfDay))+":"+String.valueOf(String.format("%02d", minute))+":"+String.valueOf("00");
                                                endDateButton.setText(EndDate);
                                            }
                                        },
                                        now.get(Calendar.HOUR_OF_DAY),
                                        now.get(Calendar.MINUTE),
                                        false
                                );

                                tpd.show(AtlasApp.getSupportFragmentManager(), "Timepickerdialog");
                            }
                        },
                        now.get(Calendar.YEAR), // Initial year selection
                        now.get(Calendar.MONTH), // Initial month selection
                        now.get(Calendar.DAY_OF_MONTH) // Inital day selection
                );


                dpd.show(AtlasApp.getSupportFragmentManager(), "Datepickerdialog");
            }
        });


        exportButton.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               if(!startDate.equals("") && !EndDate.equals("") ) {
                   export(startDate, EndDate);
               }else{

                   Toast.makeText(AtlasApp,"Please Select DateTime Start and End",Toast.LENGTH_LONG).show();
               }
           }
       });



        AtlasApp = (AtlasApp) getActivity();
//        allData = AtlasApp.dbHandler.getReadings();
//        for(int i=0;i<allData.size();i++){
//            Log.v("AhmedDatabase",allData.get(i).getId()+", "+allData.get(i).getType()+":"+allData.get(i).getValue());
//        }
        return root;
    }

    private void getResultsForSpecificElement() {
        if(!startDate.equals("") && !EndDate.equals("") ) {
            float Ph_Water = AtlasApp.dbHandler.getReadings_of_TYPE_and_WATERORSOIL(startDate, EndDate,"PH","Water");
            float Ph_Soil = AtlasApp.dbHandler.getReadings_of_TYPE_and_WATERORSOIL(startDate, EndDate,"PH","Soil");
            float EC_Water = AtlasApp.dbHandler.getReadings_of_TYPE_and_WATERORSOIL(startDate, EndDate,"EC","Water");
            float EC_Soil = AtlasApp.dbHandler.getReadings_of_TYPE_and_WATERORSOIL(startDate, EndDate,"EC","Soil");


            float I_Now = Float.parseFloat(AdjustedIrregationEdit.getText().toString());


            String url = "https://rest.isric.org/soilgrids/v2.0/properties/query?lon="+String.valueOf(HomeFragment.LONGITUDE)+"&lat="+String.valueOf(HomeFragment.LATITUDE)+"&property=bdod&depth=0-5cm&value=mean";
            StringRequest nStringReq = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    Log.v("AhmedNeee",response);
                    try {
                        JSONObject jsonObject = new JSONObject(response);


                        Ph_SOIL_VIEW.setText(String.valueOf(Ph_Soil));
                        EC_SOIL_VIEW.setText(String.valueOf(EC_Soil));
                        Ph_WATER_VIEW.setText(String.valueOf(Ph_Water));
                        EC_WATER_VIEW.setText(String.valueOf(EC_Water));
                        float LF = 0;
                        try{
                            LF = EC_Water/(5*(EC_Soil-EC_Water));
                        }catch (Exception err){

                        }


                        LF_VIEW.setText(String.valueOf(LF));



                        CEC_VIEW.setText(String.valueOf(jsonObject.getJSONObject("properties").getJSONArray("layers").getJSONObject(0).getJSONArray("depths").getJSONObject(0).getJSONObject("values").getLong("mean")));
                        float I_NEW = 0;
                        try{
                            I_NEW = I_Now/(I_Now - LF);
                        }catch (Exception err){

                        }
                        ADJUSTED_I_VIEW.setText(String.valueOf(I_NEW));


                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(AtlasApp,"Error: Cannot Excute this action ",Toast.LENGTH_LONG).show();

                    }

                }


            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(AtlasApp,error.toString(),Toast.LENGTH_LONG).show();

                }
            });


            queue.add(nStringReq);
        }else{

            Toast.makeText(AtlasApp,"Please Select DateTime Start and End",Toast.LENGTH_LONG).show();
        }

    }


    public void export(String DateStart,String DateEnd) {
        File sd = Environment.getExternalStorageDirectory();
        String csvFile = "newFile.xls";

        File directory = new File(sd.getAbsolutePath());

        //create directory if not exist
        if (!directory.isDirectory()) {
            directory.mkdirs();
        }
        try {

            //file path
            File file = new File(directory, csvFile);
            WorkbookSettings wbSettings = new WorkbookSettings();
            wbSettings.setLocale(new Locale(Locale.GERMAN.getLanguage(), Locale.GERMAN.getCountry()));
            WritableWorkbook workbook;
            workbook = Workbook.createWorkbook(file, wbSettings);

            //Excel sheetA first sheetA
            WritableSheet sheetA = workbook.createSheet("sheet A", 0);

            // column and row titles
            sheetA.addCell(new Label(0, 0, "ID"));
            sheetA.addCell(new Label(1, 0, "LAT"));
            sheetA.addCell(new Label(2, 0, "LONGITUDE"));
            sheetA.addCell(new Label(3, 0, "TYPE"));
            sheetA.addCell(new Label(4, 0, "VALUE"));
            sheetA.addCell(new Label(5, 0, "DATETIME"));
            sheetA.addCell(new Label(5, 0, "ADDRESS"));


            allData = AtlasApp.dbHandler.getReadings(DateStart,DateEnd);
            for(int i=0;i<allData.size();i++){

                // column and row titles
                sheetA.addCell(new Label(0, i+1, allData.get(i).getId()));
                sheetA.addCell(new Label(1, i+1, allData.get(i).getLat()));
                sheetA.addCell(new Label(2, i+1, allData.get(i).getLongitude()));
                sheetA.addCell(new Label(3, i+1, allData.get(i).getType()));
                sheetA.addCell(new Label(4, i+1, allData.get(i).getValue()));
                sheetA.addCell(new Label(5, i+1, allData.get(i).getDatetimeNow()));
                sheetA.addCell(new Label(5, i+1, allData.get(i).getAddress()));


            }

            allData = AtlasApp.dbHandler.getReadings(DateStart,DateEnd);
        for(int i=0;i<allData.size();i++){
            Log.v("AhmedDatabase",allData.get(i).getId()+", "+allData.get(i).getType()+":"+allData.get(i).getValue());
        }


            // close workbook
            workbook.write();
            workbook.close();

            Toast.makeText(AtlasApp,"File Exported",Toast.LENGTH_LONG).show();

//            Uri fileURL = FileProvider.getUriForFile(AtlasApp, AtlasApp.getApplicationContext().getPackageName() + ".provider",file);

            if(Build.VERSION.SDK_INT>=24){
                try{
                    Method m = StrictMode.class.getMethod("disableDeathOnFileUriExposure");
                    m.invoke(null);
                }catch(Exception e){
                    e.printStackTrace();
                }
            }

            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(file),"application/vnd.ms-excel");
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    @Override
    public void onResume() {
        super.onResume();


        exportButton.setText(getActivity().getResources().getStringArray(R.array.Export)[AtlasApp.language]);
        AdjustedIrregationViewHead.setText(getActivity().getResources().getStringArray(R.array.adjustedIrregation)[AtlasApp.language]);

        expandedDetails.setTitle(getActivity().getResources().getStringArray(R.array.AllDetails)[AtlasApp.language]);
        AtlasApp.toolbar.setTitle(getActivity().getResources().getStringArray(R.array.ManageReadings)[AtlasApp.language]);
        startDateButton.setText(getActivity().getResources().getStringArray(R.array.StartDate)[AtlasApp.language]);
        endDateButton.setText(getActivity().getResources().getStringArray(R.array.endDate)[AtlasApp.language]);


    }
}
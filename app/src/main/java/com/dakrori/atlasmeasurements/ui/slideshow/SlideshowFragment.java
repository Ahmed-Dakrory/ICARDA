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
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.dakrori.atlasmeasurements.AtlasApp;
import com.dakrori.atlasmeasurements.Helpers.ReadingObject;
import com.dakrori.atlasmeasurements.R;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.List;


import android.os.Environment;
import android.widget.Toast;

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
    String startDate ="";
    String EndDate = "";

    public com.dakrori.atlasmeasurements.AtlasApp AtlasApp;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_gallery, container, false);
        exportButton = root.findViewById(R.id.ExportSheet);
        startDateButton = root.findViewById(R.id.startDate);
        endDateButton = root.findViewById(R.id.EndDate);


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


            allData = AtlasApp.dbHandler.getReadings(DateStart,DateEnd);
            for(int i=0;i<allData.size();i++){

                // column and row titles
                sheetA.addCell(new Label(0, i+1, allData.get(i).getId()));
                sheetA.addCell(new Label(1, i+1, allData.get(i).getLat()));
                sheetA.addCell(new Label(2, i+1, allData.get(i).getLongitude()));
                sheetA.addCell(new Label(3, i+1, allData.get(i).getType()));
                sheetA.addCell(new Label(4, i+1, allData.get(i).getValue()));
                sheetA.addCell(new Label(5, i+1, allData.get(i).getDatetimeNow()));


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

        AtlasApp.toolbar.setTitle(getActivity().getResources().getStringArray(R.array.ManageReadings)[AtlasApp.language]);
        startDateButton.setText(getActivity().getResources().getStringArray(R.array.StartDate)[AtlasApp.language]);
        endDateButton.setText(getActivity().getResources().getStringArray(R.array.endDate)[AtlasApp.language]);


    }
}
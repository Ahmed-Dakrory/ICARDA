package com.dakrori.atlasmeasurements.ui.settings;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.dakrori.atlasmeasurements.AtlasApp;
import com.dakrori.atlasmeasurements.R;

public class ToolsFragment extends Fragment {


    Spinner spinnerLanguage;
    TextView languageSelect;
    Button Save;


    public AtlasApp AtlasApp;
    int languageTosave;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_tools, container, false);

        AtlasApp = (AtlasApp)getActivity();
        spinnerLanguage = root.findViewById(R.id.languageSelector);
        languageSelect = root.findViewById(R.id.languageSelect);
        spinnerLanguage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Log.v("AhmedSelect",String.valueOf(i));
                languageTosave=i;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


//                .setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                Log.v("AhmedSelect",String.valueOf(i));
//            }
//        });

        Save = root.findViewById(R.id.saveOptions);
        Save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(AtlasApp);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putInt("language",languageTosave);
                editor.apply();

                Toast.makeText(AtlasApp,getActivity().getResources().getStringArray(R.array.PleaseOpenItAgain)[languageTosave],Toast.LENGTH_LONG).show();
            }
        });

        return root;
    }



    @Override
    public void onResume() {
        super.onResume();

        languageTosave=AtlasApp.language;

        Save.setText(getActivity().getResources().getStringArray(R.array.Save)[AtlasApp.language]);
        languageSelect.setText(getActivity().getResources().getStringArray(R.array.languageSelect)[AtlasApp.language]);

        AtlasApp.toolbar.setTitle(getActivity().getResources().getStringArray(R.array.Settings)[AtlasApp.language]);
        spinnerLanguage.setSelection(languageTosave);
    }

}


package com.gita.datastorage;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by alex on 11/5/2016
 */

public class SettingsActivity extends AppCompatActivity {

    @BindView(R.id.button_english)
    Button buttonEng;

    @BindView(R.id.button_georgian)
    Button buttonGeo;

    @BindView(R.id.def_lang_tv)
    TextView defLangLabel;

    private String oldLang;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);
        oldLang = Settings.getString("lang");
    }

    @OnClick(R.id.button_georgian)
    void onGeorgianClick(){
        App.getInstance().changeLanguage("ka");
        defLangLabel.setText(getString(R.string.default_language));
    }

    @OnClick(R.id.button_english)
    void onEnglishClick(){
        App.getInstance().changeLanguage("en");
        defLangLabel.setText(getString(R.string.default_language));
    }

    @Override
    public void onBackPressed() {

        if(!oldLang.equals(Settings.getString("lang"))){
            setResult(RESULT_OK);
        }

        super.onBackPressed();
    }
}

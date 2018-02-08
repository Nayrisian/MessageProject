package com.nayrisian.dev.messageproject.activity;

import android.app.Activity;
import android.os.Bundle;

import com.nayrisian.dev.messageproject.R;
import com.nayrisian.dev.messageproject.Setting;

/**
 * Base activity.
 * Created by Nayrisian on 28/11/2016.
 */

abstract class BaseActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (Setting.getStyle() == Setting.Style.LIGHT)
            setTheme(R.style.TauThemeLight);
        else
            setTheme(R.style.TauThemeDark);
        super.onCreate(savedInstanceState);
    }
}
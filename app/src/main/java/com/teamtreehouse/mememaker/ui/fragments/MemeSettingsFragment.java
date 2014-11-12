package com.teamtreehouse.mememaker.ui.fragments;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.teamtreehouse.mememaker.R;

/**
 * Created by Evan Anger on 8/13/14.
 */
public class MemeSettingsFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }
}

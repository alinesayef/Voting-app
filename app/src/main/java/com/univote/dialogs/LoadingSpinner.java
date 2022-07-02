//this class is used for creating loading dialog in app
package com.univote.dialogs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.univote.R;

public class LoadingSpinner extends DialogFragment {

    private static String message;
    //this method creates an empty loading message
    public LoadingSpinner() {
        // use empty constructors. If something is needed use onCreate's
        message = null;
    }

    //this method is used to get a loading message which we apply to the alert
    public static LoadingSpinner spinnerWithCustomMessage(String msg) {
        LoadingSpinner d = new LoadingSpinner();
        message = msg;
        return d;
    }

    //this is start method for the class
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Translucent_NoTitleBar);
    }

    //this method is used for create loading view
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_loadingspinner, container);
    }
    //this is method used after loading view has been created
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}
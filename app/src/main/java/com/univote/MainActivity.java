//this is the start class of app when the app starts
package com.univote;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.univote.screens.EnterActivity;

import java.security.MessageDigest;

public class MainActivity extends AppCompatActivity {

    //this is the start method of the class
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);

    }

    //this method is used for going to the login screen
    public void onClickGetStarted(View view)
    {
        Intent intent = new Intent(this, EnterActivity.class);
        startActivity(intent);
    }

}

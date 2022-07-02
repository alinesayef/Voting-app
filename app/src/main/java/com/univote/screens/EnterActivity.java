//this class is used for logging in
package com.univote.screens;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.univote.R;
import com.univote.app.Appdata;
import com.univote.app.myRequestQueue;
import com.univote.dialogs.LoadingSpinner;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;


public class EnterActivity extends AppCompatActivity {

    EditText editText;
    Button get_start;


    //Hash the voterID with SHA256
    public static String sha256(final String base) {
        try {
            final MessageDigest digest = MessageDigest.getInstance("SHA-256");
            final byte[] hash = digest.digest(base.getBytes("UTF-8"));
            final StringBuilder hexString = new StringBuilder();
            for (int i = 0; i < hash.length; i++) {
                final String hex = Integer.toHexString(0xff & hash[i]);
                if (hex.length() == 1)
                    hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter);
        editText = findViewById(R.id.voter_id);
        get_start = findViewById(R.id.get_start);


        //this method is used to check if the user inputs a valid 12 digit VoterID or not
        get_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String voter_id = String.valueOf(editText.getText());

                if (voter_id.length() != 12) {
                    Toast.makeText(EnterActivity.this, "Please input your 12 digit Voter ID", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    String s = sha256(voter_id);

                    dologin(EnterActivity.this, s);
                }

            }
        });

    }

    //this method is used for 'do login' after the user inputs valid 12 digit VoterID
    public void dologin(final Context ctx, final String voter_id) {
        final LoadingSpinner spinnerDialog = LoadingSpinner.spinnerWithCustomMessage("ok");
        spinnerDialog.show(getSupportFragmentManager(), null);

        String url = Appdata.api_url + "login";


        //this is api post method for login
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            Boolean status = jsonResponse.getBoolean("status");
                            if (status) {
                                spinnerDialog.dismiss();
                                String user_id = jsonResponse.getString("id");
                                String user_voted_before = jsonResponse.getString("vote_casted");

                                Appdata.getInstance().setUser(user_id);

                                //this is warning method for an already cast vote
                                if (user_voted_before.equals("Yes")) {
                                    AlertDialog alertDialog1 = new AlertDialog.Builder(ctx).create();
                                    alertDialog1.setTitle("Warning");
                                    alertDialog1.setMessage("You have already cast your vote.");
                                    alertDialog1.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();
                                                }
                                            });
                                    alertDialog1.show();

                                    //this is the success method
                                } else {
                                    Toast.makeText(ctx, "Success!", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(ctx, VoteActivity.class);
                                    startActivity(intent);
                                    finish();
                                }

                            } else {

                                //this is used if the voter ID does not exist
                                spinnerDialog.dismiss();

                                AlertDialog alertDialog = new AlertDialog.Builder(ctx).create();
                                alertDialog.setTitle("Warning");
                                alertDialog.setMessage("Your Voter ID does not exist. Please contact admin.");
                                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        });
                                alertDialog.show();
                            }
                        } catch (JSONException e) {
                            spinnerDialog.dismiss();
                            e.printStackTrace();
                        }
                    }
                },
                //this is an error method
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        spinnerDialog.dismiss();
                        error.printStackTrace();
                    }
                }
        ) {


            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                // the POST parameters:
                params.put("voter_id", voter_id);
                return params;
            }
        };
        myRequestQueue.getInstance(this).addToRequestQueue(postRequest);

    }


}

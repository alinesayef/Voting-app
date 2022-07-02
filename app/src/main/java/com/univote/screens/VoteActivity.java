//this class is used to create candidate listview for getting all the candidates and submitting the vote
package com.univote.screens;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.univote.R;
import com.univote.app.Appdata;
import com.univote.app.myRequestQueue;
import com.univote.dialogs.LoadingSpinner;
import com.univote.models.Candidate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

class CandidateAdapter extends BaseAdapter {

    LayoutInflater _inflater;
    Context _context;

    //this is the candidate template method
    public CandidateAdapter(Context context)
    {
        _context = context;
        _inflater = LayoutInflater.from(context);
    }

    //this method is used to count the total candidates
    @Override
    public int getCount() {
        return Appdata.getInstance().getCandidates().size();
    }

    //this method is used to get the item position from the database
    @Override
    public Candidate getItem(int position) {
        return  Appdata.getInstance().getCandidates().get(position);
    }

    //this method is used to retrieve the ID of candidate position
    @Override
    public long getItemId(int position) {
        return position;
    }

    //this method gets the view for item candidate
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LinearLayout linearLayout = (LinearLayout) _inflater.inflate(R.layout.item_candidate, null);
        return buildChildView(_context, linearLayout, position);
    }

    private View buildChildView(final Context context, View view, final int position)
    {
        TextView textView = view.findViewById(R.id.candidate_name);
        textView.setText(Appdata.getInstance().getCandidates().get(position).name);

        //this is used for candidate listview row clicked listener
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, Appdata.getInstance().getCandidates().get(position).id + " clicked", Toast.LENGTH_SHORT).show();
                _listener.onClick(Appdata.getInstance().getCandidates().get(position).name);

            }
        });
        return view;
    }

    //this is the base method for candidate list click listener
    public interface ItemClickListener
    {
        public void onClick(String name);
    }

    ItemClickListener _listener;


    //this is the base method for set candidate list click listener
    public void set_listener(ItemClickListener listener)
    {
        this._listener = listener;
    }

}

public class VoteActivity extends AppCompatActivity implements CandidateAdapter.ItemClickListener{

    ListView cadidates_listview;
    CandidateAdapter adapter;

    //this method is create listview
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vote);

        getCandidates();
        cadidates_listview = (ListView) findViewById(R.id.candidates);

        adapter = new CandidateAdapter(this);
        adapter.set_listener(this);
        cadidates_listview.setAdapter(adapter);
    }

    //this is used for resume class
    @Override
    public void onResume() {
        super.onResume();


    }

    //this method gets the candidate list from database
    public void getCandidates()
    {
        if(Appdata.getInstance().getCandidates().size()>0)
            Appdata.getInstance().getCandidates().clear();
        final LoadingSpinner spinnerDialog = LoadingSpinner.spinnerWithCustomMessage("ok");
        spinnerDialog.show(getSupportFragmentManager(), null);

        String url = Appdata.api_url + "get_candidates";
        JsonArrayRequest jsonRequest = new JsonArrayRequest(Request.Method.GET, url, new JSONObject(), new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                spinnerDialog.dismiss();
                try {
                    for(int i = 0; i < response.length(); i ++)
                    {
                        JSONObject one_cand = (JSONObject) response.get(i);
                        Appdata.getInstance().addCandidate( new Candidate(Integer.parseInt(one_cand.getString("id")) , one_cand.getString("name")) );
                    }
                    adapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                spinnerDialog.dismiss();
                error.printStackTrace();
            }
        });

        myRequestQueue.getInstance(this).addToRequestQueue(jsonRequest);
    }




    //this method is used to send the vote data to server and it will reply whether a vote has been submitted successfully or not
    public void doVote(final Context ctx, final String candidate_name)
    {
        final LoadingSpinner  spinnerDialog = LoadingSpinner.spinnerWithCustomMessage("ok");
        spinnerDialog.show(getSupportFragmentManager(), null);

        String url = Appdata.api_url + "do_vote";

        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            Boolean status = jsonResponse.getBoolean("status");

                            //this condition is met when vote successfully submitted
                            if(status == true)
                            {
                                spinnerDialog.dismiss();
                                AlertDialog alertDialog = new AlertDialog.Builder(ctx).create();
                                alertDialog.setTitle("Success");
                                alertDialog.setMessage("Your vote has been successfully submitted");
                                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                Intent intent = new Intent(VoteActivity.this, EnterActivity.class);
                                                startActivity(intent);
                                                finish();
                                                dialog.dismiss();
                                            }
                                        });
                                alertDialog.show();

                            }

                            //this condition is met when vote was NOT successfully submitted
                            else
                            {
                                spinnerDialog.dismiss();
                                Toast.makeText(ctx, "Your vote was NOT successful.", Toast.LENGTH_SHORT).show();
                            }
                        }
                        catch (JSONException e) {
                            spinnerDialog.dismiss();
                            e.printStackTrace();
                        }
                    }
                },
                //this is the error message from server
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        spinnerDialog.dismiss();
                        error.printStackTrace();
                    }
                }
        )
        {

            //this method maps user_id with candidate name
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<>();
                // the POST parameters:
                params.put("user_id", Appdata.getInstance().getUser());
                params.put("candidate", candidate_name);
                return params;
            }
        };
        myRequestQueue.getInstance(this).addToRequestQueue(postRequest);

    }
    //on click voter list listener
    @Override
    public void onClick(String name) {
        doVote(this, name);
    }
}

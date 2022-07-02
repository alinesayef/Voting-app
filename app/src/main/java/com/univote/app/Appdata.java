// this class is used for getting data from the database
package com.univote.app;

import com.univote.models.Candidate;

import java.util.ArrayList;
import java.util.List;

public class Appdata {
    // this method is used for creating a reference to access data
    private static Appdata instance;

    private Appdata(){}

    public static Appdata getInstance(){
        if(instance == null){
            instance = new Appdata();
        }
        return instance;
    }
    //this is base url of server
    public static String api_url = "http://192.168.1.70/index.php/";
    private List<Candidate> candidates;
    private String user_id;

    //this method is used to get the candidate list
    public List<Candidate> getCandidates() {
        if(candidates == null)
        {
            candidates = new ArrayList<Candidate>();
        }
        return candidates;
    }

    //this method is used to get candidates from database
    public void addCandidate(Candidate candidate)
    {
        if(candidates == null)
        {
            candidates = new ArrayList<Candidate>();
        }
        candidates.add(candidate);
    }
    //this is setter method
    public void setUser(String user_id)
    {
        this.user_id = user_id;
    }

    //this is getter method for the get data
    public String getUser()
    {
        return this.user_id;
    }

}

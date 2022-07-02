//this is base class for candidate details
package com.univote.models;

//this is the candidate name reference
public class Candidate {
    public int id ;
    public String name ;

    //this is the method for setting the candidate name and id which we supply
    public Candidate(int id, String name)
    {
        this.id = id;
        this.name = name;
    }
}

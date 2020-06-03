package ca.mohawk.fallis;

import android.widget.ArrayAdapter;

import java.util.ArrayList;

/*Clubs class that has a property for Date, Description, EndTime, LeaderName, Location, MemberList, Name, StartTime. This class is used when we make an
call to the FireStore database to get the Clubs documents that store a Club date, description, end time, leader name, location, member list, name, and start time.*/
public class Clubs {
	
	//Properties for the Club object.
    private String Date;
    private String Description;
    private String EndTime;
    private String LeaderName;
    private String Location;
    private ArrayList<String> MemberList;
    private String Name;
    private String StartTime;

    public Clubs(){
        //public no-arg constructor needed
    }

	//Assigns the values from the FireStore database into the Clubs object properties.
    public Clubs(String date, String description, String endTime, String leaderName, String location, ArrayList<String> memberList, String name, String startTime){
        Date = date;
        Description = description;
        EndTime = endTime;
        LeaderName = leaderName;
        Location = location;
        MemberList = memberList;
        Name = name;
        StartTime = startTime;
    }

	/*
	All the methods below return the value of each property that they are named after.
	*/
	//Return Date.
    public String getDate() {
        return Date;
    }

	//Return Description.
    public String getDescription() {
        return Description;
    }

	//Return EndTime.
    public String getEndTime() {
        return EndTime;
    }

	//Return LeaderName.
    public String getLeaderName() {
        return LeaderName;
    }

	//Return Location.
    public String getLocation() {
        return Location;
    }

	//Return MemberList.
    public ArrayList<String> getMemberList() {
        return MemberList;
    }

	//Return Name.
    public String getName() {
        return Name;
    }

	//Return StartTime.
    public String getStartTime() {
        return StartTime;
    }
}
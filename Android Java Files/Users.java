package ca.mohawk.fallis;

import java.util.ArrayList;

/*User class that has a property for an ArrayList of Strings. This class is used when we make an
call to the FireStore database to get the User documents that store an Array of strings for each
user to track what clubs they are apart of.*/
public class Users {
	
	//Club Property that holds an Array List.
    private ArrayList<String> Clubs;

    public Users(){
        //public no-arg constructor needed
    }

	//When object is created pass in a Array List and store it in Clubs property.
    public Users(ArrayList<String> clubs){
        Clubs = clubs;
    }

	//Get method that returns the Clubs Array List.
    public ArrayList<String> getClubs() { return Clubs; }
}

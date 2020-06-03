package ca.mohawk.fallis;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class ClubViewFragment extends Fragment {


    public ClubViewFragment() {
        // Required empty public constructor
    }

    private FirebaseFirestore database = FirebaseFirestore.getInstance();
    private static final String TAG = "ClubActivity";
    private boolean statusTracker;
    private View myView;

    //OnCreateView method gets the club that was selected information and displays it to the user with a join or leave button depending if the user is already in the club or not.
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        myView = inflater.inflate(R.layout.fragment_club_view, container, false);

        //Get the arguments from the bundle.
        String date = getArguments().getString("date");
        String description = getArguments().getString("description");
        String endTime = getArguments().getString("endTime");
        String leaderName = getArguments().getString("leaderName");
        String location = getArguments().getString("location");
        final String name = getArguments().getString("name");
        String startTime = getArguments().getString("startTime");
        boolean loggedIn = getArguments().getBoolean("loggedIn");
        final String uid = getArguments().getString("uid");

        //Gets all the text views on the XML page.
        TextView dateText = myView.findViewById(R.id.dateTextView);
        TextView descriptionText = myView.findViewById(R.id.descriptionTextView);
        TextView endTimeText = myView.findViewById(R.id.endTimeTextView);
        TextView leaderNameText = myView.findViewById(R.id.leaderNameTextView);
        TextView locationText = myView.findViewById(R.id.locationTextView);
        TextView nameText = myView.findViewById(R.id.nameTextView);
        TextView startTimeText = myView.findViewById(R.id.startTimeTextView);

        //Set all the values of the text views with the values from the bundle.
        dateText.setText("Club Meeting Date: " + date);
        descriptionText.setText("Club Description: \n\n" + description);
        endTimeText.setText("Club End Time: " + endTime);
        leaderNameText.setText("Leader: " + leaderName);
        locationText.setText("Club Location Room: " + location);
        nameText.setText(name);
        startTimeText.setText("Club Start Time: " + startTime);

        final Button button = myView.findViewById(R.id.clubButton);

        //If the loggedIn boolean is true get the clubs that user is apart of and check if the user is already joined in the club that was selected.
        if(loggedIn){

            //Get the user document that stores the users clubs.
            DocumentReference documentReference = database.collection("Users").document(uid);
            documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                    //If the database read was successful check the results.
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();

                        //If the document exists set the state of status.
                        if (document.exists()) {
                            Users users = document.toObject(Users.class);
                            ArrayList<String> userClubs = users.getClubs();
                            boolean status;

                            //Check if the userClubs array contains the club selected. If it does set status to true.
                            if(userClubs.contains(name)){
                                button.setText("Leave Club");
                                status = true;
                            //Else user isn't apart of the club and set status to false.
                            } else {
                                button.setText("Join Club");
                                status = false;
                            }
                            statusTracker = status;

                            //Set a OnClickListener on the button that calls the buttonSwap method.
                            button.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    buttonSwap(statusTracker, uid, name);
                                }
                            });
                        } else {
                            Log.d(TAG, "No such document");
                        }
                    } else {
                        Log.d(TAG, "get failed with ", task.getException());
                    }
                }
            });
        //Else if the user isn't logged in disable the button.
        } else {
            button.setText("Join Club");
            button.setEnabled(false);
        }

        return myView;
    }

    //buttonSwap method allows the user to join and leave clubs depending on the status that was passed in.
    private void buttonSwap(final boolean status, String uid, final String name){

        //Check the user document for that users clubs array.
        final DocumentReference documentReference = database.collection("Users").document(uid);
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                //If the database read was successful check the results.
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();

                    //If the document exists change the button text and update the users clubs array.
                    if (document.exists()) {
                        Users users = document.toObject(Users.class);
                        ArrayList<String> userClubs = users.getClubs();
                        Button button = myView.findViewById(R.id.clubButton);

                        //If true that means we are in the club and want to leave the club.
                        if(status){
                            //Remove that club from the users club array and update the FireStore users club array.
                            userClubs.remove(name);
                            button.setText("Join Club");
                            documentReference.update("Clubs", userClubs);
                            statusTracker = false;

                        //Else we aren't in the club and we want to join the club.
                        } else {
                            //Add the club to the users club array and update the FireStore users club array.
                            userClubs.add(name);
                            button.setText("Leave Club");
                            documentReference.update("Clubs", userClubs);
                            statusTracker = true;
                        }
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }
}

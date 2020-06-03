package ca.mohawk.fallis;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class ClubFragment extends Fragment {

    private FirebaseAuth mAuth;
    private SignInButton googleSignInButton;
    private GoogleSignInClient mGoogleSignInClient;
    private static final String TAG = "ClubActivity";
    private static final int RC_SIGN_IN = 9001;
    private String idToken;
    private FirebaseFirestore database = FirebaseFirestore.getInstance();
    private List<String> clubList = new ArrayList<String>();
    private List<Clubs> clubListInformation = new ArrayList<Clubs>();
    private boolean loggedIn = false;
    private String uid;

    public ClubFragment() {
        // Required empty public constructor
    }

    private View myView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        myView =  inflater.inflate(R.layout.club_fragment, container, false);
        mAuth = FirebaseAuth.getInstance();

        //Create the google sign in button options.
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(getActivity(), googleSignInOptions);

        //Add an OnClickListener on the sign in button that will call the signIn method.
        googleSignInButton = myView.findViewById(R.id.signInButton);
        googleSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });

        //Add an OnClickListener on the sign out button that will call the signOut method.
        final Button logout = myView.findViewById(R.id.signOutButton);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Logout();
            }
        });
        mGoogleSignInClient.signOut();

        //Clear the clubList arrays
        clubList.clear();
        clubListInformation.clear();

        //Get all the Club documents from the Clubs collection.
        database.collection("Clubs")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        //If the database read is successful add all clubs documents into club objects.
                        if (task.isSuccessful()) {

                            //Loop through all the club documents we got and turn them into club objects and store them in a array of club objects.
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                clubList.add(document.getId());
                                Clubs club = document.toObject(Clubs.class);
                                clubListInformation.add(club);
                            }

                            //Get the list view from the XML layout and attach an adapter to the list view.
                            ListView listView = myView.findViewById(R.id.clubList);
                            ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, clubList);
                            listView.setAdapter(myAdapter);

                            //Set the list view with an OnItemClickListener that will get all that clubs information and call the getClubView method to display it.
                            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                    Clubs selectedClub = clubListInformation.get(i);
                                    String date = selectedClub.getDate();
                                    String description = selectedClub.getDescription();
                                    String endTime = selectedClub.getEndTime();
                                    String leaderName = selectedClub.getLeaderName();
                                    String location = selectedClub.getLocation();
                                    ArrayList<String> memberList = selectedClub.getMemberList();
                                    String name = selectedClub.getName();
                                    String startTime = selectedClub.getStartTime();

                                    //Call the getClubView method and pass in all the clubs information so it will be displayed another fragment.
                                    ((MainActivity)getActivity()).getClubView(date, description, endTime, leaderName, location, memberList, name, startTime, loggedIn, uid);
                                }
                            });
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

        return myView;
    }

    //UpdateUI method gets the users uid if the user is logged in.
    private void updateUI(FirebaseUser user) {
        if (user != null) {
            uid = user.getUid();

        }
    }

    //onStart method gets the current user and calls the updateUI method.
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    //signIn method starts a sign in intent.
    private void signIn(){
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    //onActivityResults runs after the google sign in intent is done.
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with FireBase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                idToken = account.getIdToken();
                fireBaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed
                Log.w(TAG, "Google sign in failed", e);
            }
        }
    }

    //fireBaseAuthWithGoogle method makes sure the google sign in is correct.
    private void fireBaseAuthWithGoogle(GoogleSignInAccount acct) {


        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        //Try to sign in the user with credentials.
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        //If sign in was successful try to get the user document. If the document doesn't exist create it with a empty club array.
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            uid = user.getUid();
                            loggedIn = true;

                            //Try to get the users document.
                            DocumentReference documentReference = database.collection("Users").document(uid);
                            documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                @Override
                                public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {

                                    //If the document doesn't exist create it and set the clubs array.
                                    if(!documentSnapshot.exists()){
                                        Map<String, List<Object[]>> Clubs = new HashMap<>();
                                        List<Object[]> clubList = new ArrayList<>();
                                        Clubs.put("Clubs", clubList);

                                        database.collection("Users").document(uid)
                                                .set(Clubs);
                                    }
                                }
                            });
                            MainActivity mainActivity = (MainActivity) getActivity();
                            mainActivity.mAuth = mAuth;
                        } else {

                            // If sign in fails display a message to the user.
                            Toast.makeText(getActivity(), "Login Authentication Failed", Toast.LENGTH_LONG).show();
                            updateUI(null);
                        }
                    }
                });
    }

    //Logout method logs out the current user.
    private void Logout(){
        FirebaseAuth.getInstance().signOut();
        loggedIn = false;
        uid = "";
        MainActivity mainActivity = (MainActivity) getActivity();
        mainActivity.mAuth = null;
        mGoogleSignInClient.signOut();
    }
}

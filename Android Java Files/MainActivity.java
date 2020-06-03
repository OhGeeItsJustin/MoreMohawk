package ca.mohawk.fallis;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import android.app.PendingIntent;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

	//Get FireStore instance.
    FirebaseFirestore database = FirebaseFirestore.getInstance();
    private static final String TAG = "MainActivity";
    public FirebaseAuth mAuth;
    private ArrayList<String> clubList;
    private DrawerLayout myDrawer;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

		//Get the navigation view and get the ID of the drawer layout. Set a NavigationItemSelectedListener.
        navigationView = findViewById(R.id.navigation_view);
        myDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView.setNavigationItemSelectedListener(this);


		//Get the action bar and sync it with the drawer.
        ActionBar myActionBar = getSupportActionBar();
        myActionBar.setDisplayHomeAsUpEnabled(true);

        ActionBarDrawerToggle myactionbartoggle = new ActionBarDrawerToggle(
                this, myDrawer, (R.string.open), (R.string.close));
        myDrawer.addDrawerListener(myactionbartoggle);
        myactionbartoggle.syncState();

		//Call the home fragment and display it.
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        HomeFragment homeFragment = new HomeFragment();
        fragmentTransaction.replace(R.id.frame, homeFragment);
        fragmentTransaction.commit();
    }

	//Method that creates fragments or loads links depending on what navigation item was selected.
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        // Show visual for selection
        menuItem.setChecked(true);

        // Close the Drawer
        myDrawer.closeDrawers();
        View view = getCurrentFocus();
		
		//Start up the fragment transaction
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

		//Do a switch case for the item selected.
        switch (menuItem.getItemId()) {
			//When the home navigation item is selected display a home fragment.
            case R.id.nav_home:
                HomeFragment homeFragment = new HomeFragment();
                fragmentTransaction.replace(R.id.frame, homeFragment);
                fragmentTransaction.commit();
                break;
			//When the email navigation item is selected call the openEmail method and pass in the view.
            case R.id.nav_email:
                openEmail(view);
                break;
			//When the eLearn navigation item is selected call the openElearn method and pass in the view.
            case R.id.nav_elearn:
                openElearn(view);
                break;
			//When the school map navigation item is selected display a school map fragment.
            case R.id.nav_school_map:
                SchoolInformationFragment schoolInformationFragment = new SchoolInformationFragment();
                fragmentTransaction.replace(R.id.frame, schoolInformationFragment);
                fragmentTransaction.commit();
                break;
			//When the course schedule navigation item is selected call the openNeededUri method and pass in the URL link to the course view page.
            case R.id.nav_course_schedule_view:
                openNeededUri(getString(R.string.course_view_uri));
                break;
			//When the orientation navigation item is selected call the openNeededUri method and pass in the URL link to the orientation page.
            case R.id.nav_orientation:
                openNeededUri(getString(R.string.orientation_uri));
                break;
			//When the wifi setup navigation item is selected call the openNeededUri method and pass in the URL link to the wifi setup page.
            case R.id.nav_wifi_setup:
                openNeededUri(getString(R.string.wifi_setup_uri));
                break;
			//When the techical support navigation item is selected call the openNeededUri method and pass in the URL link to the techical support page.
            case R.id.nav_technical_support:
                openNeededUri(getString(R.string.technical_support_uri));
                break;
			//When the one card navigation item is selected call the openNeededUri method and pass in the URL link to the one card page.
            case R.id.nav_one_card:
                openNeededUri(getString(R.string.one_card_url));
                break;
			//When the student academics navigation item is selected display a student academics fragment.
            case R.id.nav_student_academics:
                StudentAcademicsFragment studentAcademicsFragment = new StudentAcademicsFragment();
                fragmentTransaction.replace(R.id.frame, studentAcademicsFragment);
                fragmentTransaction.commit();
                break;
			//When the student life navigation item is selected display a student life fragment.
            case R.id.nav_student_life:
                StudentLifeFragment studentLifeFragment = new StudentLifeFragment();
                fragmentTransaction.replace(R.id.frame, studentLifeFragment);
                fragmentTransaction.commit();
                break;
			//When the clubs navigation item is selected display a club fragment.
            case R.id.nav_clubs:
                ClubFragment clubFragment = new ClubFragment();
                fragmentTransaction.replace(R.id.frame, clubFragment);
                fragmentTransaction.commit();
                break;
			//When the mohawk soical media navigation item is selected display a social media fragment.
            case R.id.nav_mohawk_social_media:
                SocialMediaFragment socialMediaFragment = new SocialMediaFragment();
                fragmentTransaction.replace(R.id.frame, socialMediaFragment);
                fragmentTransaction.commit();
                break;
			//When the news and events navigation item is selected display a news and events fragment.
            case R.id.nav_news_and_events:
                NewsAndEventsFragment newsAndEventsFragment = new NewsAndEventsFragment();
                fragmentTransaction.replace(R.id.frame, newsAndEventsFragment);
                fragmentTransaction.commit();
                break;
			//When the school infomation navigation item is selected display a school information fragment.
            case R.id.nav_school_information:
                SchoolMappingFragment schoolMappingFragment = new SchoolMappingFragment();
                fragmentTransaction.replace(R.id.frame, schoolMappingFragment);
                fragmentTransaction.commit();
                break;
        }

        return false;
    }

    // This will respond to selections in the ActionBar
    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        // Find out the current state of the drawer (open or closed)
        boolean isOpen = myDrawer.isDrawerOpen(GravityCompat.START);

        // Handle item selection
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                // Home button - open or close the drawer
                if (isOpen == true) {
                    myDrawer.closeDrawer(GravityCompat.START);
                } else {
                    myDrawer.openDrawer(GravityCompat.START);
                }
                return true;
        }

        return super.onOptionsItemSelected(menuItem);
    }

	//openEmail method parses the mohawk email link string into a URL and starts an intent to load the URL.
    public void openEmail(View view) {
        Uri uri = Uri.parse(getString(R.string.email_uri));
        Intent myIntent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(myIntent);
    }

	//openElearn method parses the Elearn link string into a URL and starts an intent to load the URL.
    public void openElearn(View view) {
        Uri uri = Uri.parse(getString(R.string.elearn_uri));
        Intent myIntent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(myIntent);
    }

	//openNeededURI takes in a string and parses the value into a URL and starts an intent to load the URL.
    public void openNeededUri(String neededUri)
    {
        Uri uri = Uri.parse(neededUri);
        Intent myIntent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(myIntent);
    }

	//openTwitter method parses the Twitter link string into a URL and starts an intent to load the URL.
    public void openTwitter(View view) {
        Uri uri = Uri.parse(getString(R.string.twitter_url));
        Intent myIntent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(myIntent);
    }

	//openInstagram method parses the Instagram link string into a URL and starts an intent to load the URL.
    public void openInstagram(View view) {
        Uri uri = Uri.parse(getString(R.string.instagram_url));
        Intent myIntent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(myIntent);
    }

	//openFacebook method parses the Facebook link string into a URL and starts an intent to load the URL.
    public void openFacebook(View view) {
        Uri uri = Uri.parse(getString(R.string.facebook_url));
        Intent myIntent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(myIntent);
    }

	//openYoutube method parses the Youtube link string into a URL and starts an intent to load the URL.
    public void openYoutube(View view) {
        Uri uri = Uri.parse(getString(R.string.youtube_url));
        Intent myIntent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(myIntent);
    }

	//openLinkdin method parses the Linkedin link string into a URL and starts an intent to load the URL.
    public void openLinkdin(View view) {
        Uri uri = Uri.parse(getString(R.string.linkedin_url));
        Intent myIntent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(myIntent);
    }

	//viewGrades method parses the view grade link string into a URL and starts an intent to load the URL.
    public void viewGrades(View view) {
        Uri uri = Uri.parse(getString(R.string.mymohawkurl));
        Intent myIntent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(myIntent);
    }

	//viewGrades method parses the view grade link string into a URL and starts an intent to load the URL.
    public void viewLibrary(View view) {
        Uri uri = Uri.parse(getString(R.string.libraryurl));
        Intent myIntent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(myIntent);
    }

    //viewTutoring method parses the Mohawk tutoring link string into a URL and starts an intent to load the URL.
    public void viewTutoring(View view) {
        Uri uri = Uri.parse(getString(R.string.tutoringurl));
        Intent myIntent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(myIntent);
    }

    //viewFoodService method parses the Mohawk food service link string into a URL and starts an intent to load the URL.
    public void viewFoodService(View view) {
        Uri uri = Uri.parse("http://dineoncampus.ca/mohawk/menus/locations");
        Intent myIntent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(myIntent);
    }

    //viewLocker method parses the Mohawk rent a locker link string into a URL and starts an intent to load the URL.
    public void viewLocker(View view) {
        Uri uri = Uri.parse("https://mohawk.bookware3000.ca/rent-a-locker");
        Intent myIntent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(myIntent);
    }

    //viewBookSearch method parses the Mohawk book search link string into a URL and starts an intent to load the URL.
    public void viewBookSearch(View view) {
        Uri uri = Uri.parse("https://mohawk.bookware3000.ca/Course/campus");
        Intent myIntent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(myIntent);
    }

    //viewBusPass method parses the Mohawk bus pass link string into a URL and starts an intent to load the URL.
    public void viewBusPass(View view) {
        Uri uri = Uri.parse("https://www.mohawkstudents.ca/transit");
        Intent myIntent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(myIntent);
    }

    //getClubView method runs when a user selects a club from the view list and passes all the information in a bundle to the new fragment.
    public void getClubView(String date, String description, String endTime, String leaderName, String location, ArrayList<String> memberList, String name, String startTime, boolean loggedIn, String uid){
        //Do transaction to the club we selected
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        ClubViewFragment clubViewFragment = new ClubViewFragment();

        //Store all the data in a bundle
        Bundle bundle = new Bundle();
        bundle.putString("date", date);
        bundle.putString("description", description);
        bundle.putString("endTime", endTime);
        bundle.putString("leaderName", leaderName);
        bundle.putString("location", location);
        bundle.putStringArrayList("memberList", memberList);
        bundle.putString("name", name);
        bundle.putString("startTime", startTime);
        bundle.putBoolean("loggedIn", loggedIn);
        bundle.putString("uid", uid);

        //Add bundle to the fragment and commit the transaction.
        clubViewFragment.setArguments(bundle);
        fragmentTransaction.replace(R.id.frame, clubViewFragment).addToBackStack(null);
        fragmentTransaction.commit();
    }

    //refreshLocation reloads the school mapping fragment.
    public void refreshLocation(View view) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        SchoolMappingFragment schoolMappingFragment = new SchoolMappingFragment();
        fragmentTransaction.replace(R.id.frame, schoolMappingFragment);
        fragmentTransaction.commit();
    }

    //openNews method parses the Mohawk news link string into a URL and starts an intent to load the URL.
    public void openNews(View view) {
        Uri uri = Uri.parse("https://www.mohawkcollege.ca/about/news");
        Intent myIntent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(myIntent);
    }

    //openEvent method parses the Mohawk event link string into a URL and starts an intent to load the URL.
    public void openEvent(View view) {
        Uri uri = Uri.parse("https://www.mohawkcollege.ca/events");
        Intent myIntent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(myIntent);
    }

    //enCampus method parses the Mohawk whereEnCampus link string into a URL and starts an intent to load the URL.
    public void enCampus(View view) {
        Uri uri = Uri.parse("https://whereencampus.mohawkcollege.ca/portal/");
        Intent myIntent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(myIntent);
    }

    //contactInformation method parses the Mohawk contact information link string into a URL and starts an intent to load the URL.
    public void contactInformation(View view) {
        Uri uri = Uri.parse("https://www.mohawkcollege.ca/about-mohawk/contact-mohawk");
        Intent myIntent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(myIntent);
    }

    //Override onStop method to call the notificationMethod to start the worker class and create a notification if the user is in a club that has a meeting that day.
    @Override
    protected void onStop() {
        super.onStop();
        //If the user is logged in.
        if(mAuth != null) {
            //Get the user uid.
            final String uid = mAuth.getUid();

            //Get the document with that uid.
            DocumentReference documentReference = database.collection("Users").document(uid);
            documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    //If database read is successful check for the document.
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        //If the document exists store the club list from that uid. Then call notificationMethod.
                        if (document.exists()) {
                            Users users = document.toObject(Users.class);
                            ArrayList<String> userClubs = users.getClubs();
                            clubList = userClubs;
                            notificationMethod();
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

    //notificationMethod method turns the club Array List into a normal array and creates a periodic work request to check to see if one of the clubs has a meeting that day and display a notification.
    public void notificationMethod(){

            //If the clubList Array List is empty do nothing.
            if(!clubList.isEmpty()){

                //Turn the Array List into a normal array so we can pass it into the periodic work request.
                String[] stringClubs = new String[clubList.size()];
                for (int i = 0; i < clubList.size(); i++) {

                    // Assign each value to String array
                    stringClubs[i] = clubList.get(i);
                }


                //Store array in data.
                Data.Builder data = new Data.Builder();
                data.putStringArray("clubArray", stringClubs);

                //Create constraints so the request only runs on wifi so we don't use the users data plan.
                Constraints constraints = new Constraints.Builder().setRequiredNetworkType(NetworkType.UNMETERED).build();

                //Create the periodic work request every 22 hours.
                PeriodicWorkRequest request = new PeriodicWorkRequest.Builder(NotificationWorker.class, 24, TimeUnit.HOURS)
                        .setInputData(data.build())
                        //.setConstraints(constraints)
                        .build();

                //Enqueue the work request.
                WorkManager.getInstance(this).enqueue(request);
            }
    }
}
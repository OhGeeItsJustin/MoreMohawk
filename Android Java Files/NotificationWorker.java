package ca.mohawk.fallis;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NotificationWorker extends Worker {
	
	//Get FireStore instance.
    private FirebaseFirestore database = FirebaseFirestore.getInstance();
    private static final String TAG = "NotificationActivity";
	
	//Values needed for the notification message.
    private String date;
    private String time;
    private String endTime;
    private String location;
    private String dayValue;
    private String clubName;

    public NotificationWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

	//Override doWork method that gets the current day value as a string. Loop through the club array that was passed in to check if that club meeting is today and display a notification.
    @NonNull
    @Override
    public Result doWork() {

		//Get the club array passed in from the Main Activity that called the worker class.
        String[] clubArray = getInputData().getStringArray("clubArray");
		
		//Get the int value of the current day
        Calendar calendar = Calendar.getInstance();
		//DAY_OF_WEEK property will return a int value from 1 to 7. 1 being sunday and 7 being saturday.
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        dayValue = "";
		
		//Use a switch case to set the "dayValue" the current day string value.
        switch (day) {
            case 1:
                dayValue = "Sunday";
                break;
            case 2:
                dayValue = "Monday";
                break;
            case 3:
                dayValue = "Tuesday";
                break;
            case 4:
                dayValue = "Wednesday";
                break;
            case 5:
                dayValue = "Thursday";
                break;
            case 6:
                dayValue = "Friday";
                break;
            case 7:
                dayValue = "Saturday";
                break;
        }
		
        //Run method that will check if we should make a notification.
        for(String club : clubArray){
			//Create the FireStore database and get the current club and get that clubs information.
            DocumentReference documentReference = database.collection("Clubs").document(club);
            documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
				//When the document is retrieved.
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
						//If the document that was retrieved exists get that clubs information.
                        if (document.exists()) {
							//Turn the document into a Clubs.class object.
                            Clubs club = document.toObject(Clubs.class);
                            date = club.getDate();
                            time = club.getStartTime();
                            endTime = club.getEndTime();
                            location = club.getLocation();
                            clubName = club.getName();
							//If the date equals the day value of that current day call the displayNotification method and pass in to string values.
                            if(date.equals(dayValue)){
                                displayNotification(clubName, clubName + " is going to be having a meeting today at " + time + " until " + endTime + " in room " + location);
                            }
                        } else {
							//Log error that no document was found
                            Log.d(TAG, "No such document");
                        }
                    } else {
						//Log error if the there is an issue with the call to the database.
                        Log.d(TAG, "Get failed with ", task.getException());
                    }
                }
            });

        }


        return Result.success();
    }

	//displayNotification method creates a notification with the title and description passed in.
    private void displayNotification(String title, String description){

		//Create notification manager.
        NotificationManager manager = (NotificationManager) getApplicationContext().getSystemService(MainActivity.NOTIFICATION_SERVICE/*Context.NOTIFICATION_SERVICE*/);

		//If the build version is equal to or greater than Oreo build create a notification channel.
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel("channel_1", "channel_1", NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("Send notification to user");
            manager.createNotificationChannel(channel);
        }

		//Create notification compat with the title and description.
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), "channel_1")
                .setContentTitle(title)
                .setContentText(description)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(description))
                .setSmallIcon(R.drawable.app_icon);

		//Display notification.
        manager.notify(1, builder.build());
    }
}

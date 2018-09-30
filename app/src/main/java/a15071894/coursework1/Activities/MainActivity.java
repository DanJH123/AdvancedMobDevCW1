package a15071894.coursework1.Activities;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.ArrayList;

import a15071894.coursework1.Control.LocationController;
import a15071894.coursework1.Control.SMSController;
import a15081794.coursework1.R;
import a15071894.coursework1.Control.Constants;
import a15071894.coursework1.Friends.Friend;

/*
* The startup class. Contains buttons and fields for finding locations, transport points, sending
* locations, viewing friendslist and checking the map. This class also initialises the broadcast
* receiver
* */
public class MainActivity extends AppCompatActivity {

    private TextView locationTV;
    private TextView distanceTV;
    private Button findButton;
    private Button friendsButton;
    private Button sendLocationButton;
    private Button sendMeetingButton;
    private Button viewMapButton;
    private ToggleButton tubeTB;
    private ToggleButton busTB;
    private ToggleButton bikeTB;
    private ArrayList<Friend> friendsList = new ArrayList<>();
    private static final String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    // Link views to Java
        locationTV = (TextView) findViewById(R.id.location_entry);
        distanceTV = (TextView) findViewById(R.id.distance_entry);
        findButton = (Button) findViewById(R.id.find_button);
        friendsButton = (Button) findViewById(R.id.friends_button);
        sendLocationButton = (Button) findViewById(R.id.send_location_button);
        sendMeetingButton = (Button) findViewById(R.id.send_meeting_button);
        viewMapButton = (Button) findViewById(R.id.view_map_button);
        tubeTB = (ToggleButton) findViewById(R.id.tube_t_button);
        busTB = (ToggleButton) findViewById(R.id.bus_t_button);
        bikeTB = (ToggleButton) findViewById(R.id.bike_t_button);

    //call the broadcase receiver on startup so messages can be retrieved from anywhere
    //This will also request permission to send/receive messages upon start up of app
        final SMSController smsController = new SMSController(getApplicationContext(), this, null, null);
        smsController.setFriendBroadcastReceiver();
        BroadcastReceiver bcr = smsController.getFriendBroadcastReceiver();
        IntentFilter filter = new IntentFilter(SMS_RECEIVED);
        registerReceiver(bcr, filter);

    //Set on click listeners
        /*
        * find button uses all fields in the interface to search for and display pins of nearby
        * transport points. Clicking this button will send all the information to the Maps Activity.
        * */
        findButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(locationTV.getText().length() > 0 && distanceTV.getText().length() > 0 &&
                        (tubeTB.isChecked() || busTB.isChecked() || bikeTB.isChecked())) {
                    Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                    intent.putExtra(Constants.LOCATION, locationTV.getText().toString());
                    intent.putExtra(Constants.DISTANCE, distanceTV.getText().toString());
                    intent.putExtra(Constants.TITLESTRING, "");
                    intent.putExtra(Constants.TUBE, tubeTB.isChecked());
                    intent.putExtra(Constants.BUS, busTB.isChecked());
                    intent.putExtra(Constants.BIKE, bikeTB.isChecked());
                    startActivity(intent);
                }
                else{
                    if(! (locationTV.getText().length() > 0)) {locationTV.setHint("Location required!");}
                    if(! (distanceTV.getText().length() > 0)) {distanceTV.setHint("Distance required!");}
                    if(!tubeTB.isChecked() && !busTB.isChecked() && !bikeTB.isChecked()){
                        Toast.makeText(getApplicationContext(),"Select a mode!", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        /*
        *Clicking this button sends an SMS containing the location to all on the friends list
        *This option sends the location as the user's current location.
        * */
        sendLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            friendsList = smsController.getFriends(friendsList);
            if(friendsList.isEmpty() || locationTV.getText().toString().isEmpty()){
                if(! (locationTV.getText().length() > 0)) {locationTV.setHint("Location required!");}
                if(friendsList.isEmpty()) {
                    Toast.makeText(getApplicationContext(),
                            R.string.no_friends_error, Toast.LENGTH_LONG).show();
                }
            }
            else{
                smsController.sendLocation(friendsList, locationTV.getText().toString());
            }
            }
        });

        /*
        *Clicking this button sends an SMS containing the location to all on the friends list
        *This option sends the location as a meeting point.
        * */
        sendMeetingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                friendsList = smsController.getFriends(friendsList);
                if(friendsList.isEmpty() || locationTV.getText().toString().isEmpty()){
                    if(! (locationTV.getText().length() > 0)) {locationTV.setHint("Location required!");}
                    if(friendsList.isEmpty()) {
                        Toast.makeText(getApplicationContext(),
                                R.string.no_friends_error, Toast.LENGTH_LONG).show();
                    }
                }
                else{
                    smsController.sendMeeting(friendsList, locationTV.getText().toString());
                }
            }
        });

        //Opens the friends list page
        friendsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), FriendsActivity.class);
                startActivity(intent);
            }
        });

        //Opens the Map
        viewMapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                startActivity(intent);
            }
        });
    }

}

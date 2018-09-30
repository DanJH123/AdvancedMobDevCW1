package a15071894.coursework1.Control;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SimpleCursorAdapter;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Scanner;
import java.util.concurrent.locks.ReentrantLock;

import a15071894.coursework1.Activities.MapsActivity;
import a15071894.coursework1.Friends.Friend;
import a15071894.coursework1.Friends.FriendContract;
import a15071894.coursework1.Friends.FriendProvider;
import a15081794.coursework1.R;

/*
* This class handles the sending and receiving of SMSs. It adds entries to the database tables and
* initiates the map activity to start when the appropriate messages are received.
* */
public class SMSController {

    private static final int MY_PERMISSIONS_REQUEST_RECEIVE_SMS = 1;

//The variables are sent as the first part of the SMSs and are also used when handling receipts of them.
    private static final String ACCEPT_STR = "ACCEPT";
    private static final String REQUEST_STR = "REQUEST";
    private static final String FRIEND_LOCATION_STR = "FRIEND-LOCATION";
    private static final String MEETING_LOCATION_STR = "MEET-LOCATION";
    private static final String DELETE_STR = "DELETE";

    private Context context;
    private Activity activity;
    private BroadcastReceiver broadcastReceiver;
    private SimpleCursorAdapter friendListAdapter;
    private SimpleCursorAdapter pendingListAdapter;
    private ReentrantLock lock = new ReentrantLock();

    public SMSController(Context context, Activity activity, SimpleCursorAdapter friendListAdapter,
                         SimpleCursorAdapter pendingListAdapter) {
        this.context = context;
        this.activity = activity;
        this.friendListAdapter = friendListAdapter;
        this.pendingListAdapter = pendingListAdapter;
        //Request Permission to use SMS if not already gained
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECEIVE_SMS) !=
                PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(context, Manifest.permission.SEND_SMS) !=
                        PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]
                            {Manifest.permission.RECEIVE_SMS, Manifest.permission.SEND_SMS},
                    MY_PERMISSIONS_REQUEST_RECEIVE_SMS);
        }
    }

    public BroadcastReceiver getFriendBroadcastReceiver() {
        return this.broadcastReceiver;
    }

    public void setFriendBroadcastReceiver() {
        this.broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Bundle bundle = intent.getExtras();
                String format = bundle.getString("format");
                Object[] pdus = (Object[]) bundle.get("pdus");
                for (Object pduObj : pdus) {
                    byte[] pdu = (byte[]) pduObj;
                    SmsMessage message = SmsMessage.createFromPdu(pdu,format);
                    processMessage(message);
                }
            }
        };
    }

    //What happens when a message is received
    private void processMessage(SmsMessage message) {
        String messageText = message.getMessageBody();
        Scanner scan = new Scanner(messageText);
        if(scan.hasNext()){
            String receiveType = scan.next();
        //ACCEPT FRIEND REQUEST
            if(receiveType.equalsIgnoreCase(ACCEPT_STR)){
                String sender = scan.next();
                String phone = message.getOriginatingAddress();
                Friend friend = new Friend(sender, phone);
                lock.lock();
                try{
                    addFriend(friend);
                }
                finally {
                    lock.lock();
                }
            }
        //FRIEND REQUEST
            else if(receiveType.equalsIgnoreCase(REQUEST_STR)){
                String sender = scan.next();
                String phone = message.getOriginatingAddress();
                Friend friend = new Friend(sender, phone);
                lock.lock();
                try{
                    addPending(sender,friend);
                }
                finally {
                    lock.lock();
                }
            }
        //LOCATION OF FRIEND
            else if(receiveType.equalsIgnoreCase(FRIEND_LOCATION_STR)){
                String location = scan.nextLine().trim();
                String phone = message.getOriginatingAddress();
                lock.lock();
                try{
                    String titleString =
                            getTime(message.getTimestampMillis())+": "+
                            getFriendByPhoneNumber(phone).getName()+" "+
                            context.getString(R.string.is_here_string) +" "+location+" ";
                    Intent intent = new Intent(context, MapsActivity.class);
                    intent.putExtra(Constants.LOCATION, location);
                    intent.putExtra(Constants.DISTANCE, "");
                    intent.putExtra(Constants.TITLESTRING, titleString);
                    intent.putExtra(Constants.TUBE, false);
                    intent.putExtra(Constants.BUS, false);
                    intent.putExtra(Constants.BIKE, false);
                    context.startActivity(intent);
                }
                finally {
                    lock.lock();
                }
            }
        //MEETING POINT
            else if(receiveType.equalsIgnoreCase(MEETING_LOCATION_STR)){
                String location = scan.nextLine().trim();
                String phone = message.getOriginatingAddress();
                String titleString =
                        getTime(message.getTimestampMillis())+": "+
                        getFriendByPhoneNumber(phone).getName()+" "+
                        context.getString(R.string.meeting_string) +" "+location+" ";
                lock.lock();
                try{
                    Intent intent = new Intent(context, MapsActivity.class);
                    intent.putExtra(Constants.LOCATION, location);
                    intent.putExtra(Constants.DISTANCE, "");
                    intent.putExtra(Constants.TITLESTRING, titleString);
                    intent.putExtra(Constants.TUBE, false);
                    intent.putExtra(Constants.BUS, false);
                    intent.putExtra(Constants.BIKE, false);
                    context.startActivity(intent);
                }
                finally {
                    lock.lock();
                }
            }
        //DELETE FRIEND
            else if(receiveType.equalsIgnoreCase(DELETE_STR)){
                String sender = scan.next();
                String phone = message.getOriginatingAddress();
                Friend friend = new Friend(sender, phone);
                lock.lock();
                try{
                    removeFriend(friend);
                }
                finally {
                    lock.lock();
                }
            }
        }
    }

    //Convert the timestamp into a more readable time
    private String getTime(long timestampMillis) {
        DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.ENGLISH);
        Date date = new Date(timestampMillis);
        return timeFormat.format(date);
    }

    //add friend to database table: Friend
    private void addFriend(Friend friend){
        ContentValues values = new ContentValues();
        values.put(FriendContract.FRIEND_COLUMN_NAME, friend.getName());
        values.put(FriendContract.FRIEND_COLUMN_PHONE, friend.getPhone());
        Uri uri = context.getContentResolver().insert(FriendProvider.FRIEND_CONTENT_URI, values);
        if(friendListAdapter != null) friendListAdapter.notifyDataSetChanged();
    }

    //add friend to database table: Pending
    private void addPending(String name, Friend friend){
        ContentValues values = new ContentValues();
        values.put(FriendContract.PENDING_COLUMN_NAME, name);
        values.put(FriendContract.PENDING_COLUMN_PHONE, friend.getPhone());
        Uri uri = context.getContentResolver().insert(FriendProvider.PENDING_CONTENT_URI, values);
        if(pendingListAdapter != null)pendingListAdapter.notifyDataSetChanged();
    }

    //remove friend from database table: Friend
    public void removeFriend(Friend friend){
        String selection = FriendContract.FRIEND_COLUMN_PHONE +" ='"+ friend.getPhone() +"'";
        context.getContentResolver().delete(FriendProvider.FRIEND_CONTENT_URI, selection,null );
        if(friendListAdapter != null)friendListAdapter.notifyDataSetChanged();
    }

    //remove friend to database table: Pending
    public void removePending(Friend friend) {
        String selection = FriendContract.PENDING_COLUMN_PHONE +" ='"+ friend.getPhone() +"'";
        context.getContentResolver().delete(FriendProvider.PENDING_CONTENT_URI, selection,null );
        if(pendingListAdapter != null)pendingListAdapter.notifyDataSetChanged();
    }

//SMS sending
    //Friend request
    public void sendFriendRequest(String sender, Friend friend) {
        SmsManager manager = SmsManager.getDefault();
        String friendRequest = REQUEST_STR+" "+sender;
        manager.sendTextMessage(friend.getPhone(),null, friendRequest, null, null);
    }
    //Accept request
    public void sendAcceptFriendRequest(Friend friend, String sender) {
        SmsManager manager = SmsManager.getDefault();
        String friendAccept =  ACCEPT_STR +" "+sender;
        addFriend(friend);
        removePending(friend);
        manager.sendTextMessage(friend.getPhone(),null, friendAccept, null, null);
    }
    //Delete friend
    public void sendDeleteFriend(Friend friend) {
        SmsManager manager = SmsManager.getDefault();
        String friendDelete =  DELETE_STR +" (This contact has been removed)";
        removeFriend(friend);
        manager.sendTextMessage(friend.getPhone(),null, friendDelete, null, null);
    }
    //Send simulated/current location
    public void sendLocation(ArrayList<Friend> friendList, String location) {
        SmsManager manager = SmsManager.getDefault();
        for (Friend friend : friendList) {
            String friendRequest = FRIEND_LOCATION_STR +" "+location;
            manager.sendTextMessage(friend.getPhone(), null, friendRequest, null, null);
        }
    }
    //Send meeting point
    public void sendMeeting(ArrayList<Friend> friendsList, String location) {
        SmsManager manager = SmsManager.getDefault();
        for (Friend friend : friendsList) {
            String friendRequest = MEETING_LOCATION_STR +" "+location;
            manager.sendTextMessage(friend.getPhone(), null, friendRequest, null, null);
        }
    }

    private Friend getFriendByPhoneNumber(String phone) {
        Friend friend = null;
        Uri uri = FriendProvider.FRIEND_CONTENT_URI;
        String[] projection = new String[] {
                FriendContract.FRIEND_COLUMN_NAME, FriendContract.FRIEND_COLUMN_PHONE};
        String selection = FriendContract.PENDING_COLUMN_PHONE +" ='"+ phone +"'";
        String[] selectionArgs = null;
        String sortOrder = null;
        Cursor cursor =
                context.getContentResolver().query(uri, projection, selection, selectionArgs, sortOrder);
        if (cursor != null) {
            cursor.moveToFirst();
            for (int i = 0; i < cursor.getCount(); i++){
                String name = cursor.getString(cursor
                        .getColumnIndexOrThrow(FriendContract.FRIEND_COLUMN_NAME));
                friend = new Friend(name, phone);
                cursor.moveToNext();
            }
            cursor.close();
        }
        return friend;
    }

    //for use with updating list view
    public ArrayList<Friend> getFriends(ArrayList<Friend> friendsList){
        friendsList = new ArrayList<>();
        Uri uri = FriendProvider.FRIEND_CONTENT_URI;
        String[] projection = new String[] {
                FriendContract.FRIEND_COLUMN_NAME, FriendContract.FRIEND_COLUMN_PHONE};
        String selection = null;
        String[] selectionArgs = null;
        String sortOrder = FriendContract.FRIEND_COLUMN_NAME + " ASC";
        Cursor cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, sortOrder);
        if (cursor != null) {
            cursor.moveToFirst();
            for (int i = 0; i < cursor.getCount(); i++){
                String name = cursor.getString(cursor
                        .getColumnIndexOrThrow(FriendContract.FRIEND_COLUMN_NAME));
                String phone = cursor.getString(cursor
                        .getColumnIndexOrThrow(FriendContract.FRIEND_COLUMN_PHONE));
                friendsList.add(new Friend(name, phone));
                cursor.moveToNext();
            }
            cursor.close();
        }
        return friendsList;
    }
}

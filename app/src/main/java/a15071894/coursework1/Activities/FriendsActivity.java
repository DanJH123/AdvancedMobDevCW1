package a15071894.coursework1.Activities;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import a15081794.coursework1.R;
import a15071894.coursework1.Friends.Friend;
import a15071894.coursework1.Friends.FriendContract;
import a15071894.coursework1.Friends.FriendProvider;
import a15071894.coursework1.Control.SMSController;

/*
* This class is for maintaining the friends list. The user can send friend requests, acceptances and
* removals with this all being managed here and in the SMSController class.
* */
public class FriendsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    private ListView friendsLV;
    private ListView pendingLV;
    private Button requestFriendButton;
    private EditText senderNameET;
    private EditText phoneET;
    private Friend clickedFriend;
    private SMSController smsController;

    private SimpleCursorAdapter friendListAdapter;
    private SimpleCursorAdapter pendingListAdapter;
    private Cursor friendCursor;
    private final int FRIEND_CURSOR_ID = 0;
    private Cursor pendingCursor;
    private final int PENDING_CURSOR_ID = 1;
    private ArrayList<Friend> friendsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friendslist);
    // Link views to Java
        friendsLV = (ListView) findViewById(R.id.accepted_friends_list);
        pendingLV = (ListView) findViewById(R.id.pending_friends_list);
        requestFriendButton = (Button) findViewById(R.id.request_friend_button);
        senderNameET = (EditText) findViewById(R.id.sender_name_entry);
        phoneET = (EditText) findViewById(R.id.friend_number_entry);

    // Set adapter to the friend list view
        String[] from= {FriendContract.FRIEND_COLUMN_NAME, FriendContract.FRIEND_COLUMN_PHONE};
        int to[] = {R.id.username_col, R.id.phone_col};
        friendListAdapter = new SimpleCursorAdapter(
                this, R.layout.friend_row, friendCursor, from, to, FRIEND_CURSOR_ID);
        getLoaderManager().initLoader(FRIEND_CURSOR_ID,null,this);
        friendsLV.setAdapter(friendListAdapter);
        friendListAdapter.notifyDataSetChanged();

    // Set adapter to the pending list view
        String[] bfrom= {FriendContract.PENDING_COLUMN_NAME, FriendContract.PENDING_COLUMN_PHONE};
        int bto[] = {R.id.pending_name_col, R.id.pending_phone_col};
        pendingListAdapter = new SimpleCursorAdapter(
                this, R.layout.pending_row, pendingCursor, bfrom, bto, PENDING_CURSOR_ID);
        getLoaderManager().initLoader(PENDING_CURSOR_ID,null,this);
        pendingLV.setAdapter(pendingListAdapter);
        pendingListAdapter.notifyDataSetChanged();

        smsController = new SMSController(getApplicationContext(), this, friendListAdapter, pendingListAdapter);
        friendsList = smsController.getFriends(friendsList);

    //Define click listeners
        //Sends a friend request to the entered number
        requestFriendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sender = senderNameET.getText().toString();
                String phone = phoneET.getText().toString();
                if(sender.isEmpty() || !validatePhone(phone)){
                    if (sender.isEmpty()) senderNameET.setHint("Your Name Required!");
                    if (!validatePhone(phone)) Toast.makeText(getApplicationContext(),
                            R.string.phone_input_error, Toast.LENGTH_LONG).show();
                }
                else{
                    Friend friend = new Friend("", phone); //No name required here
                    smsController.sendFriendRequest(sender,friend);
                }
            }
        });

        //Opens a context menu with an option to delete
        friendsLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                registerForContextMenu(friendsLV);
                //clickedFriend allows changes to be made to that specific friend in later methods
                clickedFriend = new Friend(
                        ((TextView) view.findViewById(R.id.username_col)).getText().toString(),
                        ((TextView) view.findViewById(R.id.phone_col)).getText().toString());
                view.showContextMenu();
            }
        });
        //Opens a context menu with the option to accept or delete
        pendingLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                registerForContextMenu(pendingLV);
                clickedFriend = new Friend(
                        ((TextView) view.findViewById(R.id.pending_name_col)).getText().toString(),
                        ((TextView) view.findViewById(R.id.pending_phone_col)).getText().toString());
                view.showContextMenu();
            }
        });
    }

    //Returns true if phone number is valid, false if not.
    private boolean validatePhone(String phone) {
        return !phone.isEmpty();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if(v.getId() == R.id.accepted_friends_list){
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.friendslist_context_menu, menu);
        }
        if(v.getId() == R.id.pending_friends_list){
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.pendinglist_context_menu, menu);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item){
        int id = item.getItemId();
        if(id==R.id.delete_friend){
            // deletes friend on user's list as well as themselves on the friend's list.
            smsController.sendDeleteFriend(clickedFriend);
            return true;
        }
        if(id==R.id.delete_pending){
            //removes pending request from list
            smsController.removePending(clickedFriend);
            return true;
        }
        if(id==R.id.accept_pending){
            //Accepts user and adds them to user's list as well as friend's.
            String sender = senderNameET.getText().toString();
            if(sender.isEmpty()){
                senderNameET.setHint("Name Required!");
            }
            else {
                smsController.sendAcceptFriendRequest(clickedFriend, sender);
            }
            return true;
        }
        return super.onContextItemSelected(item);
    }

    /*
    * There are 2 tables from the database being used in this activity, ID allows specification of
    * which cursor to use for them and the loader methods below use this to avoid making/not making
    * changes to tables they are not supposed to
    */
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if(id == FRIEND_CURSOR_ID) {
            return new CursorLoader(this, FriendProvider.FRIEND_CONTENT_URI, null, null, null,
                    FriendContract.FRIEND_COLUMN_NAME + " ASC");
        }
        else if(id == PENDING_CURSOR_ID) {
            return new CursorLoader(this, FriendProvider.PENDING_CONTENT_URI, null, null, null,
                    FriendContract.PENDING_COLUMN_NAME + " ASC");
        }
        else{
            return null;
        }
    }
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if(loader.getId() == FRIEND_CURSOR_ID) friendListAdapter.swapCursor(data);
        else if (loader.getId() == PENDING_CURSOR_ID) pendingListAdapter.swapCursor(data);
    }
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        friendListAdapter.swapCursor(null);
    }

}

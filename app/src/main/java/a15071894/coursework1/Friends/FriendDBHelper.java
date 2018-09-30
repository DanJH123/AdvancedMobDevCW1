package a15071894.coursework1.Friends;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

//Database helper that contains 2 tables - friend and pending
public class FriendDBHelper extends SQLiteOpenHelper{
    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";
    private static final String DATABASE_NAME = "friends.db";
    private static final int DATABASE_VERSION = 2;

    private static final String CREATE_FRIEND_ENTRIES =
            "CREATE TABLE "+ FriendContract.TABLE_NAME_FRIEND +
                    " ("+
                    FriendContract._ID+" INTEGER "+" PRIMARY KEY " +COMMA_SEP+
                    FriendContract.FRIEND_COLUMN_NAME +TEXT_TYPE+" " + COMMA_SEP +
                    FriendContract.FRIEND_COLUMN_PHONE +TEXT_TYPE+")";

    private static final String DELETE_FRIEND_ENTRIES =
                                        "DROP TABLE IF EXISTS " + FriendContract.TABLE_NAME_FRIEND;

    private static final String CREATE_PENDING_ENTRIES =
            "CREATE TABLE "+ FriendContract.TABLE_NAME_PENDING +
                    " ("+
                    FriendContract._ID+" INTEGER "+" PRIMARY KEY " +COMMA_SEP+
                    FriendContract.PENDING_COLUMN_NAME +TEXT_TYPE+" " + COMMA_SEP +
                    FriendContract.PENDING_COLUMN_PHONE +TEXT_TYPE+")";

    private static final String DELETE_PENDING_ENTRIES =
            "DROP TABLE IF EXISTS " + FriendContract.TABLE_NAME_PENDING;

    public FriendDBHelper(Context context){
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_PENDING_ENTRIES);
        db.execSQL(CREATE_FRIEND_ENTRIES);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DELETE_FRIEND_ENTRIES);
        db.execSQL(DELETE_PENDING_ENTRIES);
        onCreate(db);
    }
    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion){
        onUpgrade(db,oldVersion,newVersion);
    }
}

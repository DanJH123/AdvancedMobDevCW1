package a15071894.coursework1.Friends;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

//Provides services for handling both tables in Friend database.
public class FriendProvider extends ContentProvider{

    public static final String AUTHORITY = "a15081794.coursework1.Friends.FriendProvider";
    public static final String FRIEND_BASE_PATH = FriendContract.TABLE_NAME_FRIEND;
    public static final Uri FRIEND_CONTENT_URI = Uri.parse("content://"+ AUTHORITY +"/"+ FRIEND_BASE_PATH);
    public static final String PENDING_BASE_PATH = FriendContract.TABLE_NAME_PENDING;
    public static final Uri PENDING_CONTENT_URI = Uri.parse("content://"+ AUTHORITY +"/"+ PENDING_BASE_PATH);

    private static final int FRIENDS = 0;
    private static final int FRIEND_ID = 1;
    private static final int PENDING = 20;
    private static final int PENDING_ID = 21;

    private FriendDBHelper helper;
    private UriMatcher uriMatcher;

    public FriendProvider(){}

    @Override
    public boolean onCreate() {
        helper = new FriendDBHelper(getContext());
        uriMatcher=new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY, FRIEND_BASE_PATH, FRIENDS);
        uriMatcher.addURI(AUTHORITY, FRIEND_BASE_PATH +"/#", FRIEND_ID);
        uriMatcher.addURI(AUTHORITY, PENDING_BASE_PATH, PENDING);
        uriMatcher.addURI(AUTHORITY, PENDING_BASE_PATH +"/#", PENDING_ID);
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                                                                                String sortOrder){
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        int uriType = uriMatcher.match(uri);
        switch(uriType){
            case FRIENDS:
                builder.setTables(FriendContract.TABLE_NAME_FRIEND);
                break;
            case FRIEND_ID:
                builder.setTables(FriendContract.TABLE_NAME_FRIEND);
                builder.appendWhere(FriendContract._ID+" = "+uri.getLastPathSegment());
                break;
            case PENDING:
                builder.setTables(FriendContract.TABLE_NAME_PENDING);
                break;
            case PENDING_ID:
                builder.setTables(FriendContract.TABLE_NAME_PENDING);
                builder.appendWhere(FriendContract._ID+" = "+uri.getLastPathSegment());
                break;
            default:
                throw new IllegalArgumentException("Unsupported URI: "+uri);
        }
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = builder.query(db,projection,selection,selectionArgs,null,null,sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(),uri);
        return cursor;
    }


    @Override
    public String getType(Uri uri) {
        switch(uriMatcher.match(uri)){
            case FRIENDS:
                    return "vnd.android.cursor.dir/vnd.a15081794.friend";
            case FRIEND_ID:
                    return "vnd.android.cursor.item/vnd.a15081794.friend";
            case PENDING:
                    return "vnd.android.cursor.dir/vnd.a15081794.pending";
            case PENDING_ID:
                    return "vnd.android.cursor.item/vnd.a15081794.pending";
            default:
                throw new IllegalArgumentException("Unsupported URI: "+uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = helper.getWritableDatabase();
        int uriType = uriMatcher.match(uri);
        Uri resultUri = null;
        if(uriType == FRIENDS){
            long rowID = db.insert(FriendContract.TABLE_NAME_FRIEND,null,values);
            resultUri = ContentUris.withAppendedId(uri,rowID);
            getContext().getContentResolver().notifyChange(resultUri,null);
        }
        else if(uriType == PENDING){
            long rowID = db.insert(FriendContract.TABLE_NAME_PENDING,null,values);
            resultUri = ContentUris.withAppendedId(uri,rowID);
            getContext().getContentResolver().notifyChange(resultUri,null);
        }
        else{
            throw new IllegalArgumentException("Unsupported URI: "+uri);
        }
        return resultUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = helper.getWritableDatabase();
        int rowsDeleted=0;
        int uriType = uriMatcher.match(uri);
        String newSelection;
        switch(uriType){
            case FRIENDS:
                rowsDeleted=db.delete(FriendContract.TABLE_NAME_FRIEND,selection,selectionArgs);
                break;
            case FRIEND_ID:
                newSelection = appendToSelection(uri,selection);
                rowsDeleted=db.delete(FriendContract.TABLE_NAME_FRIEND,newSelection,selectionArgs);
                break;
            case PENDING:
                rowsDeleted=db.delete(FriendContract.TABLE_NAME_PENDING,selection,selectionArgs);
                break;
            case PENDING_ID:
                newSelection = appendToSelection(uri,selection);
                rowsDeleted=db.delete(FriendContract.TABLE_NAME_PENDING,newSelection,selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unrecognised uri: "+uri);
        }
        getContext().getContentResolver().notifyChange(uri,null);
        return rowsDeleted;
    }

    private String appendToSelection(Uri uri, String selection) {
        String id = uri.getLastPathSegment();
        StringBuilder newSelection = new StringBuilder(FriendContract._ID+"="+id);
        if (selection!=null && !selection.isEmpty()){
            newSelection.append(" AND "+selection);
        }
        return newSelection.toString();
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase db = helper.getWritableDatabase();
        int rowsUpdated=0;
        int uriType = uriMatcher.match(uri);
        String newSelection;
        switch(uriType){
            case FRIENDS:
                rowsUpdated=db.update(FriendContract.TABLE_NAME_FRIEND,values,selection,selectionArgs);
                break;
            case FRIEND_ID:
                newSelection = appendToSelection(uri,selection);
                rowsUpdated=db.update(FriendContract.TABLE_NAME_FRIEND,values,newSelection,selectionArgs);
                break;
            case PENDING:
                rowsUpdated=db.update(FriendContract.TABLE_NAME_PENDING,values,selection,selectionArgs);
                break;
            case PENDING_ID:
                newSelection = appendToSelection(uri,selection);
                rowsUpdated=db.update(FriendContract.TABLE_NAME_FRIEND,values,newSelection,selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unrecognised uri: "+uri);
        }
        getContext().getContentResolver().notifyChange(uri,null);
        return rowsUpdated;
    }
}

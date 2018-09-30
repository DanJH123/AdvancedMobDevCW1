package a15071894.coursework1.Friends;

import android.provider.BaseColumns;

public class FriendContract implements BaseColumns{
//Table 1
    public static final String TABLE_NAME_FRIEND = "friend";
    public static final String FRIEND_COLUMN_NAME = "name";
    public static final String FRIEND_COLUMN_PHONE ="phone";

    public static final int FRIEND_COLUMN_INDEX_ID = 0;
    public static final int FRIEND_COLUMN_INDEX_NAME = 1;
    public static final int FRIEND_COLUMN_INDEX_PHONE = 2;

//Table 2
    public static final String TABLE_NAME_PENDING = "pending";
    public static final String PENDING_COLUMN_NAME = "name";
    public static final String PENDING_COLUMN_PHONE ="phone";

    public static final int PENDING_COLUMN_INDEX_ID = 0;
    public static final int PENDING_COLUMN_INDEX_NAME = 1;
    public static final int PENDING_COLUMN_INDEX_PHONE = 2;

}

package com.senter.reminders;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.sql.SQLDataException;

/**
 * Created by tckj on 2016/1/28.
 */
public class RemindersDbAdapter
{
    public static final String COL_ID = "_id";
    public static final String COL_CONTENT = "_content";
    public static final String COL_IMPORTANT = "important";

    public static final int INDEX_ID = 0;
    public static final int INDEX_CONTENT = INDEX_ID + 1;
    public static final int INDEX_IMPORTANT = INDEX_ID + 2;

    private  static final String TAG = "RemindersDbAdapter";

    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;

    private static final String DATABASE_NAME = "dba_remdrs";
    private static final String TABLE_NAME = "tbl_remdrs";
    private  static int DATABASE_VERSION = 1;


    private  final Context mCtx;

    private static final String DATABASE_CREATE = "CREATE TABLE if not exists " +
            TABLE_NAME + " ( " +
            COL_ID + " INTEGER PRIMARY KEY autoincrement, " +
            COL_CONTENT + " TEXT, " +
            COL_IMPORTANT + " INTEGER );";


    private class DatabaseHelper extends SQLiteOpenHelper
    {
        public DatabaseHelper(Context context)
        {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db)
        {
            Log.w(TAG,DATABASE_CREATE);
            db.execSQL(DATABASE_CREATE);

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
        {
            Log.w(TAG, "upgrading database from version " + oldVersion + " to "+
            newVersion + ", which will destory all old data");
            db.execSQL("DROP TABLE IF EXIST " + TABLE_NAME);
            onCreate(db);

        }
    }

    public RemindersDbAdapter(Context context)
    {
        this.mCtx = context;

    }

    public void open() throws SQLDataException
    {

        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();
    }

    public void close()
    {
      if(mDbHelper !=null)
      {
          mDbHelper.close();
      }
    }

    public long createReminder(Reminder reminder)
    {
        ContentValues values = new ContentValues();
        values.put(COL_CONTENT,reminder.getContent());
        values.put(COL_IMPORTANT,reminder.getImportant());
        return mDb.insert(TABLE_NAME,null,values);

    }

    public void createReminder(String name, boolean important)
    {
        ContentValues values = new ContentValues();
        values.put(COL_CONTENT,name);
        values.put(COL_IMPORTANT,important?1:0);

        mDb.insert(TABLE_NAME, null, values);

    }

    public Reminder fetchReminderById(int id)
    {
        Cursor cursor = mDb.query(TABLE_NAME,new String[]{COL_ID,COL_CONTENT,COL_IMPORTANT},COL_ID +
                " =?", new String[]{String.valueOf(id)},null,null,null,null );
        if(cursor !=null)
        {
            cursor.moveToFirst();

        }
        return new Reminder(
                cursor.getInt(INDEX_ID),
                cursor.getString(INDEX_CONTENT),
                cursor.getInt(INDEX_IMPORTANT) );

    }

    public Cursor fetchAllReminders()
    {
        Cursor mCursor = mDb.query(TABLE_NAME,new String[]{COL_ID,COL_CONTENT,COL_IMPORTANT},null,null,null,null,null);
        if(mCursor !=null)
            mCursor.moveToFirst();
        return mCursor;
    }

    public void updateReminder(Reminder reminder)
    {
        ContentValues values = new ContentValues();
        values.put(COL_CONTENT,reminder.getContent());
        values.put(COL_IMPORTANT,reminder.getImportant());

        mDb.update(TABLE_NAME,values,COL_ID + " =?",new String[]{String.valueOf(reminder.getId())});

    }

    public void deleteReminderById(int nId)
    {
        mDb.delete(TABLE_NAME, COL_ID + " =?", new String[]{String.valueOf(nId)}
        );
    }

    public void deleteAllReminders()
    {
        mDb.delete(TABLE_NAME,null,null);
    }


}


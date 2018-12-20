package com.gps.mojito.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

import com.gps.mojito.database.model.Note;
import com.gps.mojito.decode.model.message;

public class DBHelper extends SQLiteOpenHelper {
  // Database Version
  private static final int DATABASE_VERSION = 1;

  // Database Name
  private static final String DATABASE_NAME = "GPSDATA.db";

  private String EXTERNAL_STORAGE_DIR = Environment.getExternalStorageDirectory().getAbsolutePath();
  private String DATA_STORAGE_DIR = Environment.getDataDirectory().getAbsolutePath();
  private String CURRENT_PATH = "//data//com.gps.mojito.prp_dgps//databases//gps_data_db.db";
  private String BACKUP_PATH = "gps_data_db.db";

  public DBHelper(Context context) {
    super(context, Environment.getExternalStorageDirectory().getAbsolutePath()
        + "/" + DATABASE_NAME, null, DATABASE_VERSION);
    Log.d("DBHELPER", String.format("EXTERNAL STORAGE DIRECTORY : %s", this.EXTERNAL_STORAGE_DIR));
    Log.d("DBHELPER", String.format("DATA STORAGE DIRECTORY : %s", this.DATA_STORAGE_DIR));
    Log.d("DBHELPER", String.format("CURRENT PATH : %s", this.CURRENT_PATH));
    Log.d("DBHELPER", String.format("BACKUP PATH : %s", this.BACKUP_PATH));
  }

  // Creating Tables
  @Override
  public void onCreate(SQLiteDatabase db) {

    // create notes table
    db.execSQL(Note.CREATE_TABLE);
    Log.d("DBHELPER", "CREATE TABLE " + Note.TABLE_NAME);
  }

  // Upgrading database
  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    // Drop older table if existed
    db.execSQL("DROP TABLE IF EXISTS " + Note.TABLE_NAME);

    // Create tables again
    onCreate(db);
  }

  public long insert(String note) {
    // get writable database as we want to write data
    SQLiteDatabase db = this.getWritableDatabase();

    ContentValues values = new ContentValues();
    // `id` and `timestamp` will be inserted automatically.
    // no need to add them
    message msg = new message(note);
    values.put(Note.COLUMN_RECORD, msg.split()[0]);
    values.put(Note.COLUMN_NOTE, note);

    // insert row
    long id = db.insert(Note.TABLE_NAME, null, values);

    // close db connection
    db.close();
    Log.d("DBHELPER", "SAVED " + note);
    // return newly inserted row id
    return id;
  }

  public long insert(message msg) {
    // get writable database as we want to write data
    SQLiteDatabase db = this.getWritableDatabase();

    ContentValues values = new ContentValues();
    // `id` and `timestamp` will be inserted automatically.
    // no need to add them
    values.put(Note.COLUMN_RECORD, msg.split()[0]);
    values.put(Note.COLUMN_NOTE, msg.getMsg());

    // insert row
    long id = db.insert(Note.TABLE_NAME, null, values);

    // close db connection
    db.close();
    Log.d("DBHELPER", "CREATE " + msg.getMsg());
    // return newly inserted row id
    return id;
  }

  public Note getNote(long id) {
    // get readable database as we are not inserting anything
    SQLiteDatabase db = this.getReadableDatabase();

    Cursor cursor = db.query(Note.TABLE_NAME,
        new String[]{Note.COLUMN_ID, Note.COLUMN_RECORD, Note.COLUMN_NOTE, Note.COLUMN_TIMESTAMP},
        Note.COLUMN_ID + "=?",
        new String[]{String.valueOf(id)}, null, null, null, null);

    if (cursor != null)
      cursor.moveToFirst();

    // prepare note object
    Note note = new Note(
        cursor.getInt(cursor.getColumnIndex(Note.COLUMN_ID)),
        cursor.getString(cursor.getColumnIndex(Note.COLUMN_RECORD)),
        cursor.getString(cursor.getColumnIndex(Note.COLUMN_NOTE)),
        cursor.getString(cursor.getColumnIndex(Note.COLUMN_TIMESTAMP)));

    // close the db connection
    cursor.close();

    return note;
  }

  public List<Note> getAllNotes() {
    List<Note> notes = new ArrayList<>();

    // Select All Query
    String selectQuery = "SELECT  * FROM " + Note.TABLE_NAME + " ORDER BY " +
        Note.COLUMN_TIMESTAMP + " DESC";

    SQLiteDatabase db = this.getWritableDatabase();
    Cursor cursor = db.rawQuery(selectQuery, null);

    // looping through all rows and adding to list
    if (cursor.moveToFirst()) {
      do {
        Note note = new Note();
        note.setId(cursor.getInt(cursor.getColumnIndex(Note.COLUMN_ID)));
        note.setRecord(cursor.getString(cursor.getColumnIndex(Note.COLUMN_RECORD)));
        note.setNote(cursor.getString(cursor.getColumnIndex(Note.COLUMN_NOTE)));
        note.setTimestamp(cursor.getString(cursor.getColumnIndex(Note.COLUMN_TIMESTAMP)));

        notes.add(note);
      } while (cursor.moveToNext());
    }

    // close db connection
    db.close();

    // return notes list
    return notes;
  }

  public int getNotesCount() {
    String countQuery = "SELECT  * FROM " + Note.TABLE_NAME;
    SQLiteDatabase db = this.getReadableDatabase();
    Cursor cursor = db.rawQuery(countQuery, null);

    int count = cursor.getCount();
    cursor.close();


    // return count
    return count;
  }

  public int updateNote(Note note) {
    SQLiteDatabase db = this.getWritableDatabase();

    ContentValues values = new ContentValues();
    values.put(Note.COLUMN_NOTE, note.getNote());

    // updating row
    return db.update(Note.TABLE_NAME, values, Note.COLUMN_ID + " = ?",
        new String[]{String.valueOf(note.getId())});
  }

  public void deleteNote(Note note) {
    SQLiteDatabase db = this.getWritableDatabase();
    db.delete(Note.TABLE_NAME, Note.COLUMN_ID + " = ?",
        new String[]{String.valueOf(note.getId())});
    db.close();
  }

  public void SaveDatabase() {
    List<Note> notes = getAllNotes();
    for (Note note : notes) {

    }
    try {
      File sd = Environment.getExternalStorageDirectory();
      File data = Environment.getDataDirectory();

      if (sd.canWrite()) {
        String currentDBPath = "//data//com.gps.mojito.prp_dgps//databases//gps_data_db.db";
        String backupDBPath = "gps_data_db.db";
        File currentDB = new File(data, this.CURRENT_PATH);
        File backupDB = new File(sd, this.BACKUP_PATH);

        if (currentDB.exists()) {
          FileChannel src = new FileInputStream(currentDB).getChannel();
          FileChannel dst = new FileOutputStream(backupDB).getChannel();
          dst.transferFrom(src, 0, src.size());
          src.close();
          dst.close();
          Log.d("DBHELPER", "BACKUP DATABASE");
        }
        Log.d("DBHELPER", "BACKUP DATABASE QUIT");
      }
    } catch (Exception e) {
      Log.d("DBHELPER", e.getMessage());
    }
  }

//    File f=new File("/data/data/com.gps.mojito.prp_dgps/databases/gps_data_db.db");
//    FileInputStream fis=null;
//    FileOutputStream fos=null;
//
//        try
//    {
//        fis=new FileInputStream(f);
//        fos=new FileOutputStream("/storage/emulated/0/db.db");
//        while(true)
//        {
//            int i=fis.read();
//            if(i!=-1)
//            {fos.write(i);}
//            else
//            {break;}
//        }
//        fos.flush();
//        Log.d("GPS-NMEA", "SaveDatabase: access");
//    }
//        catch(Exception e)
//    {
//        e.printStackTrace();
//        Log.d("GPS-NMEA", "SaveDatabase: ERROR");
//    }
//        finally
//    {
//        try
//        {
//            fos.close();
//            fis.close();
//        }
//        catch(IOException ioe)
//        {}
//    }
}

package com.gps.mojito.database.model;

public class Note {
  public static final String TABLE_NAME = "data";

  public static final String COLUMN_ID = "id";
  public static final String COLUMN_RECORD = "record";
  public static final String COLUMN_NOTE = "note";
  public static final String COLUMN_TIMESTAMP = "time";

  private int id;
  private String record;
  private String note;
  private String timestamp;


  // Create table SQL query
  public static final String CREATE_TABLE =
      "CREATE TABLE " + TABLE_NAME + "("
          + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
          + COLUMN_RECORD + " TEXT,"
          + COLUMN_NOTE + " TEXT,"
          + COLUMN_TIMESTAMP + " DATETIME DEFAULT CURRENT_TIMESTAMP"
          + ")";

  public Note() {
  }

  public Note(int id, String record, String note, String timestamp) {
    this.id = id;
    this.record = record;
    this.note = note;
    this.timestamp = timestamp;
  }

  public int getId() {
    return id;
  }

  public String getRecord() {
    return record;
  }

  public void setRecord(String record) {
    this.record = record;
  }

  public String getNote() {
    return note;
  }

  public void setNote(String note) {
    this.note = note;
  }

  public String getTimestamp() {
    return timestamp;
  }

  public void setId(int id) {
    this.id = id;
  }

  public void setTimestamp(String timestamp) {
    this.timestamp = timestamp;
  }
}

package com.gps.mojito.decode.model;

import android.util.Log;

public class message {
  private String msg;

  public message(String msg) { this.msg = msg; }

  public String[] split() {
    String[] splitData = msg.split(",");

    return splitData;
  }

  public String[] split(String msg) {
    this.msg = msg;
    String[] splitData = msg.split(",");

    return splitData;
  }

  public void setMsg(String msg) { this.msg = msg; }

  public String getMsg() { return this.msg; }
}

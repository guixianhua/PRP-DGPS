package com.gps.mojito.upload;

public class DataInfo {
  private String[] DifferentialVariable;

  public DataInfo (String[] DifferentialVariable) {
    this.DifferentialVariable = DifferentialVariable;
  }

  public String[] getVariable() {
    return this.DifferentialVariable;
  }
}

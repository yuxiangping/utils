package com.utils.tree.kdtree;

/**
 * @author: xiangping_yu
 * @data : 2015-7-7
 * @since : 1.5
 */
public class Point {

  private double latitude;
  private double longitude;

  public Point(double longtidude, double latidude) {
    this.longitude = longtidude;
    this.latitude = latidude;
  }
  
  public double getLatitude() {
    return latitude;
  }

  public void setLatitude(double latitude) {
    this.latitude = latitude;
  }

  public double getLongitude() {
    return longitude;
  }

  public void setLongitude(double longitude) {
    this.longitude = longitude;
  }

}

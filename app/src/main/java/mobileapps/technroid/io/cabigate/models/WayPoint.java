package mobileapps.technroid.io.cabigate.models;

import java.io.Serializable;

/**
 * Created by LAPTOP WORLD on 23/07/2016.
 */
public class WayPoint implements Serializable {

    public String getPoint() {
        return point;
    }

    public void setPoint(String point) {
        this.point = point;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    String
           point,
            lat,lng;
}

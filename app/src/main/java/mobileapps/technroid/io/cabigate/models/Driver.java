package mobileapps.technroid.io.cabigate.models;

/**
 * Created by LAPTOP WORLD on 03/08/2016.
 */
public class Driver {

     String  driver_id;
    String driver_name;
    String phone;
    String Callsign;
    String taxi_color;

    public String getDriver_id() {
        return driver_id;
    }

    public void setDriver_id(String driver_id) {
        this.driver_id = driver_id;
    }

    public String getDriver_name() {
        return driver_name;
    }

    public void setDriver_name(String driver_name) {
        this.driver_name = driver_name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCallsign() {
        return Callsign;
    }

    public void setCallsign(String callsign) {
        Callsign = callsign;
    }

    public String getTaxi_color() {
        return taxi_color;
    }

    public void setTaxi_color(String taxi_color) {
        this.taxi_color = taxi_color;
    }

    public String getTaxi_type() {
        return taxi_type;
    }

    public void setTaxi_type(String taxi_type) {
        this.taxi_type = taxi_type;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    String taxi_type;
    String distance;
}

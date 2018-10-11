package mobileapps.technroid.io.cabigate.models;

/**
 * Created by LAPTOP WORLD on 03/07/2016.
 */
public class MyZoneQueue {

    public String getVehicleid() {
        return vehicleid;
    }

    public void setVehicleid(String vehicleid) {
        this.vehicleid = vehicleid;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getWaited() {
        return waited;
    }

    public void setWaited(String waited) {
        this.waited = waited;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    String vehicleid,
            Name,
            waited,
            status;

}

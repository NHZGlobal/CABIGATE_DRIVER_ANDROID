package mobileapps.technroid.io.cabigate.models;

/**
 * Created by LAPTOP WORLD on 06/08/2016.
 */
public class Passenger {
  String passenger_id;

    public String getPassenger_id() {
        return passenger_id;
    }

    public void setPassenger_id(String passenger_id) {
        this.passenger_id = passenger_id;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    String firstname;
    String phone;

}

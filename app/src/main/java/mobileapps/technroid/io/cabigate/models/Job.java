package mobileapps.technroid.io.cabigate.models;

import java.io.Serializable;
import java.util.List;

/**
 * Created by LAPTOP WORLD on 05/06/2016.
 */


public class Job implements Serializable {

    public String getPickup_lat() {
        return pickup_lat;
    }

    public void setPickup_lat(String pickup_lat) {
        this.pickup_lat = pickup_lat;
    }

    public String getPickup_lng() {
        return pickup_lng;
    }

    public void setPickup_lng(String pickup_lng) {
        this.pickup_lng = pickup_lng;
    }

    public String getDrop_lat() {
        return drop_lat;
    }

    public void setDrop_lat(String drop_lat) {
        this.drop_lat = drop_lat;
    }

    public String getDrop_lng() {
        return drop_lng;
    }

    public void setDrop_lng(String drop_lng) {
        this.drop_lng = drop_lng;
    }

    public String getPayment_type() {
        return payment_type;
    }

    public void setPayment_type(String payment_type) {
        this.payment_type = payment_type;
    }

    public String getRoom_id() {
        return room_id;
    }

    public void setRoom_id(String room_id) {
        this.room_id = room_id;
    }

    public boolean isupdate;
    private String pickup_lat,
    pickup_lng,
   drop_lat,drop_lng,payment_type,room_id;
    private String jobid;

    public  String traveltime;


    public String getRefrence() {
        return refrence;
    }

    public void setRefrence(String refrence) {
        this.refrence = refrence;
    }

    private String refrence;
    public int awaytime;
    private String pickupLat;
    private String pickupLng;
    private String dropLat;
    private String dropLng;
    private String distance;
    private String unit;
    private String fare;
    private String duration;
    private String passengers;
    private String bags;
    private String wheelchairs;
    private String paxrid;
    private String vehicle_type;
    private String notes;
    private String paxname;
    private String paxtel;
    private String paxemail;
    private String when;
    private String pickuptime;
    private String pickupdistance;
    private String pickup;
    private String dropoff;
    private String timer;
    private String journeyType;
    private String stop_count;
    private String stop1;
    private String stop1Lat;
    private String stop1Lng;
    private String stop2;
    private String stop2Lat;
    private String stop2Lng;
    private String stop3;
    private String stop3Lat;
    private String stop3Lng;
    private String stop4;
    private String stop4Lat;
    private String stop4Lng;
    private String stop5;
    private String stop5Lat;
    private String stop5Lng;
    private String paymentType;
    private String to;
    private String roomId;
    private String sender;
    private String dispatcherid;
    private String dispatcher;
    private String currentstatus;
    public  int status1;
    public  String status;
    public  String journey_type;
    public  String tariff  ;
    public  boolean issync;
    public  boolean isprice;
    public  boolean showdropoff  ;


    public java.util.List<WayPoint> getWaypoints() {
        return waypoints;
    }

    public void setWaypoints(List<WayPoint> waypoints) {
        this.waypoints = waypoints;
    }

    java.util.List<WayPoint> waypoints;

    public String getPickup_date() {
        return pickup_date;
    }

    public void setPickup_date(String pickup_date) {
        this.pickup_date = pickup_date;
    }

    public  String pickup_date;

    public String getCurrentstatus() {
        return currentstatus;
    }

    public void setCurrentstatus(String currentstatus) {
        this.currentstatus = currentstatus;
    }

    /**
     *
     * @return
     * The jobid
     */
    public String getJobid() {
        return jobid;
    }

    /**
     *
     * @param jobid
     * The jobid
     */
    public void setJobid(String jobid) {
        this.jobid = jobid;
    }

    /**
     *
     * @return
     * The pickupLat
     */
    public String getPickupLat() {
        return pickupLat;
    }

    /**
     *
     * @param pickupLat
     * The pickup_lat
     */
    public void setPickupLat(String pickupLat) {
        this.pickupLat = pickupLat;
    }

    /**
     *
     * @return
     * The pickupLng
     */
    public String getPickupLng() {
        return pickupLng;
    }

    /**
     *
     * @param pickupLng
     * The pickup_lng
     */
    public void setPickupLng(String pickupLng) {
        this.pickupLng = pickupLng;
    }

    /**
     *
     * @return
     * The dropLat
     */
    public String getDropLat() {
        return dropLat;
    }

    /**
     *
     * @param dropLat
     * The drop_lat
     */
    public void setDropLat(String dropLat) {
        this.dropLat = dropLat;
    }

    /**
     *
     * @return
     * The dropLng
     */
    public String getDropLng() {
        return dropLng;
    }

    /**
     *
     * @param dropLng
     * The drop_lng
     */
    public void setDropLng(String dropLng) {
        this.dropLng = dropLng;
    }

    /**
     *
     * @return
     * The distance
     */
    public String getDistance() {
        return distance;
    }

    /**
     *
     * @param distance
     * The distance
     */
    public void setDistance(String distance) {
        this.distance = distance;
    }

    /**
     *
     * @return
     * The unit
     */
    public String getUnit() {
        return unit;
    }

    /**
     *
     * @param unit
     * The unit
     */
    public void setUnit(String unit) {
        this.unit = unit;
    }

    /**
     *
     * @return
     * The fare
     */
    public String getFare() {
        return fare;
    }

    /**
     *
     * @param fare
     * The fare
     */
    public void setFare(String fare) {
        this.fare = fare;
    }

    /**
     *
     * @return
     * The tariff
     */
    public String getTariff() {
        return tariff;
    }

    /**
     *
     * @param tariff
     * The tariff
     */
    public void setTariff(String tariff) {
        this.tariff = tariff;
    }

    /**
     *
     * @return
     * The duration
     */
    public String getDuration() {
        return duration;
    }

    /**
     *
     * @param duration
     * The duration
     */
    public void setDuration(String duration) {
        this.duration = duration;
    }

    /**
     *
     * @return
     * The passengers
     */
    public String getPassengers() {
        return passengers;
    }

    /**
     *
     * @param passengers
     * The passengers
     */
    public void setPassengers(String passengers) {
        this.passengers = passengers;
    }

    /**
     *
     * @return
     * The bags
     */
    public String getBags() {
        return bags;
    }

    /**
     *
     * @param bags
     * The bags
     */
    public void setBags(String bags) {
        this.bags = bags;
    }

    /**
     *
     * @return
     * The wheelchairs
     */
    public String getWheelchairs() {
        return wheelchairs;
    }

    /**
     *
     * @param wheelchairs
     * The wheelchairs
     */
    public void setWheelchairs(String wheelchairs) {
        this.wheelchairs = wheelchairs;
    }

    /**
     *
     * @return
     * The paxrid
     */
    public String getPaxrid() {
        return paxrid;
    }

    /**
     *
     * @param paxrid
     * The paxrid
     */
    public void setPaxrid(String paxrid) {
        this.paxrid = paxrid;
    }

    /**
     *
     * @return
     * The paxname
     */
    public String getPaxname() {
        return paxname;
    }

    /**
     *
     * @param paxname
     * The paxname
     */
    public void setPaxname(String paxname) {
        this.paxname = paxname;
    }

    /**
     *
     * @return
     * The paxtel
     */
    public String getPaxtel() {
        return paxtel;
    }

    /**
     *
     * @param paxtel
     * The paxtel
     */
    public void setPaxtel(String paxtel) {
        this.paxtel = paxtel;
    }

    /**
     *
     * @return
     * The paxemail
     */
    public String getPaxemail() {
        return paxemail;
    }

    /**
     *
     * @param paxemail
     * The paxemail
     */
    public void setPaxemail(String paxemail) {
        this.paxemail = paxemail;
    }

    /**
     *
     * @return
     * The when
     */
    public String getWhen() {
        return when;
    }

    /**
     *
     * @param when
     * The when
     */
    public void setWhen(String when) {
        this.when = when;
    }

    /**
     *
     * @return
     * The pickuptime
     */
    public String getPickuptime() {
        return pickuptime;
    }

    /**
     *
     * @param pickuptime
     * The pickuptime
     */
    public void setPickuptime(String pickuptime) {
        this.pickuptime = pickuptime;
    }

    /**
     *
     * @return
     * The pickupdistance
     */
    public String getPickupdistance() {
        return pickupdistance;
    }

    /**
     *
     * @param pickupdistance
     * The pickupdistance
     */
    public void setPickupdistance(String pickupdistance) {
        this.pickupdistance = pickupdistance;
    }

    /**
     *
     * @return
     * The pickup
     */
    public String getPickup() {
        return pickup;
    }

    /**
     *
     * @param pickup
     * The pickup
     */
    public void setPickup(String pickup) {
        this.pickup = pickup;
    }

    /**
     *
     * @return
     * The dropoff
     */
    public String getDropoff() {
        return dropoff;
    }

    /**
     *
     * @param dropoff
     * The dropoff
     */
    public void setDropoff(String dropoff) {
        this.dropoff = dropoff;
    }

    /**
     *
     * @return
     * The timer
     */
    public String getTimer() {
        return timer;
    }

    /**
     *
     * @param timer
     * The timer
     */
    public void setTimer(String timer) {
        this.timer = timer;
    }

    /**
     *
     * @return
     * The journeyType
     */
    public String getJourneyType() {
        return journeyType;
    }

    /**
     *
     * @param journeyType
     * The journey_type
     */
    public void setJourneyType(String journeyType) {
        this.journeyType = journeyType;
    }

    /**
     *
     * @return
     * The stopCount
     */


    /**
     *
     * @return
     * The stop1
     */
    public String getStop1() {
        return stop1;
    }

    /**
     *
     * @param stop1
     * The stop1
     */
    public void setStop1(String stop1) {
        this.stop1 = stop1;
    }

    /**
     *
     * @return
     * The stop1Lat
     */
    public String getStop1Lat() {
        return stop1Lat;
    }

    /**
     *
     * @param stop1Lat
     * The stop1_lat
     */
    public void setStop1Lat(String stop1Lat) {
        this.stop1Lat = stop1Lat;
    }

    /**
     *
     * @return
     * The stop1Lng
     */
    public String getStop1Lng() {
        return stop1Lng;
    }

    /**
     *
     * @param stop1Lng
     * The stop1_lng
     */
    public void setStop1Lng(String stop1Lng) {
        this.stop1Lng = stop1Lng;
    }

    /**
     *
     * @return
     * The stop2
     */
    public String getStop2() {
        return stop2;
    }

    /**
     *
     * @param stop2
     * The stop2
     */
    public void setStop2(String stop2) {
        this.stop2 = stop2;
    }

    /**
     *
     * @return
     * The stop2Lat
     */
    public String getStop2Lat() {
        return stop2Lat;
    }

    /**
     *
     * @param stop2Lat
     * The stop2_lat
     */
    public void setStop2Lat(String stop2Lat) {
        this.stop2Lat = stop2Lat;
    }

    /**
     *
     * @return
     * The stop2Lng
     */
    public String getStop2Lng() {
        return stop2Lng;
    }

    /**
     *
     * @param stop2Lng
     * The stop2_lng
     */
    public void setStop2Lng(String stop2Lng) {
        this.stop2Lng = stop2Lng;
    }

    /**
     *
     * @return
     * The stop3
     */
    public String getStop3() {
        return stop3;
    }

    /**
     *
     * @param stop3
     * The stop3
     */
    public void setStop3(String stop3) {
        this.stop3 = stop3;
    }

    /**
     *
     * @return
     * The stop3Lat
     */
    public String getStop3Lat() {
        return stop3Lat;
    }

    /**
     *
     * @param stop3Lat
     * The stop3_lat
     */
    public void setStop3Lat(String stop3Lat) {
        this.stop3Lat = stop3Lat;
    }

    /**
     *
     * @return
     * The stop3Lng
     */
    public String getStop3Lng() {
        return stop3Lng;
    }

    /**
     *
     * @param stop3Lng
     * The stop3_lng
     */
    public void setStop3Lng(String stop3Lng) {
        this.stop3Lng = stop3Lng;
    }

    /**
     *
     * @return
     * The stop4
     */
    public String getStop4() {
        return stop4;
    }

    /**
     *
     * @param stop4
     * The stop4
     */
    public void setStop4(String stop4) {
        this.stop4 = stop4;
    }

    /**
     *
     * @return
     * The stop4Lat
     */
    public String getStop4Lat() {
        return stop4Lat;
    }

    /**
     *
     * @param stop4Lat
     * The stop4_lat
     */
    public void setStop4Lat(String stop4Lat) {
        this.stop4Lat = stop4Lat;
    }

    /**
     *
     * @return
     * The stop4Lng
     */
    public String getStop4Lng() {
        return stop4Lng;
    }

    /**
     *
     * @param stop4Lng
     * The stop4_lng
     */
    public void setStop4Lng(String stop4Lng) {
        this.stop4Lng = stop4Lng;
    }

    /**
     *
     * @return
     * The stop5
     */
    public String getStop5() {
        return stop5;
    }

    /**
     *
     * @param stop5
     * The stop5
     */
    public void setStop5(String stop5) {
        this.stop5 = stop5;
    }

    /**
     *
     * @return
     * The stop5Lat
     */
    public String getStop5Lat() {
        return stop5Lat;
    }

    /**
     *
     * @param stop5Lat
     * The stop5_lat
     */
    public void setStop5Lat(String stop5Lat) {
        this.stop5Lat = stop5Lat;
    }

    /**
     *
     * @return
     * The stop5Lng
     */
    public String getStop5Lng() {
        return stop5Lng;
    }

    /**
     *
     * @param stop5Lng
     * The stop5_lng
     */
    public void setStop5Lng(String stop5Lng) {
        this.stop5Lng = stop5Lng;
    }

    /**
     *
     * @return
     * The paymentType
     */
    public String getPaymentType() {
        return paymentType;
    }

    /**
     *
     * @param paymentType
     * The payment_type
     */
    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    /**
     *
     * @return
     * The to
     */
    public String getTo() {
        return to;
    }

    /**
     *
     * @param to
     * The to
     */
    public void setTo(String to) {
        this.to = to;
    }

    /**
     *
     * @return
     * The roomId
     */
    public String getRoomId() {
        return roomId;
    }

    /**
     *
     * @param roomId
     * The room_id
     */
    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    /**
     *
     * @return
     * The sender
     */
    public String getSender() {
        return sender;
    }

    /**
     *
     * @param sender
     * The sender
     */
    public void setSender(String sender) {
        this.sender = sender;
    }

    /**
     *
     * @return
     * The dispatcherid
     */
    public String getDispatcherid() {
        return dispatcherid;
    }

    /**
     *
     * @param dispatcherid
     * The dispatcherid
     */
    public void setDispatcherid(String dispatcherid) {
        this.dispatcherid = dispatcherid;
    }

    /**
     *
     * @return
     * The dispatcher
     */
    public String getDispatcher() {
        return dispatcher;
    }

    /**
     *
     * @param dispatcher
     * The dispatcher
     */
    public void setDispatcher(String dispatcher) {
        this.dispatcher = dispatcher;
    }


    public String getStop_count() {
        return stop_count;
    }

    public void setStop_count(String stop_count) {
        this.stop_count = stop_count;
    }

    public String getVehicle_type() {
        return vehicle_type;
    }

    public void setVehicle_type(String vehicle_type) {
        this.vehicle_type = vehicle_type;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
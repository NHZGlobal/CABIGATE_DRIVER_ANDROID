package mobileapps.technroid.io.cabigate.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class List {

    @SerializedName("vehicleid")
    @Expose
    public String vehicleid;
    @SerializedName("name")
    @Expose
    public String name;
    @SerializedName("number")
    @Expose
    public String number;
    @SerializedName("driver")
    @Expose
    public String driver;
    @SerializedName("status")
    @Expose
    public String status;

}

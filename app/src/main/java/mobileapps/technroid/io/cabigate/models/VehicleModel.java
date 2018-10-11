package mobileapps.technroid.io.cabigate.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by LAPTOP WORLD on 01/06/2016.
 */
public class VehicleModel {


    @SerializedName("status")
    @Expose
    public int status;
    @SerializedName("response")
    @Expose
    public Response response;

}



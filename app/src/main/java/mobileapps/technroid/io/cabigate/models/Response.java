package mobileapps.technroid.io.cabigate.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Response {

    @SerializedName("count")
    @Expose
    public int count;
    @SerializedName("list")
    @Expose
    public java.util.List<List> list = new ArrayList<List>();

}

package mobileapps.technroid.io.cabigate.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by LAPTOP WORLD on 01/06/2016.
 */

public class DriverModelResponse {


        @SerializedName("SOCKETURL")
        @Expose
        public String socketUrl;

        @SerializedName("CompanyID")
        @Expose
        public String companyID;
        @SerializedName("Userid")
        @Expose
        public String userid;
        @SerializedName("Username")
        @Expose
        public String username;
        @SerializedName("driver_image")
        @Expose
        public String driverImage;
        @SerializedName("job_active")
        @Expose
        public String jobActive;
        @SerializedName("last_jobid")
        @Expose
        public String lastJobid;
        @SerializedName("last_vehicleid")
        @Expose
        public String lastVehicleid;
        @SerializedName("last_zoneid")
        @Expose
        public String lastZoneid;
        @SerializedName("last_status")
        @Expose
        public String lastStatus;
        @SerializedName("last_shiftend_time")
        @Expose
        public int lastShiftendTime;
        @SerializedName("token")
        @Expose
        public String token;
        @SerializedName("show_dispatcher")
        @Expose
        public String showDispatcher;

}

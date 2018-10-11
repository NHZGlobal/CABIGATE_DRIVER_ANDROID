package mobileapps.technroid.io.cabigate.api;

import mobileapps.technroid.io.cabigate.models.DriverModel;
import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.Query;
import retrofit.http.Streaming;
import rx.Observable;

/**
 * Created by Muhammad on 21/03/2015.
 */
public interface URLManager
{


    @FormUrlEncoded
    @POST("/login")
      Observable<DriverModel>  login(
            @Field("data") String email
           );

    @FormUrlEncoded
    @POST("/vehiclelist")
      Observable<Response> getVehicle(
            @Field("data") String email
    );

    @FormUrlEncoded
    @POST("/logout")
      Observable<Response> logout(
            @Field("data") String data
    );
    @FormUrlEncoded
    @POST("/shiftout")
      Observable<Response> shiftOut(
            @Field("data") String data
    );

    @FormUrlEncoded
    @POST("/shiftin")
      Observable<Response> shiftIN(
            @Field("data") String data
    );

    @FormUrlEncoded
    @POST("/statuspdate")
      Observable<Response> statusDriverUpdate(
            @Field("data") String data
    );

    @FormUrlEncoded
    @POST("/actualfare")
      Observable<Response> actualFare(
            @Field("data") String data
    );
 @FormUrlEncoded
    @POST("/updatejobdetails")
      Observable<Response> updateJobDetails(
            @Field("data") String data
    );


   @FormUrlEncoded
    @POST("/zonelist")
      Observable<Response> getZoneList(
            @Field("data") String data
    );

    @FormUrlEncoded
    @POST("/offers")
      Observable<Response> getOffers(
            @Field("data") String data
    );

    @FormUrlEncoded
    @POST("/acceptoffer")
      Observable<Response> acceptOffer(
            @Field("data") String data
    );

    @FormUrlEncoded
    @POST("/zonecheckout")
      Observable<Response> zoneCheckout(
            @Field("data") String data
    );
    @FormUrlEncoded
    @POST("/mylaststate")
      Observable<Response> myLastState(
            @Field("data") String data
    );
    @FormUrlEncoded
    @POST("/zonecheckin")
      Observable<Response> zoneCheckin(
            @Field("data") String data
    );


    @GET("/openjobsque")
    public Observable<Response> openJobsque(
            @Query("companyid") String companyid,
            @Query("userid") String userid,
            @Query("token") String token
    );


    @GET("/zonequeue")
    public Observable<Response> zoneQueue(
            @Query("companyid") String companyid,
            @Query("userid") String userid,
            @Query("token") String token
    );

    @GET("/myzonequeue")
    public Observable<Response> myZonequeue(
            @Query("companyid") String companyid,
            @Query("zoneid") String zoneid,
            @Query("token") String token
    );

    //companyid=2100&userid=2&jobid=18&status=callout&token=2100
    @GET("/updatestatus")
    public Observable<Response> updateStatus(
            @Query("companyid") String companyid,
            @Query("userid") String userid,
            @Query("status") String status,
            @Query("token") String token,
            @Query("jobid") String jobid
          );
    ///companyid=2100&userid=2&jobid=18&reason=noshow&token=2100

    @GET("/canceljob")
    public Observable<Response> canceljob(
            @Query("companyid") String companyid,
            @Query("userid") String userid,
            @Query("reason") String reason,
            @Query("token") String token,
            @Query("jobid") String jobid
          );

   /// http://api.cabigate.com/index.php/latestjob?companyid=2100&userid=1
   @GET("/latestjob")
    public Observable<Response> latestJob(
            @Query("companyid") String companyid,
            @Query("userid") String userid
          );



    //deliverjob?companyid=2100&userid=2&jobid=18&
    // rating=5&token=2100&comment=yesasdasd&fare=18&pay_via=cash
    @GET("/deliverjob")
    public Observable<Response> deliverJob(
            @Query("companyid") String companyid,
            @Query("userid") String userid,
            @Query("token") String token,
            @Query("jobid") String jobid,
            @Query("rating") String rating,
            @Query("comment") String comment,
            @Query("fare") String fare,
            @Query("pay_via") String pay_via

    );



    @FormUrlEncoded
    @POST("/unassignedjobs")
    public Observable<Response> unAssignedJobs(
            @Field("data") String data

    );
    @FormUrlEncoded
 @POST("/availabeldrivers")
    public Observable<Response> availabelDrivers(
            @Field("data") String data

    );

    @FormUrlEncoded
 @POST("/vehiclemodels")
    public Observable<Response> vehicleModels(
            @Field("data") String data

    );
 @FormUrlEncoded
 @POST("/createjob")
    public Observable<Response> createJob(
            @Field("data") String data

    );


 @FormUrlEncoded
 @POST("/assignjob")
    public Observable<Response> assignJob(
            @Field("data") String data

    );@FormUrlEncoded
 @POST("/searchpassengers")
    public Observable<Response> searchPassengers(
            @Field("data") String data

    );







    @GET("/drivers/getHistory")
    public void getHistory(
            @Query("driverId") String userId,
            @Query("accessKey") String accessKey,
            @Query("offset") int offset,
            Callback<Response> callback);


    @GET("/media/{directory}/{filename}")
    @Streaming
    void getVideo(
            @Path("directory") String file,
            @Path("filename") String filename,
            Callback<Response> callback
    );
}


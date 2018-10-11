package mobileapps.technroid.io.cabigate.models;

public class BookingModel
{
    public static final int BOOKING_STATUS_QUEUE          = 0;
    public static final int BOOKING_STATUS_ACCEPTED       = 1;
    public static final int BOOKING_STATUS_ACTIVE         = 2;
    public static final int BOOKING_STATUS_PENDING_RATE   = 3;
    public static final int BOOKING_STATUS_DONE           = 4;
    public static final int BOOKING_STATUS_CANCELLED      = 5;
    public static final int BOOKING_STATUS_REJECTED       = 6;
    public static final int BOOKING_STATUS_UNKNOWN        = 7;

    public String id;
    public String cardId;
    public String userId;
    public String driverId;

    public double pinLat;
    public double pinLng;
    public double destLat;
    public double destLng;

    public int bookingStatus;
    public String creationDate;
    public String updatedTime;
    public String bookingNumber;
    public String pickupAddress;
    public String destAddress;

    public int rating;
    public float distanceTravelled;
    public float totalCost;

    public String comment;

    public UserModel user;

    public BookingModel()
    {
    }

    /**
     * *********************************************************************
     *
     * @param mContext
     * @return
     */
}

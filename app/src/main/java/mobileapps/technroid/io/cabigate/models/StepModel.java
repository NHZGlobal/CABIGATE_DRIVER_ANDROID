package mobileapps.technroid.io.cabigate.models;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class StepModel
{
	public String distance = "";
	public String duration = "";
	public LatLng endLocation;
	public LatLng startLocation;
	public String instructions = "";
	public String maneuver = "";

	public ArrayList<LatLng> wayPoints = new ArrayList<LatLng>();
	
	public StepModel()
	{}		
}	